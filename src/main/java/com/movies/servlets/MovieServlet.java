package com.movies.servlets;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import com.movies.domain.Movie;
import com.movies.services.MoviesStorageService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.appengine.repackaged.com.google.common.base.StringUtil.isEmptyOrWhitespace;
import static com.google.appengine.repackaged.com.google.common.collect.Iterables.isEmpty;
import static org.apache.http.HttpStatus.*;

/**
 * Movie REST servlet. Supports getting movie by id (GET),
 * creation new movie (PIT), movie update (POST) and movie delete (DELETE).
 */
public class MovieServlet extends HttpServlet {

    private static final Pattern idPattern = Pattern.compile("/([0-9]*)");

    private Gson gson;
    private MoviesStorageService service;

    @Override
    public void init() throws ServletException {
        service = new MoviesStorageService();
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long id = getId(req);
        if (id == null) {
            resp.setStatus(SC_BAD_REQUEST);
            return;
        }
        Movie movie = service.getMovieById(id);
        if (movie == null) {
            resp.setStatus(SC_NOT_FOUND);
            return;
        }

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().append(gson.toJson(movie));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long id = getId(req);
        if (id == null) {
            resp.setStatus(SC_BAD_REQUEST);
            return;
        }

        Movie movie;
        try {
            movie = gson.fromJson(req.getReader(), Movie.class);
        } catch (Exception ignore) {
            resp.setStatus(SC_BAD_REQUEST);
            return;
        }

        if (isEmptyOrWhitespace(movie.getTitle()) || isEmpty(movie.getLocations()) ||
                movie.getId() != null && !movie.getId().equals(id)) {
            resp.setStatus(SC_BAD_REQUEST);
            return;
        }
        movie.setId(id);
        Movie prevMovieVersion = service.updateMovie(movie);
        if (prevMovieVersion == null) {
            resp.setStatus(SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Movie movie;
        try {
            movie = gson.fromJson(req.getReader(), Movie.class);
        } catch (Exception ignore) {
            resp.setStatus(SC_BAD_REQUEST);
            return;
        }

        if (isEmptyOrWhitespace(movie.getTitle()) || isEmpty(movie.getLocations()) || movie.getId() != null) {
            resp.setStatus(SC_BAD_REQUEST);
            return;
        }
        Long id = service.addMovie(movie);

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        JsonWriter jsonWriter = gson.newJsonWriter(resp.getWriter());
        jsonWriter.beginObject().name("id").value(id).endObject();
        jsonWriter.close();
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long id = getId(req);
        if (id == null) {
            resp.setStatus(SC_BAD_REQUEST);
            return;
        }
        service.deleteMovieById(id);
    }

    private static Long getId(HttpServletRequest req) {
        Matcher matcher = idPattern.matcher(req.getPathInfo());
        if (!matcher.matches()) {
            return null;
        }
        try {
            return Long.parseLong(matcher.group(1));
        } catch (NumberFormatException ignore) {
            return null;
        }
    }
}
