package pw.mihou.jaikan.models.images;

public class MediaTypes {

    public Media jpg;
    public Media webp;

    /**
     * Gets the first {@link Media} of the list from Jikan and returns the default image
     * size link.
     *
     * @return  The first {@link Media} found and the default image size returned from Jikan.
     */
    public String firstDefault() {
        if (jpg != null) {
            return jpg.imageUrl;
        }

        return webp.imageUrl;
    }

}
