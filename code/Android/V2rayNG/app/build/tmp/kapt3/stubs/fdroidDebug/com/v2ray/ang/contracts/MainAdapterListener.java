package com.v2ray.ang.contracts;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\bf\u0018\u00002\u00020\u0001J \u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH&J\u0010\u0010\n\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&J(\u0010\u000b\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\f\u001a\u00020\rH&\u00a8\u0006\u000e\u00c0\u0006\u0003"}, d2 = {"Lcom/v2ray/ang/contracts/MainAdapterListener;", "Lcom/v2ray/ang/contracts/BaseAdapterListener;", "onEdit", "", "guid", "", "position", "", "profile", "Lcom/v2ray/ang/dto/ProfileItem;", "onSelectServer", "onShare", "more", "", "app_fdroidDebug"})
public abstract interface MainAdapterListener extends com.v2ray.ang.contracts.BaseAdapterListener {
    
    public abstract void onEdit(@org.jetbrains.annotations.NotNull()
    java.lang.String guid, int position, @org.jetbrains.annotations.NotNull()
    com.v2ray.ang.dto.ProfileItem profile);
    
    public abstract void onSelectServer(@org.jetbrains.annotations.NotNull()
    java.lang.String guid);
    
    public abstract void onShare(@org.jetbrains.annotations.NotNull()
    java.lang.String guid, @org.jetbrains.annotations.NotNull()
    com.v2ray.ang.dto.ProfileItem profile, int position, boolean more);
}