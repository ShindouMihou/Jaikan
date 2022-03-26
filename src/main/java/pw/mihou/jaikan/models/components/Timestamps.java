package pw.mihou.jaikan.models.components;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

@SuppressWarnings("unused")
public class Timestamps {

    public Date from;
    public Date to;
    @SerializedName("string") private String toString;

    @Override
    public String toString() {
        return toString;
    }
}
