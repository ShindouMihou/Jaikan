package pw.mihou.jaikan.endpoints;

import pw.mihou.jaikan.endpoints.implementations.EndpointImpl;

public class Endpoints {

    /**
     * A search {@link Endpoint}, this is used to search for mangas, animes, etc.
     * For example: https://api.jikan.moe/v3/search/anime?q=Yuru Yuri.
     */
    public static Endpoint SEARCH = createEndpoint("https://api.jikan.moe/v3/search/{}?q={}", "https://api.jikan.moe/v4/{}?q={}");

    /**
     * An object {@link Endpoint}, this is a generic endpoint for objects such as anime and mangas.
     * For example: https://api.jikan.moe/v3/anime/1/
     */
    public static Endpoint OBJECT = new EndpointImpl("https://api.jikan.moe/v3/{}/{}/", "https://api.jikan.moe/v4/{}/{}/");

    /**
     * Creates a new generic {@link Endpoint}, this uses <code>{}</code> as the placeholder.
     * For example, <code>https://api.jikan.moe/v3/search/anime?q={}</code> which is the endpoint
     * for searching anime.
     *
     * @param v3 The endpoint for v3 Jikan, if supported.
     * @return A new Endpoint object.
     */
    public static Endpoint createEndpoint(String v3) {
        return new EndpointImpl(v3, null);
    }

    /**
     * Creates a new generic {@link Endpoint}, this uses <code>{}</code> as the placeholder.
     * For example, <code>https://api.jikan.moe/v3/search/anime?q={}</code> which is the endpoint
     * for searching anime.
     *
     * @param v3 The endpoint for v3 Jikan, if supported.
     * @param v4 The endpoint for v4 Jikan, if supported.
     * @return A new Endpoint object.
     */
    public static Endpoint createEndpoint(String v3, String v4) {
        return new EndpointImpl(v3, v4);
    }

    /**
     * Creates a new generic {@link Endpoint}, this uses <code>{}</code> as the placeholder.
     * For example, <code>https://api.jikan.moe/v3/search/anime?q={}</code> which is the endpoint
     * for searching anime.
     *
     * @param endpoint The endpoint for v4 Jikan, if supported.
     * @return A new Endpoint object.
     */
    public static Endpoint createV4(String endpoint) {
        return new EndpointImpl(null, endpoint);
    }

}
