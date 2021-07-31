package com.reactlibrary.util;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.couchbase.lite.Blob;
import com.couchbase.lite.CouchbaseLite;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseChange;
import com.couchbase.lite.DatabaseChangeListener;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.Document;
import com.couchbase.lite.ListenerToken;
import com.couchbase.lite.MutableDocument;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.couchbase.lite.internal.utils.JSONUtils;
import com.facebook.react.bridge.Callback;

public class DatabaseManager {
    private static Database database;
    private static DatabaseManager instance = null;
    private ListenerToken listenerToken;
    //public String currentUser = null;


    protected DatabaseManager() {

    }

    public static DatabaseManager getSharedInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        CouchbaseLite.init(context);
        return instance;
    }

    public static Database getDatabase() {
        return database;
    }


    public String openOrCreateDatabase(String args,Callback cb)
    {

        String response;
        DatabaseArgs dars=null;
        try {
            dars = new DatabaseArgs(args);
        }
        catch(JSONException exception){
            return response = "Invalid Args object";
        }

        DatabaseConfiguration config = new DatabaseConfiguration();

        if(dars.dbName.isEmpty())
        {
            response = "Missing arguments : Database Name";
            return response;
        }
        else if (dars.dbName.isEmpty()) {
            response = "Missing arguments : Directory";
            return response;
        }
        else {
            try {
                config.setDirectory(dars.directory);
                database = new Database(dars.dbName, config);

                registerForDatabaseChanges(cb);
                response = "Database Created.";
            } catch (CouchbaseLiteException e) {

                response = "There was an exception opening database. error: " + e.getMessage();
            }

            return response;
        }

    }

    public String deleteDocument(String docArgs) throws CouchbaseLiteException
    {

        String response;
        DocumentArgs dars=null;

        //Check args object

        try {
            dars = new DocumentArgs(docArgs);
        }
        catch(JSONException exception){
            return response = "Invalid Args object";
        }



        // Check id
        if(dars.docid.isEmpty())
        {
            return response = "Missing args : Document Id";
        }



        Document document = null;

        if (database != null) {
            document = database.getDocument(dars.docid);
            database.delete();
        } else
        {
            return response = "Database not found";
        }

        return "Document Deleted";

    }

    public String getDocument(String docArgs)
    {
        String response;
        DocumentArgs dars=null;

        //Check args object
        try {
            dars = new DocumentArgs(docArgs);
        }
        catch(JSONException exception){
            return response = "Invalid Args object";
        }



        // Check id
        if(dars.docid.isEmpty())
        {
            return response = "Missing args : Document Id";
        }



        Document document = null;
        if (database != null) {
            document = database.getDocument(dars.docid);
        }
        else
        {
            return response = "Database not found";
        }

        if(document==null)
            return "Document is null";
        else
            return document.toJSON();

    }

    public String setDocument(String docArgs)  throws CouchbaseLiteException
    {

        String response;
        DocumentArgs dars=null;

        //Check args object

        try {
            dars = new DocumentArgs(docArgs);
        }
        catch(JSONException exception){
            return response = "Invalid Args object";
        }



        // Check id
        if(dars.docid.isEmpty())
        {
            return response = "Missing args : Document Id";
        }
        // Check data
        if(dars.data.length()<1)
        {
            return response = "Missing args : No Data";
        }


        MutableDocument mutableDocument = new MutableDocument(dars.docid, dars.data.toString());
        database.save(mutableDocument);


        return "Document Saved";
    }


    public String setBlob(String type,String imageData)
    {

        String response;

        // Check id
        if(type.isEmpty())
        {
            return response = "Missing args : Content type";
        }
        // Check data
        if(imageData.isEmpty())
        {
            return response = "Missing args : No Image Data";
        }

        Blob image = new Blob(type, Base64.decode(imageData, Base64.DEFAULT));
        database.saveBlob(image);


        return image.toJSON();
    }

    public String getBlob(String blobdata)
    {

        String response;
        JSONObject blob = null;

        // check Object is valid
        try {
            blob = new JSONObject(blobdata);
        } catch (JSONException exception) {
            return response = "Invalid Object : Blob Data";
        }


        // Check id
        if(blobdata.isEmpty())
        {
            return response = "Missing args : Blob Data";
        }

        Map<String, Object> keyvalue = null;
        try {
            keyvalue = JSONUtils.fromJSON(blob);
        } catch (JSONException exception) {
            exception.printStackTrace();
        }

        Blob getblob = database.getBlob(keyvalue);

        String imagedata = Base64.encodeToString(getblob.getContent(), Base64.NO_WRAP);

        return imagedata;
    }


    //todo on sync phase
    private void registerForDatabaseChanges(final Callback cb)
    {

        listenerToken = database.addChangeListener(new DatabaseChangeListener() {
            @Override
            public void changed(final DatabaseChange change) {
                if (change != null) {
                    for(String docId : change.getDocumentIDs()) {
                        Document doc = database.getDocument(docId);
//                        cb.invoke("dbChanged",doc.toJSON());
                    }
                }
            }
        });
    }

    public void closeDatabase()
    {
        try {
            if (database != null) {
                deregisterForDatabaseChanges();
                database.close();
                database = null;
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    public void deregisterForDatabaseChanges()
    {
        if (listenerToken != null) {
            database.removeChangeListener(listenerToken);
        }
    }

}