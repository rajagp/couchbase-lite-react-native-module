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

    //listing of actions that the plugin can do from javascript
    // private static final String actionCreateDatabase = "createDatabase";
    // private static final String actionCloseDatabase = "closeDatabase";
    private static final String actionCopyDatabase = "copyDatabase";
    //   private static final String actionAddChangeListener = "addChangeListener";
    //  private static final String actionRemoveChangeListener = "removeChangeListener";
    // private static final String actionDeleteDocument = "deleteDocument";
    //  private static final String actionGetDocument = "getDocument";
    //  private static final String actionSaveDocument = "saveDocument";
    //  private static final String actionSaveDocument = "setBlob";
    //  private static final String actionSaveDocument = "getBlob";
    private static final String actionMutableDocument = "createMutableDocument";
    private static final String actionMutableDocumentString = "mutableDocumentSetString";
    private static final String actionMutableDocumentBlob = "mutableDocumentSetBlob";
    private static final String actionEnableLogging = "enableLogging";


    protected DatabaseManager() {

    }

    public static DatabaseManager getSharedInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public static Database getDatabase() {
        return database;
    }

    public void initCouchbaseLite(Context context) {
        CouchbaseLite.init(context);
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

        config.setDirectory(dars.directory);


        if(dars.dbName.isEmpty())
        {
            response = "Missing arguments : Database Name";
            return response;
        }
        else {
            try {

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


        return "Blob Saved";
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

    private void registerForDatabaseChanges(final Callback cb)
    {

        listenerToken = database.addChangeListener(new DatabaseChangeListener() {
            @Override
            public void changed(final DatabaseChange change) {
                if (change != null) {
                    for(String docId : change.getDocumentIDs()) {
                        Document doc = database.getDocument(docId);
                        cb.invoke("dbChanged",doc.toJSON());
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