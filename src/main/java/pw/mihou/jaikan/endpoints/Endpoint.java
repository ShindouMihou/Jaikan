package pw.mihou.jaikan.endpoints;

public class Endpoint {

    public String endpoint;

    /**
     * Creates a new Endpoint that will be used by the generic API.
     *
     * @param endpoint The generic endpoint that will be formatted later using {@link Endpoint#format(Object...)}
     *                 An example of a generic endpoint is: {@link Endpoints#OBJECT} or {@link Endpoints#SEARCH}
     *                 which looks like: https://api.jikan.moe/v3/search/%s?q=%s.
     *                 The first %s stands for the type of item (for example, anime).
     *                 The last %s stands for the query which, for example, is "Yuru Yuri".
     *                 The endpoint would then become something like: <a href="https://api.jikan.moe/v3/search/anime?q=Yuru Yuri">https://api.jikan.moe/v3/search/anime?q=Yuru Yuri</a>
     */
    public Endpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    /**
     * Fills the placeholders with the following values.
     *
     * @param values The values to fill the placeholders (IN ORDER).
     * @return The formatted endpoint.
     */
    public String format(Object... values){
        return String.format(endpoint, values);
    }

    @Override
    public String toString() {
        return endpoint;
    }
}
