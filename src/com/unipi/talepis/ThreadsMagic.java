package com.unipi.talepis;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.logging.Level;

public class ThreadsMagic implements  Runnable {

    private String hash;
    private String previousHash;
    private String data;
    private long timeStamp;
    private ArrayList<Integer> buffer;
    private int start,end;
    private Object lock;
    private int nonce=0;
    private int prefix;

    public ThreadsMagic(String data , String previousHash , long timeStamp ,int start ,int end ,Object lock ,int prefix , ArrayList<Integer> buffer) {
        this.start = start;
        this.end = end;
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = timeStamp;
        this.hash = calculateBlockHash();
        this.buffer = buffer;
        this.lock=lock;
        this.prefix = prefix;
    }

    @Override
    public void run() {

        /*for (int nonce = start; nonce<=end ; nonce++) {
            Object lock = new Object();
            String prefixString = new String(new char[prefix]).replace('\0', '0');
            hash = calculateBlockHash();

            if (hash.substring(0, prefix).equals(prefixString)){
                    System.out.println("Hash found");
                    lock.notify();
                    buffer.add(nonce);
                }else{
                nonce++;
            }
        }*/

        for (int nonce = start; nonce<=end;nonce++){
            Object lock = new Object();
            String prefixString = new String(new char[prefix]).replace('\0', '0');
            hash = calculateBlockHash();
            synchronized (lock) {
                if (hash.substring(0, prefix).equals(prefixString)) {
                    System.out.println("Hash found");
                    lock.notify();
                    buffer.add(nonce);
                }
            }
                break;
        }
    }

    public String calculateBlockHash() {
        String dataToHash = previousHash + Long.toString(timeStamp) + Integer.toString(nonce) + data;
        MessageDigest digest = null;
        byte[] bytes = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            bytes = digest.digest(dataToHash.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
        StringBuffer buffer = new StringBuffer();
        for (byte b : bytes) {
            buffer.append(String.format("%02x", b));
        }
        return buffer.toString();
    }
}
