package pw.mihou.jaikan.models.components;

import com.google.gson.annotations.SerializedName;
import pw.mihou.jaikan.models.images.Media;

public class Trailer {

    @SerializedName("youtube_id") public String youtubeId;
    public String url;
    @SerializedName("embed_url") public String embedUrl;
    public Media images;

}
