.class public Lcom/eta/dseek/RegTool;
.super Ljava/lang/Object;
.source "RegTool.java"


# direct methods
.method public constructor <init>()V
    .locals 0

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method public static main([Ljava/lang/String;)V
    .locals 6

    :try_start_0
    const-string v0, "/data/adb/lspd/config/modules_config.db"
    const/4 v1, 0x0
    const/4 v2, 0x0
    invoke-static {v0, v1, v2}, Landroid/database/sqlite/SQLiteDatabase;->openDatabase(Ljava/lang/String;Landroid/database/sqlite/SQLiteDatabase$CursorFactory;I)Landroid/database/sqlite/SQLiteDatabase;
    move-result-object v0

    const-string v1, "com.eta.harkpro.unloc"
    const-string v2, "/data/app/~~5-gDTeiuFG4ZYGBqtpAV1g==/com.eta.harkpro.unloc-i9U3rnA2MfpXlgFiGxHBVA==/base.apk"

    invoke-static {v0, v1, v2}, Lcom/eta/dseek/RegTool;->ensureModule(Landroid/database/sqlite/SQLiteDatabase;Ljava/lang/String;Ljava/lang/String;)V
    invoke-static {v0, v1}, Lcom/eta/dseek/RegTool;->ensureState(Landroid/database/sqlite/SQLiteDatabase;Ljava/lang/String;)V
    invoke-static {v0, v1}, Lcom/eta/dseek/RegTool;->ensureScope(Landroid/database/sqlite/SQLiteDatabase;Ljava/lang/String;)V

    invoke-virtual {v0}, Landroid/database/sqlite/SQLiteDatabase;->close()V

    sget-object v0, Ljava/lang/System;->out:Ljava/io/PrintStream;
    const-string v1, "EtaHark: db ok"
    invoke-virtual {v0, v1}, Ljava/io/PrintStream;->println(Ljava/lang/String;)V
    :try_end_0
    .catch Ljava/lang/Throwable; {:try_start_0 .. :try_end_0} :catch_0

    return-void

    :catch_0
    move-exception v0

    sget-object v1, Ljava/lang/System;->out:Ljava/io/PrintStream;
    invoke-virtual {v0}, Ljava/lang/Throwable;->toString()Ljava/lang/String;
    move-result-object v0
    invoke-virtual {v1, v0}, Ljava/io/PrintStream;->println(Ljava/lang/String;)V

    return-void
.end method

.method private static ensureModule(Landroid/database/sqlite/SQLiteDatabase;Ljava/lang/String;Ljava/lang/String;)V
    .locals 4

    :try_start_0
    const-string v1, "INSERT OR REPLACE INTO modules (module_pkg_name, apk_path) VALUES (?, ?)"
    const/4 v2, 0x2
    new-array v2, v2, [Ljava/lang/String;
    const/4 v3, 0x0
    aput-object p1, v2, v3
    const/4 v3, 0x1
    aput-object p2, v2, v3
    invoke-virtual {p0, v1, v2}, Landroid/database/sqlite/SQLiteDatabase;->execSQL(Ljava/lang/String;[Ljava/lang/Object;)V
    :try_end_0
    .catch Ljava/lang/Throwable; {:try_start_0 .. :try_end_0} :catch_0

    goto :goto_0

    :catch_0
    move-exception v0

    sget-object v1, Ljava/lang/System;->out:Ljava/io/PrintStream;
    invoke-virtual {v0}, Ljava/lang/Throwable;->toString()Ljava/lang/String;
    move-result-object v0
    invoke-virtual {v1, v0}, Ljava/io/PrintStream;->println(Ljava/lang/String;)V

    :goto_0
    return-void
.end method

.method private static ensureState(Landroid/database/sqlite/SQLiteDatabase;Ljava/lang/String;)V
    .locals 4

    :try_start_0
    const-string v1, "INSERT OR REPLACE INTO modules_state (module_pkg_name, user_id, enabled, scope_request_blocked) VALUES (?, 0, 1, 0)"
    const/4 v2, 0x1
    new-array v2, v2, [Ljava/lang/String;
    const/4 v3, 0x0
    aput-object p1, v2, v3
    invoke-virtual {p0, v1, v2}, Landroid/database/sqlite/SQLiteDatabase;->execSQL(Ljava/lang/String;[Ljava/lang/Object;)V
    :try_end_0
    .catch Ljava/lang/Throwable; {:try_start_0 .. :try_end_0} :catch_0

    goto :goto_0

    :catch_0
    move-exception v0

    sget-object v1, Ljava/lang/System;->out:Ljava/io/PrintStream;
    invoke-virtual {v0}, Ljava/lang/Throwable;->toString()Ljava/lang/String;
    move-result-object v0
    invoke-virtual {v1, v0}, Ljava/io/PrintStream;->println(Ljava/lang/String;)V

    :goto_0
    return-void
.end method

.method private static ensureScope(Landroid/database/sqlite/SQLiteDatabase;Ljava/lang/String;)V
    .locals 5

    :try_start_0
    const-string v1, "INSERT OR REPLACE INTO scope (module_pkg_name, app_pkg_name, user_id) VALUES (?, ?, 0)"
    const/4 v2, 0x2
    new-array v2, v2, [Ljava/lang/String;
    const/4 v3, 0x0
    aput-object p1, v2, v3
    const/4 v3, 0x1
    const-string v4, "com.hark.android"
    aput-object v4, v2, v3
    invoke-virtual {p0, v1, v2}, Landroid/database/sqlite/SQLiteDatabase;->execSQL(Ljava/lang/String;[Ljava/lang/Object;)V
    :try_end_0
    .catch Ljava/lang/Throwable; {:try_start_0 .. :try_end_0} :catch_0

    goto :goto_0

    :catch_0
    move-exception v0

    sget-object v1, Ljava/lang/System;->out:Ljava/io/PrintStream;
    invoke-virtual {v0}, Ljava/lang/Throwable;->toString()Ljava/lang/String;
    move-result-object v0
    invoke-virtual {v1, v0}, Ljava/io/PrintStream;->println(Ljava/lang/String;)V

    :goto_0
    return-void
.end method
