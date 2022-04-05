package pw.mihou.jaikan.cache.implementation;

import pw.mihou.jaikan.cache.RequestCache;
import pw.mihou.jaikan.cache.implementation.requests.RequestEvaluationCache;

public class RequestCacheImplementation {

    private final RequestCache requestCache;

    public RequestCacheImplementation(RequestCache requestCache) {
        this.requestCache = requestCache;
    }

    public RequestEvaluationCache get(String key) {
        return new RequestEvaluationCache(requestCache, key);
    }

}
