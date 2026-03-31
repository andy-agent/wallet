package com.v2ray.ang.helper;

/**
 * Interface to notify an item ViewHolder of relevant callbacks from [ ].
 *
 * @author Paul Burke (ipaulpro)
 */
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H&J\b\u0010\u0004\u001a\u00020\u0003H&\u00a8\u0006\u0005\u00c0\u0006\u0003"}, d2 = {"Lcom/v2ray/ang/helper/ItemTouchHelperViewHolder;", "", "onItemSelected", "", "onItemClear", "app_fdroidDebug"})
public abstract interface ItemTouchHelperViewHolder {
    
    /**
     * Called when the [ItemTouchHelper] first registers an item as being moved or swiped.
     * Implementations should update the item view to indicate it's active state.
     */
    public abstract void onItemSelected();
    
    /**
     * Called when the [ItemTouchHelper] has completed the move or swipe, and the active item
     * state should be cleared.
     */
    public abstract void onItemClear();
}