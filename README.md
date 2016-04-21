## HTTP2 Server Ssing Jetty.

### Build the jar 

```bash
mvn package
```

### Identify the correct ALPN boot jar based on JDK version  

http://www.eclipse.org/jetty/documentation/current/alpn-chapter.html#alpn-versions

### Download the ALPN-boot jar

### Run the server
```bash
java -Xbootclasspath/p:/path/to/alpn-boot-8.1.5.v20150921.jar -jar target/http2-0.0.1-SNAPSHOT.jar
```
