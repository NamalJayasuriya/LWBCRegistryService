package com.lightweightbc.utils;


import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public interface Crypto {

    public KeyPair generateKeys(String seed);

    public String hash(String data);

    public String sign(String data, PrivateKey privatekey);

    public boolean verify(String data, String signature, PublicKey publickey);

    public PublicKey stringToPubKey(String pKey);

}


