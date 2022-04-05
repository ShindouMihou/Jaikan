package pw.mihou.jaikan.requests;

import pw.mihou.jaikan.cache.implementation.requests.RequestEvaluationEntry;

import java.util.concurrent.CompletableFuture;

/**
 * An internal class that is used by the {@link pw.mihou.jaikan.Jaikan} queue to process requests
 * in accordance to the rate-limit. This contains the future of the request and the url intended to be
 * requested.
 */
public class JaikanRequest {

    public final CompletableFuture<RequestEvaluationEntry> future;
    public final String url;

    public JaikanRequest(String url) {
        this.url = url;
        this.future = new CompletableFuture<>();
    }

}
