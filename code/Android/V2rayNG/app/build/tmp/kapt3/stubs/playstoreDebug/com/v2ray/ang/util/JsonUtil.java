package com.v2ray.ang.util;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0006\u001a\u00020\u00072\b\u0010\b\u001a\u0004\u0018\u00010\u0001J)\u0010\t\u001a\u0004\u0018\u0001H\n\"\u0004\b\u0000\u0010\n2\u0006\u0010\b\u001a\u00020\u00072\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u0002H\n0\f\u00a2\u0006\u0002\u0010\rJ\u0012\u0010\u000e\u001a\u0004\u0018\u00010\u00072\b\u0010\b\u001a\u0004\u0018\u00010\u0001J\u0012\u0010\u000f\u001a\u0004\u0018\u00010\u00102\b\u0010\b\u001a\u0004\u0018\u00010\u0007R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0011"}, d2 = {"Lcom/v2ray/ang/util/JsonUtil;", "", "<init>", "()V", "gson", "Lcom/google/gson/Gson;", "toJson", "", "src", "fromJson", "T", "cls", "Ljava/lang/Class;", "(Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/Object;", "toJsonPretty", "parseString", "Lcom/google/gson/JsonObject;", "app_playstoreDebug"})
public final class JsonUtil {
    @org.jetbrains.annotations.NotNull()
    private static com.google.gson.Gson gson;
    @org.jetbrains.annotations.NotNull()
    public static final com.v2ray.ang.util.JsonUtil INSTANCE = null;
    
    private JsonUtil() {
        super();
    }
    
    /**
     * Converts an object to its JSON representation.
     *
     * @param src The object to convert.
     * @return The JSON representation of the object.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String toJson(@org.jetbrains.annotations.Nullable()
    java.lang.Object src) {
        return null;
    }
    
    /**
     * Parses a JSON string into an object of the specified class.
     *
     * @param src The JSON string to parse.
     * @param cls The class of the object to parse into.
     * @return The parsed object.
     */
    @org.jetbrains.annotations.Nullable()
    public final <T extends java.lang.Object>T fromJson(@org.jetbrains.annotations.NotNull()
    java.lang.String src, @org.jetbrains.annotations.NotNull()
    java.lang.Class<T> cls) {
        return null;
    }
    
    /**
     * Converts an object to its pretty-printed JSON representation.
     *
     * @param src The object to convert.
     * @return The pretty-printed JSON representation of the object, or null if the object is null.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String toJsonPretty(@org.jetbrains.annotations.Nullable()
    java.lang.Object src) {
        return null;
    }
    
    /**
     * Parses a JSON string into a JsonObject.
     *
     * @param src The JSON string to parse.
     * @return The parsed JsonObject, or null if parsing fails.
     */
    @org.jetbrains.annotations.Nullable()
    public final com.google.gson.JsonObject parseString(@org.jetbrains.annotations.Nullable()
    java.lang.String src) {
        return null;
    }
}