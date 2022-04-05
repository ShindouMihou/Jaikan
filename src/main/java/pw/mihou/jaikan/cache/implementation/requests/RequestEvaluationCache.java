package pw.mihou.jaikan.cache.implementation.requests;

import pw.mihou.jaikan.cache.RequestCache;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class RequestEvaluationCache {

    private final RequestCache requestCache;
    private final String key;
    private Function<String, CompletableFuture<RequestEvaluationEntry>> otherwise;

    public RequestEvaluationCache(RequestCache requestCache, String key) {
        this.requestCache = requestCache;
        this.key = key;
    }

    public RequestEvaluationCache otherwise(Function<String, CompletableFuture<RequestEvaluationEntry>> otherwise) {
        this.otherwise = otherwise;
        return this;
    }

    public CompletableFuture<String> apply() {
        return CompletableFuture.supplyAsync(() -> {
            if (requestCache != null) {
                final String result = requestCache.get(key);

                if (result == null && otherwise == null) {
                    return null;
                }

                if (result != null) {
                    return result;
                }

                final RequestEvaluationEntry otherwiseResult = otherwise.apply(key).join();

                if (!otherwiseResult.value.isBlank() && otherwiseResult.expires != -2004) {
                    requestCache.put(key, otherwiseResult.value, otherwiseResult.expires);
                }

                return otherwiseResult.value;
            }

            final RequestEvaluationEntry otherwiseResult = otherwise.apply(key).join();
            return otherwiseResult.value;
        });
    }

}
