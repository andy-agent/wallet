package com.v2ray.ang.payment.ui.activity;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0007\u0018\u0000 \'2\u00020\u0001:\u0001\'B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\r\u001a\u00020\u000e2\b\u0010\u000f\u001a\u0004\u0018\u00010\u0010H\u0014J\b\u0010\u0011\u001a\u00020\tH\u0002J\b\u0010\u0012\u001a\u00020\u000eH\u0002J\b\u0010\u0013\u001a\u00020\u000eH\u0002J\b\u0010\u0014\u001a\u00020\u000eH\u0002J\b\u0010\u0015\u001a\u00020\u000eH\u0002J\b\u0010\u0016\u001a\u00020\u000eH\u0002J\b\u0010\u0017\u001a\u00020\u000eH\u0002J\b\u0010\u0018\u001a\u00020\u000eH\u0002J\u0018\u0010\u0019\u001a\u00020\t2\u0006\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u001bH\u0002J \u0010\u001d\u001a\u00020\t2\u0006\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u001b2\u0006\u0010\u001e\u001a\u00020\u001bH\u0002J \u0010\u001f\u001a\u00020\u000e2\u0006\u0010 \u001a\u00020!2\u0006\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\"\u001a\u00020\tH\u0002J\u0010\u0010#\u001a\u00020\u000e2\u0006\u0010$\u001a\u00020\tH\u0002J\u0010\u0010%\u001a\u00020\u000e2\u0006\u0010&\u001a\u00020\u001bH\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006("}, d2 = {"Lcom/v2ray/ang/payment/ui/activity/LoginActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "<init>", "()V", "binding", "Lcom/v2ray/ang/databinding/ActivityLoginBinding;", "repository", "Lcom/v2ray/ang/payment/data/repository/PaymentRepository;", "isRegisterMode", "", "passwordRegex", "Lkotlin/text/Regex;", "emailRegex", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "checkAutoLogin", "navigateToUserProfile", "setupUI", "setupListeners", "toggleMode", "updateUIForCurrentMode", "attemptLogin", "attemptRegister", "validateLoginInput", "email", "", "password", "validateRegisterInput", "confirmPassword", "handleAuthSuccess", "authData", "Lcom/v2ray/ang/payment/data/model/AuthData;", "isRegistration", "showLoading", "show", "showError", "message", "Companion", "app_playstoreDebug"})
public final class LoginActivity extends androidx.appcompat.app.AppCompatActivity {
    private com.v2ray.ang.databinding.ActivityLoginBinding binding;
    private com.v2ray.ang.payment.data.repository.PaymentRepository repository;
    private boolean isRegisterMode = false;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.text.Regex passwordRegex = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.text.Regex emailRegex = null;
    public static final int RESULT_CODE_LOGIN_SUCCESS = android.app.Activity.RESULT_OK;
    @org.jetbrains.annotations.NotNull()
    public static final com.v2ray.ang.payment.ui.activity.LoginActivity.Companion Companion = null;
    
    public LoginActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final boolean checkAutoLogin() {
        return false;
    }
    
    private final void navigateToUserProfile() {
    }
    
    private final void setupUI() {
    }
    
    private final void setupListeners() {
    }
    
    private final void toggleMode() {
    }
    
    private final void updateUIForCurrentMode() {
    }
    
    private final void attemptLogin() {
    }
    
    private final void attemptRegister() {
    }
    
    private final boolean validateLoginInput(java.lang.String email, java.lang.String password) {
        return false;
    }
    
    private final boolean validateRegisterInput(java.lang.String email, java.lang.String password, java.lang.String confirmPassword) {
        return false;
    }
    
    private final void handleAuthSuccess(com.v2ray.ang.payment.data.model.AuthData authData, java.lang.String email, boolean isRegistration) {
    }
    
    private final void showLoading(boolean show) {
    }
    
    private final void showError(java.lang.String message) {
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0006"}, d2 = {"Lcom/v2ray/ang/payment/ui/activity/LoginActivity$Companion;", "", "<init>", "()V", "RESULT_CODE_LOGIN_SUCCESS", "", "app_playstoreDebug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}