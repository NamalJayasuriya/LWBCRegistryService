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
    MongoDb mongoDb = MongoDb.getInstance();

    public static RegistryService getInstance() {
        return registryService;
    }

    //main method of  the Peer
    public static void main(String[] args) {

        logger.info("Sreting LWBC Registry server with GRPC @ port 50050");

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

        //ToDo create if db is not created

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
