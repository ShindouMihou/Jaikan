# ❤️ Jaikan
Jaikan is a simple generic Jikan wrapper written in Java with the aim of being **lightweight** and **simple** to use with the code
written to be as generic as possible to allow instant support for any other endpoints of Jikan that will be added. The code
also only has a few models built-in (mainly Anime and Manga) as well as the endpoints to help guide you to making your own
models.

## ⭐ Configuring Jaikan
You can easily configure either `Jaikan` through the `.setConfiguration(builder -> ...)` method which allows you to create a new Jaikan
Configuration easily, you can also opt to use a pre-made one `.setConfiguration(configuration)` if you want since both methods are technically the same.

An example of this is:
```java
Jaikan.setConfiguration(builder -> builder
        .setOkHTTPClient(new OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS).build())
        .setUserAgent("Jaikan 4 (by Mihou)")
        .setRatelimit(5, TimeUnit.SECONDS)
        .setRequestCache(...)
        .build());
```

## 📦 Are all results cached?
Jaikan v2.1 has disabled default caching to enable support for Android development. If you want to intercept the requests with caching using, for instance Redis, then 
you can simply add a RequestCache implementation on the JaikanConfiguration.

<details>
    <summary>Jedis-based Request Cache</summary>

```java
public class JedisCache extends RequestCache {

    JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");

    @Override
    public String get(String key) {
        try (Jedis jedis = pool.getResource()) {
            return jedis.get(key);
        }
    }

    @Override
    public void put(String key, String value, long timeInSeconds) {
        try (Jedis jedis = pool.getResource()) {
            jedis.setex(key, timeInSecodns, value);
        }
    }
    
}
```
```java
Jaikan.setConfiguration(builder -> builder.setRequestCache(new JedisCache()).build());
```
</details>

## 🌡 Supported Jikan Versions
Here is a table that shows which API version is supported starting from which version of Jaikan.
| Jikan API Version 	| Supported from Jaikan Version 	|
|:-----------------:	|:-----------------------------:	|
|         v3        	|            v1.0.0+            	|
|         v4        	|            v1.0.5+            	|

> Jikan v3 is now dropped from v2.0.0 onwards to keep consistent with what Jikan will be doing in the future.

> Jaikan v2.1 deprecated the synchronous requests in favor of asynchronous requests, although there are no plans to remove 
> the synchronous methods as of this moment but we recommend using the asynchronous as soon as possible. You can read more 
> about how the asynchronous methods look on **🖨️ How do you make a request?**

> Jaikan v2.1 also dropped the default Caffeine cache to support Android development but you can still enforce a custom cache 
> instead which would be better for flexibility. You can check the example of a custom Jedis-based cache on **📦 Are all results cached?**

## 💻 How to install?
To install via Maven:
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
  <groupId>pw.mihou</groupId>
  <artifactId>Jaikan</artifactId>
  <version>v2.1.0</version>
</dependency>
```

To install via Gradle:
```gradle
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}

implementation 'pw.mihou:Jaikan:v2.1.0'
```

Other Build Tools, please check out the Maven Repository at [Jitpack](https://jitpack.io/#pw.mihou/Jaikan/)

## 🖨️ How do you make a request?

### Making a request to Jikan
A simple anime search and transformation looks like this:
```java
Jaikan.list(Endpoints.SEARCH, AnimeResult.class, "anime", "Yuru Yuri").thenAccept(list -> list.stream().limit(5).forEach(animeResult -> {
    Anime anime = animeResult.asAnime();
    System.out.println("Title: " + animeResult.getTitle());
    System.out.println("\nSynopsis: " + anime.getSynopsis());
    System.out.println("\n\n");
}));
```

You can fetch paginated results by replacing the `list(...)` to `paginated(...)`:
```java
Jaikan.paginated(Endpoints.SEARCH, AnimeResult.class, "anime", "Yuru Yuri").thenAccept(paginatedResponse -> {
    System.out.println("Has next page: " + paginatedResponse.pagination.hasNextPage);
    paginatedResponse.data.stream().limit(5).forEach(animeResult -> {
        Anime anime = animeResult.asAnime();
        System.out.println("Title: " + animeResult.getTitle());
        System.out.println("\nSynopsis: " + anime.getSynopsis());
        System.out.println("\n\n");
    });  
});
```

You can also get the Anime immediately if you have the MyAnimeList ID (MAL ID), for example:
```java
Anime anime = Jaikan.as(Endpoints.OBJECT, Anime.class, "anime", 40842)
        .thenAccept(anime -> System.out.println(anime.getTitle()));
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
* Anime.class - This model is used for full anime results.
* Manga.class - This model is the same as the `Anime.class` but for mangas.

## 🕹️ How do I create my own endpoint?
You can easily create an endpoint by using the following:
```java
Endpoint custom = Endpoints.create("https://api.jikan.moe/...");
```

Every endpoint in Jaikan must follow the following rules:
- If a placeholder for a value is needed, you must use `{}` which indicates `value to be filled` for Jaikan.

The custom placeholder system of Jaikan is inspired a bit by SLF4J's placeholder system in which we use `{}` to replace values with, for example, 
if you want to format `https://api.jikan.moe/v4/{}/{}/` with `anime, 1` then all we need to do is fill the `{}` with the values in specific order to make it
into `https://api.jikan.moe/v4/anime/1/`.

If creating a module pack for Jaikan, please follow the following rules (at least):
1. The endpoints **SHOULD** be as generic as possible, for example: `https://api.jikan.moe/v4/{}/{}/` which can be `https://api.jikan.moe/v4/anime/1/` or `https://api.jikan.moe/v4/manga/1/`.

## 🍱 How to write my own model?
You can easily write your own model with the help of `SerializedName` which comes from GSON. A model is basically a class
which GSON will serialize the data into. For example, we have this data from Jikan: https://api.jikan.moe/v4/anime/40842

We can easily create a model for it that looks like this (This example comes from the pre-defined model `Anime`). Please refer to the following classes for examples:
- [Anime.class](https://github.com/ShindouMihou/Jaikan/blob/master/src/main/java/pw/mihou/jaikan/models/Anime.java)
- [Manga.class](https://github.com/ShindouMihou/Jaikan/blob/master/src/main/java/pw/mihou/jaikan/models/Manga.java)

## ✈️ How frequently will you update this?
Jaikan will be updated as frequently as possible to keep up with Jikan, although the update times will be random and based on when my applications somehow 
encounter an issue while processing the data or similar. Jaikan is created to be as flexible as possible with its model system that allows you to create your own models 
which makes it easier to fix issues when Jikan changes their format once again.
