import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pw.mihou.jaikan.Jaikan;
import pw.mihou.jaikan.configuration.JaikanConfigurationBuilder;
import pw.mihou.jaikan.endpoints.Endpoints;
import pw.mihou.jaikan.models.Anime;

import static org.junit.jupiter.api.Assertions.*;

public class RatelimitTest {

    @Test
    @Order(1)
    @DisplayName("Anime Get By Id Ratelimit Testing")
    public void testAnimeGetById() {
        Jaikan.setConfiguration(JaikanConfigurationBuilder::build);
        for (int i = 0; i < 10; i++) {
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
    }

}
