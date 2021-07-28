package com.reactlibrary.util;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

public class DocumentArgs {
    String docid;
    JSONObject data;
    String databaseName;


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
