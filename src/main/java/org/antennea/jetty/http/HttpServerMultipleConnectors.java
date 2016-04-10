package org.antennea.jetty.http;

import java.io.File;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.WebAppContext;

public class HttpServerMultipleConnectors {

	public static void main( String[] args ) throws Exception{
		
		// Create a basic jetty server object without declaring the port. Since
        // we are configuring connectors directly we'll be setting ports on
        // those connectors.
        Server server = new Server();
        
     // HTTP Configuration
        // HttpConfiguration is a collection of configuration information
        // appropriate for http and https. The default scheme for http is
        // <code>http</code> of course, as the default for secured http is
        // <code>https</code> but we show setting the scheme to show it can be
        // done. The port for secured communication is also set here.
        HttpConfiguration http_config = new HttpConfiguration();
        http_config.setSecureScheme("https");
        http_config.setSecurePort(8443);
        http_config.setOutputBufferSize(32768);
        
     // HTTP connector  1
        // The first server connector we create is the one for http, passing in
        // the http configuration we configured above so it can get things like
        // the output buffer size, etc. We also set the port (8080) and
        // configure an idle timeout.
        ServerConnector http1 = new ServerConnector(server,new HttpConnectionFactory(http_config));
        http1.setPort(8080);
        http1.setIdleTimeout(30000);
        
      // HTTP connector  2
        // The first server connector we create is the one for http, passing in
        // the http configuration we configured above so it can get things like
        // the output buffer size, etc. We also set the port (8080) and
        // configure an idle timeout.
        ServerConnector http2 = new ServerConnector(server,new HttpConnectionFactory(http_config));
        http2.setPort(8081);
        http2.setIdleTimeout(30000);
        
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
 
        // A WebAppContext is a ContextHandler as well so it needs to be set to
        // the server so it is aware of where to send the appropriate requests.
        server.setHandler(webapp);
        
        server.setConnectors(new Connector[] { http1, http2 });
        
        // Start things up! 
        server.start();
 
        // The use of server.join() the will make the current thread join and
        // wait until the server is done executing.
        // See http://docs.oracle.com/javase/7/docs/api/java/lang/Thread.html#join()
        server.join();
        
        server.stop();
	}
}
