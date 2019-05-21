/*
 * Copyright 2015 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lightweightbc.grpc;

import com.google.gson.Gson;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * gRPC client
 */
public class GrpcClient {

    private static final Logger logger = Logger.getLogger(GrpcClient.class.getName());

    public static Gson gson = new Gson();

    private final ManagedChannel channel;
    private final PeerServiceGrpc.PeerServiceBlockingStub blockingStub;

    /**
     * Construct client connecting to HelloWorld server at {@code host:port}.
     */
    public GrpcClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port)
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
                // needing certificates.
                .usePlaintext()
                .build());
    }

    /**
     * Construct client for accessing HelloWorld server using the existing channel.
     */
    GrpcClient(ManagedChannel channel) {
        this.channel = channel;
        blockingStub = PeerServiceGrpc.newBlockingStub(channel);
    }

    //  /**
//   *
//   */
//    public static void main(String[] args) throws Exception {
//        GrpcClient client = new GrpcClient("localhost", 50050);
//        try {
//            /* Access a service running on the local machine on port 50051 */
//            String user = "world-456";
//            if (args.length > 0) { //ToDo
//                user = args[0]; /* Use the arg as the name to greet if provided */
//            }
//            client.requestPubKey("aaaaaa");
//        } finally {
//            client.shutdown();
//        }
//    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    //sending new event via gRPC
    public void sendPing(String sender) {
        //logger.info(String.format("From:%s  To:%s  Data:%s",event.getSender(),event.getReceiver(),event.getData()));
        PingRequest request = PingRequest.newBuilder()
                .setSender(sender)
                .build();
        PingReply response;
        try {
            response = blockingStub.pingHandle(request);
            //ToDo handle the responce for this request
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
        logger.info("Response : " + response.getLatblock());
    }

//    public void requestPubKey(String account) {
//        //logger.info(String.format("From:%s  To:%s  Data:%s",event.getSender(),event.getReceiver(),event.getData()));
//        KeyRequest request = KeyRequest.newBuilder()
//                .setAccount(account)
//                .build();
//        KeyReply response;
//        try {
//            response = blockingStub.keyHandle(request);
//            //ToDo handle the responce for this request
//        } catch (StatusRuntimeException e) {
//            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
//            return;
//        }
//        logger.info("Response : " + response.getPubKey());
//    }

}
