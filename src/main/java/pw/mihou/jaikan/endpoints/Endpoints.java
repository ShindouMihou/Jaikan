package pw.mihou.jaikan.endpoints;

public class Endpoints {

    /**
     * A search {@link Endpoint}, this is used to search for mangas, animes, etc.
     * For example: https://api.jikan.moe/v3/search/anime?q=Yuru Yuri.
     */
    public static Endpoint SEARCH = new Endpoint("https://api.jikan.moe/v3/search/%s?q=%s");

    /**
     * An object {@link Endpoint}, this is a generic endpoint for objects such as anime and mangas.
     * For example: https://api.jikan.moe/v3/anime/1/
     */
    public static Endpoint OBJECT = new Endpoint("https://api.jikan.moe/v3/%s/%d/");

    /**
     * Creates a new generic {@link Endpoint}, this uses String.format's placeholders.
     * The placeholders consists of the following:
     * %s - String.
     * %d - numerical value.
     * More information can be found on https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Formatter.html#syntax.
     *
     * @param generic
     * @return
     */
    public static Endpoint createEndpoint(String generic) {
        return new Endpoint(generic);
    }

}
