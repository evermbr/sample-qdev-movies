package com.amazonaws.samples.qdevmovies.movies;

import com.amazonaws.samples.qdevmovies.utils.MovieIconUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class MoviesController {
    private static final Logger logger = LogManager.getLogger(MoviesController.class);

    @Autowired
    private MovieService movieService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/movies")
    public String getMovies(org.springframework.ui.Model model) {
        logger.info("Fetching movies");
        model.addAttribute("movies", movieService.getAllMovies());
        return "movies";
    }

    @GetMapping("/movies/{id}/details")
    public String getMovieDetails(@PathVariable("id") Long movieId, org.springframework.ui.Model model) {
        logger.info("Fetching details for movie ID: {}", movieId);
        
        Optional<Movie> movieOpt = movieService.getMovieById(movieId);
        if (!movieOpt.isPresent()) {
            logger.warn("Movie with ID {} not found", movieId);
            model.addAttribute("title", "Movie Not Found");
            model.addAttribute("message", "Movie with ID " + movieId + " was not found.");
            return "error";
        }
        
        Movie movie = movieOpt.get();
        model.addAttribute("movie", movie);
        model.addAttribute("movieIcon", MovieIconUtils.getMovieIcon(movie.getMovieName()));
        model.addAttribute("allReviews", reviewService.getReviewsForMovie(movie.getId()));
        
        return "movie-details";
    }

    /**
     * Ahoy matey! This be the search endpoint for finding movies in our treasure chest!
     * Ye can search by name, id, or genre - or any combination of these parameters.
     * 
     * @param name Optional movie name to search for (partial matches allowed)
     * @param id Optional specific movie ID to find
     * @param genre Optional genre to filter by (partial matches allowed)
     * @return JSON response with search results or error message
     */
    @GetMapping("/movies/search")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> searchMovies(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "genre", required = false) String genre) {
        
        logger.info("Arrr! Search request received - name: {}, id: {}, genre: {}", name, id, genre);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate that at least one search parameter is provided
            if (!movieService.isValidSearchRequest(name, id, genre)) {
                logger.warn("Invalid search request - no valid parameters provided");
                response.put("success", false);
                response.put("message", "Arrr! Ye need to provide at least one search parameter, matey! Use 'name', 'id', or 'genre'.");
                response.put("movies", List.of());
                return ResponseEntity.badRequest().body(response);
            }
            
            // Perform the search
            List<Movie> searchResults = movieService.searchMovies(name, id, genre);
            
            if (searchResults.isEmpty()) {
                logger.info("No movies found matching search criteria");
                response.put("success", true);
                response.put("message", "Shiver me timbers! No movies found matching yer search criteria. Try different parameters, ye scallywag!");
                response.put("movies", List.of());
                response.put("searchCriteria", createSearchCriteriaMap(name, id, genre));
                return ResponseEntity.ok(response);
            }
            
            // Return successful search results
            logger.info("Found {} movies matching search criteria", searchResults.size());
            response.put("success", true);
            response.put("message", String.format("Ahoy! Found %d treasure%s matching yer search!", 
                searchResults.size(), searchResults.size() == 1 ? "" : "s"));
            response.put("movies", searchResults);
            response.put("searchCriteria", createSearchCriteriaMap(name, id, genre));
            response.put("totalResults", searchResults.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error occurred during movie search: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Blimey! Something went wrong while searching for movies. Try again later, matey!");
            response.put("movies", List.of());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Helper method to create a map of search criteria for the response
     */
    private Map<String, Object> createSearchCriteriaMap(String name, Long id, String genre) {
        Map<String, Object> criteria = new HashMap<>();
        if (name != null && !name.trim().isEmpty()) {
            criteria.put("name", name.trim());
        }
        if (id != null && id > 0) {
            criteria.put("id", id);
        }
        if (genre != null && !genre.trim().isEmpty()) {
            criteria.put("genre", genre.trim());
        }
        return criteria;
    }
}