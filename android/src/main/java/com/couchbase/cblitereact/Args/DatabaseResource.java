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
    private ArrayList<Replicator> replicator;
    private ListenerToken replicatorChangeListenerToken;
    private String replicatorChangeListenerJSFunction;

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

    public Replicator getReplicator(int i) {
        return replicator.get(i);
    }

    public int setReplicator(Replicator replicator) {
        this.replicator.add(replicator);
        return this.replicator.indexOf(replicator);
    }

    public ListenerToken getReplicatorChangeListenerToken() {
        return replicatorChangeListenerToken;
    }

    public void setReplicatorChangeListenerToken(ListenerToken replicatorChangeListenerToken) {
        this.replicatorChangeListenerToken = replicatorChangeListenerToken;
    }

    public String getReplicatorChangeListenerJSFunction() {
        return replicatorChangeListenerJSFunction;
    }

    public void setReplicatorChangeListenerJSFunction(String replicatorChangeListenerJSFunction) {
        this.replicatorChangeListenerJSFunction = replicatorChangeListenerJSFunction;
    }


}
