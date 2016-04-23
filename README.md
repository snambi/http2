## HTTP2 Server Using Jetty.

### Build the jar 

```bash
mvn package
```

### Identify the correct ALPN boot jar based on JDK version  

http://www.eclipse.org/jetty/documentation/current/alpn-chapter.html#alpn-versions

### Download the ALPN-boot jar

http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22org.mortbay.jetty.alpn%22%20AND%20a%3A%22alpn-boot%22

### Run the server
```bash
java -Xbootclasspath/p:/path/to/alpn-boot-<version>.jar -jar target/http2-0.0.1-SNAPSHOT.jar
```

### Access the page

Using chrome or firefox open the url (https://ip-address:8443/h1)
