package org.antennea.jetty.http2;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.security.KeyStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.antennea.jetty.http2.server.HTTP2Server;
import org.eclipse.jetty.server.PushBuilder;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.webapp.WebAppContext;

public class Http2ServerSimple {

	public static void main( String[] args ) throws Exception{
		
		InputStream ksin = Http2ServerSimple.class.getClassLoader().getResourceAsStream("keystore.jks");
		KeyStore ks = KeyStore.getInstance("jks");
		ks.load(ksin, "test123".toCharArray() );
				
		HTTP2Server server = new HTTP2Server(8080, 8443, true, ks, "test123");
		
		WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/test");
        
        File warFile = new File("src/main/webapp");
        webapp.setWar(warFile.getAbsolutePath());
        webapp.addServlet(Http2Servlet.class, "/h1");
        webapp.addServlet(JsServlet.class, "/s1.js");
       
 
        // A WebAppContext is a ContextHandler as well so it needs to be set to
        // the server so it is aware of where to send the appropriate requests.
        server.setHandler(webapp);

		server.start();
		server.join();
	}
	
	public static class Http2Servlet extends HttpServlet{
    	
		private static final long serialVersionUID = 198603515368538086L;

		@Override
        protected void doGet( HttpServletRequest request,
                              HttpServletResponse response ) throws ServletException,
                                                            IOException
        {
    		
    		Request srvRequest = (Request) request;
    		
    		
    		if( srvRequest.getRequestURI().equals("/test/h1" ) && srvRequest.isPushSupported() ){
    			System.out.println("push supported");
    			
    			PushBuilder pb = srvRequest.getPushBuilder();
    			
    			pb.addHeader("PUSH", "hello");
    			pb.path("/test/s1.js");
    			pb.push();
    		}
    		
            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println("<html><head><script type='text/javascript' src='/test/s1.js'></script></head><body><h2>Hello from HelloServlet</h2></body></html>");
        }
    }
	
	public static class JsServlet extends HttpServlet{
		
		
		protected void doGet( HttpServletRequest request,
                HttpServletResponse response ) throws ServletException,
                                              IOException{
			
			response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println("<script>console.log('hello world');</script>");
		}
	}

}
