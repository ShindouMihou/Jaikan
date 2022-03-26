package pw.mihou.jaikan.endpoints.implementations;

import pw.mihou.jaikan.endpoints.Endpoint;
public class EndpointImpl implements Endpoint {

    private final String endpoint;

    /**
     * Creates a new Endpoint that will be used by the generic API, this uses <code>{}</code> as placeholders
     * similar to SLF4J instead of the older version of Jaikan which used {@link String#format(String, Object...)} which looked
     * a bit harder to use.
     *
     * @param endpoint The generic endpoint, if supported.
     */
    public EndpointImpl(String endpoint) {
        this.endpoint = endpoint;
    }

    public String create(Object... values) {
        return format(endpoint, values);
    }

    private String format(String formatted, Object... values) {

        for (Object value : values) {
            formatted = formatted.replaceFirst("\\{}", value.toString());
        }

        return formatted;
    }

    @Override
    public String endpoint() {
        return endpoint;
    }
}
