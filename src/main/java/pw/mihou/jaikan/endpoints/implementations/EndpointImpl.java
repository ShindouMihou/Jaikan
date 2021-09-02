package pw.mihou.jaikan.endpoints.implementations;

import pw.mihou.jaikan.endpoints.Endpoint;
public class EndpointImpl implements Endpoint {

    private final String endpoint3;
    private final String endpoint4;

    /**
     * Creates a new Endpoint that will be used by the generic API, this uses <code>{}</code> as placeholders
     * similar to SLF4J instead of the older version of Jaikan which used {@link String#format(String, Object...)} which looked
     * a bit harder to use.
     *
     * @param endpoint3 The generic endpoint for v3, if supported.
     * @param endpoint4 The generic endpoint for v4, if supported.
     */
    public EndpointImpl(String endpoint3, String endpoint4) {
        this.endpoint3 = endpoint3;
        this.endpoint4 = endpoint4;
    }

    public String formatV3(Object... values) {
        return format(getEndpointV3(), values);
    }

    public String formatV4(Object... values) {
        return format(getEndpointV4(), values);
    }

    private String format(String formatted, Object... values) {

        for (Object value : values) {
            formatted = formatted.replaceFirst("\\{}", value.toString());
        }

        return formatted;
    }

    @Override
    public String getEndpointV3() {
        return endpoint3;
    }

    @Override
    public String getEndpointV4() {
        return endpoint4;
    }

    @Override
    public boolean supportsV4() {
        return endpoint4 != null;
    }

    @Override
    public boolean supportsV3() {
        return endpoint3 != null;
    }
}
