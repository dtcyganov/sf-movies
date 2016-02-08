package com.movies.servlets;

import com.google.gson.Gson;
import com.movies.domain.Movie;
import com.movies.services.MoviesStorageService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * This servlet using in auto-complete feature.
 * Returns movies matched with phrase.
 */
public class SuggestMoviesNamesServlet extends HttpServlet {

    private static final int LIMIT = 10;

    private Gson gson;
    private MoviesStorageService service;

    @Override
    public void init() throws ServletException {
        gson = new Gson();
        service = new MoviesStorageService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String phrase = req.getParameter("term");
        List<Movie> movies = service.findMoviesBySearchPhrase(phrase, LIMIT);

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        resp.getWriter().append(gson.toJson(movies));
    }
}
