package com.v2ray.ang.viewmodel;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001:\u0001\u001fB\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00060\bJ\u0010\u0010\u000f\u001a\u0004\u0018\u00010\u00062\u0006\u0010\u0010\u001a\u00020\u000bJ\u000e\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\tJ&\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00060\b2\u000e\u0010\u0015\u001a\n\u0012\u0004\u0012\u00020\u0006\u0018\u00010\b2\u0006\u0010\u0013\u001a\u00020\tH\u0002J\u0016\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u000bJ \u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u000bH\u0002R\u0014\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0011\u0010\n\u001a\u00020\u000b8F\u00a2\u0006\u0006\u001a\u0004\b\f\u0010\r\u00a8\u0006 "}, d2 = {"Lcom/v2ray/ang/viewmodel/UserAssetViewModel;", "Landroidx/lifecycle/ViewModel;", "<init>", "()V", "assets", "", "Lcom/v2ray/ang/dto/AssetUrlCache;", "builtInGeoFiles", "", "", "itemCount", "", "getItemCount", "()I", "getAssets", "getAsset", "position", "reload", "", "geoFilesSource", "buildAssetList", "decodedAssets", "downloadGeoFiles", "Lcom/v2ray/ang/viewmodel/UserAssetViewModel$GeoDownloadResult;", "extDir", "Ljava/io/File;", "httpPort", "tryDownload", "", "item", "Lcom/v2ray/ang/dto/AssetUrlItem;", "GeoDownloadResult", "app_playstoreDebug"})
public final class UserAssetViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.v2ray.ang.dto.AssetUrlCache> assets = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<java.lang.String> builtInGeoFiles = null;
    
    public UserAssetViewModel() {
        super();
    }
    
    public final int getItemCount() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.v2ray.ang.dto.AssetUrlCache> getAssets() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.v2ray.ang.dto.AssetUrlCache getAsset(int position) {
        return null;
    }
    
    public final void reload(@org.jetbrains.annotations.NotNull()
    java.lang.String geoFilesSource) {
    }
    
    private final java.util.List<com.v2ray.ang.dto.AssetUrlCache> buildAssetList(java.util.List<com.v2ray.ang.dto.AssetUrlCache> decodedAssets, java.lang.String geoFilesSource) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.v2ray.ang.viewmodel.UserAssetViewModel.GeoDownloadResult downloadGeoFiles(@org.jetbrains.annotations.NotNull()
    java.io.File extDir, int httpPort) {
        return null;
    }
    
    private final boolean tryDownload(com.v2ray.ang.dto.AssetUrlItem item, java.io.File extDir, int httpPort) {
        return false;
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\b\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006\u00a2\u0006\u0004\b\b\u0010\tJ\t\u0010\u000f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0010\u001a\u00020\u0003H\u00c6\u0003J\u000f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006H\u00c6\u0003J-\u0010\u0012\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\u000e\b\u0002\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006H\u00c6\u0001J\u0013\u0010\u0013\u001a\u00020\u00142\b\u0010\u0015\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0016\u001a\u00020\u0003H\u00d6\u0001J\t\u0010\u0017\u001a\u00020\u0007H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u000bR\u0017\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000e\u00a8\u0006\u0018"}, d2 = {"Lcom/v2ray/ang/viewmodel/UserAssetViewModel$GeoDownloadResult;", "", "successCount", "", "failureCount", "failedAssets", "", "", "<init>", "(IILjava/util/List;)V", "getSuccessCount", "()I", "getFailureCount", "getFailedAssets", "()Ljava/util/List;", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "toString", "app_playstoreDebug"})
    public static final class GeoDownloadResult {
        private final int successCount = 0;
        private final int failureCount = 0;
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<java.lang.String> failedAssets = null;
        
        public GeoDownloadResult(int successCount, int failureCount, @org.jetbrains.annotations.NotNull()
        java.util.List<java.lang.String> failedAssets) {
            super();
        }
        
        public final int getSuccessCount() {
            return 0;
        }
        
        public final int getFailureCount() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<java.lang.String> getFailedAssets() {
            return null;
        }
        
        public final int component1() {
            return 0;
        }
        
        public final int component2() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<java.lang.String> component3() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.v2ray.ang.viewmodel.UserAssetViewModel.GeoDownloadResult copy(int successCount, int failureCount, @org.jetbrains.annotations.NotNull()
        java.util.List<java.lang.String> failedAssets) {
            return null;
        }
        
        @java.lang.Override()
        public boolean equals(@org.jetbrains.annotations.Nullable()
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override()
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public java.lang.String toString() {
            return null;
        }
    }
}