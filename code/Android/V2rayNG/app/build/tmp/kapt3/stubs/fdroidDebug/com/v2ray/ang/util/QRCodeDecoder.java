package com.v2ray.ang.util;

/**
 * QR code decoder utility.
 */
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010%\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u001a\u0010\t\u001a\u0004\u0018\u00010\n2\u0006\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\r\u001a\u00020\u000eJ\u0010\u0010\u000f\u001a\u0004\u0018\u00010\f2\u0006\u0010\u0010\u001a\u00020\fJ\u0012\u0010\u000f\u001a\u0004\u0018\u00010\f2\b\u0010\u0011\u001a\u0004\u0018\u00010\nJ\u0012\u0010\u0012\u001a\u0004\u0018\u00010\n2\u0006\u0010\u0010\u001a\u00020\fH\u0002R\u001f\u0010\u0004\u001a\u0010\u0012\u0004\u0012\u00020\u0006\u0012\u0006\u0012\u0004\u0018\u00010\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\b\u00a8\u0006\u0013"}, d2 = {"Lcom/v2ray/ang/util/QRCodeDecoder;", "", "<init>", "()V", "HINTS", "", "Lcom/google/zxing/DecodeHintType;", "getHINTS", "()Ljava/util/Map;", "createQRCode", "Landroid/graphics/Bitmap;", "text", "", "size", "", "syncDecodeQRCode", "picturePath", "bitmap", "getDecodeAbleBitmap", "app_fdroidDebug"})
public final class QRCodeDecoder {
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Map<com.google.zxing.DecodeHintType, java.lang.Object> HINTS = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.v2ray.ang.util.QRCodeDecoder INSTANCE = null;
    
    private QRCodeDecoder() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Map<com.google.zxing.DecodeHintType, java.lang.Object> getHINTS() {
        return null;
    }
    
    /**
     * Creates a QR code bitmap from the given text.
     *
     * @param text The text to encode in the QR code.
     * @param size The size of the QR code bitmap.
     * @return The generated QR code bitmap, or null if an error occurs.
     */
    @org.jetbrains.annotations.Nullable()
    public final android.graphics.Bitmap createQRCode(@org.jetbrains.annotations.NotNull()
    java.lang.String text, int size) {
        return null;
    }
    
    /**
     * Decodes a QR code from a local image file. This method is time-consuming and should be called in a background thread.
     *
     * @param picturePath The local path of the image file to decode.
     * @return The content of the QR code, or null if decoding fails.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String syncDecodeQRCode(@org.jetbrains.annotations.NotNull()
    java.lang.String picturePath) {
        return null;
    }
    
    /**
     * Decodes a QR code from a bitmap. This method is time-consuming and should be called in a background thread.
     *
     * @param bitmap The bitmap to decode.
     * @return The content of the QR code, or null if decoding fails.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String syncDecodeQRCode(@org.jetbrains.annotations.Nullable()
    android.graphics.Bitmap bitmap) {
        return null;
    }
    
    /**
     * Converts a local image file to a bitmap that can be decoded as a QR code. The image is compressed to avoid being too large.
     *
     * @param picturePath The local path of the image file.
     * @return The decoded bitmap, or null if an error occurs.
     */
    private final android.graphics.Bitmap getDecodeAbleBitmap(java.lang.String picturePath) {
        return null;
    }
}