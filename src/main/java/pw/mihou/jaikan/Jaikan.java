package pw.mihou.jaikan;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.gson.Gson;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pw.mihou.jaikan.endpoints.Endpoint;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class Jaikan {

    private static final Retry retry = Retry.of("jaikanRetry", RetryConfig.custom()
            .waitDuration(Duration.ofSeconds(2)).maxAttempts(10).build());
    private static final OkHttpClient client = new OkHttpClient.Builder().connectTimeout(Duration.ofSeconds(5))
            .build();
    private static final Duration duration = Duration.ofHours(6);
    private static final Cache<String, String> requestCache = Caffeine.newBuilder()
            .expireAfterWrite(duration).build();
    private static final Logger log = LoggerFactory.getLogger("Jaikan");
    private static final Gson gson = new Gson().newBuilder().create();
    private static String userAgent = "Jaikan (Java 1.8/https://github.com/ShindouMihou/Jaikan)";

    /**
     * Formats an endpoint and requests data from the endpoint before
     * parsing it into the object that it will be casted to.
     *
     * <h3>This is primarily used for searching as the data format is for searches (or anything
     * that has multiple objects).
     *
     * @param endpoint The endpoint to format, you can select default ones from
     *                 {@link pw.mihou.jaikan.endpoints.Endpoints} such as
     *                 {@link pw.mihou.jaikan.endpoints.Endpoints#SEARCH}
     *                 which is used to search for either a manga or something else.
     * @param castTo The model to parse the results into, for example, {@link pw.mihou.jaikan.models.AnimeResult}
     * @param values The values to append onto the endpoint (for example, we have a query foramt of
     * @param <T> The model it will be cast on, for example, {@link pw.mihou.jaikan.models.AnimeResult}
     * @return A list of all the models with all the possible values filled.
     */
    public static <T> CompletableFuture<List<T>> search(Endpoint endpoint, Class<T> castTo, Object... values) {
            return genericRequest(endpoint, values).thenApply(s -> new JSONObject(s).getJSONArray("results")
                    .toList().stream().map(o -> gson.fromJson(gson.toJson(o), castTo)).collect(Collectors.toList()));
    }

    /**
     * Formats an endpoint and requests data from the endpoint before
     * parsing it into the object that it will be casted to.
     *
     * <h3>This is used for object searches (basically where the JSON result is an entire
     * object itself and not a list of them).
     *
     * @param endpoint The endpoint to format, you can select default ones from
     *                 {@link pw.mihou.jaikan.endpoints.Endpoints} such as
     *                 {@link pw.mihou.jaikan.endpoints.Endpoints#SEARCH}
     *                 which is used to search for either a manga or something else.
     * @param castTo The model to parse the results into, for example, {@link pw.mihou.jaikan.models.Anime}
     * @param values The values to append onto the endpoint (for example, we have a query foramt of
     * @param <T> The model it will be cast on, for example, {@link pw.mihou.jaikan.models.Anime}
     * @return The model with all the possible values filled.
     */
    public static <T> CompletableFuture<T> as(Endpoint endpoint, Class<T> castTo, Object... values) {
        return genericRequest(endpoint, values).thenApply(s -> {
            if(s != null)
                return gson.fromJson(s, castTo);
            return null;
        });
    }

    /**
     * Formats an endpoint and requests data from the endpoint before
     * parsing it into the object that it will be casted to.
     *
     * <h3>This is used for generic requests, you can use this to grab results from the
     * API and parse it on your own way.
     *
     * @param endpoint The endpoint to format, you can select default ones from
     *                 {@link pw.mihou.jaikan.endpoints.Endpoints} such as
     *                 {@link pw.mihou.jaikan.endpoints.Endpoints#SEARCH}
     *                 which is used to search for either a manga or something else.
     * @param values The values to append onto the endpoint (for example, we have a query foramt of
     * @return
     */
    public static CompletableFuture<String> genericRequest(Endpoint endpoint, Object... values) {
        return CompletableFuture.supplyAsync(() -> requestCache.get(endpoint.format(values), s -> {
            try {
                Response response = client.newCall(new Request.Builder().url(s)
                        .header("User-Agent", userAgent).get().build())
                        .execute();
                if(response.code() == 429){
                    log.warn("Jaikan was struck with an rate-limit, attempting to retry in 2 seconds... ({})", s);
                    return retry.executeCallable(() -> genericRequest(endpoint, values).join());
                }

                if(response.code() != 200){
                    log.error("An error occurred while trying to fetch {} with Jaikan, status code: {}", s, response.code());
                    return null;
                }

                if(response.body() != null) {
                    return response.body().string();
                }

                return null;
            } catch (IOException exception) {
                log.error("An error occurred while trying to fetch from {}: {}!\nAttempting to retry in 2 seconds...", s, exception.getMessage());
                try {
                    return retry.executeCallable(() -> genericRequest(endpoint, values).join());
                } catch (Exception e) {
                    log.error("Attempt to retry fetch of {} was received with an error: {}! Aborting request!", s, e.getMessage());
                    e.printStackTrace();
                }
            } catch (Exception exception) {
                log.error("Attempt to retry fetch of {} was received with an error: {}! Aborting request!", s, exception.getMessage());
                exception.printStackTrace();
            }
            return null;
        }));
    }

    public static void setUserAgent(String userAgent){
        Jaikan.userAgent = userAgent;
    }

}
