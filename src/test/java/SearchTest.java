import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pw.mihou.jaikan.Jaikan;
import pw.mihou.jaikan.endpoints.Endpoints;
import pw.mihou.jaikan.models.Anime;
import pw.mihou.jaikan.models.Manga;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SearchTest {

    @Test
    @Order(1)
    @DisplayName("Anime Search Results Testing")
    public void testAnimeSearch() {
        List<Anime> animes = Jaikan.search(Endpoints.SEARCH, Anime.class, "anime", "Yuru");

        assertFalse(animes.isEmpty());
        assertNotNull(animes.get(0));

        animes.forEach(anime -> {
            assertNotNull(anime.title, "The title of an anime is null.");
            assertNotNull(anime.url, "The URL of an anime is null.");
        });
    }

    @Test
    @Order(2)
    @DisplayName("Manga Search Results Testing")
    public void testMangaSearch() {
        List<Manga> mangas = Jaikan.search(Endpoints.SEARCH, Manga.class, "manga", "Villainess");

        assertFalse(mangas.isEmpty());
        assertNotNull(mangas.get(0));

        mangas.forEach(manga -> {
            assertNotNull(manga.title, "The title of an manga is null.");
            assertNotNull(manga.url, "The URL of an manga is null.");
        });
    }

}
