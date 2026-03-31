package com.v2ray.ang;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u0000 \u000b2\u00020\u0001:\u0001\u000bB\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\u0004\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u0014J\b\u0010\n\u001a\u00020\u0005H\u0016R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\f"}, d2 = {"Lcom/v2ray/ang/AngApplication;", "Landroidx/multidex/MultiDexApplication;", "<init>", "()V", "attachBaseContext", "", "base", "Landroid/content/Context;", "workManagerConfiguration", "Landroidx/work/Configuration;", "onCreate", "Companion", "app_fdroidDebug"})
public final class AngApplication extends androidx.multidex.MultiDexApplication {
    public static com.v2ray.ang.AngApplication application;
    @org.jetbrains.annotations.NotNull()
    private final androidx.work.Configuration workManagerConfiguration = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.v2ray.ang.AngApplication.Companion Companion = null;
    
    public AngApplication() {
        super();
    }
    
    /**
     * Attaches the base context to the application.
     * @param base The base context.
     */
    @java.lang.Override()
    protected void attachBaseContext(@org.jetbrains.annotations.Nullable()
    android.content.Context base) {
    }
    
    /**
     * Initializes the application.
     */
    @java.lang.Override()
    public void onCreate() {
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u001a\u0010\u0004\u001a\u00020\u0005X\u0086.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0006\u0010\u0007\"\u0004\b\b\u0010\t\u00a8\u0006\n"}, d2 = {"Lcom/v2ray/ang/AngApplication$Companion;", "", "<init>", "()V", "application", "Lcom/v2ray/ang/AngApplication;", "getApplication", "()Lcom/v2ray/ang/AngApplication;", "setApplication", "(Lcom/v2ray/ang/AngApplication;)V", "app_fdroidDebug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.v2ray.ang.AngApplication getApplication() {
            return null;
        }
        
        public final void setApplication(@org.jetbrains.annotations.NotNull()
        com.v2ray.ang.AngApplication p0) {
        }
    }
}