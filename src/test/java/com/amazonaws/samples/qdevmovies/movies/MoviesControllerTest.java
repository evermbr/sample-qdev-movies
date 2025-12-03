package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.ui.ExtendedModelMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Ahoy matey! These be the tests for our MoviesController - the captain of our ship!
 * We be testing all the endpoints to make sure they be seaworthy.
 */
public class MoviesControllerTest {

    private MoviesController moviesController;
    private Model model;
    private MockMovieService mockMovieService;
    private ReviewService mockReviewService;

    @BeforeEach
    public void setUp() {
        moviesController = new MoviesController();
        model = new ExtendedModelMap();
        
        // Create mock services with test data
        mockMovieService = new MockMovieService();
        mockReviewService = new MockReviewService();
        
        // Inject mocks using reflection
        try {
            java.lang.reflect.Field movieServiceField = MoviesController.class.getDeclaredField("movieService");
            movieServiceField.setAccessible(true);
            movieServiceField.set(moviesController, mockMovieService);
            
            java.lang.reflect.Field reviewServiceField = MoviesController.class.getDeclaredField("reviewService");
            reviewServiceField.setAccessible(true);
            reviewServiceField.set(moviesController, mockReviewService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock services", e);
        }
    }

    @Test
    @DisplayName("Should return movies view with all movies")
    public void testGetMovies() {
        String result = moviesController.getMovies(model);
        assertNotNull(result, "Result should not be null, ye scallywag!");
        assertEquals("movies", result, "Should return movies view!");
        
        // Verify model contains movies
        assertTrue(model.containsAttribute("movies"), "Model should contain movies attribute!");
    }

    @Test
    @DisplayName("Should return movie details for valid ID")
    public void testGetMovieDetailsValid() {
        String result = moviesController.getMovieDetails(1L, model);
        assertNotNull(result, "Result should not be null!");
        assertEquals("movie-details", result, "Should return movie-details view!");
        
        // Verify model contains required attributes
        assertTrue(model.containsAttribute("movie"), "Model should contain movie attribute!");
        assertTrue(model.containsAttribute("movieIcon"), "Model should contain movieIcon attribute!");
        assertTrue(model.containsAttribute("allReviews"), "Model should contain allReviews attribute!");
    }

    @Test
    @DisplayName("Should return error view for invalid movie ID")
    public void testGetMovieDetailsInvalid() {
        String result = moviesController.getMovieDetails(999L, model);
        assertNotNull(result, "Result should not be null!");
        assertEquals("error", result, "Should return error view for invalid ID!");
        
        // Verify error attributes
        assertTrue(model.containsAttribute("title"), "Model should contain title attribute!");
        assertTrue(model.containsAttribute("message"), "Model should contain message attribute!");
    }

    @Test
    @DisplayName("Should search movies by name successfully")
    public void testSearchMoviesByName() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMovies("Test Movie", null, null);
        
        assertEquals(200, response.getStatusCodeValue(), "Should return 200 OK!");
        assertNotNull(response.getBody(), "Response body should not be null!");
        
        Map<String, Object> body = response.getBody();
        assertTrue((Boolean) body.get("success"), "Search should be successful!");
        assertNotNull(body.get("movies"), "Should contain movies list!");
        assertNotNull(body.get("message"), "Should contain success message!");
        assertNotNull(body.get("searchCriteria"), "Should contain search criteria!");
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) body.get("movies");
        assertEquals(1, movies.size(), "Should find one movie!");
        assertEquals("Test Movie", movies.get(0).getMovieName(), "Should find the correct movie!");
    }

    @Test
    @DisplayName("Should search movies by ID successfully")
    public void testSearchMoviesById() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMovies(null, 1L, null);
        
        assertEquals(200, response.getStatusCodeValue(), "Should return 200 OK!");
        Map<String, Object> body = response.getBody();
        assertTrue((Boolean) body.get("success"), "Search should be successful!");
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) body.get("movies");
        assertEquals(1, movies.size(), "Should find one movie by ID!");
        assertEquals(1L, movies.get(0).getId(), "Should find the correct movie by ID!");
    }

    @Test
    @DisplayName("Should search movies by genre successfully")
    public void testSearchMoviesByGenre() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMovies(null, null, "Drama");
        
        assertEquals(200, response.getStatusCodeValue(), "Should return 200 OK!");
        Map<String, Object> body = response.getBody();
        assertTrue((Boolean) body.get("success"), "Search should be successful!");
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) body.get("movies");
        assertTrue(movies.size() >= 1, "Should find at least one drama movie!");
        
        // Verify all returned movies contain the genre
        for (Movie movie : movies) {
            assertTrue(movie.getGenre().toLowerCase().contains("drama"), 
                "All returned movies should contain 'drama' in genre!");
        }
    }

    @Test
    @DisplayName("Should return bad request for invalid search parameters")
    public void testSearchMoviesInvalidParameters() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMovies(null, null, null);
        
        assertEquals(400, response.getStatusCodeValue(), "Should return 400 Bad Request!");
        Map<String, Object> body = response.getBody();
        assertFalse((Boolean) body.get("success"), "Search should not be successful!");
        assertNotNull(body.get("message"), "Should contain error message!");
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) body.get("movies");
        assertTrue(movies.isEmpty(), "Should return empty movies list!");
    }

    @Test
    @DisplayName("Should return empty results for no matches")
    public void testSearchMoviesNoMatches() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMovies("NonExistentMovie", null, null);
        
        assertEquals(200, response.getStatusCodeValue(), "Should return 200 OK even with no matches!");
        Map<String, Object> body = response.getBody();
        assertTrue((Boolean) body.get("success"), "Search should be successful even with no results!");
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) body.get("movies");
        assertTrue(movies.isEmpty(), "Should return empty movies list for no matches!");
        
        String message = (String) body.get("message");
        assertTrue(message.contains("No movies found"), "Message should indicate no movies found!");
    }

    // Mock MovieService for testing
    private static class MockMovieService extends MovieService {
        private final List<Movie> testMovies;

        public MockMovieService() {
            this.testMovies = Arrays.asList(
                new Movie(1L, "Test Movie", "Test Director", 2023, "Drama", "Test description", 120, 4.5),
                new Movie(2L, "Another Movie", "Another Director", 2022, "Action", "Another description", 110, 4.0),
                new Movie(3L, "Drama Movie", "Drama Director", 2021, "Drama", "Drama description", 130, 4.2)
            );
        }

        @Override
        public List<Movie> getAllMovies() {
            return testMovies;
        }

        @Override
        public Optional<Movie> getMovieById(Long id) {
            return testMovies.stream().filter(movie -> movie.getId().equals(id)).findFirst();
        }

        @Override
        public List<Movie> searchMovies(String name, Long id, String genre) {
            List<Movie> results = new ArrayList<>();
            
            // If ID is provided, search by ID only
            if (id != null && id > 0) {
                Optional<Movie> movie = getMovieById(id);
                if (movie.isPresent()) {
                    results.add(movie.get());
                }
                return results;
            }
            
            // Filter by name and genre
            for (Movie movie : testMovies) {
                boolean matches = true;
                
                if (name != null && !name.trim().isEmpty()) {
                    matches = movie.getMovieName().toLowerCase().contains(name.toLowerCase());
                }
                
                if (matches && genre != null && !genre.trim().isEmpty()) {
                    matches = movie.getGenre().toLowerCase().contains(genre.toLowerCase());
                }
                
                if (matches) {
                    results.add(movie);
                }
            }
            
            return results;
        }

        @Override
        public boolean isValidSearchRequest(String name, Long id, String genre) {
            boolean hasValidName = name != null && !name.trim().isEmpty();
            boolean hasValidId = id != null && id > 0;
            boolean hasValidGenre = genre != null && !genre.trim().isEmpty();
            
            return hasValidName || hasValidId || hasValidGenre;
        }
    }

    // Mock ReviewService for testing
    private static class MockReviewService extends ReviewService {
        @Override
        public List<Review> getReviewsForMovie(long movieId) {
            return new ArrayList<>();
        }
    }
}
