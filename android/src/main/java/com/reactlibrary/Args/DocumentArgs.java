package com.reactlibrary.Args;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

public class DocumentArgs {

    public String docid;
    public JSONObject data;
    public String databaseName;

    public String getDocid() {
        return docid;
    }

    public JSONObject getData() {
        return data;
    }

    public String getDatabaseName() {
        return databaseName;
    }


    public DocumentArgs(String dbname,String docid,JSONObject data) throws JSONException {
        this.docid = docid;
        this.databaseName = dbname;
        this.data  = data;

    }
    public DocumentArgs(String dbname,String docid) throws JSONException {
        this.docid = docid;
        this.databaseName = dbname;
    }

    // sample object
    // {"dbname":"mydb" , "docid":"12312-sae12-31", "data":{"email":"abc@gmail.com","name":"Abc"}}

    public DocumentArgs(String docArgsJSON) throws JSONException
    {
        JSONObject docArgs;

        if(!docArgsJSON.isEmpty())
        {
            docArgs  = new JSONObject(docArgsJSON);


            if(docArgs.has("dbName"))
            {
                databaseName = docArgs.getString("dbName");
            }

            if(docArgs.has("data"))
            {
                data = docArgs.getJSONObject("data");
            }

            if(docArgs.has("docid"))
            {
                docid = docArgs.getString("docid");
            }

        }

    }


}
