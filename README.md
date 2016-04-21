## HTTP2 server using Jetty.

Build the jar 

'''
mvn package
'''

Run the server
'''
java -Xbootclasspath/p:/path/to/maven-repo/org/mortbay/jetty/alpn/alpn-boot/8.1.5.v20150921/alpn-boot-8.1.5.v20150921.jar -jar target/http2-0.0.1-SNAPSHOT.jar
'''
