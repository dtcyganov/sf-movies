package com.movies.services;

import com.google.appengine.api.datastore.DatastoreServiceConfig;
import com.googlecode.objectify.ObjectifyService;
import com.movies.domain.Movie;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Listener used to set up some properties which are common for all application.
 */
public class SetupContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        // change default behaviour of empty collections serialization/deserialization
        System.setProperty(DatastoreServiceConfig.DATASTORE_EMPTY_LIST_SUPPORT, Boolean.TRUE.toString());
        // register domain classes for storing
        ObjectifyService.register(Movie.class);
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
    }
}
