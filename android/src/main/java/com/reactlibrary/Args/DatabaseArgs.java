package com.reactlibrary.Args;

import android.content.Context;

import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.EncryptionKey;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableMap;

import org.json.JSONException;
import org.json.JSONObject;

public class DatabaseArgs {

    public String directory;
    public String dbName;
    public String encryptionKey;
    public ReadableMap databaseConfig;

    protected DatabaseArgs() {
    }

    public String getDirectory() {
        return directory;
    }

    public String getDbName() {
        return dbName;
    }

    public String getEncryptionKey() {
        return encryptionKey;
    }


    public DatabaseArgs(String dbname, String directory, String encryptionKey) {
        this.dbName = dbname;
        this.directory = directory;
        this.encryptionKey = encryptionKey;
    }

    public DatabaseArgs(String dbname, String directory) {
        this.dbName = dbname;
        this.directory = directory;
    }


    public DatabaseArgs(String dbName) throws JSONException {

        this.dbName = dbName;


    }

    public DatabaseArgs(String dbName, ReadableMap databaseConfig) throws JSONException {

        this.dbName = dbName;
        this.databaseConfig = databaseConfig;
        directory = databaseConfig.hasKey("Directory") ? databaseConfig.getString("Directory") : null;
        encryptionKey = databaseConfig.hasKey("encryptionKey") ? databaseConfig.getString("encryptionKey") : null;


    }


}
