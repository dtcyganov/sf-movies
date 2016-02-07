package com.movies.scripts;

import com.google.appengine.repackaged.org.apache.http.HttpResponse;
import com.google.appengine.repackaged.org.apache.http.client.methods.HttpGet;
import com.google.appengine.repackaged.org.apache.http.impl.client.DefaultHttpClient;
import com.google.gson.Gson;
import com.movies.domain.Location;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.List;

/**
 * Geo coder service. Sends requests to google geo coder.
 */
public class GeoCoder {

    private static final String GOOGLE_MAPS_KEY = "...";

    private static final String GOOGLE_GEOCODE_URL = "https://maps.googleapis.com/maps/api/geocode/json";

    private DefaultHttpClient httpClient = new DefaultHttpClient();
    private Gson gson = new Gson();

    // class used for json deserialization
    private static class GeoCodeResults {
        List<GeoCodeResult> results;
    }

    // class used for json deserialization
    private static class GeoCodeResult {
        String formatted_address;
        Geometry geometry;
    }

    // class used for json deserialization
    private static class Geometry {
        Coordinates location;
    }

    // class used for json deserialization
    private static class Coordinates {
        double lat;
        double lng;
    }

    public Location geoCode(String place) throws IOException {
        String encoded = URLEncoder.encode(place, "UTF-8");
        HttpGet request = new HttpGet(GOOGLE_GEOCODE_URL + "?key=" + GOOGLE_MAPS_KEY + "&address=" + encoded);

        GeoCodeResults geoCodeResults = null;
        try {
            HttpResponse response = httpClient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                throw new RuntimeException("Failed to code place : " + place + ", code : " + statusCode);
            }

            InputStreamReader reader = new InputStreamReader(response.getEntity().getContent());
            geoCodeResults = gson.fromJson(reader, GeoCodeResults.class);
        } finally {
            request.releaseConnection();
        }

        return geoCodeResultsToLocation(place, geoCodeResults);
    }

    private Location geoCodeResultsToLocation(String place, GeoCodeResults geoCodeResults) {
        if (geoCodeResults.results == null || geoCodeResults.results.isEmpty()) {
            return null;
        }
        GeoCodeResult geoCodeResult = geoCodeResults.results.get(0);

        Location location = new Location();
        location.setName(place.trim());
        location.setAddress(geoCodeResult.formatted_address);
        location.setLat(geoCodeResult.geometry.location.lat);
        location.setLng(geoCodeResult.geometry.location.lng);

        return location;
    }

}
