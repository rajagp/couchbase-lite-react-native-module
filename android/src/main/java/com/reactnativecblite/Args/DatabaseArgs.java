package com.reactnativecblite.Args;

import com.facebook.react.bridge.ReadableMap;

import org.json.JSONException;

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


    public DatabaseArgs(String dbName) {

        this.dbName = dbName;


    }

    public DatabaseArgs(String dbName, ReadableMap databaseConfig) {

        this.dbName = dbName;
        this.databaseConfig = databaseConfig;
        directory = databaseConfig.hasKey("Directory") ? databaseConfig.getString("Directory") : null;
        encryptionKey = databaseConfig.hasKey("encryptionKey") ? databaseConfig.getString("encryptionKey") : null;


    }


}
