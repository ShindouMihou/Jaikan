package pw.mihou.jaikan.components.pagination;

import com.google.gson.annotations.SerializedName;
import pw.mihou.jaikan.components.pagination.links.PaginationLinks;

import java.util.List;

public class PaginationMeta {

    @SerializedName("current_page")
    public int currentPage;

    public int from;

    @SerializedName("last_page")
    public int lastPage;

    public List<PaginationLinks> links;

    public String path;

    @SerializedName("per_page")
    public int perPage;

    public int to;

    public int total;

}
