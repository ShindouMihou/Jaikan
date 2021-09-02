package pw.mihou.jaikan;

import com.google.gson.Gson;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pw.mihou.jaikan.configuration.JaikanConfiguration;
import pw.mihou.jaikan.configuration.JaikanConfigurationBuilder;
import pw.mihou.jaikan.endpoints.Endpoint;
import pw.mihou.jaikan.endpoints.implementations.EndpointImpl;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Jaikan4 {

    private static JaikanConfiguration configuration = JaikanConfiguration.ofDefaults();
    private static final Logger log = LoggerFactory.getLogger("Jaikan v4");
    private static final Gson gson = new Gson();
    private static volatile long reqTimer = 0L;

    /**
     * Formats an endpoint and requests data from the endpoint before
     * parsing it into the object that it will be cast to.
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
        return new JSONObject(genericRequest(endpoint, values))
                .getJSONArray("data")
                .toList()
                .stream()
                .map(o -> gson.fromJson(gson.toJson(o), castTo))
                .collect(Collectors.toList());
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
        if (!endpoint.supportsV4())
            throw new IllegalArgumentException("The endpoint used does not support Jikan V4, please try using Jaikan instead.");

        return configuration.getRequestCache().get(((EndpointImpl) endpoint).formatV4(values), Jaikan4::__request);
    }

    private static synchronized boolean checkRateLimit() {
        return reqTimer + configuration.getRateDuration().toMillis() < System.currentTimeMillis();
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
                Thread.sleep(configuration.getRateDuration().toMillis());
            }

            reqTimer = System.currentTimeMillis();
            try (Response response = configuration.getClient().newCall(new Request.Builder().url(s)
                    .header("User-Agent", configuration.getUserAgent()).get().build())
                    .execute()) {

                if (response.code() == 429) {
                    log.warn("Jaikan was struck with an rate-limit, attempting to retry soon... ({})", s);
                    // This is for protection reasons.
                    TimeUnit.SECONDS.sleep(5);
                    return __request(s);
                }

                if(response.body() != null) {

                    String body = Objects.requireNonNull(response.body()).string();
                    if (response.code() != 200) {
                        log.error("An error occurred while trying to fetch {} with Jaikan, status code: {}", s, response.code());
                        return "";
                    }

                    response.close();
                    return body;
                } else {
                    return "";
                }
            } catch (IOException exception) {
                log.error("An error occurred while trying to fetch from {}: {}!", s, exception.getMessage());
            }
        } catch (InterruptedException e) {
            log.error("Attempt to retry fetch of {} was received with an error: {}! Aborting request!", s, e.getMessage());
        }

        return "";
    }

    /**
     * Sets the {@link JaikanConfiguration} configuration that this Jaikan should use.
     *
     * @param configuration The new Jaikan configuration to use.
     */
    public static void setConfiguration(JaikanConfiguration configuration) {
        Jaikan4.configuration = configuration;
    }

    /**
     * Sets the {@link JaikanConfiguration} configuration that this Jaikan should use.
     *
     * @param configuration The new Jaikan configuration to use.
     */
    public static void setConfiguration(Function<JaikanConfigurationBuilder, JaikanConfiguration> configuration) {
        Jaikan4.configuration = configuration.apply(JaikanConfiguration.newBuilder());
    }

}
