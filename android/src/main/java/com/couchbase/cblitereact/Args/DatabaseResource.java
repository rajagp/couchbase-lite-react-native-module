package com.couchbase.cblitereact.Args;

import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.ListenerToken;
import com.couchbase.lite.Replicator;

import java.util.ArrayList;

public class DatabaseResource {
    private Database database;
    private DatabaseConfiguration configuration;


    private ListenerToken listenerToken;
    //replication
    private ArrayList<ReplicatorResource> replicator = new ArrayList<>();

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

    public Replicator getReplicator(int replicatorID) {
        return replicator.get(replicatorID).getReplicator();
    }

    public int setReplicator(Replicator replicator) {
        ReplicatorResource setreplicator = new ReplicatorResource();
        setreplicator.setReplicator(replicator);
        this.replicator.add(setreplicator);
        return this.replicator.indexOf(setreplicator);
    }

    public ListenerToken getReplicatorChangeListenerToken(int replicatorID) {
        return replicator.get(replicatorID).getReplicatorChangeListenerToken();
    }

    public void setReplicatorChangeListenerToken(ListenerToken replicatorChangeListenerToken,int replicatorID) {
        this.replicator.get(replicatorID).setReplicatorChangeListenerToken(replicatorChangeListenerToken);
    }

    public String getReplicatorChangeListenerJSFunction(int replicatorID) {
        return this.replicator.get(replicatorID).getReplicatorChangeListenerJSFunction();
    }

    public void setReplicatorChangeListenerJSFunction(String replicatorChangeListenerJSFunction,int replicatorID) {
        this.replicator.get(replicatorID).setReplicatorChangeListenerJSFunction(replicatorChangeListenerJSFunction);
    }


}
