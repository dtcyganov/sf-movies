package com.movies.scripts;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.movies.domain.Location;
import com.movies.domain.Movie;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Uses other services to:
 * 1. Parse raw movies data.
 * 2. Geocode places used in raw data.
 * 3. Create collection of movies with geo data.
 * 4. Posts data to sa-movies service.
 *
 * (after each step data stored to files for debugging and
 * testing different parts).
 */
public class Main {

    public static final String RAW_DATA_FILE_PATH      = "/Users/dtcyganov/Downloads/sf-movies-raw-data.json";
    public static final String DATA_FILE_PATH          = "/Users/dtcyganov/Downloads/sf-movies-data.json";
    public static final String LOCATIONS_FILE_PATH     = "/Users/dtcyganov/Downloads/sf-movies-locations.json";
    public static final String DATA_WITH_GEO_FILE_PATH = "/Users/dtcyganov/Downloads/sf-movies-data-with-geo.json";

    public static final String SAN_FRANCISCO = "San Francisco, CA, USA";

    public static void main(String[] args) throws IOException {

        RawDataParser rawDataParser = new RawDataParser();
        Collection<Movie> movies = rawDataParser.readMoviesRawData(RAW_DATA_FILE_PATH);
        writeToJson(movies, DATA_FILE_PATH);

        System.out.println("Movies count : " + movies.size());

//        Map<String, Location> geoCodedLocations = getGeoCodedLocations(movies);
//        writeToJson(geoCodedLocations, LOCATIONS_FILE_PATH);

        Map<String, Location> geoCodedLocations = readGeoCodedLocations(LOCATIONS_FILE_PATH);

        addGeoDataToLocations(movies, geoCodedLocations);
        writeToJson(movies, DATA_WITH_GEO_FILE_PATH);

//        Collection<Movie> movies = readMovies(DATA_WITH_GEO_FILE_PATH);
        removeMoviesWithoutGoodLocations(movies);
        Exporter exporter = new Exporter();
        exporter.putMoviesToService(movies, "http://localhost:8080/api/v1/movie");
    }

    private static void removeMoviesWithoutGoodLocations(Collection<Movie> readMovies) {
        for (Iterator<Movie> mi = readMovies.iterator(); mi.hasNext();) {
            Movie movie = mi.next();
            for (Iterator<Location> li = movie.getLocations().iterator(); li.hasNext();) {
                Location location = li.next();
                if (location.getAddress().equals(SAN_FRANCISCO)) {
                    li.remove();
                }
            }
            if (movie.getLocations().isEmpty()) {
                mi.remove();
            }
        }
    }

    private static Map<String, Location> getGeoCodedLocations(Collection<Movie> movies) throws IOException {
        GeoCoder geoCoder = new GeoCoder();
        Set<String> uniqueLocations = getUniqueLocations(movies);
        System.out.println("Unique locations count : " + uniqueLocations.size());

        Map<String, Location> geoCodedLocations = new LinkedHashMap<>();
        for (String locationName : uniqueLocations) {
            Location location = geoCoder.geoCode(locationName + " " + SAN_FRANCISCO);
            System.out.println(locationName + "  -->  " + location.getAddress());
            geoCodedLocations.put(locationName, location);
        }
        return geoCodedLocations;
    }

    private static Set<String> getUniqueLocations(Collection<Movie> movies) {
        Set<String> uniqueLocations = new TreeSet<>();
        for (Movie movie : movies) {
            for (Location location : movie.getLocations()) {
                uniqueLocations.add(location.getName());
            }
        }
        return uniqueLocations;
    }

    private static void addGeoDataToLocations(Collection<Movie> movies, Map<String, Location> geoCodedLocations) {
        for (Movie movie : movies) {
            for (Location location : movie.getLocations()) {
                Location geoLocation = geoCodedLocations.get(location.getName());
                if (geoLocation != null) {
                    location.setAddress(geoLocation.getAddress());
                    location.setLat(geoLocation.getLat());
                    location.setLng(geoLocation.getLng());
                }
            }
        }
    }

    private static void writeToJson(Object object, String filePath) throws IOException {
        Gson gson = new Gson();
        File f = new File(filePath);
        f.createNewFile();
        Writer writer = new BufferedWriter(new FileWriter(f));
        gson.toJson(object, writer);
        writer.close();
    }

    private static Collection<Movie> readMovies(String filePath) throws IOException {
        Gson gson = new Gson();
        Reader reader = new BufferedReader(new FileReader(filePath));
        Type type = new TypeToken<Collection<Movie>>() {}.getType();
        Collection<Movie> movies = gson.fromJson(reader, type);
        reader.close();
        return movies;
    }

    private static Map<String, Location> readGeoCodedLocations(String locationsFilePath) throws IOException {
        Gson gson = new Gson();
        Reader reader = new BufferedReader(new FileReader(locationsFilePath));
        Type type = new TypeToken<Map<String, Location>>() {}.getType();
        Map<String, Location> geoCodedLocations = gson.fromJson(reader, type);
        reader.close();
        return geoCodedLocations;
    }

}
