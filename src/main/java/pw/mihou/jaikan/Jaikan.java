package pw.mihou.jaikan;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.gson.Gson;
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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Jaikan {

    private static final OkHttpClient client = new OkHttpClient.Builder().connectTimeout(Duration.ofSeconds(5))
            .build();
    private static final Duration duration = Duration.ofHours(6);
    private static final Cache<String, String> requestCache = Caffeine.newBuilder()
            .expireAfterWrite(duration).build();
    private static Duration rateDuration = Duration.ofMillis(2000);
    private static final Logger log = LoggerFactory.getLogger("Jaikan");
    private static final Gson gson = new Gson().newBuilder().serializeNulls().create();
    private static volatile long reqTimer = 0L;
    private static String userAgent = "Jaikan (Java 1.8/https://github.com/ShindouMihou/Jaikan)";

    /**
     * Formats an endpoint and requests data from the endpoint before
     * parsing it into the object that it will be casted to.
     *
     * <h3>This is primarily used for searching as the data format is for searches (or anything
     * that has multiple objects).</h3>
     *
     * @param endpoint The endpoint to format, you can select default ones from
     *                 {@link pw.mihou.jaikan.endpoints.Endpoints} such as
     *                 {@link pw.mihou.jaikan.endpoints.Endpoints#SEARCH}
     *                 which is used to search for either a manga or something else.
     * @param castTo   The model to parse the results into, for example, {@link pw.mihou.jaikan.models.AnimeResult}
     * @param values   The values to append onto the endpoint (for example, we have a query format of
     * @param <T>      The model it will be cast on, for example, {@link pw.mihou.jaikan.models.AnimeResult}
     * @return A list of all the models with all the possible values filled.
     */
    public static <T> List<T> search(Endpoint endpoint, Class<T> castTo, Object... values) {
        return new JSONObject(genericRequest(endpoint, values)).getJSONArray("results")
                .toList().stream().map(o -> gson.fromJson(gson.toJson(o), castTo)).collect(Collectors.toList());
    }

    /**
     * Formats an endpoint and requests data from the endpoint before
     * parsing it into the object that it will be casted to.
     *
     * <h3>This is used for object searches (basically where the JSON result is an entire
     * object itself and not a list of them).</h3>
     *
     * @param endpoint The endpoint to format, you can select default ones from
     *                 {@link pw.mihou.jaikan.endpoints.Endpoints} such as
     *                 {@link pw.mihou.jaikan.endpoints.Endpoints#SEARCH}
     *                 which is used to search for either a manga or something else.
     * @param castTo   The model to parse the results into, for example, {@link pw.mihou.jaikan.models.Anime}
     * @param values   The values to append onto the endpoint (for example, we have a query foramt of
     * @param <T>      The model it will be cast on, for example, {@link pw.mihou.jaikan.models.Anime}
     * @return The model with all the possible values filled.
     */
    public static <T> T as(Endpoint endpoint, Class<T> castTo, Object... values) {
        return gson.fromJson(genericRequest(endpoint, values), castTo);
    }

    /**
     * Formats an endpoint and requests data from the endpoint before
     * parsing it into the object that it will be casted to.
     *
     * <h3>This is used for generic requests, you can use this to grab results from the
     * API and parse it on your own way.</h3>
     *
     * @param endpoint The endpoint to format, you can select default ones from
     *                 {@link pw.mihou.jaikan.endpoints.Endpoints} such as
     *                 {@link pw.mihou.jaikan.endpoints.Endpoints#SEARCH}
     *                 which is used to search for either a manga or something else.
     * @param values   The values to append onto the endpoint (for example, we have a query foramt of
     * @return Returns the result from the endpoint.
     */
    public static String genericRequest(Endpoint endpoint, Object... values) {
        return requestCache.get(endpoint.format(values), Jaikan::__request);
    }

    private static synchronized boolean checkRateLimit() {
        return reqTimer + rateDuration.toMillis() < System.currentTimeMillis();
    }

    /**
     * <h3>This is an internal method, please do not attempt to mess with it unless you know what you are doing.</h3>
     * This is used to send requests to the specified location.
     *
     * @param s The complete URL to send the request.
     * @return The server response.
     */
    private static String __request(String s) {
        try {
            if(!checkRateLimit()) {
                TimeUnit.NANOSECONDS.sleep(rateDuration.getNano());
            }

            reqTimer = System.currentTimeMillis();
            try (Response response = client.newCall(new Request.Builder().url(s)
                    .header("User-Agent", userAgent).get().build())
                    .execute()) {

                String body = response.body().string();
                if (response.code() == 429) {
                    log.warn("Jaikan was struck with an rate-limit, attempting to retry soon... ({})", s);
                    // This is for protection reasons.
                    TimeUnit.SECONDS.sleep(5);
                    return __request(s);
                }

                if (response.code() != 200) {
                    log.error("An error occurred while trying to fetch {} with Jaikan, status code: {}", s, response.code());
                    return "";
                }

                response.close();
                return body;
            } catch (IOException exception) {
                log.error("An error occurred while trying to fetch from {}: {}!\nAttempting to retry soon...", s, exception.getMessage());
            }
        } catch (InterruptedException e) {
            log.error("Attempt to retry fetch of {} was received with an error: {}! Aborting request!", s, e.getMessage());
        }
        return "";
    }

    /**
     * Do you think the rate of requests is too slow, change the value here.
     * By default, we are running at 1 request every 2 seconds (the best number according to our tests of 10 requests within a second).
     * Be careful with rate-limits though as one mistake can cause mayhem to your entire request poll.
     *
     * <h3>We do not recommend changing the rate-limit value UNLESS you are experiencing rate-limits even with 2 seconds,
     * that is when you will change the value to a longer one.</h3>
     *
     * @param duration The duration when the rate-limit should function.
     */
    public static void setRatelimit(Duration duration) {
        Jaikan.rateDuration = duration;
    }

    /**
     * Sets the user-agent that the client will use to send requests to the API.
     * <h3>By default, this is Jaikan (Java 1.8/https://github.com/ShindouMihou/Jaikan) but we recommend
     * customizing this to help the API developers identify.</h3>
     *
     * @param userAgent The user-agent to use when sending requests.
     */
    public static void setUserAgent(String userAgent) {
        Jaikan.userAgent = userAgent;
    }

}
