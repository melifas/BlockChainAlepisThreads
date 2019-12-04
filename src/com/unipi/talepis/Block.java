package com.unipi.talepis;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Block {

    private static Logger logger = Logger.getLogger(Block.class.getName());

    private String data;
    private String previousHash;
    private String hash;
    private long timeStamp;
    private int nonce;


    public Block(String data, String previousHash, long timeStamp) {
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = timeStamp;
        this.hash = calculateBlockHash();
    }



    public String mineBlock(int prefix) {
        Object lock = new Object();
        ArrayList<Integer> buffer = new ArrayList<>();


        synchronized (lock) {
            Thread t1 = new Thread(new ThreadsMagic(data,previousHash,timeStamp,0,536870911,lock,prefix,buffer));
            Thread t2 = new Thread(new ThreadsMagic(data,previousHash,timeStamp,536870912,1073741824,lock,prefix,buffer));
            Thread t3 = new Thread(new ThreadsMagic(data,previousHash,timeStamp,1073741824,1610612736,lock,prefix,buffer));
            Thread t4 = new Thread(new ThreadsMagic(data,previousHash,timeStamp,1610612737,2147483647,lock,prefix,buffer));


            t1.start();
            t2.start();
            t3.start();
            t4.start();

            try {
                //Περίμενε μέχρι να βρούν τα Threads το nonce και μόλις το βρούν συνεχίζουν απο το wait και θα πάρουν το nonce απο τον buffer
                lock.wait();
                nonce = buffer.get(0);
                hash = calculateBlockHash();
                return hash;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    return  null;
}


    public String calculateBlockHash() {
        String dataToHash = previousHash + Long.toString(timeStamp) + Integer.toString(nonce) + data;
        MessageDigest digest = null;
        byte[] bytes = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            bytes = digest.digest(dataToHash.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
        }
        StringBuffer buffer = new StringBuffer();
        for (byte b : bytes) {
            buffer.append(String.format("%02x", b));
        }
        return buffer.toString();
    }

    public String getHash() {
        return this.hash;
    }

    public String getPreviousHash() {
        return this.previousHash;
    }

    public void setData(String data) {
        this.data = data;
    }
}
