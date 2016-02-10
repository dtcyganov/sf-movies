package com.movies.scripts;

import com.google.gson.Gson;
import com.movies.domain.Person;
import com.movies.domain.Location;
import com.movies.domain.Movie;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Parses movies data taken from
 * https://data.sfgov.org/Culture-and-Recreation/Film-Locations-in-San-Francisco/yitu-d5am.
 */
public class RawDataParser {

    private Gson gson = new Gson();

    // class used for json deserialization
    private static class DataSet {
        List<List<String>> data;
    }

    public Collection<Movie> readMoviesRawData(String filePath) throws IOException {
        Reader reader = new BufferedReader(new FileReader(filePath));
        DataSet dataSet = gson.fromJson(reader, DataSet.class);
        Map<String, Movie> moviesMap = new LinkedHashMap<>();
        reader.close();
        for (List<String> row : dataSet.data) {
            String title = row.get(8).trim();
            int releaseYear = Integer.parseInt(row.get(9));
            String location = row.get(10);
            String productionCompany = row.get(12);
            String distributor = row.get(13);
            String director = row.get(14);
            String writers = row.get(15);
            String actor1 = row.get(16);
            String actor2 = row.get(17);
            String actor3 = row.get(18);

            Movie movie = moviesMap.get(title);
            if (movie == null) {
                movie = new Movie(null,
                        title,
                        releaseYear,
                        productionCompany,
                        distributor,
                        getPersonsFromString(director),
                        getPersonsFromString(writers),
                        new ArrayList<Location>(),
                        getPersonsFromArray(actor1, actor2, actor3));

                moviesMap.put(title, movie);
            }
            movie.getLocations().add(new Location(location == null ? "" : location.trim()));
        }
        return moviesMap.values();
    }

    private List<Person> getPersonsFromString(String s) {
        return isBlank(s) ? Collections.<Person>emptyList() : getPersonsFromArray(s.split(",|&"));
    }

    private List<Person> getPersonsFromArray(String... persons) {
        ArrayList<Person> result = new ArrayList<>();
        for (String person : persons) {
            if (!isBlank(person)) {
                result.add(new Person(person.trim()));
            }
        }
        return result;
    }

}
