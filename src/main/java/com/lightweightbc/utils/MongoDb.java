package com.lightweightbc.utils;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MongoDb {

    MongoClient mongo;
    MongoCredential credential;
    MongoDatabase database;
    MongoCollection<Document> collection;

    static MongoDb mongoDb = new MongoDb();

    public static MongoDb getInstance(){
        return mongoDb;
    }

    MongoDb(){
        // Creating a Mongo client
        mongo = new MongoClient("localhost", 27017);

        //Accessing the database
        database = mongo.getDatabase("lwbcRegistry");

        //get collection
        collection = database.getCollection("peerInfo");

    }

    public void createCollection(){

        // Creating Credentials
        credential = MongoCredential.createCredential("admin", "lwbcRegistry", "password".toCharArray());
        System.out.println("Connected to the database successfully");

        //Creating a collection
        database.createCollection("peerInfo");
        System.out.println(" 'peerInfo' Collection created successfully");

    }

    public void insert(Document document){
        collection.insertOne(document);
    }

    public List getAllPubKeys(){

        List<String> keys = new ArrayList<>();
        //pubKeys.forEach((k, v) -> keys.add(v));

        // Getting the iterable object
        FindIterable<Document> iterDoc = collection.find();
        int i = 1;

        // Getting the iterator
        Iterator it = iterDoc.iterator();

        while (it.hasNext()) {
            keys.add(((Document) it.next()).get("pubkey").toString());
            i++;
        }

        return keys;
    }

    public List getAllPeerInfo(){

        List<String> info = new ArrayList<>();
        //pubKeys.forEach((k, v) -> keys.add(v));

        // Getting the iterable object
        FindIterable<Document> iterDoc = collection.find();
        int i = 1;

        // Getting the iterator
        Iterator it = iterDoc.iterator();

        while (it.hasNext()) {
            info.add(((Document) it.next()).toJson());
            i++;
        }
        //System.out.println(info.get(0));
        return info;
    }

    public void updateAddress(String id, String address){
        collection.updateOne(Filters.eq("_id", id), Updates.set("address", address));
        System.out.println("Peer info update successfully...");
    }

}
