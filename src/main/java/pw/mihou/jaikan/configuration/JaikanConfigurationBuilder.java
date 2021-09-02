package pw.mihou.jaikan.configuration;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import okhttp3.OkHttpClient;

import java.time.Duration;
import java.util.function.Function;

public interface JaikanConfigurationBuilder {

    /**
     * Sets the OkHTTP Client that will be used for sending requests to the Jikan API, you can
     * use this to configure the connect timeout, read timeout, etc. if you are having issues with
     * socket timeouts.
     *
     * @param client The OkHTTP Client to use.
     * @return {@link JaikanConfigurationBuilder} for chain-calling methods.
     */
    JaikanConfigurationBuilder setOkHTTPClient(OkHttpClient client);

    /**
     * Sets the {@link Caffeine} cache specification that Jaikan will use to cache the requests from
     * Jikan, lowering the requests and speeding up each query. You can set the expiration times, eviction method
     * and similar here.
     *
     * @param cache The cache to use for Jaikan.
     * @return {@link JaikanConfigurationBuilder} for chain-calling methods.
     */
    JaikanConfigurationBuilder setRequestCache(Function<Caffeine<Object, Object>, Caffeine<Object, Object>> cache);

    /**
     * Sets the rate of requests that should be fired for each second. This is a per-milliseconds basis which means whatever
     * you write here will be converted to millisecond-precision, which means you can configure it to send a request every
     * 1.5 seconds or similar.
     * <br><br>
     * By default, this is already running at the optimal rate-limit value that we have benchmarked. It is not recommended
     * changing this value unless you are experiencing rate-limits even with this limit in place, be careful when changing
     * as it will definitely cause mayhem to your entire request poll.
     *
     * @param rateDuration At what rate should we send the requests (per request).
     * @return {@link JaikanConfigurationBuilder} for chain-calling methods.
     */
    JaikanConfigurationBuilder setRatelimit(Duration rateDuration);

    /**
     * Sets the user-agent to use when connecting to Jikan, it is recommended that you set this to one that helps the Jikan
     * developers identify who you are since it really helps with diagnostics.
     *
     * @param userAgent The user-agent to use when sending requests to Jikan.
     * @return {@link JaikanConfigurationBuilder} for chain-calling methods.
     */
    JaikanConfigurationBuilder setUserAgent(String userAgent);

    /**
     * Builds a new Jaikan Configuration which is then used by Jaikan.
     *
     * @return The Jaikan Configuration that can be used by Jaikan.
     */
    JaikanConfiguration build();

}
