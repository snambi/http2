package org.antennea.jetty.http2.server;

import org.eclipse.jetty.http2.api.server.ServerSessionListener;
import org.eclipse.jetty.http2.server.HTTP2ServerConnectionFactory;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;

public class HTTP2ServerSessionTrackingConnectionFactory extends HTTP2ServerConnectionFactory {

	//private ServerSessionListener listener;
	
	public HTTP2ServerSessionTrackingConnectionFactory(HttpConfiguration httpConfiguration) {
		super(httpConfiguration);
		//this.listener = listener;
	}

	@Override
    protected ServerSessionListener newSessionListener(Connector connector, EndPoint endPoint){
        //return listener;
		return new HTTPServerSessionListener(connector, endPoint);
    }
}
