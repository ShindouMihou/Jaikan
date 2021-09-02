package pw.mihou.jaikan.configuration;

import com.github.benmanes.caffeine.cache.Cache;
import okhttp3.OkHttpClient;
import pw.mihou.jaikan.configuration.implementation.JaikanConfigurationBuilderImpl;

import java.time.Duration;

public class JaikanConfiguration {

    private final OkHttpClient client;
    private final Cache<String, String> requestCache;
    private final Duration rateDuration;
    private final String userAgent;

    public JaikanConfiguration(JaikanConfigurationBuilderImpl builder) {
        this.client = builder.getClient();
        this.requestCache = builder.getRequestCache();
        this.rateDuration = builder.getRateDuration();
        this.userAgent = builder.getUserAgent();
    }

    /**
     * Retrieves the client used for this configuration, this is used to
     * send HTTP requests towards Jikan.
     *
     * @return The configuration's OKHttp client.
     */
    public OkHttpClient getClient() {
        return client;
    }

    /**
     * The request cache to use for Jaikan, you can build this through {@link com.github.benmanes.caffeine.cache.Caffeine} which is
     * a static class containing static methods for building your own Caffeine cache.
     *
     * @return The configuration's cache.
     */
    public Cache<String, String> getRequestCache() {
        return requestCache;
    }

    /**
     * Gets the rate of requests that will be fired every X second, this is a per-second basis.
     *
     * @return The rate per second that a request should be fired.
     */
    public Duration getRateDuration() {
        return rateDuration;
    }

    /**
     * The user agent set for this configuration of Jaikan.
     *
     * @return The configuration's user agent.
     */
    public String getUserAgent() {
        return userAgent;
    }

    /**
     * Creates a new Jaikan Configuration Builder which you can use to build a new
     * Jaikan Configuration instance.
     *
     * @return A new Jaikan Configuration Builder.
     */
    public static JaikanConfigurationBuilder newBuilder() {
        return new JaikanConfigurationBuilderImpl();
    }

    /**
     * Builds a default Jaikan Configuration which is configured specifically for the
     * V3 API and shouldn't be used for the V4 API as the rate-limit is different.
     *
     * @return The default configuration for v3 Jikan API.
     */
    public static JaikanConfiguration ofDefaults() {
        return new JaikanConfigurationBuilderImpl().build();
    }
}
