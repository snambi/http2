package org.antennea.jetty.http2;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Date;
import java.util.Random;

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

		String host = "127.0.0.1";
		int http_port = 8080;
		int https_port = 8443;

		if( args.length > 0 ){ 
			
			if( args.length != 3 ){
				System.err.println("Usage: java -jar http2.jar <ip-address> <http-port> <https-port>");
				System.exit(-1);
			}
			
			host = args[0];
			http_port = Integer.parseInt(args[1]);
			https_port = Integer.parseInt(args[2]);
		}
			
		InputStream ksin = Http2ServerSimple.class.getClassLoader().getResourceAsStream("keystore.jks");
		KeyStore ks = KeyStore.getInstance("jks");
		ks.load(ksin, "test123".toCharArray() );
				
		HTTP2Server server = new HTTP2Server(host, http_port, https_port, true, ks, "test123");
		
		WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("");
        
        File warFile = new File(".");
        webapp.setWar(warFile.getAbsolutePath());
        webapp.addServlet(Http2Servlet.class, "/h2");
        webapp.addServlet(JsServlet.class, "/s1.js");
       
 
        // A WebAppContext is a ContextHandler as well so it needs to be set to
        // the server so it is aware of where to send the appropriate requests.
        server.setHandler(webapp);

		server.start();
		server.join();
	}
	
	
	public static class Http2Servlet extends HttpServlet{
    	
		private static final long serialVersionUID = 198603515368538086L;
		
		private static String word = generateRandomWord();

		@Override
        protected void doGet( HttpServletRequest request,
                              HttpServletResponse response ) throws ServletException,
                                                            IOException
        {
    		
    		Request srvRequest = (Request) request;
    		
    		
    		if( srvRequest.getRequestURI().equals("/h2" ) && srvRequest.isPushSupported() ){
    			
    			PushBuilder pb = srvRequest.getPushBuilder();
    			
    			pb.addHeader("PUSH", "hello");
    			pb.path("/s1.js");
    			System.out.println("s1.js pushed");
    			pb.push();
    		}
    		
    		String timestr = (new Date()).toString();
            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);
            //Keep-Alive: timeout=15, max=100
            //response.setHeader("Keep-Alive", "timeout=15, max=100");
            
            response.getWriter().println("<html><head><script type='text/javascript' src='/s1.js'></script></head><body><h2>Hello from HelloServlet</h2><p>"+ word+ "</p><p>"+ timestr +"</p></body></html>");
            System.out.println("response sent");
        }
    }
	
	public static class JsServlet extends HttpServlet{
		
		
		protected void doGet( HttpServletRequest request,
                HttpServletResponse response ) throws ServletException,
                                              IOException{
			
			response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println("console.log('hello world');");
            System.out.println("Js Response sent");
		}
	}
	
	public static String generateRandomWord(){
		
		Random random = new Random();
		char[] word = new char[random.nextInt(8)+ 5];
		for( int i=0 ; i< word.length ; i++ ){
			word[i] = (char) ( 'a' + random.nextInt(26));
		}
		
		return new String(word);
	}

}
