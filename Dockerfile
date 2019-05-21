# Build stage
#####################################################

# Package stage
#####################################################
FROM adoptopenjdk/openjdk12
COPY target/LWBlockchain-1.0-SNAPSHOT-jar-with-dependencies.jar /usr/local/lib/LWBlockchain.jar
EXPOSE 50050
#ENTRYPOINT ["java","-jar","/usr/local/lib/LWBlockchain.jar","12345","Org1","Peer1","50051","localhost:50051","localhost:50051,localhost:50052", "true"]
ENTRYPOINT ["java","-jar","/usr/local/lib/LWBlockchain.jar"]

### --- Deployment Document --- ###
#####################################################
#
# Build and run
#####################################################
#--- build command ---
#Docker build
#
#`docker build -t lwbc_registry:V1 .`
#
#Run containers
#
#`docker-compose up`
#
#Down containers
#
#`docker-compose down`
