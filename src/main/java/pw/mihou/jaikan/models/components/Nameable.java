package pw.mihou.jaikan.models.components;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class Nameable {

    @SerializedName("malId")
    public int id;
    public String type;
    public String name;
    public String url;

    @Override
    public String toString() {
        return name;
    }

}
