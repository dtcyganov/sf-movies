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

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Set of test cases for <code>MovieServlet</code>.
 * Test running web instance.
 */
public class MovieServletTest {

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
    public void testGetWithoutId() throws ServletException, IOException {
        MovieServlet movieServlet = new MovieServlet();
        movieServlet.init();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getPathInfo()).thenReturn("");
        movieServlet.doGet(request, response);

        verify(response, atLeastOnce()).setStatus(400); // bad request
    }

    @Test
    public void testGetWithBadId() throws ServletException, IOException {
        MovieServlet movieServlet = new MovieServlet();
        movieServlet.init();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getPathInfo()).thenReturn("/some_string");
        movieServlet.doGet(request, response);

        verify(response, atLeastOnce()).setStatus(400); // bad request
    }

    @Test
    public void testGetWithNotExistingId() throws ServletException, IOException {
        MovieServlet movieServlet = new MovieServlet();
        movieServlet.init();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getPathInfo()).thenReturn("/333");
        movieServlet.doGet(request, response);

        verify(response, atLeastOnce()).setStatus(404); // not found
    }

    @Test
    public void testPutAndGet() throws ServletException, IOException {
        MovieServlet movieServlet = new MovieServlet();
        movieServlet.init();

        long movieId = testPut(movieServlet);
        testGet(movieServlet, movieId);
    }

    private long testPut(MovieServlet movieServlet) throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        BufferedReader reader = new BufferedReader(new StringReader("{" + TEST_MOVIE_JSON + "}"));
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

    private void testGet(MovieServlet movieServlet, long movieId) throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getPathInfo()).thenReturn("/" + movieId);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        movieServlet.doGet(request, response);

        String responseString = stringWriter.toString();
        assertTrue(responseString.contains("\"id\":" + movieId + "," + TEST_MOVIE_JSON));
    }

}
