package com.unipi.talepis;

import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Main {
    public static List<Block> blockchain = new ArrayList<Block>();
    public static int prefix = 1;

    public static void main(String[] args) {


        long startTime = System.nanoTime();
        System.out.println("Process Started");
	    // Add some nodes
        // First the Genesis Block n. O
        Block genesisBlock = new Block("The is the Genesis Block.", "0", new Date().getTime());
        genesisBlock.mineBlock(prefix);
        blockchain.add(genesisBlock);
        System.out.println("Node "+(blockchain.size()-1)+" created...");

        // Then all the others
        Block firstBlock = new Block("The is the First Block.", blockchain.get(blockchain.size()-1).getHash(), new Date().getTime());
        firstBlock.mineBlock(prefix);
        blockchain.add(firstBlock);
        System.out.println("Node "+(blockchain.size()-1)+" created...");

        // Then all the others
        Block secondBlock = new Block("The is the Second Block.", blockchain.get(blockchain.size()-1).getHash(), new Date().getTime());
        secondBlock.mineBlock(prefix);
        blockchain.add(secondBlock);
        System.out.println("Node "+(blockchain.size()-1)+" created...");

        //Transform BlockChain into Json and print it
        String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
        System.out.println("\nThe block chain: ");
        System.out.println(blockchainJson);

        // Check for validity
        System.out.println("\nBlockchain is Valid: " + isChainValid());

        long endTime = System.nanoTime();
        long duration = endTime-startTime;
        System.out.println("Total time ellapsed: "+(float)duration/1000000000 +" seconds");

    }
    public static Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[prefix]).replace('\0', '0');

        //loop through blockchain to check hashes:
        for(int i=1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i-1);
            //compare registered hash and calculated hash:
            if(!currentBlock.getHash().equals(currentBlock.calculateBlockHash()) ){
                System.out.println("Current Hashes not equal");
                return false;
            }
            //compare previous hash and registered previous hash
            if(!previousBlock.getHash().equals(currentBlock.getPreviousHash()) ) {
                System.out.println("Previous Hashes not equal");
                return false;
            }
            //check if hash is solved
            if(!currentBlock.getHash().substring( 0, prefix).equals(hashTarget)) {
                System.out.println("This block hasn't been mined");
                return false;
            }
        }
        return true;
    }
}
