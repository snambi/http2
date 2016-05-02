package org.antennea.jetty.http2.server;

import java.security.KeyStore;

import org.eclipse.jetty.alpn.server.ALPNServerConnectionFactory;
import org.eclipse.jetty.http2.HTTP2Cipher;
import org.eclipse.jetty.http2.server.HTTP2CServerConnectionFactory;
import org.eclipse.jetty.http2.server.HTTP2ServerConnectionFactory;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.NegotiatingServerConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

/**
 * <code>HTTP2Server</code> has two ports.
 * One for HTTP and the other one HTTPS which upgrades to HTTP2.
 * 
 * @author snambi
 */
public class HTTP2Server extends Server{

	public HTTP2Server( String host, int httpPort, int httpsPort, boolean http2clear, 
						KeyStore keystore, String keyManagerPassword ){
		
		
		// create HTTP configuration
        HttpConfiguration httpConfig = new HttpConfiguration();
        httpConfig.setSecureScheme("https");
        httpConfig.setSecurePort(httpsPort);
        httpConfig.setSendXPoweredBy(true);
        httpConfig.setSendServerVersion(true);
        
        // HTTP Connector
        ServerConnector httpConnector = new ServerConnector(this, new HttpConnectionFactory(httpConfig));
        
        if( http2clear == true ){
        	httpConnector.addConnectionFactory(new HTTP2CServerConnectionFactory(httpConfig) );
        }
        
        httpConnector.setPort(httpPort);
        httpConnector.setHost(host);
        
        httpConnector.addBean(new Connection.Listener(){
				public void onClosed(Connection conn) {
					Class x = conn.getClass();
					System.out.println("Closed connection:" + x.getCanonicalName() );
				}
				public void onOpened(Connection conn) {
					Class x = conn.getClass();
					System.out.println("Opened connection:" + x.getCanonicalName() );
				}
			}
        );
        
        addConnector(httpConnector);
        
        
        // SSL Context factory for HTTPS and HTTP/2
        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStore(keystore);
        sslContextFactory.setKeyManagerPassword(keyManagerPassword);
        sslContextFactory.setCipherComparator(HTTP2Cipher.COMPARATOR);
        
        NegotiatingServerConnectionFactory.checkProtocolNegotiationAvailable();
        ALPNServerConnectionFactory alpn = new ALPNServerConnectionFactory();
        alpn.setDefaultProtocol(httpConnector.getDefaultProtocol());

        // SSL Connection Factory
        SslConnectionFactory ssl = new SslConnectionFactory(sslContextFactory,alpn.getProtocol());

        // Create HTTPS Configuration from HTTP configuration
        HttpConfiguration httpsConfig = new HttpConfiguration(httpConfig);
        httpsConfig.addCustomizer(new SecureRequestCustomizer());

        // HTTP2 Connection Factory
        //HTTP2ServerConnectionFactory h2 = new HTTP2ServerConnectionFactory(httpsConfig);
        HTTP2ServerSessionTrackingConnectionFactory h2 = new HTTP2ServerSessionTrackingConnectionFactory(httpsConfig);
        
        // HTTP2 Connector
        ServerConnector http2Connector =
            new ServerConnector(this,ssl,alpn,h2,new HttpConnectionFactory(httpsConfig));
        
        http2Connector.setPort(httpsPort);
        http2Connector.addBean( new Connection.Listener(){
			public void onClosed(Connection conn) {
				Class x = conn.getClass();
				System.out.println("Closed SSL connection:" + System.identityHashCode(conn)+ " : " + x.getCanonicalName() + " : " + conn.getEndPoint().getRemoteAddress().toString());
			}
			public void onOpened(Connection conn) {
				Class x = conn.getClass();
				System.out.println("Opened SSL connection:" + System.identityHashCode(conn)+ " : " + x.getCanonicalName() + " : " + conn.getEndPoint().getRemoteAddress().toString());
			}
        });
        
        
        // Add HTTP2 connector
        addConnector(http2Connector);
        
        ((QueuedThreadPool)getThreadPool()).setName("http2-server");

	}
}
