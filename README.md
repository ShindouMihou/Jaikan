# ❤️ Jaikan
Jaikan is a simple generic Jikan wrapper written in Java with the aim of being **lightweight** and **simple** to use with the code
written to be as generic as possible to allow instant support for any other endpoints of Jikan that will be added. The code
also only has a few models built-in (mainly Anime and Manga) as well as the endpoints to help guide you to making your own
models.

## ✨ Why are you aiming for a generic system?
The modular system is to reduce the size of the library and also your application as much as possible. The reason why this reduces
size of your application is because you can select what kind of data you want to add, you can have models that only has the name
and synopsis of the anime or something else entirely.

## 📦 Are all results cached?
Yes, all results are cached with the help of [Caffeine](https://github.com/ben-manes/caffeine) which is one of the best
caching libraries I have used. This both helps reduce the requests you make towards Jikan but also speeds up all your repeated requests.
By default, all items are cached up to 6 hours before they are evicted from the cache.

## 💻 How to install?
For installation details, please check out [Jitpack](https://jitpack.io/#pw.mihou/Jaikan) until we are able to 
receive our own Central Maven repository.

To install via Maven:
```xml
<dependency>
  <groupId>pw.mihou</groupId>
  <artifactId>Jaikan</artifactId>
  <version>1.0.0</version>
</dependency>
```

To install via Gradle:
```gradle
implementation 'pw.mihou:Jaikan:1.0.0'
```

Other Build Tools, please check out the Maven Repository at [Central Maven](https://search.maven.org/artifact/pw.mihou/Jaikan/)

## 🖨️ How do you make a request?
A simple anime search and transformation looks like this:
```java
Jaikan.search(Endpoints.SEARCH, AnimeResult.class, "anime", "Yuru Yuri").thenAccept(animeResults -> {
        animeResults.stream().limit(5).forEach(animeResult -> {
               Anime anime = animeResult.asAnime();
               System.out.println("Title: " + animeResult.getTitle());
               System.out.println("\nSynopsis: " + anime.getSynopsis());
               System.out.println("\n\n");
           });
        });
```

You can also get the Anime immediately if you have the MyAnimeList ID (MAL ID), for example:
```java
Jaikan.as(Endpoints.OBJECT, Anime.class, "anime", 40842).thenAccept(anime -> {
            System.out.println(anime.getTitle());
        });
```

This will output: `Idoly Pride`.

## ❔ Are there any pre-defined endpoints and models?
Yes, there are pre-defined models and endpoints which are specifically the ones I made intiailly for my Discord bot. The list
of them are as written below.

**Pre-defined Endpoints**
Here is the list of pre-defined endpoints.
* Endpoints.SEARCH - This endpoint is used to search for animes, mangas, etc. It requires two values: type of object and query (for example, anime and "Yuru Yuri").
* Endpoints.OBJECT - This endpoint is used to retrieve the full results of animes, mangas, etc. It requires two values as well, same as above.

**Pre-defined Models**
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
Every endpoint is formatted with `String.format(...)` which is why you have to follow the formatter placeholders on https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html which is basically:
- `%s` for Strings.
- `%d` for Integers.
- `%b` for booleans.

An example of a generic endpoint is: `https://api.jikan.moe/v3/%s/%d/` which is the endpoint for `Endpoints.OBJECT`. Basically, you can format it to become: `https://api.jikan.moe/v3/anime/1/` which is an actual working route on Jikan.

## 🍱 How to write my own model?
You can easily write your own model with the help of `SerializedName` which comes from GSON. A model is basically a class
which GSON will serialize the data into. For example, we have this data from Jikan:
```json
{
  "request_hash": "request:anime:e31e3e824cbf63dd6808c6334783d6893d37a911",
  "request_cached": true,
  "request_cache_expiry": 68121,
  "mal_id": 40842,
  "url": "https://myanimelist.net/anime/40842/Idoly_Pride",
  "image_url": "https://cdn.myanimelist.net/images/anime/1025/111962.jpg",
  "trailer_url": "https://www.youtube.com/embed/TzvYKewUXSc?enablejsapi=1&wmode=opaque&autoplay=1",
  "title": "Idoly Pride",
  "title_english": "Idoly Pride",
  "title_japanese": "IDOLY PRIDE",
  "title_synonyms": [],
  "type": "TV",
  "source": "Original",
  "episodes": 12,
  "status": "Finished Airing",
  "airing": false,
  "aired": {
    "from": "2021-01-10T00:00:00+00:00",
    "to": "2021-03-28T00:00:00+00:00",
    "prop": {
      "from": {
        "day": 10,
        "month": 1,
        "year": 2021
      },
      "to": {
        "day": 28,
        "month": 3,
        "year": 2021
      }
    },
    "string": "Jan 10, 2021 to Mar 28, 2021"
  },
  "duration": "23 min per ep",
  "rating": "None",
  "score": 7.4,
  "scored_by": 7820,
  "rank": 1944,
  "popularity": 2986,
  "members": 32827,
  "favorites": 286,
  "synopsis": "To become an idol, I shed blood, sweat, and tears. Even so, I push on. I want to shine. I want to earn it. I want to become number one. I want to smile. I want to make my dreams come true. I want to look back at a sea of people. I want to be noticed. I want to find it. I want to overcome obstacles. I have only pride inside my chest. No one is in the spotlight from the beginning. Everyone is weak. Only those who do not break will reach the highest peak to become an idol. This is the story of idols who face big dreams and harsh reality. (Source: MAL News)",
  "background": null,
  "premiered": "Winter 2021",
  "broadcast": "Sundays at 23:30 (JST)",
  "related": {
    "Other": [
      {
        "mal_id": 48427,
        "type": "anime",
        "name": "The Sun, Moon and Stars",
        "url": "https://myanimelist.net/anime/48427/The_Sun_Moon_and_Stars"
      }
    ]
  },
  "producers": [
    {
      "mal_id": 213,
      "type": "anime",
      "name": "Half H.P Studio",
      "url": "https://myanimelist.net/anime/producer/213/Half_HP_Studio"
    },
    {
      "mal_id": 1289,
      "type": "anime",
      "name": "F.M.F",
      "url": "https://myanimelist.net/anime/producer/1289/FMF"
    },
    {
      "mal_id": 2165,
      "type": "anime",
      "name": "Wicky.Records",
      "url": "https://myanimelist.net/anime/producer/2165/WickyRecords"
    }
  ],
  "licensors": [
    {
      "mal_id": 102,
      "type": "anime",
      "name": "Funimation",
      "url": "https://myanimelist.net/anime/producer/102/Funimation"
    }
  ],
  "studios": [
    {
      "mal_id": 456,
      "type": "anime",
      "name": "Lerche",
      "url": "https://myanimelist.net/anime/producer/456/Lerche"
    }
  ],
  "genres": [
    {
      "mal_id": 19,
      "type": "anime",
      "name": "Music",
      "url": "https://myanimelist.net/anime/genre/19/Music"
    }
  ],
  "opening_themes": [
    "\"IDOLY PRIDE\" by Hoshimi Production (星見プロダクション)"
  ],
  "ending_themes": [
    "#1: \"The Sun, Moon and Stars\" by Hoshimi Production (星見プロダクション) (eps 1-2, 5-6)",
    "#2: \"The Last Chance\" by LizNoir (Rio & Aoi Version) (eps 3, 11)",
    "#3: \"réaliser\" by TRINITYAiLE (eps 4, 10)",
    "#4: \"Shining Days\" by Sunny Peace (サニーピース) (ep 7)",
    "#5: \"Daytime Moon\" by Tsuki no Tempest (月のテンペスト) (ep 8)",
    "#6: \"song for you (SUNNY PEACE ver.)\" by SUNNY PEACE (ep 9)",
    "#7: \"Pray for you\" by Hoshimi Production (星見プロダクション) (ep 12)"
  ]
}
```

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
