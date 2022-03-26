package pw.mihou.jaikan.configuration.implementation;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import okhttp3.OkHttpClient;
import pw.mihou.jaikan.configuration.JaikanConfiguration;
import pw.mihou.jaikan.configuration.JaikanConfigurationBuilder;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class JaikanConfigurationBuilderImpl implements JaikanConfigurationBuilder{

    private OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .build();

    private Cache<String, String> requestCache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofHours(6))
            .build();

    private Duration rateDuration = Duration.ofMillis(2000);

    private String userAgent = "Jaikan (Java 1.8/https://github.com/ShindouMihou/Jaikan)";

    @Override
    public JaikanConfigurationBuilder setOkHTTPClient(OkHttpClient client) {
        this.client = client;
        return this;
    }

    @Override
    public JaikanConfigurationBuilder setRequestCache(Function<Caffeine<Object, Object>, Caffeine<Object, Object>> cache) {
        this.requestCache = cache.apply(Caffeine.newBuilder()).build();
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

    public Cache<String, String> getRequestCache() {
        return requestCache;
    }

    public Duration getRateDuration() {
        return rateDuration;
    }

    public String getUserAgent() {
        return userAgent;
    }

}
