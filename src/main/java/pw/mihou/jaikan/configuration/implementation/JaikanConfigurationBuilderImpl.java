package pw.mihou.jaikan.configuration.implementation;

import okhttp3.OkHttpClient;
import pw.mihou.jaikan.cache.RequestCache;
import pw.mihou.jaikan.configuration.JaikanConfiguration;
import pw.mihou.jaikan.configuration.JaikanConfigurationBuilder;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class JaikanConfigurationBuilderImpl implements JaikanConfigurationBuilder{

    private OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .build();

    private RequestCache requestCache;

    private Duration rateDuration = Duration.ofMillis(562);

    private String userAgent = "Jaikan (Java 1.8/https://github.com/ShindouMihou/Jaikan)";

    @Override
    public JaikanConfigurationBuilder setOkHTTPClient(OkHttpClient client) {
        this.client = client;
        return this;
    }

    @Override
    public JaikanConfigurationBuilder setRequestCache(RequestCache cache) {
        this.requestCache = cache;
        return this;
    }

    @Override
    public JaikanConfigurationBuilder setRatelimit(Duration rateDuration) {
        this.rateDuration = rateDuration;
        return this;
    }

    @Override
    public JaikanConfigurationBuilder setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    @Override
    public JaikanConfiguration build() {
        return new JaikanConfiguration(this);
    }

    // This is where the internal methods which are generally used
    // by JaikanConfiguration starts.

    public OkHttpClient getClient() {
        return client;
    }

    public RequestCache getRequestCache() {
        return requestCache;
    }

    public Duration getRateDuration() {
        return rateDuration;
    }

    public String getUserAgent() {
        return userAgent;
    }

}
