# Kafka Connector for ZSTD Files

This code is ripped from the standard Kafka FileStreamSource. The code was slightly modified to work with ZstdStreams.

ZSTD is a compression format. Read more about it here: https://facebook.github.io/zstd/

Files compressed in ZSTD can be used as input for Kafka streams. To setup the connector you need:

   * kafka-zstd.jar (the connector code)
   * zstd-jni.jar (the JNI to the ZSTD library, found at https://github.com/luben/zstd-jni )
   
This project was built using zstd-jni-1.4.4-6 but it is likely compatible with other versions too.

1. Place the two JAR files into a directory, eg. D:\Kafka\connectors
2. Write a **connect-zstd.properties** file based on connect-file
   1. `plugin.path=D:/Kafka/connectors` # to pick up the JARs
   2. Set the bootstrap.servers, offset.storage etc.
3. Write a **connect-zstd-source.properties** file based on connect-source
   1. `connector.class=org.apache.kafka.connect.file.ZstdFileStreamSourceConnector`
   2. `file=path/to/the/zstd/file`
   3. `topic=target-topic`
4. Now you can run kafka-connect with the command:
`connect-standalone.bat connect-zstd.properties connect-zstd-source.properties`
