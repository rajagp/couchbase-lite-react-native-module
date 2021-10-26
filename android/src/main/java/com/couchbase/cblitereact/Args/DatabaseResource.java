package com.couchbase.cblitereact.Args;

import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.ListenerToken;
import com.couchbase.lite.Replicator;
import com.couchbase.lite.ReplicatorConfiguration;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

public class DatabaseResource {
    private Database database;
    private DatabaseConfiguration configuration;


    private ListenerToken listenerToken;

    //replication
    private HashMap<String,ReplicatorResource> replicator =new HashMap<String,ReplicatorResource>();

    public DatabaseResource(Database db, DatabaseConfiguration config) {
        this.database = db;
        this.configuration = config;

    }

    public DatabaseResource(Database db) {
        this.database = db;
    }

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public DatabaseConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(DatabaseConfiguration configuration) {
        this.configuration = configuration;
    }

    public ListenerToken getListenerToken() {
        return listenerToken;
    }

    public void setListenerToken(ListenerToken listenerToken) {
        this.listenerToken = listenerToken;
    }

    public Replicator getReplicator(String replicatorID) {
        return replicator.get(replicatorID).getReplicator();
    }

    String generateReplicatorConfigHash(Replicator replicator) throws NoSuchAlgorithmException {

        ReplicatorConfiguration replicatorConfiguration = replicator.getConfig();
        StringBuilder sbhash = new StringBuilder();
        sbhash.append(replicatorConfiguration.getDatabase().getName());
        sbhash.append(replicatorConfiguration.isContinuous());
        sbhash.append(replicatorConfiguration.getType());
        sbhash.append(replicatorConfiguration.getChannels());
        sbhash.append(replicatorConfiguration.getDocumentIDs());

        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] messageDigest = md.digest(sbhash.toString().getBytes());
        BigInteger number = new BigInteger(1, messageDigest);
        String hashtext = number.toString(16);

        return hashtext;
    }

    public String setReplicator(Replicator replicator) throws NoSuchAlgorithmException {
        ReplicatorResource setreplicator = new ReplicatorResource();
        setreplicator.setReplicator(replicator);

        String hash = generateReplicatorConfigHash(replicator);
        this.replicator.put(hash,setreplicator);
        return hash;
    }

    public ListenerToken getReplicatorChangeListenerToken(String replicatorID) {
        return replicator.get(replicatorID).getReplicatorChangeListenerToken();
    }

    public void setReplicatorChangeListenerToken(ListenerToken replicatorChangeListenerToken,String replicatorID) {
        this.replicator.get(replicatorID).setReplicatorChangeListenerToken(replicatorChangeListenerToken);
    }

    public String getReplicatorChangeListenerJSFunction(String replicatorID) {
        return this.replicator.get(replicatorID).getReplicatorChangeListenerJSFunction();
    }

    public void setReplicatorChangeListenerJSFunction(String replicatorChangeListenerJSFunction,String replicatorID) {
        this.replicator.get(replicatorID).setReplicatorChangeListenerJSFunction(replicatorChangeListenerJSFunction);
    }


}
