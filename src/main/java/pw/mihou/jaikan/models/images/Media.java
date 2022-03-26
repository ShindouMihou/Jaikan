package pw.mihou.jaikan.models.images;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class Media {

    @SerializedName("image_url") public String imageUrl;
    @SerializedName("small_image_url") public String smallImageUrl;
    @SerializedName("large_image_url") public String largeImageUrl;
    @SerializedName("maximum_image_url") public String maximumImageUrl;

}
