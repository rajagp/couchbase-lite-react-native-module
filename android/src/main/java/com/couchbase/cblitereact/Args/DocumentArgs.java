package com.couchbase.cblitereact.Args;

import com.facebook.react.bridge.ReadableMap;

import org.json.JSONException;

public class DocumentArgs {

    public String docid;
    public String data;
    public ReadableMap jsondata;
    public String databaseName;

    public String getDocid() {
        return docid;
    }

    public String getData() {
        return data;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    protected DocumentArgs() {
    }

    public DocumentArgs(String dbname, String docid, String data) {
        this.docid = docid;
        this.databaseName = dbname;
        this.data = data;

    }

    public DocumentArgs(String dbname, String docid, ReadableMap jsondata) throws JSONException {
        this.docid = docid;
        this.databaseName = dbname;
        this.jsondata = jsondata;

    }

    public DocumentArgs(String dbname, String docid) throws JSONException {
        this.docid = docid;
        this.databaseName = dbname;
    }

    // sample object
    // {"dbname":"mydb" , "docid":"12312-sae12-31", "data":{"email":"abc@gmail.com","name":"Abc"}}



}
