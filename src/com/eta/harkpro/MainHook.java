package com.eta.harkpro;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Hark Pro 解锁
 *
 * 目标: com.hark.android
 *
 * PairIP (com.pairip.licensecheck) 会在授权校验失败时下发一个 PendingIntent，
 * 由 LicenseClient.startPaywallActivity 启动，弹出 "Upgrade to Pro²" 页面，
 * 并在同一条链路里调用 scheduleAppShutdown 杀掉 App。
 *
 * 本模块直接短路该方法，弹窗不再出现，也不触发关机。
 *
 * 另附：把计费模型里的 unlimited 置 true、用量上限拉到最大，
 * 让已解锁的功能在 UI 层保持可用。
 */
public class MainHook implements IXposedHookLoadPackage {

    private static final String TAG = "EtaHark: ";
    private static final String TARGET = "com.hark.android";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lp) {
        try {
            if (!TARGET.equals(lp.packageName)) return;
            XposedBridge.log(TAG + "installing in " + lp.processName);

            killPaywall(lp.classLoader);
            forceUnlimited(lp.classLoader);
            raiseMeters(lp.classLoader);
        } catch (Throwable t) {
            XposedBridge.log(t);
        }
    }

    /** 1. 拦掉 PairIP 的付费墙 Activity */
    private void killPaywall(ClassLoader cl) {
        try {
            Class<?> c = XposedHelpers.findClassIfExists(
                    "com.pairip.licensecheck.LicenseClient", cl);
            if (c == null) {
                XposedBridge.log(TAG + "LicenseClient not found");
                return;
            }

            boolean hit = false;
            for (Method m : c.getDeclaredMethods()) {
                if ("startPaywallActivity".equals(m.getName())) {
                    XposedBridge.hookMethod(m, XC_MethodReplacement.DO_NOTHING);
                    hit = true;
                    XposedBridge.log(TAG + "killed startPaywallActivity");
                }
                // 兜底：关闭 App 的调度也一并拦掉
                if ("scheduleAppShutdown".equals(m.getName())
                        || "createCloseAppIntentOrExitIfAppInBackground".equals(m.getName())) {
                    XposedBridge.hookMethod(m, XC_MethodReplacement.DO_NOTHING);
                }
            }
            if (!hit) XposedBridge.log(TAG + "startPaywallActivity missing");
        } catch (Throwable t) {
            XposedBridge.log(TAG + "paywall hook fail " + t);
        }
    }

    /** 2. 计费模型 unlimited 强制 true */
    private void forceUnlimited(ClassLoader cl) {
        // Ltu1; = BillingSummaryPlanDto，字段 b:Z = unlimited（构造参数第 3 个）
        try {
            Class<?> c = XposedHelpers.findClassIfExists("tu1", cl);
            if (c == null) return;
            for (Constructor<?> ctor : c.getDeclaredConstructors()) {
                ctor.setAccessible(true);
                XposedBridge.hookMethod(ctor, new XC_MethodHook() {
                    @Override protected void afterHookedMethod(MethodHookParam p) {
                        setBool(p.thisObject, "b", true);
                        // endsAt 清空，避免过期判断继续挡
                        setStr(p.thisObject, "e", null);
                    }
                });
            }
            XposedBridge.log(TAG + "unlimited forced");
        } catch (Throwable t) {
            XposedBridge.log(TAG + "unlimited fail " + t);
        }
    }

    /** 3. 用量上限拉到最大、已用量清零 */
    private void raiseMeters(ClassLoader cl) {
        // Lll1; = BillingFeatureUsageDto
        // e=countUsed f=countLimit g=harkUsed h=harkLimit i=durationUsedMs j=durationLimitMs
        try {
            Class<?> c = XposedHelpers.findClassIfExists("ll1", cl);
            if (c == null) return;
            for (Constructor<?> ctor : c.getDeclaredConstructors()) {
                ctor.setAccessible(true);
                XposedBridge.hookMethod(ctor, new XC_MethodHook() {
                    @Override protected void afterHookedMethod(MethodHookParam p) {
                        setLong(p.thisObject, "e", 0L);
                        setLong(p.thisObject, "f", Long.MAX_VALUE);
                        setLong(p.thisObject, "g", 0L);
                        setLong(p.thisObject, "h", Long.MAX_VALUE);
                        setLong(p.thisObject, "i", 0L);
                        setLong(p.thisObject, "j", Long.MAX_VALUE);
                    }
                });
            }
            XposedBridge.log(TAG + "meters raised");
        } catch (Throwable t) {
            XposedBridge.log(TAG + "meters fail " + t);
        }
    }

    // ---------------------------------------------------------------- 工具

    private static void setBool(Object obj, String name, boolean v) {
        try {
            Field f = obj.getClass().getDeclaredField(name);
            f.setAccessible(true);
            stripFinal(f);
            f.setBoolean(obj, v);
        } catch (Throwable ignored) { }
    }

    private static void setLong(Object obj, String name, long v) {
        try {
            Field f = obj.getClass().getDeclaredField(name);
            f.setAccessible(true);
            stripFinal(f);
            f.setLong(obj, v);
        } catch (Throwable ignored) { }
    }

    private static void setStr(Object obj, String name, String v) {
        try {
            Field f = obj.getClass().getDeclaredField(name);
            f.setAccessible(true);
            stripFinal(f);
            f.set(obj, v);
        } catch (Throwable ignored) { }
    }

    /** 字段是 PUBLIC FINAL，先清 modifiers 的 FINAL 位再写 */
    private static void stripFinal(Field f) {
        try {
            Field mod = Field.class.getDeclaredField("modifiers");
            mod.setAccessible(true);
            mod.setInt(f, mod.getInt(f) & ~0x10);
        } catch (Throwable ignored) { }
    }
}
