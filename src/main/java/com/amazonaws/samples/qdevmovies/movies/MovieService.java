package com.amazonaws.samples.qdevmovies.movies;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

@Service
public class MovieService {
    private static final Logger logger = LogManager.getLogger(MovieService.class);
    private final List<Movie> movies;
    private final Map<Long, Movie> movieMap;

    public MovieService() {
        this.movies = loadMoviesFromJson();
        this.movieMap = new HashMap<>();
        for (Movie movie : movies) {
            movieMap.put(movie.getId(), movie);
        }
    }

    private List<Movie> loadMoviesFromJson() {
        List<Movie> movieList = new ArrayList<>();
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("movies.json");
            if (inputStream != null) {
                Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name());
                String jsonContent = scanner.useDelimiter("\\A").next();
                scanner.close();
                
                JSONArray moviesArray = new JSONArray(jsonContent);
                for (int i = 0; i < moviesArray.length(); i++) {
                    JSONObject movieObj = moviesArray.getJSONObject(i);
                    movieList.add(new Movie(
                        movieObj.getLong("id"),
                        movieObj.getString("movieName"),
                        movieObj.getString("director"),
                        movieObj.getInt("year"),
                        movieObj.getString("genre"),
                        movieObj.getString("description"),
                        movieObj.getInt("duration"),
                        movieObj.getDouble("imdbRating")
                    ));
                }
            }
        } catch (Exception e) {
            logger.error("Failed to load movies from JSON: {}", e.getMessage());
        }
        return movieList;
    }

    public List<Movie> getAllMovies() {
        return movies;
    }

    public Optional<Movie> getMovieById(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return Optional.ofNullable(movieMap.get(id));
    }

    /**
     * Ahoy matey! This here method searches through our treasure chest of movies
     * using the search criteria ye provide. It can search by name, id, or genre.
     * 
     * @param name The movie name to search for (partial matches allowed, case-insensitive)
     * @param id The specific movie ID to find
     * @param genre The genre to filter by (partial matches allowed, case-insensitive)
     * @return A list of movies that match yer search criteria, or an empty list if no treasure be found
     */
    public List<Movie> searchMovies(String name, Long id, String genre) {
        logger.info("Arrr! Searching for movies with criteria - name: {}, id: {}, genre: {}", name, id, genre);
        
        List<Movie> searchResults = new ArrayList<>(movies);
        
        // Filter by movie ID if provided - this be the most specific search, matey!
        if (id != null && id > 0) {
            Optional<Movie> movieById = getMovieById(id);
            return movieById.map(movie -> {
                List<Movie> result = new ArrayList<>();
                result.add(movie);
                return result;
            }).orElse(new ArrayList<>());
        }
        
        // Filter by movie name if provided - search through the ship's manifest!
        if (name != null && !name.trim().isEmpty()) {
            String searchName = name.trim().toLowerCase();
            searchResults = searchResults.stream()
                .filter(movie -> movie.getMovieName().toLowerCase().contains(searchName))
                .collect(Collectors.toList());
        }
        
        // Filter by genre if provided - sort the treasure by type!
        if (genre != null && !genre.trim().isEmpty()) {
            String searchGenre = genre.trim().toLowerCase();
            searchResults = searchResults.stream()
                .filter(movie -> movie.getGenre().toLowerCase().contains(searchGenre))
                .collect(Collectors.toList());
        }
        
        logger.info("Search completed! Found {} movies matching yer criteria", searchResults.size());
        return searchResults;
    }

    /**
     * Validates search parameters to ensure they be ship-shape and Bristol fashion!
     * 
     * @param name The movie name parameter
     * @param id The movie ID parameter  
     * @param genre The genre parameter
     * @return true if at least one valid search parameter is provided, false otherwise
     */
    public boolean isValidSearchRequest(String name, Long id, String genre) {
        // At least one search parameter must be provided and valid
        boolean hasValidName = name != null && !name.trim().isEmpty();
        boolean hasValidId = id != null && id > 0;
        boolean hasValidGenre = genre != null && !genre.trim().isEmpty();
        
        return hasValidName || hasValidId || hasValidGenre;
    }
}
