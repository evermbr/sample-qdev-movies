# Movie Search API Documentation 🏴‍☠️

Ahoy matey! This be the comprehensive documentation for our movie search API. Navigate these waters carefully to find the treasure ye seek!

## Overview

The Movie Search API provides powerful search and filtering capabilities for the movie catalog. It supports searching by movie name, ID, or genre with flexible parameter combinations and comprehensive error handling.

## Base URL

```
http://localhost:8080
```

## Authentication

No authentication required - this be a free treasure for all!

## Endpoints

### Search Movies

**Endpoint:** `GET /movies/search`

**Description:** Search through the movie catalog using various criteria. At least one search parameter must be provided.

#### Request Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `name` | String | No | Movie name to search for (partial matches, case-insensitive) |
| `id` | Long | No | Specific movie ID to find (exact match, must be positive) |
| `genre` | String | No | Genre to filter by (partial matches, case-insensitive) |

**Parameter Rules:**
- At least one parameter must be provided
- Empty strings and whitespace-only values are considered invalid
- ID parameter takes priority over name and genre when provided
- Multiple parameters create AND conditions (all must match)

#### Response Format

All responses return JSON with the following structure:

```json
{
  "success": boolean,
  "message": "string",
  "movies": [Movie],
  "searchCriteria": {object},
  "totalResults": number
}
```

#### Response Fields

| Field | Type | Description |
|-------|------|-------------|
| `success` | Boolean | Indicates if the search was successful |
| `message` | String | Human-readable message with pirate flair |
| `movies` | Array | List of matching movies (empty if no matches) |
| `searchCriteria` | Object | Echo of the search parameters used |
| `totalResults` | Number | Count of movies returned (only in successful searches) |

#### Movie Object Structure

```json
{
  "id": 1,
  "movieName": "The Prison Escape",
  "director": "John Director",
  "year": 1994,
  "genre": "Drama",
  "description": "Two imprisoned men bond over a number of years...",
  "duration": 142,
  "imdbRating": 5.0
}
```

## Examples

### Search by Movie Name

**Request:**
```http
GET /movies/search?name=prison
```

**Response:**
```json
{
  "success": true,
  "message": "Ahoy! Found 1 treasure matching yer search!",
  "movies": [
    {
      "id": 1,
      "movieName": "The Prison Escape",
      "director": "John Director",
      "year": 1994,
      "genre": "Drama",
      "description": "Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.",
      "duration": 142,
      "imdbRating": 5.0
    }
  ],
  "searchCriteria": {
    "name": "prison"
  },
  "totalResults": 1
}
```

### Search by Movie ID

**Request:**
```http
GET /movies/search?id=2
```

**Response:**
```json
{
  "success": true,
  "message": "Ahoy! Found 1 treasure matching yer search!",
  "movies": [
    {
      "id": 2,
      "movieName": "The Family Boss",
      "director": "Michael Filmmaker",
      "year": 1972,
      "genre": "Crime/Drama",
      "description": "The aging patriarch of an organized crime dynasty transfers control of his clandestine empire to his reluctant son.",
      "duration": 175,
      "imdbRating": 5.0
    }
  ],
  "searchCriteria": {
    "id": 2
  },
  "totalResults": 1
}
```

### Search by Genre

**Request:**
```http
GET /movies/search?genre=action
```

**Response:**
```json
{
  "success": true,
  "message": "Ahoy! Found 3 treasures matching yer search!",
  "movies": [
    {
      "id": 3,
      "movieName": "The Masked Hero",
      "director": "Chris Moviemaker",
      "year": 2008,
      "genre": "Action/Crime",
      "description": "When a menacing villain wreaks havoc and chaos on the people of the city, a masked hero must accept one of the greatest psychological and physical tests.",
      "duration": 152,
      "imdbRating": 5.0
    },
    {
      "id": 6,
      "movieName": "Dream Heist",
      "director": "Chris Moviemaker",
      "year": 2010,
      "genre": "Action/Sci-Fi",
      "description": "A thief who steals corporate secrets through dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O.",
      "duration": 148,
      "imdbRating": 4.5
    },
    {
      "id": 7,
      "movieName": "The Virtual World",
      "director": "Alex Director",
      "year": 1999,
      "genre": "Action/Sci-Fi",
      "description": "A computer programmer is led to fight an underground war against powerful computers who have constructed his entire reality with a system called the Matrix.",
      "duration": 136,
      "imdbRating": 4.5
    }
  ],
  "searchCriteria": {
    "genre": "action"
  },
  "totalResults": 3
}
```

### Multiple Search Criteria

**Request:**
```http
GET /movies/search?name=family&genre=crime
```

**Response:**
```json
{
  "success": true,
  "message": "Ahoy! Found 1 treasure matching yer search!",
  "movies": [
    {
      "id": 2,
      "movieName": "The Family Boss",
      "director": "Michael Filmmaker",
      "year": 1972,
      "genre": "Crime/Drama",
      "description": "The aging patriarch of an organized crime dynasty transfers control of his clandestine empire to his reluctant son.",
      "duration": 175,
      "imdbRating": 5.0
    }
  ],
  "searchCriteria": {
    "name": "family",
    "genre": "crime"
  },
  "totalResults": 1
}
```

### No Results Found

**Request:**
```http
GET /movies/search?name=nonexistent
```

**Response:**
```json
{
  "success": true,
  "message": "Shiver me timbers! No movies found matching yer search criteria. Try different parameters, ye scallywag!",
  "movies": [],
  "searchCriteria": {
    "name": "nonexistent"
  }
}
```

## Error Responses

### Invalid Request (No Parameters)

**Request:**
```http
GET /movies/search
```

**Response:**
```json
{
  "success": false,
  "message": "Arrr! Ye need to provide at least one search parameter, matey! Use 'name', 'id', or 'genre'.",
  "movies": []
}
```

**HTTP Status:** `400 Bad Request`

### Invalid Request (Empty Parameters)

**Request:**
```http
GET /movies/search?name=&genre=
```

**Response:**
```json
{
  "success": false,
  "message": "Arrr! Ye need to provide at least one search parameter, matey! Use 'name', 'id', or 'genre'.",
  "movies": []
}
```

**HTTP Status:** `400 Bad Request`

### Server Error

**Response:**
```json
{
  "success": false,
  "message": "Blimey! Something went wrong while searching for movies. Try again later, matey!",
  "movies": []
}
```

**HTTP Status:** `500 Internal Server Error`

## HTTP Status Codes

| Status Code | Description |
|-------------|-------------|
| `200 OK` | Successful search (with or without results) |
| `400 Bad Request` | Invalid search parameters |
| `500 Internal Server Error` | Server-side error during search |

## Search Behavior

### Case Sensitivity
- All text searches (name and genre) are **case-insensitive**
- "DRAMA", "drama", and "Drama" will all match movies with "Drama" genre

### Partial Matching
- Name and genre searches support **partial matching**
- Searching for "prison" will match "The Prison Escape"
- Searching for "action" will match "Action/Crime" and "Action/Sci-Fi"

### ID Priority
- When an ID parameter is provided, it takes **priority** over name and genre
- Other parameters are ignored when a valid ID is specified
- Invalid IDs (negative, zero, or non-existent) return empty results

### Multiple Criteria
- When multiple parameters are provided, results must match **ALL** criteria (AND logic)
- Example: `name=family&genre=crime` returns movies that contain "family" in the name AND "crime" in the genre

### Parameter Validation
- Empty strings and whitespace-only values are treated as invalid
- At least one valid parameter must be provided
- ID parameters must be positive integers

## Rate Limiting

Currently, no rate limiting is implemented. This API is designed for demonstration purposes.

## Caching

No caching is currently implemented. All searches query the in-memory movie data directly.

## Data Source

Movies are loaded from `src/main/resources/movies.json` at application startup. The catalog contains 12 movies with IDs from 1 to 12.

## Client Integration

### JavaScript Example

```javascript
async function searchMovies(name, id, genre) {
    const params = new URLSearchParams();
    if (name) params.append('name', name);
    if (id) params.append('id', id);
    if (genre) params.append('genre', genre);
    
    try {
        const response = await fetch(`/movies/search?${params.toString()}`);
        const data = await response.json();
        
        if (data.success) {
            console.log(`Found ${data.totalResults} movies:`, data.movies);
        } else {
            console.error('Search failed:', data.message);
        }
        
        return data;
    } catch (error) {
        console.error('Network error:', error);
        throw error;
    }
}

// Usage examples
searchMovies('prison', null, null);           // Search by name
searchMovies(null, 1, null);                  // Search by ID
searchMovies(null, null, 'drama');            // Search by genre
searchMovies('family', null, 'crime');        // Multiple criteria
```

### cURL Examples

```bash
# Search by name
curl "http://localhost:8080/movies/search?name=prison"

# Search by ID
curl "http://localhost:8080/movies/search?id=1"

# Search by genre
curl "http://localhost:8080/movies/search?genre=drama"

# Multiple criteria
curl "http://localhost:8080/movies/search?name=family&genre=crime"

# URL-encoded parameters
curl "http://localhost:8080/movies/search?name=The%20Prison%20Escape"
```

## Testing

The API includes comprehensive unit tests covering:

- Valid search scenarios (name, ID, genre)
- Multiple criteria combinations
- Edge cases (empty results, invalid parameters)
- Error conditions (server errors, malformed requests)
- Parameter validation and sanitization

Run tests with:
```bash
mvn test -Dtest=MoviesControllerTest
mvn test -Dtest=MovieServiceTest
```

## Performance Considerations

- **In-Memory Search**: All searches are performed on in-memory data for fast response times
- **Linear Complexity**: Search time is O(n) where n is the number of movies
- **No Indexing**: Currently no search indexing is implemented
- **Memory Usage**: Entire movie catalog is loaded into memory at startup

For production use with larger datasets, consider:
- Implementing search indexing (e.g., Elasticsearch)
- Adding database persistence with optimized queries
- Implementing caching for frequently searched terms
- Adding pagination for large result sets

## Future Enhancements

Potential improvements for the search API:

1. **Advanced Filtering**
   - Year range filtering
   - Rating range filtering
   - Duration filtering
   - Director search

2. **Search Features**
   - Fuzzy matching for typos
   - Search suggestions/autocomplete
   - Search result ranking
   - Full-text search in descriptions

3. **Performance**
   - Search result caching
   - Database indexing
   - Pagination support
   - Asynchronous search for large datasets

4. **API Enhancements**
   - Sorting options (by rating, year, name)
   - Field selection (return only specific fields)
   - Search analytics and logging
   - Rate limiting and throttling

---

*Arrr! May this documentation guide ye safely through the treacherous waters of movie searching! 🏴‍☠️*