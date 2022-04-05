import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pw.mihou.jaikan.Jaikan;
import pw.mihou.jaikan.cache.RequestCache;
import pw.mihou.jaikan.configuration.JaikanConfigurationBuilder;
import pw.mihou.jaikan.endpoints.Endpoints;
import pw.mihou.jaikan.endpoints.implementations.EndpointImpl;
import pw.mihou.jaikan.models.Anime;
import pw.mihou.jaikan.models.Manga;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class SimpleHashMapCache extends RequestCache {
    ConcurrentHashMap<String, String> cacheMap = new ConcurrentHashMap<>();

    @Override
    public String get(String key) {
        return cacheMap.get(key);
    }

    @Override
    public void put(String key, String value, long timeInSeconds) {
        System.out.println("PUT CACHE " + key + " " + timeInSeconds);
        cacheMap.put(key, value);
    }
}

public class CacheTest {

    public static final SimpleHashMapCache cache = new SimpleHashMapCache();

    static {
        Jaikan.setConfiguration(builder -> builder.setRequestCache(cache).build());
    }

    @Test
    @Order(1)
    @DisplayName("Anime Get By Id Cache Testing")
    public void testAnimeGetById() {
        Anime anime = Jaikan.object(Endpoints.OBJECT, Anime.class, "anime", "33106").join();
        assertNotNull(anime);
        assertNotNull(anime.title);
        assertNotNull(anime.url);
        assertEquals(33106, anime.id);
        assertEquals("Love Live! μ's Final Love Live! Opening Animation", anime.title);
        assertEquals("https://myanimelist.net/anime/33106/Love_Live_μs_Final_Love_Live_Opening_Animation", anime.url);
        assertEquals("Original", anime.source);
        assertEquals("Special", anime.type);
        assertEquals("5 min", anime.duration);
        assertEquals("G - All Ages", anime.rating);
        assertTrue(anime.themes.stream().anyMatch(nameable -> nameable.name.equalsIgnoreCase("Music")));

        assertNotEquals(cache.get(((EndpointImpl) Endpoints.OBJECT).create("anime", "33106")), null);
    }

    @Test
    @Order(2)
    @DisplayName("Manga Get By Id Cache Testing")
    public void testMangaGetById() {
        Manga manga = Jaikan.object(Endpoints.OBJECT, Manga.class, "manga", "144441").join();
        assertNotNull(manga);
        assertNotNull(manga.title);
        assertNotNull(manga.url);
        assertEquals(144441, manga.id);
        assertEquals("A Villainess for the Tyrant", manga.title);
        assertEquals("https://myanimelist.net/manga/144441/A_Villainess_for_the_Tyrant", manga.url);
        assertEquals("Manhwa", manga.type);
        assertTrue(manga.genres.stream().anyMatch(nameable -> nameable.name.equalsIgnoreCase("Romance")));

        assertNotEquals(cache.get(((EndpointImpl) Endpoints.OBJECT).create("manga", "144441")), null);
    }

    @Test
    @Order(3)
    @DisplayName("Anime Search Results Cache Testing")
    public void testAnimeSearch() {
        List<Anime> animes = Jaikan.list(Endpoints.SEARCH, Anime.class, "anime", "Yuru").join();

        assertFalse(animes.isEmpty());
        assertNotNull(animes.get(0));

        animes.forEach(anime -> {
            assertNotNull(anime.title, "The title of an anime is null.");
            assertNotNull(anime.url, "The URL of an anime is null.");
        });

        assertNotEquals(cache.get(((EndpointImpl) Endpoints.SEARCH).create("anime", "Yuru")), null);
    }

    @Test
    @Order(4)
    @DisplayName("Manga Search Results Cache Testing")
    public void testMangaSearch() {
        List<Manga> mangas = Jaikan.list(Endpoints.SEARCH, Manga.class, "manga", "Villainess").join();

        assertFalse(mangas.isEmpty());
        assertNotNull(mangas.get(0));

        mangas.forEach(manga -> {
            assertNotNull(manga.title, "The title of an manga is null.");
            assertNotNull(manga.url, "The URL of an manga is null.");
        });

        assertNotEquals(cache.get(((EndpointImpl) Endpoints.SEARCH).create("manga", "Villainess")), null);
    }

}
