# ❤️ Jaikan
Jaikan is a simple generic Jikan wrapper written in Java with the aim of being **lightweight** and **simple** to use with the code
written to be as generic as possible to allow instant support for any other endpoints of Jikan that will be added. The code
also only has a few models built-in (mainly Anime and Manga) as well as the endpoints to help guide you to making your own
models.

## ✨ Why are you aiming for a generic system?
The generic system is to reduce the size of the library and also your application as much as possible. The reason why this reduces
size of your application is because you can select what kind of data you want to add, you can have models that only has the name
and synopsis of the anime or something else entirely.

This also helps when a feature of Jikan is suddenly breaks on the library and you need a fix immediately, you could create your own
endpoint and model that has all the fixes needed ~~and most importantly, it won't require much if any updates, laziness is a sin we all love~~ which
also means, you don't have to keep checking the library for updates and you could immediately implement new features yourself! >W< (Yes, lazy developer).

## ⭐ Configuring Jaikan
You can easily configure either `Jaikan4` or `Jaikan` through the `.setConfiguration(builder -> ...)` method which allows you to create a new Jaikan
Configuration easily, you can also opt to use a pre-made one `.setConfiguration(configuration)` if you want since both methods are technically the same.

An example of this is:
```java
Jaikan4.setConfiguration(builder -> builder
        .setOkHTTPClient(new OkHttpClient.Builder().connectTimeout(Duration.ofSeconds(5)).build())
        .setUserAgent("Jaikan 4 (by Mihou)")
        .setRatelimit(Duration.ofSeconds(2))
        .setRequestCache(caffeine -> caffeine.expireAfterWrite(Duration.ofHours(6)))
        .build());
```

Both `Jaikan` and `Jaikan4` have their own independent configuration, so feel free to configure either one without worrying of either causing issues
with the other.

## 📦 Are all results cached?
Yes, all results are cached with the help of [Caffeine](https://github.com/ben-manes/caffeine) which is one of the best
caching libraries I have used. This both helps reduce the requests you make towards Jikan but also speeds up all your repeated requests.
By default, all items are cached up to 6 hours before they are evicted from the cache, you can configure this time via the `.setConfiguration(...)` 
static method of `Jaikan` or `Jaikan4`.

## 🌡 Supported Jikan Versions
Here is a table that shows which API version is supported starting from which version of Jaikan.
| Jikan API Version 	| Supported from Jaikan Version 	|
|:-----------------:	|:-----------------------------:	|
|         v3        	|            v1.0.0+            	|
|         v4        	|            v1.0.5+            	|

## 💻 How to install?
To install via Maven:
```xml
<dependency>
  <groupId>pw.mihou</groupId>
  <artifactId>Jaikan</artifactId>
  <version>1.0.5</version>
</dependency>
```

To install via Gradle:
```gradle
implementation 'pw.mihou:Jaikan:1.0.5'
```

Other Build Tools, please check out the Maven Repository at [Central Maven](https://search.maven.org/artifact/pw.mihou/Jaikan/)

## 🖨️ How do you make a request?

### Making a request to Jikan v3
A simple anime search and transformation looks like this:
```java
Jaikan.search(Endpoints.SEARCH, AnimeResult.class, "anime", "Yuru Yuri")
  .stream().limit(5).forEach(animeResult -> {
               Anime anime = animeResult.asAnime();
               System.out.println("Title: " + animeResult.getTitle());
               System.out.println("\nSynopsis: " + anime.getSynopsis());
               System.out.println("\n\n");
});
```

You can also get the Anime immediately if you have the MyAnimeList ID (MAL ID), for example:
```java
Anime anime = Jaikan.as(Endpoints.OBJECT, Anime.class, "anime", 40842);
System.out.println(anime.getTitle());
```

### Making a request to Jikan v4
A simple anime search and transformation looks like this:
```java
Jaikan4.search(Endpoints.SEARCH, AnimeResult.class, "anime", "Yuru Yuri")
  .stream().limit(5).forEach(animeResult -> {
               Anime anime = animeResult.asAnime();
               System.out.println("Title: " + animeResult.getTitle());
               System.out.println("\nSynopsis: " + anime.getSynopsis());
               System.out.println("\n\n");
});
```

You can also get the Anime immediately if you have the MyAnimeList ID (MAL ID), for example:
```java
Anime anime = Jaikan4.as(Endpoints.OBJECT, Anime.class, "anime", 40842);
System.out.println(anime.getTitle());
```

## ❔ Are there any pre-defined endpoints and models?
Yes, there are pre-defined models and endpoints which are specifically the ones I made intiailly for my Discord bot. The list
of them are as written below.

### Pre-defined Endpoints
Here is the list of pre-defined endpoints.
* Endpoints.SEARCH - This endpoint is used to search for animes, mangas, etc. It requires two values: type of object and query (for example, anime and "Yuru Yuri").
* Endpoints.OBJECT - This endpoint is used to retrieve the full results of animes, mangas, etc. It requires two values as well, same as above.

### Pre-defined Models
Here is the list of pre-defined models.
* Anime.class - This model is used for full anime results (not recommended to use it on `.search(...)` method but is instead used for `.as(...)` method).
* Manga.class - This model is the same as the `Anime.class` and is not recommended to use for `.search(...)` method but is instead used for `.as(...)` method.
* AnimeResult.class - This model is used for `.search(...)` method which returns a mini version of `Anime` that is returned by Jikan.
* AnimeResult.class - This model is used for `.search(...)` method which returns a mini version of `Manga` that is returned by Jikan.

## 🕹️ How do I create my own endpoint?
You can easily create an endpoint by using the following:
```java
Endpoint custom = Endpoints.create("https://api.jikan.moe/...");
```

You can also create an endpoint that supports both v3 and v4, for example:
```java
Endpoint custom = Endpoints.create("https://api.jikan.moe/v3/...",  "https://api.jikan.moe/v4/...");
```

If you want, you can create a custom endpoint that only supports v4:
```java
Endpoint custom = Endpoints.createV4("https://api.jikan.moe/v4/...");
```

Every endpoint in Jaikan must follow the following rules:
- If a placeholder for a value is needed, you must use `{}` which indicates `value to be filled` for Jaikan.

The custom placeholder system of Jaikan is inspired a bit by SLF4J's placeholder system in which we use `{}` to replace values with, for example, 
if you want to format `https://api.jikan.moe/v4/{}/{}/` with `anime, 1` then all we need to do is fill the `{}` with the values in specific order to make it
into `https://api.jikan.moe/v4/anime/1/`.

If creating a module pack for Jaikan, please follow the following rules (at least):
1. All endpoints **SHOULD** support both v3 and v4, allowing the end-user to use the same endpoint for both versions (see: [reference](https://github.com/ShindouMihou/Jaikan#-are-endpoints-cross-compatible-with-each-version-of-jikan-by-default))
2. The endpoints **SHOULD** be as generic as possible, for example: `https://api.jikan.moe/v4/{}/{}/` which can be `https://api.jikan.moe/v4/anime/1/` or `https://api.jikan.moe/v4/manga/1/`.

## ❓ Are endpoints cross-compatible with each version of Jikan by default?
All the endpoints on Jaikan are not cross-compatible by default (the pre-defined ones are, though) but you can make endpoints
that can be used by `Jaikan4` and `Jaikan` through the `Endpoints.create(String, String)` class with the first `String` being the `v3` endpoint and
the second one being `v4` endpoint.

## 🍱 How to write my own model?
You can easily write your own model with the help of `SerializedName` which comes from GSON. A model is basically a class
which GSON will serialize the data into. For example, we have this data from Jikan: https://api.jikan.moe/v3/anime/40842

We can easily create a model for it that looks like this (This example comes from the pre-defined model `Anime`, also it is 
recommended to put fallback variables if the value is not on the results since it will be serialized as null):
```java
public class Anime {

    @SerializedName("mal_id")
    public int id = 0;
    
    public String url = "";
    
    @SerializedName("image_url")
    public String image = "";
    
    public String title = "";
    public boolean airing = false;
    public String synopsis = "";
    public String type = "";
    public int episodes = 0;
    public int rank = 0;
    public double score = 0.0;
    public Dates aired = new Dates();
    public int members = 0;
    @SerializedName("scored_by")
    public int scored = 0;
    
    public int popularity = 0;
    public int favorites = 0;
    public List<Nameable> genres = Collections.emptyList();
    public String premiered = "";
    public String background = "";
    public List<Nameable> producers = Collections.emptyList();
    public List<Nameable> licensors = Collections.emptyList();
    public List<Nameable> studios = Collections.emptyList();
    public String rating = "";
    public String duration = "";
    public String status = "";
    public String source = "";
    public String broadcast = "";
    
    @SerializedName("opening_themes")
    public List<String> opening = Collections.emptyList();
    
    @SerializedName("ending_Themes")
    public List<String> ending = Collections.emptyList();

}
```

## ✈️ How frequently will you update this?
Jaikan will be updated as soon as my Discord bot encounters an issue with it since it has replaced Schalar's Jikan wrapper on my Discord bot (the reason why I created
this wrapper in the first place), you don't have to worry about updates as long as I am still active in the developer community which is going to be my future job. >w<
