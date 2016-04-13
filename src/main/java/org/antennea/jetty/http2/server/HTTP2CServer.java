package org.antennea.jetty.http2.server;

import org.eclipse.jetty.http2.server.HTTP2CServerConnectionFactory;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

public class HTTP2CServer extends Server {

	 public HTTP2CServer(int httpPort){
		 
	        HttpConfiguration config = new HttpConfiguration();
	        // HTTP + HTTP/2 connector
	        
	        HttpConnectionFactory http1 = new HttpConnectionFactory(config);
	        HTTP2CServerConnectionFactory http2c = new HTTP2CServerConnectionFactory(config);
	        
	        ServerConnector connector = new ServerConnector(this,http1,http2c);
	        connector.setPort(httpPort);
	        addConnector(connector);

	        ((QueuedThreadPool)getThreadPool()).setName("http2c-server");

	        //setHandler(new SimpleHandler()); 
	 }
}
