package com.lightweightbc.grpc;

import com.lightweightbc.main.RegistryService;
import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.logging.Logger;

public class GrpcServer {

    //private static final int MINER_PORT = 50051;
    private static final Logger logger = Logger.getLogger(GrpcServer.class.getName());
    private RegistryService registryService = RegistryService.getInstance();

    private int serverPort;
    private Server server;

    public GrpcServer(int port) {
        serverPort = port;
    }

    public void start() throws IOException {

        /* The port on which the server should run */
        server = ServerBuilder.forPort(serverPort)
                .addService((BindableService) registryService)
                .build()
                .start();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                GrpcServer.this.stop();
                System.err.println("*** server shut down");
            }
        });
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    /**
     * Await termination on the main thread since the com.lightweightbc.grpc library uses daemon threads.
     */
    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }


    /**
     * RegistryService launches the server from the command line.
     */
//    public static void main(String[] args) throws IOException, InterruptedException {
//
//        logger.info("Sreting LWBC Registry server with GRPC @ port 50050");
//        final GrpcServer server = new GrpcServer(50050);
//        server.start();
//        server.blockUntilShutdown();
//    }

//    static class RegistryServiceImpl extends PeerServiceGrpc.PeerServiceImplBase {
//
//        @Override
//        public void pingHandle(PingRequest req, StreamObserver<PingReply> responseObserver) {
//
//            logger.info(String.format("From:%s", req.getSender()));
//
//
//            PingReply reply = PingReply.newBuilder().setLatblock(
//
//                    req.getLastBlock() + " Received."
//
//            ).build();
//
//            responseObserver.onNext(reply);
//            responseObserver.onCompleted();
//        }
//
//        @Override
//        public void keyHandle(KeyRequest req, StreamObserver<KeyReply> responseObserver) {
//
//            logger.info(String.format("From:%s", req.getAccount()));
//
//
//            KeyReply reply = KeyReply.newBuilder()
//                    .setPubKey(" New Pub key ")
//                    .build();
//
//            responseObserver.onNext(reply);
//            responseObserver.onCompleted();
//        }
//
//
//    }

}
