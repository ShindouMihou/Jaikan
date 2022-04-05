import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pw.mihou.jaikan.Jaikan;
import pw.mihou.jaikan.configuration.JaikanConfigurationBuilder;
import pw.mihou.jaikan.endpoints.Endpoints;
import pw.mihou.jaikan.models.Anime;
import pw.mihou.jaikan.models.Manga;

import static org.junit.jupiter.api.Assertions.*;

public class ByIdTest {

    @Test
    @Order(1)
    @DisplayName("Anime Get By Id Testing")
    public void testAnimeGetById() {
        Jaikan.setConfiguration(JaikanConfigurationBuilder::build);
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
    }

    @Test
    @Order(2)
    @DisplayName("Manga Get By Id Testing")
    public void testMangaGetById() {
        Jaikan.setConfiguration(JaikanConfigurationBuilder::build);
        Manga manga = Jaikan.object(Endpoints.OBJECT, Manga.class, "manga", "144441").join();
        assertNotNull(manga);
        assertNotNull(manga.title);
        assertNotNull(manga.url);
        assertEquals(144441, manga.id);
        assertEquals("A Villainess for the Tyrant", manga.title);
        assertEquals("https://myanimelist.net/manga/144441/A_Villainess_for_the_Tyrant", manga.url);
        assertEquals("Manhwa", manga.type);
        assertTrue(manga.genres.stream().anyMatch(nameable -> nameable.name.equalsIgnoreCase("Romance")));
    }

}
