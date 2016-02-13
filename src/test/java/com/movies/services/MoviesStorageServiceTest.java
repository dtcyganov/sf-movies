package com.movies.services;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.gson.Gson;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;
import com.movies.domain.Location;
import com.movies.domain.Movie;
import com.movies.domain.Person;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.*;

public class MoviesStorageServiceTest {

    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    private Closeable session;
    private Movie testMovie;
    private Gson gson = new Gson();

    @Before
    public void setUp() {
        session = ObjectifyService.begin();
        helper.setUp();
        testMovie = new Movie(null, "A View to a Kill", 1985, "Metro-Goldwyn Mayer", "MGM/UA Entertainemnt Company",
                new ArrayList<>(
                        singletonList(new Person("John Glen"))
                ),
                new ArrayList<>(
                        singletonList(new Person("Richard Maibaum"))
                ),
                new ArrayList<>(asList(
                        new Location(
                                "Van Ness Avenue",
                                "Van Ness Ave, San Francisco, CA, USA",
                                37.7913102, -122.422592
                        ),
                        new Location(
                                "Burger Island (901 3rd Street, China Basin)",
                                "901 3rd St, San Francisco, CA 94107, USA",
                                37.7941378, -122.4077914
                        )
                )),
                new ArrayList<>(asList(
                        new Person("Roger Moore"),
                        new Person("Christopher Walken")
                ))
        );
    }

    @After
    public void tearDown() {
        this.session.close();
        helper.tearDown();
    }

    @BeforeClass
    public static void setUpClass() {
        ObjectifyService.setFactory(new ObjectifyFactory());
        new SetupContextListener().contextInitialized(null);
    }

    @BeforeClass
    public static void tearDownClass() {
        new SetupContextListener().contextDestroyed(null);
    }

    @Test(expected = NullPointerException.class)
    public void testAddNull() {
        MoviesStorageService service = new MoviesStorageService();
        service.addMovie(null);
    }

    @Test
    public void testAddAndGet() {
        MoviesStorageService service = new MoviesStorageService();
        assertNull(testMovie.getId());
        Long movieId = service.addMovie(testMovie);
        assertEquals(movieId, testMovie.getId());
        Movie loadedMovie = service.getMovieById(movieId);
        assertNotNull(loadedMovie);
        assertEquals(
                gson.toJson(testMovie, Movie.class),
                gson.toJson(loadedMovie, Movie.class)
        );
    }

    @Test
    public void testAddAndDelete() {
        MoviesStorageService service = new MoviesStorageService();
        Long movieId = service.addMovie(testMovie);
        Movie loadedMovie1 = service.getMovieById(movieId);
        assertNotNull(loadedMovie1);
        service.deleteMovieById(movieId);
        Movie loadedMovie2 = service.getMovieById(movieId);
        assertNull(loadedMovie2);
    }

    @Test
    public void testAddAndUpdate() {
        MoviesStorageService service = new MoviesStorageService();
        Long movieId = service.addMovie(testMovie);
        Movie movie = service.getMovieById(movieId);
        movie.getLocations().add(new Location(
                "Golden Gate Bridge",
                "Golden Gate Bridge, Golden Gate Bridge, San Francisco, CA, USA",
                37.8199286, -122.4782551
        ));
        service.updateMovie(movie);
        Movie loadedMovie = service.getMovieById(movieId);
        assertEquals(
                gson.toJson(movie, Movie.class),
                gson.toJson(loadedMovie, Movie.class)
        );
    }

    @Test
    public void testFindMoviesBySearchPhrase() {
        MoviesStorageService service = new MoviesStorageService();
        Long movieId = service.addMovie(testMovie);

        List<Movie> result1 = service.findMoviesBySearchPhrase("A View to a Kill", 10);
        List<Movie> result2 = service.findMoviesBySearchPhrase("a view to a kill", 10);
        List<Movie> result3 = service.findMoviesBySearchPhrase("View Kill", 10);
        List<Movie> result4 = service.findMoviesBySearchPhrase("Vi Ki", 10);
        for (List<Movie> result : asList(result1, result2, result3, result4)) {
            assertEquals(1, result.size());
            assertEquals(movieId, result.get(0).getId());
        }

        List<Movie> emptyResult1 = service.findMoviesBySearchPhrase("Goldfinger", 10);
        List<Movie> emptyResult2 = service.findMoviesBySearchPhrase("A View to a Kill 2", 10);
        for (List<Movie> result : asList(emptyResult1, emptyResult2)) {
            assertEquals(0, result.size());
        }
    }

    @Test
    public void testFindMoviesBySearchPhraseLimit() {
        MoviesStorageService service = new MoviesStorageService();
        for (int i = 0; i < 5; i++) {
            Movie movie = gson.fromJson(gson.toJson(testMovie, Movie.class), Movie.class);//deep copy
            service.addMovie(movie);
        }
        assertEquals(5, service.findMoviesBySearchPhrase("View Kill", 10).size());
        assertEquals(5, service.findMoviesBySearchPhrase("View Kill", 5).size());
        assertEquals(2, service.findMoviesBySearchPhrase("View Kill", 2).size());
        assertEquals(1, service.findMoviesBySearchPhrase("View Kill", 1).size());
        assertEquals(0, service.findMoviesBySearchPhrase("View Kill", 0).size());
    }
}
