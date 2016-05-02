package org.antennea.jetty.http2.server;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.http2.ErrorCode;
import org.eclipse.jetty.http2.FlowControlStrategy;
import org.eclipse.jetty.http2.IStream;
import org.eclipse.jetty.http2.api.Session;
import org.eclipse.jetty.http2.api.Stream;
import org.eclipse.jetty.http2.api.server.ServerSessionListener;
import org.eclipse.jetty.http2.frames.DataFrame;
import org.eclipse.jetty.http2.frames.HeadersFrame;
import org.eclipse.jetty.http2.frames.PushPromiseFrame;
import org.eclipse.jetty.http2.frames.ResetFrame;
import org.eclipse.jetty.http2.frames.SettingsFrame;
import org.eclipse.jetty.http2.server.HTTP2ServerConnection;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.util.Callback;

public class HTTPServerSessionListener extends ServerSessionListener.Adapter implements Stream.Listener
{
    private final Connector connector;
    private final EndPoint endPoint;
    
    private int maxDynamicTableSize = 4096;
    private int initialStreamSendWindow = FlowControlStrategy.DEFAULT_WINDOW_SIZE;
    private int maxConcurrentStreams = -1;

    public HTTPServerSessionListener(Connector connector, EndPoint endPoint)
    {
        this.connector = connector;
        this.endPoint = endPoint;
    }

    private HTTP2ServerConnection getConnection()
    {
        return (HTTP2ServerConnection)endPoint.getConnection();
    }

    @Override
    public void onAccept(Session session){
    	System.out.println("New Session: " + session.toString() );
    }
    
    @Override
    public Map<Integer, Integer> onPreface(Session session)
    {
        Map<Integer, Integer> settings = new HashMap<Integer, Integer>();
        settings.put(SettingsFrame.HEADER_TABLE_SIZE, getMaxDynamicTableSize());
        settings.put(SettingsFrame.INITIAL_WINDOW_SIZE, getInitialStreamSendWindow());
        int maxConcurrentStreams = getMaxConcurrentStreams();
        if (maxConcurrentStreams >= 0)
            settings.put(SettingsFrame.MAX_CONCURRENT_STREAMS, maxConcurrentStreams);
        return settings;
    }

    @Override
    public Stream.Listener onNewStream(Stream stream, HeadersFrame frame)
    {
        getConnection().onNewStream(connector, (IStream)stream, frame);
        return frame.isEndStream() ? null : this;
    }

    public void onHeaders(Stream stream, HeadersFrame frame)
    {
        // Servers do not receive responses.
        close(stream, "response_headers");
    }

    public Stream.Listener onPush(Stream stream, PushPromiseFrame frame)
    {
        // Servers do not receive pushes.
        close(stream, "push_promise");
        return null;
    }

    public void onData(Stream stream, DataFrame frame, Callback callback)
    {
        getConnection().onData((IStream)stream, frame, callback);
    }

    //@Override
    public void onReset(Stream stream, ResetFrame frame)
    {
        // TODO:
    }

    //@Override
    public void onTimeout(Stream stream, Throwable x){
        // TODO
    }

    private void close(Stream stream, String reason)
    {
        final Session session = stream.getSession();
        session.close(ErrorCode.PROTOCOL_ERROR.code, reason, Callback.NOOP);
    }

	public int getMaxDynamicTableSize() {
		return maxDynamicTableSize;
	}

	public void setMaxDynamicTableSize(int maxDynamicTableSize) {
		this.maxDynamicTableSize = maxDynamicTableSize;
	}

	public int getInitialStreamSendWindow() {
		return initialStreamSendWindow;
	}

	public void setInitialStreamSendWindow(int initialStreamSendWindow) {
		this.initialStreamSendWindow = initialStreamSendWindow;
	}

	public int getMaxConcurrentStreams() {
		return maxConcurrentStreams;
	}

	public void setMaxConcurrentStreams(int maxConcurrentStreams) {
		this.maxConcurrentStreams = maxConcurrentStreams;
	}
}
