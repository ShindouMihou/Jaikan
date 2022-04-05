package pw.mihou.jaikan.components.pagination;

import com.google.gson.annotations.SerializedName;

public class PaginationData {

    @SerializedName("last_visible_page")
    public int lastVisiblePage;
    @SerializedName("has_next_page")
    public boolean hasNextPage;

}
