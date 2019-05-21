# BaseLine Blockchain


#### Compile proto

Generate gRPC service

`mvn protobuf:compile-custom`


Generate gRPC message classes

`mvn protobuf:compile`

####Compile source for the Peer Service

`mvn -f pom.xml clean compile assembly:single`

####Docker Build

`docker build -t lwbc_registry:V1 .`

####Docker run

`docker run --rm -p 50050:50050/tcp -i lwbc_registry:V1`