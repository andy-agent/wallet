package com.v2ray.ang.contracts;

/**
 * A common Adapter -> host callback interface that includes common actions: edit, remove and refresh.
 * Extend this interface or define more specific interfaces for different adapters as needed.
 */
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\bf\u0018\u00002\u00020\u0001J\u0018\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H&J\u0018\u0010\b\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H&J\u0010\u0010\t\u001a\u00020\u00032\u0006\u0010\n\u001a\u00020\u0005H&J\b\u0010\u000b\u001a\u00020\u0003H&\u00a8\u0006\f\u00c0\u0006\u0003"}, d2 = {"Lcom/v2ray/ang/contracts/BaseAdapterListener;", "", "onEdit", "", "guid", "", "position", "", "onRemove", "onShare", "url", "onRefreshData", "app_playstoreDebug"})
public abstract interface BaseAdapterListener {
    
    /**
     * Request the host to edit the specified item.
     * @param guid Unique identifier (GUID) of the item
     * @param position Current position in the adapter (optional; host should validate it)
     */
    public abstract void onEdit(@org.jetbrains.annotations.NotNull()
    java.lang.String guid, int position);
    
    /**
     * Request the host to remove the specified item. Position is provided for optional animation or validation.
     * @param guid Unique identifier (GUID) of the item
     * @param position Current position in the adapter (optional; host should validate it)
     */
    public abstract void onRemove(@org.jetbrains.annotations.NotNull()
    java.lang.String guid, int position);
    
    /**
     * Request the host to share the specified URL.
     * @param url The URL to be shared
     */
    public abstract void onShare(@org.jetbrains.annotations.NotNull()
    java.lang.String url);
    
    /**
     * Request the host to refresh data (for example, reload from the ViewModel or call notifyDataSetChanged).
     */
    public abstract void onRefreshData();
}