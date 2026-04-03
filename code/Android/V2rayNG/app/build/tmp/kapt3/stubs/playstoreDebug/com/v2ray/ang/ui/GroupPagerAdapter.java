package com.v2ray.ang.ui;

/**
 * Pager adapter for subscription groups.
 */
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u00a2\u0006\u0004\b\u0007\u0010\bJ\b\u0010\r\u001a\u00020\u000eH\u0016J\u0010\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u000eH\u0016J\u0010\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0011\u001a\u00020\u000eH\u0016J\u0010\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u0013H\u0016J\u0016\u0010\u0017\u001a\u00020\u00182\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005H\u0007R \u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\t\u0010\n\"\u0004\b\u000b\u0010\f\u00a8\u0006\u0019"}, d2 = {"Lcom/v2ray/ang/ui/GroupPagerAdapter;", "Landroidx/viewpager2/adapter/FragmentStateAdapter;", "activity", "Landroidx/fragment/app/FragmentActivity;", "groups", "", "Lcom/v2ray/ang/dto/GroupMapItem;", "<init>", "(Landroidx/fragment/app/FragmentActivity;Ljava/util/List;)V", "getGroups", "()Ljava/util/List;", "setGroups", "(Ljava/util/List;)V", "getItemCount", "", "createFragment", "Lcom/v2ray/ang/ui/GroupServerFragment;", "position", "getItemId", "", "containsItem", "", "itemId", "update", "", "app_playstoreDebug"})
public final class GroupPagerAdapter extends androidx.viewpager2.adapter.FragmentStateAdapter {
    @org.jetbrains.annotations.NotNull()
    private java.util.List<com.v2ray.ang.dto.GroupMapItem> groups;
    
    public GroupPagerAdapter(@org.jetbrains.annotations.NotNull()
    androidx.fragment.app.FragmentActivity activity, @org.jetbrains.annotations.NotNull()
    java.util.List<com.v2ray.ang.dto.GroupMapItem> groups) {
        super(null);
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.v2ray.ang.dto.GroupMapItem> getGroups() {
        return null;
    }
    
    public final void setGroups(@org.jetbrains.annotations.NotNull()
    java.util.List<com.v2ray.ang.dto.GroupMapItem> p0) {
    }
    
    @java.lang.Override()
    public int getItemCount() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public com.v2ray.ang.ui.GroupServerFragment createFragment(int position) {
        return null;
    }
    
    @java.lang.Override()
    public long getItemId(int position) {
        return 0L;
    }
    
    @java.lang.Override()
    public boolean containsItem(long itemId) {
        return false;
    }
    
    @android.annotation.SuppressLint(value = {"NotifyDataSetChanged"})
    public final void update(@org.jetbrains.annotations.NotNull()
    java.util.List<com.v2ray.ang.dto.GroupMapItem> groups) {
    }
}