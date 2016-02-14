package com.movies.servlets;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;
import com.movies.services.SetupContextListener;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Set of test cases for <code>MovieServlet</code>.
 * Test running web instance.
 */
public class FindMoviesTest {

    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    private Closeable session;

    private static final String TEST_MOVIE_JSON =
            "\"title\":\"A View to a Kill\"," +
            "\"releaseYear\":1985," +
            "\"productionCompany\":\"Metro-Goldwyn Mayer\"," +
            "\"distributor\":\"MGM/UA Entertainemnt Company\"," +
            "\"directors\":[{\"name\":\"John Glen\"}]," +
            "\"writers\":[{\"name\":\"Richard Maibaum\"}]," +
            "\"locations\":[" +
                "{\"name\":\"Van Ness Avenue\",\"address\":\"Van Ness Ave, San Francisco, CA, USA\",\"lat\":37.7913102,\"lng\":-122.422592}," +
                "{\"name\":\"Burger Island (901 3rd Street, China Basin)\",\"address\":\"901 3rd St, San Francisco, CA 94107, USA\",\"lat\":37.777359,\"lng\":-122.3908507}" +
            "]," +
            "\"actors\":[{\"name\":\"Roger Moore\"},{\"name\":\"Christopher Walken\"}]";

    @Before
    public void setUp() {
        session = ObjectifyService.begin();
        helper.setUp();
    }

    @After
    public void tearDown() {
        this.session.close();
        helper.tearDown();
    }

    @BeforeClass
    public static void setUpClass() {
        ObjectifyService.setFactory(new ObjectifyFactory());
        new SetupContextListener().contextInitialized(null);
    }

    @BeforeClass
    public static void tearDownClass() {
        new SetupContextListener().contextDestroyed(null);
    }

    @Test
    public void testFindMovies() throws ServletException, IOException {
        FindMoviesServlet findMoviesServlet = new FindMoviesServlet();
        findMoviesServlet.init();

        assertEquals(0, findMovies(findMoviesServlet, "view"));

        put("{" + TEST_MOVIE_JSON + "}");

        assertEquals(1, findMovies(findMoviesServlet, "A View to a Kill"));
        assertEquals(1, findMovies(findMoviesServlet, "view"));
        assertEquals(0, findMovies(findMoviesServlet, "Goldfinger"));
    }

    private long findMovies(FindMoviesServlet findMoviesServlet, String term) throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("term")).thenReturn(term);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        findMoviesServlet.doGet(request, response);

        String responseString = stringWriter.toString();
        return responseString.split("\"id\":\\d+").length - 1;
    }

    private long put(String movieJson) throws IOException, ServletException {
        MovieServlet movieServlet = new MovieServlet();
        movieServlet.init();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        BufferedReader reader = new BufferedReader(new StringReader(movieJson));
        when(request.getReader()).thenReturn(reader);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        movieServlet.doPut(request, response);

        String responseString = stringWriter.toString();
        Pattern pattern = Pattern.compile("\\{\"id\":(\\d+)\\}");
        Matcher matcher = pattern.matcher(responseString);
        assertTrue(matcher.matches());
        return Long.parseLong(matcher.group(1));
    }
}
