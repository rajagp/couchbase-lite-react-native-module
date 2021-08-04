package com.reactlibrary.util;

import android.content.Context;
import android.util.Base64;

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

import java.util.Map;

import com.couchbase.lite.internal.utils.JSONUtils;
import com.facebook.react.bridge.Callback;
import com.reactlibrary.Args.DatabaseArgs;
import com.reactlibrary.Args.DocumentArgs;
import com.reactlibrary.strings.ResponseStrings;

public class DatabaseManager {
    private static Database database;
    private static DatabaseManager instance = null;
    private ListenerToken listenerToken;
    //public String currentUser = null;
    private ResponseStrings responseStrings;

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


    public String openOrCreateDatabase(DatabaseArgs dars)
    {

        String response;

        DatabaseConfiguration config = new DatabaseConfiguration();

        config.setDirectory(dars.directory);

        try {
            database = new Database(dars.dbName, config);
        } catch (CouchbaseLiteException e) {
            response = responseStrings.ExceptionDB + e.getMessage();
        }

        response = responseStrings.DBCreated;

        return response;

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
            return response = responseStrings.invalidArgs;
        }



        // Check id
        if(dars.docid.isEmpty())
        {
            return response = responseStrings.MissingargsDCID;
        }



        Document document = null;

        if (database != null) {
            document = database.getDocument(dars.docid);
            database.delete();
        } else
        {
            return response = responseStrings.DBnotfound;
        }

        return responseStrings.DocDeleted;

    }

    public String getDocument(DocumentArgs dars)
    {
        String response;

        Document document = null;
        if (database != null) {
            document = database.getDocument(dars.docid);
        }
        else
        {
            return response = responseStrings.DBnotfound;
        }

        if(document==null)
            return responseStrings.Docnotfound;
        else
            return document.toJSON();

    }

    public String setDocument(DocumentArgs dars)
    {
        MutableDocument mutableDocument = new MutableDocument(dars.docid, dars.data.toString());
        try {
            database.save(mutableDocument);
            return responseStrings.DocCreated;
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
            return responseStrings.ExceptionDOC +e.getMessage();
        }

    }


    public String setBlob(String type,String blobdata)
    {
        Blob blob = new Blob(type, Base64.decode(blobdata, Base64.DEFAULT));
        database.saveBlob(blob);
        return blob.toJSON();
    }

    public String getBlob(String blobdata)
    {

        JSONObject blob = null;

        // check Object is valid
        try {
            blob = new JSONObject(blobdata);
        } catch (JSONException exception) {
            return responseStrings.invalidblob;
        }


        Map<String, Object> keyvalue = null;
        try {
            keyvalue = JSONUtils.fromJSON(blob);
        } catch (JSONException exception) {
            exception.printStackTrace();
        }

        Blob getblob = database.getBlob(keyvalue);

        String blobres = Base64.encodeToString(getblob.getContent(), Base64.NO_WRAP);

        return blobres;
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