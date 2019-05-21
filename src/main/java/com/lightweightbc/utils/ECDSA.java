package com.lightweightbc.utils;

/*
 * Author : Namal Jayasuriya
 * Reference : https://metamug.com/article/sign-verify-digital-signature-ecdsa-java.html
 * */

import java.security.*;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import static java.util.Base64.getDecoder;
import static java.util.Base64.getEncoder;


public class ECDSA implements Crypto {

    private static Crypto ourInstance = new ECDSA();

    public static Crypto getInstance() {
        return ourInstance;
    }


    @Override
    public KeyPair generateKeys(String seed) {

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        KeyPair keypair = null;

        try {

            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(seed.getBytes());
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256k1");
            keyGen.initialize(ecSpec, random);

            keypair = keyGen.generateKeyPair();

//            PublicKey publicKey = keypair.getPublic();
//            PrivateKey privateKey = keypair.getPrivate();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        return keypair;

    }

    public PublicKey stringToPubKey(String pKey) {

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        PublicKey publicKey = null;
        byte[] keyBytes = getDecoder().decode(pKey);
        KeyFactory factory = null;
        try {
            factory = KeyFactory.getInstance("ECDSA", "BC");
            publicKey = (ECPublicKey) factory.generatePublic(new X509EncodedKeySpec(keyBytes));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return publicKey;

    }

    //create hash of given string and return hash as a Base64 Encoded string
    @Override
    public String hash(String data) {
        try {

            MessageDigest digest = MessageDigest.getInstance("SHA3-256");

            //Applies sha256 to our input,
            byte[] hash = digest.digest(data.getBytes("UTF-8"));

            //encode to Base64 string
            return getEncoder().encodeToString(hash);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String sign(String data, PrivateKey privateKey) {
        Signature dsa;
        try {
            dsa = Signature.getInstance("ECDSA", "BC");
            dsa.initSign(privateKey);
            byte[] strByte = data.getBytes();
            dsa.update(strByte);
            byte[] realSig = dsa.sign();
            return getEncoder().encodeToString(realSig);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean verify(String data, String signature, PublicKey publicKey) {
        try {
            Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(data.getBytes());
            return ecdsaVerify.verify(getDecoder().decode(signature));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
