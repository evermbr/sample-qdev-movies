# Movie Service - Spring Boot Demo Application 🏴‍☠️

Ahoy matey! Welcome to our treasure chest of movies! A simple movie catalog web application built with Spring Boot, demonstrating Java application development best practices with a pirate twist.

## Features

- **Movie Catalog**: Browse 12 classic movies with detailed information
- **Movie Details**: View comprehensive information including director, year, genre, duration, and description
- **🆕 Movie Search & Filtering**: Search through our treasure chest of movies by name, ID, or genre
- **Customer Reviews**: Each movie includes authentic customer reviews with ratings and avatars
- **Responsive Design**: Mobile-first design that works on all devices
- **Modern UI**: Dark theme with gradient backgrounds and smooth animations
- **🆕 Pirate-themed Search Interface**: Interactive search form with pirate language and styling

## Technology Stack

- **Java 8**
- **Spring Boot 2.7.18**
- **Maven** for dependency management
- **Thymeleaf** for templating
- **Log4j 2.20.0**
- **JUnit 5.8.2**
- **JSON** for data processing

## Quick Start

### Prerequisites

- Java 8 or higher
- Maven 3.6+

### Run the Application

```bash
git clone https://github.com/<youruser>/sample-qdev-movies.git
cd sample-qdev-movies
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Access the Application

- **Movie List**: http://localhost:8080/movies
- **Movie Details**: http://localhost:8080/movies/{id}/details (where {id} is 1-12)
- **🆕 Movie Search API**: http://localhost:8080/movies/search (see API documentation below)

## Building for Production

```bash
mvn clean package
java -jar target/sample-qdev-movies-0.1.0.jar
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/amazonaws/samples/qdevmovies/
│   │       ├── movies/
│   │       │   ├── MoviesApplication.java    # Main Spring Boot application
│   │       │   ├── MoviesController.java     # REST controller for movie endpoints
│   │       │   ├── MovieService.java         # Business logic for movie operations
│   │       │   ├── Movie.java                # Movie data model
│   │       │   ├── Review.java               # Review data model
│   │       │   └── ReviewService.java        # Review business logic
│   │       └── utils/
│   │           ├── MovieIconUtils.java       # Movie icon utilities
│   │           └── MovieUtils.java           # Movie validation utilities
│   └── resources/
│       ├── application.yml                   # Application configuration
│       ├── movies.json                       # Movie catalog data
│       ├── mock-reviews.json                 # Mock review data
│       ├── log4j2.xml                        # Logging configuration
│       └── templates/
│           ├── movies.html                   # Movie list page with search
│           └── movie-details.html            # Movie details page
└── test/                                     # Unit tests
    └── java/
        └── com/amazonaws/samples/qdevmovies/movies/
            ├── MovieServiceTest.java         # Service layer tests
            ├── MoviesControllerTest.java     # Controller tests
            └── MovieTest.java                # Model tests
```

## API Endpoints

### Get All Movies (HTML)
```
GET /movies
```
Returns an HTML page displaying all movies with ratings, basic information, and a search form.

### Get Movie Details (HTML)
```
GET /movies/{id}/details
```
Returns an HTML page with detailed movie information and customer reviews.

**Parameters:**
- `id` (path parameter): Movie ID (1-12)

**Example:**
```
http://localhost:8080/movies/1/details
```

### 🆕 Search Movies (JSON API)
```
GET /movies/search
```
Arrr! This be the new search endpoint for finding movies in our treasure chest!

**Query Parameters:**
- `name` (optional): Movie name to search for (partial matches allowed, case-insensitive)
- `id` (optional): Specific movie ID to find (exact match)
- `genre` (optional): Genre to filter by (partial matches allowed, case-insensitive)

**Note**: At least one parameter must be provided, matey!

**Examples:**

Search by movie name:
```
GET /movies/search?name=prison
```

Search by movie ID:
```
GET /movies/search?id=1
```

Search by genre:
```
GET /movies/search?genre=drama
```

Search by multiple criteria:
```
GET /movies/search?name=family&genre=crime
```

**Response Format:**

Successful search with results:
```json
{
  "success": true,
  "message": "Ahoy! Found 2 treasures matching yer search!",
  "movies": [
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
  ],
  "searchCriteria": {
    "name": "prison"
  },
  "totalResults": 1
}
```

No results found:
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

Invalid request (no parameters):
```json
{
  "success": false,
  "message": "Arrr! Ye need to provide at least one search parameter, matey! Use 'name', 'id', or 'genre'.",
  "movies": []
}
```

Server error:
```json
{
  "success": false,
  "message": "Blimey! Something went wrong while searching for movies. Try again later, matey!",
  "movies": []
}
```

## Search Features

### Web Interface
- **Interactive Search Form**: Pirate-themed search interface on the main movies page
- **Real-time Search**: JavaScript-powered search with instant results
- **Multiple Search Fields**: Search by name, ID, or genre simultaneously
- **Responsive Design**: Mobile-friendly search form
- **Visual Feedback**: Success, error, and info messages with pirate language

### API Features
- **Flexible Search**: Search by any combination of name, ID, or genre
- **Case-insensitive**: All text searches are case-insensitive
- **Partial Matching**: Name and genre searches support partial matches
- **Input Validation**: Comprehensive parameter validation with helpful error messages
- **Error Handling**: Graceful error handling with appropriate HTTP status codes
- **Pirate Language**: Fun, themed response messages

### Search Logic
1. **ID Priority**: If an ID is provided, it takes priority over other parameters
2. **Combined Filtering**: When multiple parameters are provided, results must match ALL criteria
3. **Empty Results**: Returns appropriate messages when no movies match the criteria
4. **Performance**: Efficient in-memory filtering for fast response times

## Testing

Run the comprehensive test suite:

```bash
# Run all tests
mvn test

# Run specific test classes
mvn test -Dtest=MovieServiceTest
mvn test -Dtest=MoviesControllerTest

# Run tests with coverage
mvn test jacoco:report
```

### Test Coverage
- **MovieService**: Complete coverage of search functionality, validation, and edge cases
- **MoviesController**: Full endpoint testing including search API and error scenarios
- **Integration Tests**: End-to-end testing of search workflows
- **Edge Cases**: Comprehensive testing of invalid inputs, empty results, and error conditions

## Troubleshooting

### Port 8080 already in use

Run on a different port:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

### Build failures

Clean and rebuild:
```bash
mvn clean compile
```

### Search not working

1. Check that JavaScript is enabled in your browser
2. Verify the application is running on the correct port
3. Check browser console for any JavaScript errors
4. Ensure the `/movies/search` endpoint is accessible

### No search results

1. Verify your search parameters are valid
2. Try partial matches (e.g., "prison" instead of "The Prison Escape")
3. Check that the movie data is loaded correctly
4. Use case-insensitive searches

## Contributing

Ahoy! This project be designed as a demonstration application. Feel free to:
- Add more movies to the treasure chest (`movies.json`)
- Enhance the UI/UX with more pirate themes
- Add new search features like year range or rating filters
- Improve the responsive design for mobile devices
- Add more comprehensive error handling
- Implement caching for better performance

## Development Guidelines

### Code Style
- Follow Java naming conventions (camelCase for methods, PascalCase for classes)
- Use meaningful variable and method names
- Add JavaDoc comments for public methods
- Include pirate-themed comments where appropriate (but keep them professional)

### Testing
- Write unit tests for all new functionality
- Maintain minimum 80% code coverage
- Test both happy path and error scenarios
- Use descriptive test names and assertions

### API Design
- Follow RESTful conventions
- Provide clear error messages
- Include comprehensive response documentation
- Handle edge cases gracefully

## License

This sample code is licensed under the MIT-0 License. See the LICENSE file.

---

*Arrr! May fair winds fill yer sails as ye explore this treasure chest of movies! 🏴‍☠️*
