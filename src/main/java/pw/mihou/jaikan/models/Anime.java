package pw.mihou.jaikan.models;

import com.google.gson.annotations.SerializedName;
import pw.mihou.jaikan.models.components.Nameable;
import pw.mihou.jaikan.models.components.Timestamps;
import pw.mihou.jaikan.models.images.MediaTypes;

import java.util.List;

@SuppressWarnings("unused")
public class Anime {

    @SerializedName("mal_id") public int id;
    public String url;

    public MediaTypes images;

    public String title;
    @SerializedName("title_synonyms") public List<String> titleSynonyms;

    public String synopsis;
    public String background;

    public String type;
    public String source;

    public String duration;
    public boolean airing;
    public Timestamps aired;
    public String status;

    public int episodes;

    public double score;
    @SerializedName("scored_by") public int scoredBy;
    public int rank;
    public int popularity;
    public int members;
    public int favorites;

    public String rating;

    public List<Nameable> genres;
    public List<Nameable> producers;
    public List<Nameable> studios;
    public List<Nameable> themes;

    public String rated;
}
