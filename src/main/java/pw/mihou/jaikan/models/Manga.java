package pw.mihou.jaikan.models;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

public class Manga {

    @SerializedName("mal_id")
    private int id = 0;
    private String url = "";
    @SerializedName("image_url")
    private String image = "";
    private String title = "";
    private boolean publishing = false;
    private String synopsis = "";
    private String type = "";
    private int chapters = 0;
    private int volumes = 0;
    private int rank = 0;
    private double score = 0.0;
    private Dates published = new Dates();
    private int members = 0;
    private String status = "";
    private int popularity = 0;
    private int favorites = 0;
    private List<Nameable> genres = Collections.emptyList();
    private List<Nameable> authors = Collections.emptyList();

    public int getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getUrl() {
        return url;
    }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public boolean isPublishing() {
        return publishing;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public String getType() {
        return type;
    }

    public int getChapters() {
        return chapters;
    }

    public int getVolumes() {
        return volumes;
    }

    public int getRank() {
        return rank;
    }

    public double getScore() {
        return score;
    }

    public Dates getPublished() {
        return published;
    }

    public int getMembers() {
        return members;
    }

    public int getPopularity() {
        return popularity;
    }

    public int getFavorites() {
        return favorites;
    }

    public List<Nameable> getGenres() {
        return genres;
    }

    public List<Nameable> getAuthors() {
        return authors;
    }

}
