package com.couchbase.cblitereact.Args;

import com.couchbase.lite.Database;
import com.couchbase.lite.ReplicatorConfiguration;
import com.facebook.react.bridge.ReadableMap;

import org.json.JSONException;

public class ReplicatorArgs {

    public String dbName;
    public ReadableMap replicatorConfig;

    protected ReplicatorArgs() {
    }


    public String getDbName() {
        return dbName;
    }

    public ReplicatorArgs(String dbName, ReadableMap repConfig) {

        this.dbName = dbName;
        this.replicatorConfig = repConfig;

    }





}