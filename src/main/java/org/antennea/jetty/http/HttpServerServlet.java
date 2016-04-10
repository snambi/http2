package org.antennea.jetty.http;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class HttpServerServlet {

    public static void main( String[] args ) throws Exception
    {
        // Create a basic jetty server object that will listen on port 8080.
        // Note that if you set this to port 0 then a randomly available port
        // will be assigned that you can either look in the logs for the port,
        // or programmatically obtain it for use in test cases.
        Server server = new Server(9400);
 
        // Setup JMX
        //MBeanContainer mbContainer = new MBeanContainer(ManagementFactory.getPlatformMBeanServer());
        //server.addBean(mbContainer);
 
        // The WebAppContext is the entity that controls the environment in
        // which a web application lives and breathes. In this example the
        // context path is being set to "/" so it is suitable for serving root
        // context requests and then we see it setting the location of the war.
        // A whole host of other configurations are available, ranging from
        // configuring to support annotation scanning in the webapp (through
        // PlusConfiguration) to choosing where the webapp will unpack itself.
        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/test");
        
        File warFile = new File("src/main/webapp");
        webapp.setWar(warFile.getAbsolutePath());
        webapp.addServlet(HelloServlet.class, "/h1");
        //webapp.addServlet(HelloServlet.class.getCanonicalName(), "/hello");
 
        // A WebAppContext is a ContextHandler as well so it needs to be set to
        // the server so it is aware of where to send the appropriate requests.
        server.setHandler(webapp);
 
        // Start things up! 
        server.start();
 
        // The use of server.join() the will make the current thread join and
        // wait until the server is done executing.
        // See http://docs.oracle.com/javase/7/docs/api/java/lang/Thread.html#join()
        server.join();	
    }

    public static class HelloServlet extends HttpServlet{
    	
    	@Override
        protected void doGet( HttpServletRequest request,
                              HttpServletResponse response ) throws ServletException,
                                                            IOException
        {
            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println("<h1>Hello from HelloServlet</h1>");
        }
    }
}
