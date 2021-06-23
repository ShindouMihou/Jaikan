package pw.mihou.jaikan.models;

import com.google.gson.annotations.SerializedName;
import pw.mihou.jaikan.Jaikan;
import pw.mihou.jaikan.endpoints.Endpoints;

import java.util.Date;

public class MangaResult {

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
    private double score = 0.0;
    @SerializedName("start_date")
    private Date start = new Date();
    @SerializedName("end_date")
    private Date end = new Date();
    private int members = 0;

    public int getId() {
        return id;
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

    public double getScore() {
        return score;
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    public int getMembers() {
        return members;
    }

    public Manga as(){
        return Jaikan.as(Endpoints.OBJECT, Manga.class, "manga", this.id);
    }

}
