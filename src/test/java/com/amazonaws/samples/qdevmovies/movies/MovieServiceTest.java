package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Ahoy matey! These be the tests for our MovieService treasure chest!
 * We be testing all the search functionality to make sure it be ship-shape.
 */
public class MovieServiceTest {

    private MovieService movieService;

    @BeforeEach
    public void setUp() {
        movieService = new MovieService();
    }

    @Test
    @DisplayName("Should load movies from JSON file successfully")
    public void testGetAllMovies() {
        List<Movie> movies = movieService.getAllMovies();
        assertNotNull(movies, "Movies list should not be null, ye scallywag!");
        assertFalse(movies.isEmpty(), "Movies list should not be empty, matey!");
        
        // Verify we have the expected test movies
        assertTrue(movies.size() >= 12, "Should have at least 12 movies in our treasure chest!");
    }

    @Test
    @DisplayName("Should find movie by valid ID")
    public void testGetMovieByIdValid() {
        Optional<Movie> movie = movieService.getMovieById(1L);
        assertTrue(movie.isPresent(), "Should find movie with ID 1, arrr!");
        assertEquals("The Prison Escape", movie.get().getMovieName());
        assertEquals("John Director", movie.get().getDirector());
    }

    @Test
    @DisplayName("Should return empty for invalid ID")
    public void testGetMovieByIdInvalid() {
        Optional<Movie> movie = movieService.getMovieById(999L);
        assertFalse(movie.isPresent(), "Should not find movie with invalid ID, matey!");
    }

    @Test
    @DisplayName("Should return empty for null or negative ID")
    public void testGetMovieByIdNullOrNegative() {
        Optional<Movie> nullMovie = movieService.getMovieById(null);
        assertFalse(nullMovie.isPresent(), "Should not find movie with null ID!");
        
        Optional<Movie> negativeMovie = movieService.getMovieById(-1L);
        assertFalse(negativeMovie.isPresent(), "Should not find movie with negative ID!");
        
        Optional<Movie> zeroMovie = movieService.getMovieById(0L);
        assertFalse(zeroMovie.isPresent(), "Should not find movie with zero ID!");
    }

    @Test
    @DisplayName("Should search movies by name (case-insensitive)")
    public void testSearchMoviesByName() {
        // Test exact match
        List<Movie> results = movieService.searchMovies("The Prison Escape", null, null);
        assertEquals(1, results.size(), "Should find exactly one movie with exact name match!");
        assertEquals("The Prison Escape", results.get(0).getMovieName());

        // Test partial match (case-insensitive)
        results = movieService.searchMovies("prison", null, null);
        assertEquals(1, results.size(), "Should find movie with partial name match!");
        assertEquals("The Prison Escape", results.get(0).getMovieName());

        // Test case-insensitive
        results = movieService.searchMovies("PRISON", null, null);
        assertEquals(1, results.size(), "Should find movie with case-insensitive match!");

        // Test multiple matches
        results = movieService.searchMovies("The", null, null);
        assertTrue(results.size() >= 4, "Should find multiple movies starting with 'The'!");
    }

    @Test
    @DisplayName("Should search movies by ID")
    public void testSearchMoviesById() {
        List<Movie> results = movieService.searchMovies(null, 1L, null);
        assertEquals(1, results.size(), "Should find exactly one movie by ID!");
        assertEquals(1L, results.get(0).getId());
        assertEquals("The Prison Escape", results.get(0).getMovieName());

        // Test non-existent ID
        results = movieService.searchMovies(null, 999L, null);
        assertTrue(results.isEmpty(), "Should find no movies with non-existent ID!");
    }

    @Test
    @DisplayName("Should search movies by genre (case-insensitive)")
    public void testSearchMoviesByGenre() {
        // Test exact genre match
        List<Movie> results = movieService.searchMovies(null, null, "Drama");
        assertTrue(results.size() >= 3, "Should find multiple drama movies!");
        
        // Verify all results contain Drama in genre
        for (Movie movie : results) {
            assertTrue(movie.getGenre().toLowerCase().contains("drama"), 
                "All results should contain 'drama' in genre!");
        }

        // Test partial genre match
        results = movieService.searchMovies(null, null, "Action");
        assertTrue(results.size() >= 2, "Should find action movies!");

        // Test case-insensitive
        results = movieService.searchMovies(null, null, "DRAMA");
        assertTrue(results.size() >= 3, "Should find drama movies with case-insensitive search!");

        // Test compound genre
        results = movieService.searchMovies(null, null, "Crime");
        assertTrue(results.size() >= 3, "Should find crime movies!");
    }

    @Test
    @DisplayName("Should search movies by multiple criteria")
    public void testSearchMoviesByMultipleCriteria() {
        // Test name and genre combination
        List<Movie> results = movieService.searchMovies("Family", null, "Crime");
        assertEquals(1, results.size(), "Should find one movie matching both name and genre!");
        assertEquals("The Family Boss", results.get(0).getMovieName());
        assertTrue(results.get(0).getGenre().contains("Crime"));

        // Test name with non-matching genre (should return empty)
        results = movieService.searchMovies("Prison", null, "Comedy");
        assertTrue(results.isEmpty(), "Should find no movies when name matches but genre doesn't!");
    }

    @Test
    @DisplayName("Should return empty list for no matches")
    public void testSearchMoviesNoMatches() {
        List<Movie> results = movieService.searchMovies("NonExistentMovie", null, null);
        assertTrue(results.isEmpty(), "Should return empty list for non-existent movie name!");

        results = movieService.searchMovies(null, null, "NonExistentGenre");
        assertTrue(results.isEmpty(), "Should return empty list for non-existent genre!");
    }

    @Test
    @DisplayName("Should handle empty and whitespace-only search parameters")
    public void testSearchMoviesEmptyParameters() {
        List<Movie> results = movieService.searchMovies("", null, null);
        assertEquals(movieService.getAllMovies().size(), results.size(), 
            "Empty name should return all movies!");

        results = movieService.searchMovies("   ", null, null);
        assertEquals(movieService.getAllMovies().size(), results.size(), 
            "Whitespace-only name should return all movies!");

        results = movieService.searchMovies(null, null, "");
        assertEquals(movieService.getAllMovies().size(), results.size(), 
            "Empty genre should return all movies!");

        results = movieService.searchMovies(null, null, "   ");
        assertEquals(movieService.getAllMovies().size(), results.size(), 
            "Whitespace-only genre should return all movies!");
    }

    @Test
    @DisplayName("Should validate search request parameters correctly")
    public void testIsValidSearchRequest() {
        // Valid requests
        assertTrue(movieService.isValidSearchRequest("movie", null, null), 
            "Should be valid with movie name!");
        assertTrue(movieService.isValidSearchRequest(null, 1L, null), 
            "Should be valid with movie ID!");
        assertTrue(movieService.isValidSearchRequest(null, null, "Drama"), 
            "Should be valid with genre!");
        assertTrue(movieService.isValidSearchRequest("movie", 1L, "Drama"), 
            "Should be valid with all parameters!");

        // Invalid requests
        assertFalse(movieService.isValidSearchRequest(null, null, null), 
            "Should be invalid with all null parameters!");
        assertFalse(movieService.isValidSearchRequest("", null, null), 
            "Should be invalid with empty name!");
        assertFalse(movieService.isValidSearchRequest("   ", null, null), 
            "Should be invalid with whitespace-only name!");
        assertFalse(movieService.isValidSearchRequest(null, 0L, null), 
            "Should be invalid with zero ID!");
        assertFalse(movieService.isValidSearchRequest(null, -1L, null), 
            "Should be invalid with negative ID!");
        assertFalse(movieService.isValidSearchRequest(null, null, ""), 
            "Should be invalid with empty genre!");
        assertFalse(movieService.isValidSearchRequest(null, null, "   "), 
            "Should be invalid with whitespace-only genre!");
    }

    @Test
    @DisplayName("Should prioritize ID search over other parameters")
    public void testSearchMoviesIdPriority() {
        // When ID is provided, it should take priority over name and genre
        List<Movie> results = movieService.searchMovies("Different Movie", 1L, "Different Genre");
        assertEquals(1, results.size(), "Should find exactly one movie by ID!");
        assertEquals(1L, results.get(0).getId());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    @DisplayName("Should handle special characters in search parameters")
    public void testSearchMoviesSpecialCharacters() {
        // Test with special characters that might exist in movie names
        List<Movie> results = movieService.searchMovies(":", null, null);
        // Should not crash and should return appropriate results
        assertNotNull(results, "Should handle special characters gracefully!");

        results = movieService.searchMovies(null, null, "/");
        // Should find movies with "/" in genre like "Crime/Drama"
        assertTrue(results.size() >= 2, "Should find movies with compound genres!");
    }
}