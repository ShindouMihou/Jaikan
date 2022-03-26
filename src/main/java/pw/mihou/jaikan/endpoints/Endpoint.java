package pw.mihou.jaikan.endpoints;

public interface Endpoint {

    /**
     * Returns the unformatted endpoints that Jaikan uses, this is used to grab
     * the endpoint for v4.
     *
     * @return The unformatted v4 endpoint that Jaikan is using.
     */
    String endpoint();

}
