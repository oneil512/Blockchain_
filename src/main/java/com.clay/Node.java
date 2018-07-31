package com.clay;

import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcMethod;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcParam;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcService;
import org.apache.commons.codec.digest.DigestUtils;


import java.security.MessageDigest;
import java.util.ArrayList;

@JsonRpcService
public class Node {

    private ArrayList<String> pendingTransactions = new ArrayList<>();
    private Blockchain blockchain;
    private MessageDigest digest;
    private Wallet wallet;

    public Node(Blockchain blockchain, Wallet wallet){
        this.blockchain = blockchain;
	this.wallet = wallet;
        try {
            this.digest = MessageDigest.getInstance("SHA-256");
        } catch (Exception e) {
            System.out.print(e.getLocalizedMessage());
        }
    }

    public void mine(int difficulty){
        String check = new String(new char[difficulty]).replace("\0", "0");
        while(pendingTransactions.size() > 0){
            boolean minedBlock = false;
            Block block = new Block(blockchain.getLastBlock().getBlockHash(), pendingTransactions, wallet.getAddress());
            while(!minedBlock){
                String sha256hex = DigestUtils.sha256Hex(block.getBlockHead());
                if (sha256hex.startsWith(check)){
                    block.setBlockHash(sha256hex);
                    minedBlock = true;
                    System.out.print(block.getBlockHash());
                }
                block.incrementNonce();
            }
            broadcastBlock(block);

            checkForNewBlock();
        }
    }

    @JsonRpcMethod
    public void getTransactions(@JsonRpcParam("transaction") Transaction transaction){
        pendingTransactions.add(transaction.toString());
    }

    private void checkForNewBlock(){

    }
    
    private void broadcastBlock(Block block){
    }
}
