package com.reactlibrary.Args;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

public class DocumentArgs {

    public String docid;
    public String data;
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

    public DocumentArgs(String dbname, String docid, String data) throws JSONException {
        this.docid = docid;
        this.databaseName = dbname;
        this.data = data;

    }

    public DocumentArgs(String dbname, String docid) throws JSONException {
        this.docid = docid;
        this.databaseName = dbname;
    }

    // sample object
    // {"dbname":"mydb" , "docid":"12312-sae12-31", "data":{"email":"abc@gmail.com","name":"Abc"}}



}
