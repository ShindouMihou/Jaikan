package pw.mihou.jaikan;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pw.mihou.jaikan.cache.implementation.requests.RequestEvaluationEntry;
import pw.mihou.jaikan.components.PaginatedResponse;
import pw.mihou.jaikan.components.gson.GsonIgnore;
import pw.mihou.jaikan.configuration.JaikanConfiguration;
import pw.mihou.jaikan.configuration.JaikanConfigurationBuilder;
import pw.mihou.jaikan.endpoints.Endpoint;
import pw.mihou.jaikan.endpoints.implementations.EndpointImpl;
import pw.mihou.jaikan.models.Anime;
import pw.mihou.jaikan.requests.JaikanRequest;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Jaikan {

    private static final AtomicReference<JaikanConfiguration> CONFIGURATION = new AtomicReference<>(JaikanConfiguration.ofDefaults());
    private static final Logger LOG = LoggerFactory.getLogger("Jaikan v4");

    private static final Gson GSON = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
        @Override
        public boolean shouldSkipField(FieldAttributes fieldAttributes) {
            return fieldAttributes.getAnnotation(GsonIgnore.class) != null;
        }

        @Override
        public boolean shouldSkipClass(Class<?> aClass) {
            return aClass.getAnnotation(GsonIgnore.class) != null;
        }
    }).create();

    private static final BlockingQueue<JaikanRequest> QUEUE = new LinkedBlockingQueue<>();
    private static final AtomicBoolean PROCESSING = new AtomicBoolean(false);
    private static final AtomicLong REQ_TIMER = new AtomicLong(0L);
    private static final AtomicLong DISTANCE_ADJUSTMENT = new AtomicLong(0L);

    /**
     * Asynchronously requests the specified endpoint and returns the paginated response.
     *
     * @param endpoint  The endpoint to request the data.
     * @param castTo    The expected type of the data.
     * @param values    The values to append into the placeholders.
     * @param <T>       The type to transform the results into.
     * @return The {@link PaginatedResponse} wrapped in a {@link CompletableFuture} to indicate progress and completion.
     */
    public static <T> CompletableFuture<PaginatedResponse<T>> paginated(Endpoint endpoint, Class<T> castTo, Object... values) {
        return genericRequest(endpoint, values).thenApply(body -> {
            PaginatedResponse<T> response = GSON.fromJson(body, new TypeToken<PaginatedResponse<T>>(){}.getType());

            // The response data is managed by Jaikan directly since GSON has issues with managing lists like these.
            response.data = fromDataToList(new JSONObject(body), castTo);

            return response;
        });
    }

    /**
     * Asynchronously requests the specified endpoint and returns the data from the response, this can be used
     * for when you want to skip the pagination section of {@link Jaikan#paginated(Endpoint, Class, Object...)}.
     *
     * @param endpoint  The endpoint to request the data.
     * @param castTo    The expected type of the data.
     * @param values    The values to append into the placeholders.
     * @param <T>       The type to transform the results into.
     * @return The {@link List} wrapped in a {@link CompletableFuture} to indicate progress and completion.
     */
    public static <T> CompletableFuture<List<T>> list(Endpoint endpoint, Class<T> castTo, Object... values) {
        return genericRequest(endpoint, values).thenApply(body -> fromDataToList(new JSONObject(body), castTo));
    }

    /**
     * Collects the array of objects from the data array and converts them into a List of the given type
     * with the help of GSON to perform the conversion.
     *
     * @param body      The body to fetch the array from.
     * @param castTo    The type to cast the objects into.
     * @param <T>       The type to cast the objects into.
     * @return  A list of the objects given in the array but in the form of the type specified.
     */
    private static <T> List<T> fromDataToList(JSONObject body, Class<T> castTo) {
        return StreamSupport.stream(body.getJSONArray("data").spliterator(), false)
                .map(object -> GSON.fromJson(((JSONObject) object).toString(), castTo)).collect(Collectors.toList());
    }

    /**
     * Asynchronously requests the specified endpoint and returns the data from the response, this is used for
     * single object routes such as {@code https://api.jikan.moe/v4/anime/33106}.
     *
     * @param endpoint  The endpoint to request the data.
     * @param castTo    The expected type of the data.
     * @param values    The values to append into the placeholders.
     * @param <T>       The type to transform the results into.
     * @return  The {@link T} wrapped in a {@link CompletableFuture} to indicate progress adn completion.
     */
    public static <T> CompletableFuture<T> object(Endpoint endpoint, Class<T> castTo, Object... values) {
        return genericRequest(endpoint, values)
                .thenApply(body -> new JSONObject(body).getJSONObject("data"))
                .thenApply(data -> GSON.fromJson(data.toString(), castTo));
    }

    /**
     * <b>This is deprecated for the asynchronous method {@link Jaikan#paginated(Endpoint, Class, Object...)} or {@link Jaikan#list(Endpoint, Class, Object...)}</b>
     * <br>
     * <br>
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
     * @param castTo   The model to parse the results into, for example, {@link Anime}
     * @param values   The values to append onto the endpoint (for example, we have a query format of
     * @param <T>      The model it will be cast on, for example, {@link Anime}
     * @return A list of all the models with all the possible values filled.
     */
    @Deprecated(forRemoval = true)
    public static <T> List<T> search(Endpoint endpoint, Class<T> castTo, Object... values) {
        return list(endpoint, castTo, values).join();
    }

    /**
     * <b>This is deprecated for the asynchronous method {@link Jaikan#object(Endpoint, Class, Object...)}</b>
     * <br>
     * <br>
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
    @Deprecated(forRemoval = true)
    public static <T> T as(Endpoint endpoint, Class<T> castTo, Object... values) {
        return GSON.fromJson(new JSONObject(genericRequest(endpoint, values).join()).getJSONObject("data").toString(), castTo);
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
    public static CompletableFuture<String> genericRequest(Endpoint endpoint, Object... values) {
        return CONFIGURATION.get().getRequestCache().get(((EndpointImpl) endpoint).create(values)).otherwise(key -> {
            JaikanRequest request = new JaikanRequest(key);
            queue(request);

            return request.future;
        }).apply();
    }

    private static synchronized boolean checkRateLimit() {
        return (REQ_TIMER.get() + CONFIGURATION.get().getRateDuration().toMillis() + DISTANCE_ADJUSTMENT.get()) <= System.currentTimeMillis();
    }

    private static void queue(JaikanRequest request) {
        QUEUE.add(request);

        if (QUEUE.isEmpty() && PROCESSING.get()) {
            return;
        }

        CompletableFuture.runAsync(() -> {
            PROCESSING.set(true);

            while (!QUEUE.isEmpty()) {
                JaikanRequest queuedRequest = QUEUE.poll();

                if (queuedRequest != null) {
                    __request(queuedRequest.url, queuedRequest.future);
                }
            }

            PROCESSING.set(false);
        });
    }

    /**
     * <h3>This is an internal method, please do not attempt to mess with it unless you know what you are doing.</h3>
     * This is used to send requests to the specified location.
     *
     * @param url The complete URL to send the request.
     * @param future The future to complete if the request was evaluated successfully or unsuccessfully.
     */
    private static void __request(String url, CompletableFuture<RequestEvaluationEntry> future) {
        try {
            if(!checkRateLimit()) {
                Thread.sleep(CONFIGURATION.get().getRateDuration().toMillis());
            }

            try (Response response = CONFIGURATION.get().getClient().newCall(new Request.Builder().url(url)
                    .header("User-Agent", CONFIGURATION.get().getUserAgent()).get().build())
                    .execute()) {

                if (response.code() == 429) {
                    LOG.warn("Jaikan was struck with an rate-limit, attempting to retry soon... ({})", url);
                    TimeUnit.MILLISECONDS.sleep(1500L);
                    __request(url, future);
                    return;
                }

                REQ_TIMER.set(System.currentTimeMillis());
                if(response.body() != null) {
                    String body = Objects.requireNonNull(response.body()).string();
                    if (response.code() != 200) {
                        future.completeExceptionally(
                                new IOException("An error occurred while trying to fetch " + url + " with Jaikan, status code: " + response.code())
                        );
                        return;
                    }

                    long ttl = TimeUnit.DAYS.toSeconds(1); // Default cache standard time according to https://docs.api.jikan.moe/#section/Information/Caching
                    String expires = response.header("Expires");
                    String date = response.header("Date");

                    if (expires != null && date != null) {
                        try {
                            ttl = ZonedDateTime.parse(date, DateTimeFormatter.RFC_1123_DATE_TIME).until(
                                    ZonedDateTime.parse(expires, DateTimeFormatter.RFC_1123_DATE_TIME),
                                    ChronoUnit.SECONDS
                            );
                        } catch (DateTimeParseException ignored) {
                            // We'll ignore and use the standard time header, this is usually because the Expires header is at "-1"
                            ttl = TimeUnit.DAYS.toSeconds(1);
                        }

                        // This ensures that, for some reason, the TTL is below 0 and not -2004 then we default to a day.
                        if (ttl < 0 && ttl != -2004) {
                            ttl = TimeUnit.DAYS.toSeconds(1);
                        }
                    }

                    response.close();
                    future.complete(new RequestEvaluationEntry(body, ttl));
                } else {
                    future.completeExceptionally(
                            new IOException("An error occurred while trying to fetch " + url + " with Jaikan: the body is empty, status code: " + response.code())
                    );
                }
            } catch (IOException exception) {
                future.completeExceptionally(
                        new IOException("An error occurred while trying to fetch " + url + " with Jaikan:  " + exception.getMessage())
                );
            }
        } catch (InterruptedException exception) {
            future.completeExceptionally(
                    new IOException("Retry attempt was captured with an exception for " + url + " by Jaikan with error:  " + exception.getMessage())
            );
        }

        // Adjust for the connection times since this is important to not get rate-limited.
        long distanceEnd = System.currentTimeMillis() - REQ_TIMER.get();
        DISTANCE_ADJUSTMENT.set(REQ_TIMER.get() + distanceEnd);
    }

    /**
     * Sets the {@link JaikanConfiguration} configuration that this Jaikan should use.
     *
     * @param configuration The new Jaikan configuration to use.
     */
    public static void setConfiguration(JaikanConfiguration configuration) {
        Jaikan.CONFIGURATION.set(configuration);
    }

    /**
     * Sets the {@link JaikanConfiguration} configuration that this Jaikan should use.
     *
     * @param configuration The new Jaikan configuration to use.
     */
    public static void setConfiguration(Function<JaikanConfigurationBuilder, JaikanConfiguration> configuration) {
        Jaikan.CONFIGURATION.set(configuration.apply(JaikanConfiguration.newBuilder()));
    }

}
