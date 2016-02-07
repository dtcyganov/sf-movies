package com.movies.domain;

import com.googlecode.objectify.annotation.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Class for actor.
 *
 * Participating in serialization/deserialization with
 * gson and objectify (pay attention during refactoring).
 */
@Entity
public class Movie {

    /**
     * Autogenerated identifier
     */
    @Id
    private Long id;
    /**
     * Need to index title field for ordering by it
     */
    @Index
    private String title;
    private int releaseYear;
    private String productionCompany;
    private String distributor;
    private String director;
    private String writers;
    private Collection<Location> locations;
    private List<Actor> actors;

    /**
     * This index used for auto-complete.
     * Computed automatically using title.
     */
    @Index
    private Collection<String> titleIndex;

    // gson deserialization needs default constructor
    public Movie() {
    }

    public Movie(Long id,
                 String title,
                 int releaseYear,
                 String productionCompany,
                 String distributor,
                 String director,
                 String writers,
                 Collection<Location> locations,
                 List<Actor> actors) {

        this.id = id;
        this.title = title;
        this.releaseYear = releaseYear;
        this.productionCompany = productionCompany;
        this.distributor = distributor;
        this.director = director;
        this.writers = writers;
        this.locations = locations;
        this.actors = actors;
    }

    /**
     * Called before entity would be stored in a storage.
     * Computing titleIndex here.
     */
    @OnSave
    private void onSave() {
        titleIndex = createTitleIndex(title);
    }

    /**
     * Called after entity loaded from a stoage.
     * Do not need titleIndex after it.
     */
    @OnLoad
    private void onLoad() {
        titleIndex = null;
    }

    /**
     * In index we include prefixes of all words in a movie title
     * (lowercase it before).
     *
     * @param title movie title to create index
     * @return index for the title
     */
    private static Collection<String> createTitleIndex(String title) {
        String lowercase = title.toLowerCase();
        String[] parts = lowercase.split("\\s+");
        Collection<String> result = new HashSet<>();
        for (String part : parts) {
            for (int i = 1; i <= part.length(); i++) {
                result.add(part.substring(0, i));
            }
        }
        return result;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public Collection<Location> getLocations() {
        return locations;
    }

}
