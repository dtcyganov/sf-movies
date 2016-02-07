package com.movies.scripts;

import com.google.appengine.repackaged.org.apache.http.HttpResponse;
import com.google.appengine.repackaged.org.apache.http.client.methods.HttpPut;
import com.google.appengine.repackaged.org.apache.http.entity.StringEntity;
import com.google.appengine.repackaged.org.apache.http.impl.client.DefaultHttpClient;
import com.google.gson.Gson;
import com.movies.domain.Movie;

import java.io.IOException;
import java.util.Collection;

/**
 * Puts movies to sf-movies application.
 */
public class Exporter {

    private final Gson gson = new Gson();
    private final DefaultHttpClient httpClient = new DefaultHttpClient();

    public void putMoviesToService(Collection<Movie> movies, String url) throws IOException {
        for (Movie movie : movies) {
            HttpPut putRequest = new HttpPut(url);
            StringEntity input = new StringEntity(gson.toJson(movie));
            putRequest.setEntity(input);
            HttpResponse response = httpClient.execute(putRequest);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + statusCode);
            }
            putRequest.releaseConnection();
        }
    }

}
