package com.movies.scripts;

import com.google.gson.Gson;
import com.movies.domain.Movie;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Collection;

/**
 * Puts movies to sf-movies application.
 */
public class Exporter {

    private final Gson gson = new Gson();
    private final CloseableHttpClient httpClient = HttpClients.createDefault();

    public void putMoviesToService(Collection<Movie> movies, String url) throws IOException {
        for (Movie movie : movies) {
            HttpPut putRequest = new HttpPut(url);
            StringEntity input = new StringEntity(gson.toJson(movie));
            putRequest.setEntity(input);
            try (CloseableHttpResponse response = httpClient.execute(putRequest)) {
                int statusCode = response.getStatusLine().getStatusCode();
                EntityUtils.consume(response.getEntity());
                if (statusCode != 200) {
                    throw new RuntimeException("Failed : HTTP error code : " + statusCode);
                }
            }
        }
    }

}
