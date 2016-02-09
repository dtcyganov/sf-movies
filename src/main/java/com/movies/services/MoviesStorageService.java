package com.movies.services;

import com.google.appengine.api.datastore.Query;
import com.movies.domain.Movie;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Service for storing movies in a google storage.
 * Objectify library used for serialization.
 *
 * Doesn't contain state.
 */
public class MoviesStorageService {

    public Movie getMovieById(long id) {
        return ofy().load().type(Movie.class).id(id).now();
    }

    public void deleteMovieById(long id) {
        ofy().delete().type(Movie.class).id(id).now();
    }

    public Long addMovie(Movie movie) {
        ofy().save().entity(movie).now();
        return movie.getId();
    }

    public Movie updateMovie(Movie movie) {
        Movie previousMovie = getMovieById(movie.getId());
        if (previousMovie == null) {
            return null;
        }
        addMovie(movie);
        return previousMovie;
    }

    /**
     * Lowercase the phrase, then split it to separate words and
     * try to find these words in prefixes of the movie title.
     *
     * @param phrase phrase to search
     * @param limit how many results to return
     * @return found movies
     */
    public List<Movie> findMoviesBySearchPhrase(String phrase, int limit) {
        if (StringUtils.isBlank(phrase)) {
            return ofy().load().type(Movie.class).
                    order("title").
                    limit(limit).
                    list();
        }
        String[] parts = phrase.toLowerCase().split("\\s+");
        List<Query.Filter> titleFilters = new ArrayList<>(parts.length);
        for (String part : parts) {
            titleFilters.add(new Query.FilterPredicate("titleIndex", Query.FilterOperator.EQUAL, part));
        }

        // Composite filter requires several sub-filters
        Query.Filter filter = titleFilters.size() == 1 ?
                titleFilters.get(0) :
                new Query.CompositeFilter(Query.CompositeFilterOperator.AND, titleFilters);

        return ofy().load().type(Movie.class).
                filter(filter).
                order("title").
                limit(limit).
                list();
    }
}
