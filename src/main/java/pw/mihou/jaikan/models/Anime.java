package pw.mihou.jaikan.models;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

public class Anime {

    @SerializedName("mal_id")
    private int id = 0;
    private String url = "";
    @SerializedName("image_url")
    private String image = "";
    private String title = "";
    private boolean airing = false;
    private String synopsis = "";
    private String type = "";
    private int episodes = 0;
    private int rank = 0;
    private double score = 0.0;
    private Dates aired = new Dates();
    private int members = 0;
    @SerializedName("scored_by")
    private int scored = 0;
    private int popularity = 0;
    private int favorites = 0;
    private List<Nameable> genres = Collections.emptyList();
    private String premiered = "";
    private String background = "";
    private List<Nameable> producers = Collections.emptyList();
    private List<Nameable> licensors = Collections.emptyList();
    private List<Nameable> studios = Collections.emptyList();
    private String rating = "";
    private String duration = "";
    private String status = "";
    private String source = "";
    private String broadcast = "";
    @SerializedName("opening_themes")
    private List<String> opening = Collections.emptyList();
    @SerializedName("ending_Themes")
    private List<String> ending = Collections.emptyList();

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

    public boolean isAiring() {
        return airing;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public String getType() {
        return type;
    }

    public int getEpisodes() {
        return episodes;
    }

    public int getRank() {
        return rank;
    }

    public double getScore() {
        return score;
    }

    public Dates getAired() {
        return aired;
    }

    public int getMembers() {
        return members;
    }

    public int getScored() {
        return scored;
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

    public String getPremiered() {
        return premiered;
    }

    public String getBackground() {
        return background;
    }

    public List<Nameable> getProducers() {
        return producers;
    }

    public List<Nameable> getLicensors() {
        return licensors;
    }

    public List<Nameable> getStudios() {
        return studios;
    }

    public String getRating() {
        return rating;
    }

    public String getDuration() {
        return duration;
    }

    public String getStatus() {
        return status;
    }

    public String getSource() {
        return source;
    }

    public String getBroadcast() {
        return broadcast;
    }

    public List<String> getOpening() {
        return opening;
    }

    public List<String> getEnding() {
        return ending;
    }

}
