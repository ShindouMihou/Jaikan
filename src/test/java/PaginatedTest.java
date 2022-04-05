import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pw.mihou.jaikan.Jaikan;
import pw.mihou.jaikan.components.PaginatedResponse;
import pw.mihou.jaikan.configuration.JaikanConfigurationBuilder;
import pw.mihou.jaikan.endpoints.Endpoints;
import pw.mihou.jaikan.models.Anime;
import pw.mihou.jaikan.models.Manga;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PaginatedTest {

    @Test
    @Order(1)
    @DisplayName("Anime Search Results Pagination Testing")
    public void testAnimeSearch() {
        PaginatedResponse<Anime> animes = Jaikan.paginated(Endpoints.SEARCH, Anime.class, "anime", "Yuru").join();

        assertNotNull(animes.pagination);
        assertFalse(animes.data.isEmpty());
        assertNotNull(animes.data.get(0));

        animes.data.forEach(anime -> {
            assertNotNull(anime.title, "The title of an anime is null.");
            assertNotNull(anime.url, "The URL of an anime is null.");
        });
    }

    @Test
    @Order(2)
    @DisplayName("Manga Search Results Pagination Testing")
    public void testMangaSearch() {
        Jaikan.setConfiguration(JaikanConfigurationBuilder::build);
        PaginatedResponse<Manga> mangas = Jaikan.paginated(Endpoints.SEARCH, Manga.class, "manga", "Villainess").join();

        assertNotNull(mangas.pagination);
        assertFalse(mangas.data.isEmpty());
        assertNotNull(mangas.data.get(0));

        mangas.data.forEach(manga -> {
            assertNotNull(manga.title, "The title of an manga is null.");
            assertNotNull(manga.url, "The URL of an manga is null.");
        });
    }

}
