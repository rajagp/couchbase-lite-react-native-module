package com.reactlibrary.util;

import android.content.Context;
import android.util.Log;

import com.couchbase.lite.CouchbaseLite;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseChange;
import com.couchbase.lite.DatabaseChangeListener;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.Document;
import com.couchbase.lite.ListenerToken;
import com.couchbase.lite.MutableDocument;

public class DatabaseManager {
    private static Database database;
    private static DatabaseManager instance = null;
    private ListenerToken listenerToken;
    //public String currentUser = null;

    //listing of actions that the plugin can do from javascript
   // private static final String actionCreateDatabase = "createDatabase";
   // private static final String actionCloseDatabase = "closeDatabase";
    private static final String actionCopyDatabase = "copyDatabase";
    private static final String actionAddChangeListener = "addChangeListener";
  //  private static final String actionRemoveChangeListener = "removeChangeListener";
    private static final String actionDeleteDocument = "deleteDocument";
  //  private static final String actionGetDocument = "getDocument";
  //  private static final String actionSaveDocument = "saveDocument";
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



    public String openOrCreateDatabase(Context context, String args)
    {

        String response;

        DatabaseArgs dars = new DatabaseArgs(context,args);

        DatabaseConfiguration config = new DatabaseConfiguration();

        config.setDirectory(dars.directory);


        if(dars.dbName.isEmpty())
        {
            response = "Missing arguments : Database Name";
            return response;
        }
        else {
            try {
                // tag::createDatabase[]
                database = new Database(dars.dbName, config);
                // end::createDatabase[]
                registerForDatabaseChanges();
                response = "Database Created.";
            } catch (CouchbaseLiteException e) {

                response = "There was an exception opening database. error: " + e.getMessage();
            }

            return response;
        }

    }


    public String getDocument(String docId)
    {

        Document document = null;

        if (database != null) {
            document = database.getDocument(docId);
        }

        return document.toJSON();

    }



    public String setDocument(String docId, String JSONDoc)
    {
        MutableDocument mutableDocument = new MutableDocument(docId, JSONDoc);
        try {
            database.save(mutableDocument);
        } catch (CouchbaseLiteException ex) {
            ex.printStackTrace();

        }

        return docId;
    }



    private void registerForDatabaseChanges()
    {
        // tag::addDatabaseChangelistener[]
        // Add database change listener
        listenerToken = database.addChangeListener(new DatabaseChangeListener() {
            @Override
            public void changed(final DatabaseChange change) {
                if (change != null) {
                    for(String docId : change.getDocumentIDs()) {
                        Document doc = database.getDocument(docId);
                        if (doc != null) {
                            Log.i("DatabaseChangeEvent", "Document was added/updated");
                        }
                        else {

                            Log.i("DatabaseChangeEvent","Document was deleted");
                        }
                    }
                }
            }
        });
        // end::addDatabaseChangelistener[]
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