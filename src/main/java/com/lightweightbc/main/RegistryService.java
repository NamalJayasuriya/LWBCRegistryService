package com.lightweightbc.main;

import com.lightweightbc.grpc.*;
import com.lightweightbc.utils.Crypto;
import com.lightweightbc.utils.ECDSA;
import com.lightweightbc.utils.MongoDb;
import io.grpc.stub.StreamObserver;
import org.bson.Document;


import java.io.IOException;
import java.util.logging.Logger;

public class RegistryService extends PeerServiceGrpc.PeerServiceImplBase {

    private static final Logger logger = Logger.getLogger(GrpcServer.class.getName());
    public static Crypto crypto = ECDSA.getInstance();
    private static RegistryService registryService = new RegistryService();
    //private static Map<String, String> pubKeys = new HashMap();
    //private static Map<String, String> addresses = new HashMap();

    //MongoCollection<Document> collection = MongoDb.getInstance().collection;
    MongoDb mongoDb = MongoDb.getInstance();

    public static RegistryService getInstance() {
        return registryService;
    }

    //main method of  the Peer
    public static void main(String[] args) {

        logger.info("Sreting LWBC Registry server with GRPC @ port 50050");

//        addPubKeys("MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAEfKh2hLwVeTEih1Jo9CGt2+f/qUoR32lpboklBOjg4hd8TjP/Ns7EpK7HRMRx9ShTwheepEUblXZikswyUyw4wA==");
//        addPubKeys("MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAEmQiAt2OYDwubEr8e0rBFUnOmr0RpA7PfY932U7EBHCVwxmxIEdrzsIFovd8jvqxCPFYA0JXGNhfx7Iwn5gn0uQ==");
//        addPubKeys("MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAEgksolyLAL8Cfd8Kj1zUPt12FaY9kjXj/ce5NWs8j8dYEqOnLeoLQ/DJtWK33LRqkhELr07oWkWekZvWCO4sDwg==");
//        addPubKeys("MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAEPqRWzxP143FNBG5nmZ9jUKCA/IMJOO2QN39whXzvWY3kx7uKxcxlHU6Nn5AV1YAad+Cogsg41Lqk3KyVM9rE6w==");
//        addPubKeys("MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAEjqW6VQazgakmcOiQyexbK5xIL+F3aExgkeHXgAdJLPfmcxfNrZGn/lFmSCAoaYftfp+PPhuYrkhUkhT7x9CfYg==");
//        addPubKeys("MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAEWNvsU4sZ7Jkc4Krm9EHG+ZSZg3XoFnIHO9ZbZOmARViO/MDGBHq7wKIfVOQ23I5QUrxXJuDN9jlwtsi1xNPT2w==");

        //instantiate a grpc server
        final GrpcServer server = new GrpcServer(50050);
        //starting grpc server
        try {
            server.start();
            server.blockUntilShutdown();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //ToDo create db is not create

    }

//    public static void addPubKeys(String pKey) {
//        PublicKey publicKey = crypto.stringToPubKey(pKey);
//        pubKeys.put(crypto.hash(publicKey.toString()), pKey);
//    }

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
    public void keyHandle(InfoRequest req, StreamObserver<PeerInfo> responseObserver) {

        logger.info(String.format("Request for :%s", req.getAccount()));
        String accountId = req.getAccount();
        PeerInfo info = null;

        if (accountId.contentEquals("all")) {

            info = PeerInfo.newBuilder()
                    .setAddress(mongoDb.getAllPeerInfo().toString())
                    .build();
        }
        //ToDo handle single key request set individual attribute (id, pubkey, address, ...)

        responseObserver.onNext(info);
        responseObserver.onCompleted();
    }


    @Override
    public void keyStore(PeerInfo req, StreamObserver<InfoResponse> responseObserver) {

        logger.info(String.format("New Pub Key Received :%s", req.getPubKey()));

        Document document = new Document()
                .append("_id", req.getId()) //crypto.hash(crypto.stringToPubKey(req.getPubKey()).toString()))
                .append("pubkey",req.getPubKey())
                .append("address", req.getAddress())
                .append("mining",req.getMiner());
        mongoDb.insert(document);
        System.out.println("A peerInfo inserted successfully");

        InfoResponse reply = null;

        reply = InfoResponse.newBuilder()
                .setResponse("SUCCESS")
                .build();

        responseObserver.onNext(reply);
        responseObserver.onCompleted();

    }

}
