package com.v2ray.ang.helper;

/**
 * An implementation of [ItemTouchHelper.Callback] that enables basic drag & drop and
 * swipe-to-dismiss. Drag events are automatically started by an item long-press.<br></br>
 *
 * Expects the `RecyclerView.Adapter` to listen for [ ] callbacks and the `RecyclerView.ViewHolder` to implement
 * [ItemTouchHelperViewHolder].
 *
 * @author Paul Burke (ipaulpro)
 */
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u000b\u0018\u0000 &2\u00020\u0001:\u0001&B\u0019\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0004\b\u0006\u0010\u0007J\b\u0010\n\u001a\u00020\u0005H\u0016J\b\u0010\u000b\u001a\u00020\u0005H\u0016J\u0018\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011H\u0016J \u0010\u0012\u001a\u00020\u00052\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0013\u001a\u00020\u00112\u0006\u0010\u0014\u001a\u00020\u0011H\u0016J\u0018\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0017\u001a\u00020\rH\u0016J@\u0010\u0018\u001a\u00020\u00162\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001c2\u0006\u0010\u001e\u001a\u00020\r2\u0006\u0010\u001f\u001a\u00020\u0005H\u0016J\u0010\u0010 \u001a\u00020\u00162\u0006\u0010\u0010\u001a\u00020\u0011H\u0002J\u001a\u0010!\u001a\u00020\u00162\b\u0010\u0010\u001a\u0004\u0018\u00010\u00112\u0006\u0010\u001e\u001a\u00020\rH\u0016J\u0018\u0010\"\u001a\u00020\u00162\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011H\u0016J\u0010\u0010#\u001a\u00020\u001c2\u0006\u0010\u0010\u001a\u00020\u0011H\u0016J\u0010\u0010$\u001a\u00020\u001c2\u0006\u0010%\u001a\u00020\u001cH\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\'"}, d2 = {"Lcom/v2ray/ang/helper/SimpleItemTouchHelperCallback;", "Landroidx/recyclerview/widget/ItemTouchHelper$Callback;", "mAdapter", "Lcom/v2ray/ang/helper/ItemTouchHelperAdapter;", "allowSwipe", "", "<init>", "(Lcom/v2ray/ang/helper/ItemTouchHelperAdapter;Z)V", "mReturnAnimator", "Landroid/animation/ValueAnimator;", "isLongPressDragEnabled", "isItemViewSwipeEnabled", "getMovementFlags", "", "recyclerView", "Landroidx/recyclerview/widget/RecyclerView;", "viewHolder", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "onMove", "source", "target", "onSwiped", "", "direction", "onChildDraw", "c", "Landroid/graphics/Canvas;", "dX", "", "dY", "actionState", "isCurrentlyActive", "returnViewToOriginalPosition", "onSelectedChanged", "clearView", "getSwipeThreshold", "getSwipeEscapeVelocity", "defaultValue", "Companion", "app_playstoreDebug"})
public final class SimpleItemTouchHelperCallback extends androidx.recyclerview.widget.ItemTouchHelper.Callback {
    @org.jetbrains.annotations.NotNull()
    private final com.v2ray.ang.helper.ItemTouchHelperAdapter mAdapter = null;
    private final boolean allowSwipe = false;
    @org.jetbrains.annotations.Nullable()
    private android.animation.ValueAnimator mReturnAnimator;
    private static final float ALPHA_FULL = 1.0F;
    private static final float SWIPE_THRESHOLD = 0.25F;
    private static final long ANIMATION_DURATION = 200L;
    @org.jetbrains.annotations.NotNull()
    public static final com.v2ray.ang.helper.SimpleItemTouchHelperCallback.Companion Companion = null;
    
    public SimpleItemTouchHelperCallback(@org.jetbrains.annotations.NotNull()
    com.v2ray.ang.helper.ItemTouchHelperAdapter mAdapter, boolean allowSwipe) {
        super();
    }
    
    @java.lang.Override()
    public boolean isLongPressDragEnabled() {
        return false;
    }
    
    @java.lang.Override()
    public boolean isItemViewSwipeEnabled() {
        return false;
    }
    
    @java.lang.Override()
    public int getMovementFlags(@org.jetbrains.annotations.NotNull()
    androidx.recyclerview.widget.RecyclerView recyclerView, @org.jetbrains.annotations.NotNull()
    androidx.recyclerview.widget.RecyclerView.ViewHolder viewHolder) {
        return 0;
    }
    
    @java.lang.Override()
    public boolean onMove(@org.jetbrains.annotations.NotNull()
    androidx.recyclerview.widget.RecyclerView recyclerView, @org.jetbrains.annotations.NotNull()
    androidx.recyclerview.widget.RecyclerView.ViewHolder source, @org.jetbrains.annotations.NotNull()
    androidx.recyclerview.widget.RecyclerView.ViewHolder target) {
        return false;
    }
    
    @java.lang.Override()
    public void onSwiped(@org.jetbrains.annotations.NotNull()
    androidx.recyclerview.widget.RecyclerView.ViewHolder viewHolder, int direction) {
    }
    
    @java.lang.Override()
    public void onChildDraw(@org.jetbrains.annotations.NotNull()
    android.graphics.Canvas c, @org.jetbrains.annotations.NotNull()
    androidx.recyclerview.widget.RecyclerView recyclerView, @org.jetbrains.annotations.NotNull()
    androidx.recyclerview.widget.RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
    }
    
    private final void returnViewToOriginalPosition(androidx.recyclerview.widget.RecyclerView.ViewHolder viewHolder) {
    }
    
    @java.lang.Override()
    public void onSelectedChanged(@org.jetbrains.annotations.Nullable()
    androidx.recyclerview.widget.RecyclerView.ViewHolder viewHolder, int actionState) {
    }
    
    @java.lang.Override()
    public void clearView(@org.jetbrains.annotations.NotNull()
    androidx.recyclerview.widget.RecyclerView recyclerView, @org.jetbrains.annotations.NotNull()
    androidx.recyclerview.widget.RecyclerView.ViewHolder viewHolder) {
    }
    
    @java.lang.Override()
    public float getSwipeThreshold(@org.jetbrains.annotations.NotNull()
    androidx.recyclerview.widget.RecyclerView.ViewHolder viewHolder) {
        return 0.0F;
    }
    
    @java.lang.Override()
    public float getSwipeEscapeVelocity(float defaultValue) {
        return 0.0F;
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\t"}, d2 = {"Lcom/v2ray/ang/helper/SimpleItemTouchHelperCallback$Companion;", "", "<init>", "()V", "ALPHA_FULL", "", "SWIPE_THRESHOLD", "ANIMATION_DURATION", "", "app_playstoreDebug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}