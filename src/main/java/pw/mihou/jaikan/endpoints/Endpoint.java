package pw.mihou.jaikan.endpoints;

public interface Endpoint {

    /**
     * Returns the unformatted endpoints that Jaikan uses, this is used to grab
     * the endpoint for v3.
     *
     * @return The unformatted v3 endpoint that Jaikan is using.
     */
    String getEndpointV3();

    /**
     * Returns the unformatted endpoints that Jaikan uses, this is used to grab
     * the endpoint for v4.
     *
     * @return The unformatted v4 endpoint that Jaikan is using.
     */
    String getEndpointV4();

    /**
     * Checks if this endpoint supports the v4 Jikan.
     *
     * @return Does this endpoint have a v4 equivalent?
     */
    boolean supportsV4();

    /**
     * Checks if this endpoint supports the v3 Jikan.
     *
     * @return Does this endpoint have a v3 equivalent?
     */
    boolean supportsV3();

}
