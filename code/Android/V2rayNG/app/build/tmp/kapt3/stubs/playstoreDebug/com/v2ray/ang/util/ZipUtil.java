package com.v2ray.ang.util;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0016\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\tJ\u0016\u0010\u000b\u001a\u00020\u00072\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\tJ\u0018\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\tH\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0014"}, d2 = {"Lcom/v2ray/ang/util/ZipUtil;", "", "<init>", "()V", "BUFFER_SIZE", "", "zipFromFolder", "", "folderPath", "", "outputZipFilePath", "unzipToFolder", "zipFile", "Ljava/io/File;", "destDirectory", "extractFile", "", "inputStream", "Ljava/io/InputStream;", "destFilePath", "app_playstoreDebug"})
public final class ZipUtil {
    private static final int BUFFER_SIZE = 4096;
    @org.jetbrains.annotations.NotNull()
    public static final com.v2ray.ang.util.ZipUtil INSTANCE = null;
    
    private ZipUtil() {
        super();
    }
    
    /**
     * Zip the contents of a folder.
     *
     * @param folderPath The path to the folder to zip.
     * @param outputZipFilePath The path to the output zip file.
     * @return True if the operation is successful, false otherwise.
     * @throws IOException If an I/O error occurs.
     */
    @kotlin.jvm.Throws(exceptionClasses = {java.io.IOException.class})
    public final boolean zipFromFolder(@org.jetbrains.annotations.NotNull()
    java.lang.String folderPath, @org.jetbrains.annotations.NotNull()
    java.lang.String outputZipFilePath) throws java.io.IOException {
        return false;
    }
    
    /**
     * Unzip the contents of a zip file to a folder.
     *
     * @param zipFile The zip file to unzip.
     * @param destDirectory The destination directory.
     * @return True if the operation is successful, false otherwise.
     * @throws IOException If an I/O error occurs.
     */
    @kotlin.jvm.Throws(exceptionClasses = {java.io.IOException.class})
    public final boolean unzipToFolder(@org.jetbrains.annotations.NotNull()
    java.io.File zipFile, @org.jetbrains.annotations.NotNull()
    java.lang.String destDirectory) throws java.io.IOException {
        return false;
    }
    
    /**
     * Extract a file from an input stream.
     *
     * @param inputStream The input stream to read from.
     * @param destFilePath The destination file path.
     * @throws IOException If an I/O error occurs.
     */
    @kotlin.jvm.Throws(exceptionClasses = {java.io.IOException.class})
    private final void extractFile(java.io.InputStream inputStream, java.lang.String destFilePath) throws java.io.IOException {
    }
}