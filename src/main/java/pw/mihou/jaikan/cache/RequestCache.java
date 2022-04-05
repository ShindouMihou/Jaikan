package pw.mihou.jaikan.cache;

public abstract class RequestCache {

    /**
     * Gets the value of the key stored inside the cache, this can return null
     * which will result in {@link RequestCache} performing an evaluation to evaluate
     * a response.
     *
     * @param key The key to get.
     * @return The value of the key given otherwise null.
     */
    public abstract String get(String key);

    /**
     * Upserts the value of the key into the cache with the specified TTL (in seconds).
     *
     * @param key The key to store the value towards.
     * @param value The value to store to the cache.
     * @param timeInSeconds The time to live of the key in seconds.
     */
    public abstract void put(String key, String value, long timeInSeconds);

}
