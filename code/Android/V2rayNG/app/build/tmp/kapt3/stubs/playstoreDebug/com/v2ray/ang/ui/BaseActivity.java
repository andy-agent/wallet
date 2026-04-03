package com.v2ray.ang.ui;

/**
 * BaseActivity provides common helpers and UI wiring used across the app's activities.
 *
 * Responsibilities:
 * - Inflate a shared base layout that contains a toolbar and a content container.
 * - Provide convenient overloads of `setContentViewWithToolbar` to attach child layouts or
 *  view-binding roots into the base container and initialize the toolbar.
 * - Expose a global in-layout `ProgressBar` (cached) with `showLoading()` / `hideLoading()` helpers.
 * - Provide a helper to add a custom divider to RecyclerViews.
 * - Wrap base context according to user locale settings.
 */
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\\\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\r\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\b&\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\u0006\u001a\u00020\u00072\b\u0010\b\u001a\u0004\u0018\u00010\tH\u0014J\u0010\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0016J\u0012\u0010\u000e\u001a\u00020\u00072\b\u0010\u000f\u001a\u0004\u0018\u00010\u0010H\u0014J,\u0010\u0011\u001a\u00020\u00072\u0006\u0010\u0012\u001a\u00020\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u00102\u0006\u0010\u0015\u001a\u00020\u00162\b\b\u0002\u0010\u0017\u001a\u00020\u0016H\u0004J(\u0010\u0018\u001a\u00020\u00072\b\u0010\u0019\u001a\u0004\u0018\u00010\u001a2\b\b\u0002\u0010\u001b\u001a\u00020\u000b2\n\b\u0002\u0010\u001c\u001a\u0004\u0018\u00010\u001dH\u0004J&\u0010\u001e\u001a\u00020\u00072\u0006\u0010\u001f\u001a\u00020\u00162\b\b\u0002\u0010\u001b\u001a\u00020\u000b2\n\b\u0002\u0010\u001c\u001a\u0004\u0018\u00010\u001dH\u0004J&\u0010\u001e\u001a\u00020\u00072\u0006\u0010 \u001a\u00020!2\b\b\u0002\u0010\u001b\u001a\u00020\u000b2\n\b\u0002\u0010\u001c\u001a\u0004\u0018\u00010\u001dH\u0004J\"\u0010\u0018\u001a\u00020\u00072\u0006\u0010\"\u001a\u00020!2\u0006\u0010\u001b\u001a\u00020\u000b2\b\u0010\u001c\u001a\u0004\u0018\u00010\u001dH\u0002J\b\u0010#\u001a\u00020\u0007H\u0004J\b\u0010$\u001a\u00020\u0007H\u0004J\b\u0010%\u001a\u00020\u000bH\u0004R\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006&"}, d2 = {"Lcom/v2ray/ang/ui/BaseActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "<init>", "()V", "progressBar", "Lcom/google/android/material/progressindicator/LinearProgressIndicator;", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "onOptionsItemSelected", "", "item", "Landroid/view/MenuItem;", "attachBaseContext", "newBase", "Landroid/content/Context;", "addCustomDividerToRecyclerView", "recyclerView", "Landroidx/recyclerview/widget/RecyclerView;", "context", "drawableResId", "", "orientation", "setupToolbar", "toolbar", "Landroidx/appcompat/widget/Toolbar;", "showHomeAsUp", "title", "", "setContentViewWithToolbar", "layoutResId", "childView", "Landroid/view/View;", "baseRoot", "showLoading", "hideLoading", "isLoadingVisible", "app_playstoreDebug"})
public abstract class BaseActivity extends androidx.appcompat.app.AppCompatActivity {
    @org.jetbrains.annotations.Nullable()
    private com.google.android.material.progressindicator.LinearProgressIndicator progressBar;
    
    public BaseActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    /**
     * Handle action bar item selections.
     *
     * Currently this handles the home/up button by delegating to the activity's
     * onBackPressedDispatcher to provide consistent back navigation behavior.
     *
     * @param item the selected menu item
     * @return true if the event was handled, otherwise delegates to the superclass
     */
    @java.lang.Override()
    public boolean onOptionsItemSelected(@org.jetbrains.annotations.NotNull()
    android.view.MenuItem item) {
        return false;
    }
    
    /**
     * Wrap the base context with the user's locale settings.
     *
     * This ensures resources are loaded using the configured locale.
     *
     * @param newBase the original base context to wrap
     */
    @java.lang.Override()
    protected void attachBaseContext(@org.jetbrains.annotations.Nullable()
    android.content.Context newBase) {
    }
    
    /**
     * Adds a custom divider drawable to the provided RecyclerView.
     *
     * This is a convenience helper that constructs a [CustomDividerItemDecoration]
     * using the given drawable resource id and adds it to the RecyclerView.
     *
     * @param recyclerView the target RecyclerView
     * @param context the context used to resolve resources (may be activity or application context)
     * @param drawableResId the drawable resource id to use as the divider
     * @param orientation one of [DividerItemDecoration.VERTICAL] or [DividerItemDecoration.HORIZONTAL]
     *
     * @throws IllegalArgumentException if the drawable resource cannot be found
     */
    protected final void addCustomDividerToRecyclerView(@org.jetbrains.annotations.NotNull()
    androidx.recyclerview.widget.RecyclerView recyclerView, @org.jetbrains.annotations.Nullable()
    android.content.Context context, int drawableResId, int orientation) {
    }
    
    /**
     * Configure the toolbar instance using the default toolbar id if null is passed.
     *
     * This helper will set the toolbar as the action bar and configure the up button
     * visibility plus optional title.
     *
     * @param toolbar the toolbar instance to configure (may be null, in which case the view
     *               with id R.id.toolbar in the activity content will be used)
     * @param showHomeAsUp whether the home/up affordance should be shown (default true)
     * @param title optional title to set on the activity
     */
    protected final void setupToolbar(@org.jetbrains.annotations.Nullable()
    androidx.appcompat.widget.Toolbar toolbar, boolean showHomeAsUp, @org.jetbrains.annotations.Nullable()
    java.lang.CharSequence title) {
    }
    
    /**
     * Inflate the shared base layout, attach the child layout resource into the base
     * content container, cache the in-layout ProgressBar and configure the toolbar.
     *
     * Typical usage in subclasses:
     * setContentViewWithToolbar(R.layout.activity_settings, showHomeAsUp = true, title = "Settings")
     *
     * @param layoutResId child layout resource to inflate into the base content container
     * @param showHomeAsUp whether to show the up/home affordance on the toolbar (default true)
     * @param title optional activity title to set on the toolbar
     */
    protected final void setContentViewWithToolbar(int layoutResId, boolean showHomeAsUp, @org.jetbrains.annotations.Nullable()
    java.lang.CharSequence title) {
    }
    
    /**
     * Inflate the shared base layout, attach the provided child view (commonly a view-binding root)
     * into the base content container, cache the in-layout ProgressBar and configure the toolbar.
     *
     * Typical usage with view binding:
     * setContentViewWithToolbar(binding.root, showHomeAsUp = true, title = "...")
     *
     * @param childView the already-inflated child view to add to the base content container
     * @param showHomeAsUp whether to show the up/home affordance on the toolbar (default true)
     * @param title optional activity title to set on the toolbar
     */
    protected final void setContentViewWithToolbar(@org.jetbrains.annotations.NotNull()
    android.view.View childView, boolean showHomeAsUp, @org.jetbrains.annotations.Nullable()
    java.lang.CharSequence title) {
    }
    
    /**
     * Internal helper that configures the MaterialToolbar found in the inflated base root.
     *
     * @param baseRoot the root view of the inflated base layout
     * @param showHomeAsUp whether to show the up/home affordance
     * @param title optional title to set on the support action bar
     */
    private final void setupToolbar(android.view.View baseRoot, boolean showHomeAsUp, java.lang.CharSequence title) {
    }
    
    /**
     * Show the base layout's ProgressBar.
     *
     * This method is safe to call from background threads; the visibility change will
     * be posted to the UI thread via [runOnUiThread]. If the base layout was not set yet
     * (progressBar == null) the call is a no-op.
     */
    protected final void showLoading() {
    }
    
    /**
     * Hide the base layout's ProgressBar.
     *
     * Safe to call from background threads. No-op if the progress bar hasn't been cached.
     */
    protected final void hideLoading() {
    }
    
    /**
     * Returns true when the base ProgressBar is currently visible.
     *
     * @return true if the progress bar exists and its visibility is VISIBLE
     */
    protected final boolean isLoadingVisible() {
        return false;
    }
}