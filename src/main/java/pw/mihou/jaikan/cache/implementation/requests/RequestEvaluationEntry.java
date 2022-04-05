package pw.mihou.jaikan.cache.implementation.requests;

public class RequestEvaluationEntry {

    public String value;
    public long expires;

    public RequestEvaluationEntry(String value, long expires) {
        this.value = value;
        this.expires = expires;
    }

}
