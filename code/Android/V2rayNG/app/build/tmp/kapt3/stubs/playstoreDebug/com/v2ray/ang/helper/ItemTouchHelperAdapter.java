package com.v2ray.ang.helper;

/**
 * Interface to listen for a move or dismissal event from a [ItemTouchHelper.Callback].
 *
 * @author Paul Burke (ipaulpro)
 */
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\bf\u0018\u00002\u00020\u0001J\u0018\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0005H&J\b\u0010\u0007\u001a\u00020\bH&J\u0010\u0010\t\u001a\u00020\b2\u0006\u0010\n\u001a\u00020\u0005H&\u00a8\u0006\u000b\u00c0\u0006\u0003"}, d2 = {"Lcom/v2ray/ang/helper/ItemTouchHelperAdapter;", "", "onItemMove", "", "fromPosition", "", "toPosition", "onItemMoveCompleted", "", "onItemDismiss", "position", "app_playstoreDebug"})
public abstract interface ItemTouchHelperAdapter {
    
    /**
     * Called when an item has been dragged far enough to trigger a move. This is called every time
     * an item is shifted, and **not** at the end of a "drop" event.<br></br>
     * <br></br>
     * Implementations should call [RecyclerView.Adapter.notifyItemMoved] after
     * adjusting the underlying data to reflect this move.
     *
     * @param fromPosition The start position of the moved item.
     * @param toPosition   Then resolved position of the moved item.
     * @return True if the item was moved to the new adapter position.
     * @see RecyclerView.getAdapterPositionFor
     * @see RecyclerView.ViewHolder.getAdapterPosition
     */
    public abstract boolean onItemMove(int fromPosition, int toPosition);
    
    public abstract void onItemMoveCompleted();
    
    /**
     * Called when an item has been dismissed by a swipe.<br></br>
     * <br></br>
     * Implementations should call [RecyclerView.Adapter.notifyItemRemoved] after
     * adjusting the underlying data to reflect this removal.
     *
     * @param position The position of the item dismissed.
     * @see RecyclerView.getAdapterPositionFor
     * @see RecyclerView.ViewHolder.getAdapterPosition
     */
    public abstract void onItemDismiss(int position);
}