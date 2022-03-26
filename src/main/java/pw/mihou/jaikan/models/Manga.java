package pw.mihou.jaikan.models;

import com.google.gson.annotations.SerializedName;
import pw.mihou.jaikan.models.components.Nameable;
import pw.mihou.jaikan.models.components.Timestamps;
import pw.mihou.jaikan.models.images.MediaTypes;

import java.util.List;

@SuppressWarnings("unused")
public class Manga {

    @SerializedName("mal_id") public int id;
    public String url;

    public String title;
    @SerializedName("title_synonyms") public List<String> titleSynonyms;

    public String type;
    public int chapters;
    public int volumes;

    public String status;
    public boolean publishing;
    public Timestamps published;

    public MediaTypes images;

    public double score;
    @SerializedName("scored_by") public int scoredBy;
    public int rank;
    public int popularity;
    public int members;
    public int favorites;

    public String synopsis;
    public String background;

    public List<Nameable> authors;
    public List<Nameable> serializations;
    public List<Nameable> genres;

}
