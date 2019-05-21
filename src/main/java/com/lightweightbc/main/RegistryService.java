package com.lightweightbc.main;

import com.lightweightbc.utils.Crypto;
import com.lightweightbc.utils.ECDSA;
import com.lightweightbc.grpc.*;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class RegistryService extends PeerServiceGrpc.PeerServiceImplBase {

    private static final Logger logger = Logger.getLogger(GrpcServer.class.getName());
    public static Crypto crypto = ECDSA.getInstance();
    private static RegistryService registryService = new RegistryService();
    private static Map<String, String> pubKeys = new HashMap();

    public static RegistryService getInstance() {
        return registryService;
    }

    //main method of  the Peer
    public static void main(String[] args) {

        logger.info("Sreting LWBC Registry server with GRPC @ port 50050");

        addPubKeys("MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAEfKh2hLwVeTEih1Jo9CGt2+f/qUoR32lpboklBOjg4hd8TjP/Ns7EpK7HRMRx9ShTwheepEUblXZikswyUyw4wA==");
        addPubKeys("MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAEmQiAt2OYDwubEr8e0rBFUnOmr0RpA7PfY932U7EBHCVwxmxIEdrzsIFovd8jvqxCPFYA0JXGNhfx7Iwn5gn0uQ==");
        addPubKeys("MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAEgksolyLAL8Cfd8Kj1zUPt12FaY9kjXj/ce5NWs8j8dYEqOnLeoLQ/DJtWK33LRqkhELr07oWkWekZvWCO4sDwg==");
        addPubKeys("MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAEPqRWzxP143FNBG5nmZ9jUKCA/IMJOO2QN39whXzvWY3kx7uKxcxlHU6Nn5AV1YAad+Cogsg41Lqk3KyVM9rE6w==");
        addPubKeys("MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAEjqW6VQazgakmcOiQyexbK5xIL+F3aExgkeHXgAdJLPfmcxfNrZGn/lFmSCAoaYftfp+PPhuYrkhUkhT7x9CfYg==");
        addPubKeys("MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAEWNvsU4sZ7Jkc4Krm9EHG+ZSZg3XoFnIHO9ZbZOmARViO/MDGBHq7wKIfVOQ23I5QUrxXJuDN9jlwtsi1xNPT2w==");

        final GrpcServer server = new GrpcServer(50050);
        try {
            server.start();
            server.blockUntilShutdown();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static void addPubKeys(String pKey) {
        PublicKey publicKey = crypto.stringToPubKey(pKey);
        pubKeys.put(crypto.hash(publicKey.toString()), pKey);
    }

    @Override
    public void pingHandle(PingRequest req, StreamObserver<PingReply> responseObserver) {

        logger.info(String.format("From:%s", req.getSender()));


        PingReply reply = PingReply.newBuilder().setLatblock(

                req.getLastBlock() + " Received."

        ).build();

        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    @Override
    public void keyHandle(KeyRequest req, StreamObserver<KeyReply> responseObserver) {

        logger.info(String.format("Request for :%s", req.getAccount()));
        String accountId = req.getAccount();
        KeyReply reply = null;

        if (accountId.contentEquals("all")) {

            List<String> keys = new ArrayList<>();
            pubKeys.forEach((k, v) -> keys.add(v));

            reply = KeyReply.newBuilder()
                    .setPubKey(keys.toString())
                    .build();
        }
        //ToDo handle single key request

        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    @Override
    public void keyStore(MessageRequest req, StreamObserver<MessageResponse> responseObserver) {

        logger.info(String.format("New Pub Key Received :%s", req.getRequest()));
        addPubKeys(req.getRequest());

        MessageResponse reply = null;

        reply = MessageResponse.newBuilder()
                .setResponse("SUCCESS")
                .build();

        responseObserver.onNext(reply);
        responseObserver.onCompleted();

    }

}
