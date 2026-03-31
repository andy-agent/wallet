package com.v2ray.ang.service;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0010\u000e\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u001c\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000bJ\u0006\u0010\r\u001a\u00020\u0007R\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000e"}, d2 = {"Lcom/v2ray/ang/service/ProcessService;", "", "<init>", "()V", "process", "Ljava/lang/Process;", "runProcess", "", "context", "Landroid/content/Context;", "cmd", "", "", "stopProcess", "app_fdroidDebug"})
public final class ProcessService {
    @org.jetbrains.annotations.Nullable()
    private java.lang.Process process;
    
    public ProcessService() {
        super();
    }
    
    /**
     * Runs a process with the given command.
     * @param context The context.
     * @param cmd The command to run.
     */
    public final void runProcess(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> cmd) {
    }
    
    /**
     * Stops the running process.
     */
    public final void stopProcess() {
    }
}