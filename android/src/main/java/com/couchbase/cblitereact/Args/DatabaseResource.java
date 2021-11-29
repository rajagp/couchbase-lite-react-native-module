package com.couchbase.cblitereact.Args;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.ListenerToken;
import com.couchbase.lite.Query;
import com.couchbase.lite.Replicator;
import com.couchbase.lite.ReplicatorConfiguration;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class DatabaseResource {
    private Database database;
    private DatabaseConfiguration configuration;


    private ListenerToken listenerToken;

    //replication
    private HashMap<String,ReplicatorResource> replicators =new HashMap<>();

    //Querys
    private HashMap<Integer,QueryResource> querys =new HashMap<>();

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
        return replicators.get(replicatorID).getReplicator();
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
        if(!this.replicators.containsKey(hash))
        this.replicators.put(hash,setreplicator);

        return hash;
    }

    public void removeReplicator(String replicatorId) {
        this.replicators.remove(replicatorId);
    }

    public ListenerToken getReplicatorChangeListenerToken(String replicatorID) {
        return this.replicators.get(replicatorID).getReplicatorChangeListenerToken();
    }

    public void setReplicatorChangeListenerToken(ListenerToken replicatorChangeListenerToken,String replicatorID) {
        this.replicators.get(replicatorID).setReplicatorChangeListenerToken(replicatorChangeListenerToken);
    }

    public String getReplicatorChangeListenerJSFunction(String replicatorID) {
        return this.replicators.get(replicatorID).getReplicatorChangeListenerJSFunction();
    }

    public void setReplicatorChangeListenerJSFunction(String replicatorChangeListenerJSFunction,String replicatorID) {
        this.replicators.get(replicatorID).setReplicatorChangeListenerJSFunction(replicatorChangeListenerJSFunction);
    }

    public Query getQuery(Integer queryID) {
        if(this.querys.get(queryID)!=null)
        return this.querys.get(queryID).getQuery();
        else
        return null;
    }

    public Integer setQuery(Query query) throws CouchbaseLiteException {
        QueryResource queryResource = new QueryResource();
        queryResource.setQuery(query);

        Integer hash = query.explain().hashCode();

        if(!querys.containsKey(hash))
        this.querys.put(hash,queryResource);

        return hash;
    }

    public void removeQuery(Integer queryId) {
        this.querys.remove(queryId);
    }

    public ListenerToken getQueryChangeListenerToken(Integer queryId) {
        return querys.get(queryId).getQueryChangeListenerToken();
    }

    public void setQueryChangeListenerToken(ListenerToken QueryChangeListenerToken,Integer queryID) {
        this.querys.get(queryID).setQueryChangeListenerToken(QueryChangeListenerToken);
    }

    public String getQueryChangeListenerJSFunction(Integer QueryID) {
        return this.querys.get(QueryID).getQueryChangeListenerJSFunction();
    }

    public void setQueryChangeListenerJSFunction(String QueryChangeListenerJSFunction,Integer QueryID) {
        this.querys.get(QueryID).setQueryChangeListenerJSFunction(QueryChangeListenerJSFunction);
    }


}
