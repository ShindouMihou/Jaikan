package pw.mihou.jaikan.components;

import pw.mihou.jaikan.components.gson.GsonIgnore;
import pw.mihou.jaikan.components.pagination.PaginationData;
import pw.mihou.jaikan.components.pagination.PaginationMeta;
import pw.mihou.jaikan.components.pagination.PaginationShortLinks;

import java.util.List;

public class PaginatedResponse<Type> {

    public PaginationData pagination;

    @GsonIgnore
    public List<Type> data; // This is managed by Jaikan directly.

    public PaginationShortLinks links;
    public PaginationMeta meta;

}
