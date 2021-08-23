package com.couchbase.cblitereact.util;

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
import com.couchbase.lite.EncryptionKey;
import com.couchbase.lite.ListenerToken;
import com.couchbase.lite.MutableDocument;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.couchbase.lite.internal.utils.JSONUtils;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.couchbase.cblitereact.Args.*;
import com.couchbase.cblitereact.strings.*;

public class DatabaseManager {

    private static Map<String, DatabaseResource> databases;


    private static DatabaseManager instance = null;
    private ListenerToken listenerToken;
    private ResponseStrings responseStrings;

    protected DatabaseManager() {

    }

    public static ReactApplicationContext context;

    public static DatabaseManager getSharedInstance(ReactApplicationContext context) {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        DatabaseManager.context = context;
        databases = new HashMap<>();
        CouchbaseLite.init(context);
        return instance;
    }

    public static Map<String, DatabaseResource> getDatabases() {
        return databases;
    }

    private DatabaseConfiguration getDatabaseConfig(DatabaseArgs args) {

        String directory = args.getDirectory();
        String encryptionKey = args.getEncryptionKey();

        if ((directory == null && encryptionKey == null) || (directory.equals("") && encryptionKey.equals(""))) {
            return null;
        }

        DatabaseConfiguration dbConfig = new DatabaseConfiguration();

        if (directory != null && !directory.equals("")) {
            dbConfig.setDirectory(directory);
        }

        if (encryptionKey != null && !encryptionKey.equals("")) {
            dbConfig.setEncryptionKey(new EncryptionKey(encryptionKey));
        }

        return dbConfig;
    }


    public String openOrCreateDatabase(DatabaseArgs dars) {

        String response;
        try {
            DatabaseConfiguration dbConfig = getDatabaseConfig(dars);

            if (databases.containsKey(dars.dbName)) {
                return responseStrings.DBExists;
            }

            if (dars.dbName != null && dars.databaseConfig != null) {

                Database database = null;
                database = new Database(dars.dbName, dbConfig);
                databases.put(dars.dbName, new DatabaseResource(database, dbConfig));
                return responseStrings.SuccessCode;

            } else if (dars.dbName != null) {

                Database database = new Database(dars.dbName);
                databases.put(dars.dbName, new DatabaseResource(database));
                return responseStrings.SuccessCode;
            } else {
                return responseStrings.MissingargsDBN;
            }


        } catch (CouchbaseLiteException exception) {
            return responseStrings.ExceptionDB + exception.getMessage();
        }
    }

    public String closeDatabase(String dbName) {
        try {

            if (!databases.containsKey(dbName)) {
                return responseStrings.DBnotfound;
            }

            if (dbName != null && !dbName.equals("")) {

                DatabaseResource resource = databases.get(dbName);
                resource.getDatabase().close();
                databases.remove(dbName);
                return responseStrings.SuccessCode;
            } else {
                return responseStrings.MissingargsDBN;
            }
        } catch (CouchbaseLiteException e) {
            return responseStrings.ExceptionDBclose + e.getMessage();
        }
    }

    public String deleteDatabase(String dbName) {
        try {

            if (!databases.containsKey(dbName)) {
                return responseStrings.DBnotfound;
            }

            if (dbName != null && !dbName.equals("")) {

                DatabaseResource resource = databases.get(dbName);
                resource.getDatabase().delete();
                databases.remove(dbName);
                return responseStrings.SuccessCode;
            } else {
                return responseStrings.MissingargsDBN;
            }
        } catch (CouchbaseLiteException e) {
            return responseStrings.ExceptionDBdelete + e.getMessage();
        }
    }

    public String copyDatabase(DatabaseArgs cargs, DatabaseArgs nargs) {

        String responsecreate;
        try {

            responsecreate = this.openOrCreateDatabase(cargs);
            if (!responsecreate.equals(responseStrings.SuccessCode)) {
                return responsecreate;
            }


            if (new File(nargs.getDirectory() + "/" + nargs.getDbName() + ".cblite2").isDirectory()) {
                return responseStrings.DBExists;
            }

            String dbPath = databases.get(cargs.getDbName()).getDatabase().getPath();
            File file = new File(dbPath);
            if (file != null) {
                DatabaseConfiguration newdbConfig = getDatabaseConfig(nargs);
                Database.copy(file, nargs.getDbName(), newdbConfig);
                return responseStrings.SuccessCode;
            }
            return responseStrings.ErrorCode;


        } catch (CouchbaseLiteException exception) {
            return responseStrings.ExceptionDBcopy + exception.getMessage();
        }
    }


    public String deleteDocument(DocumentArgs documentArgs) {

        String response;
        DocumentArgs dars = null;

        //Check args object

        try {
            dars = new DocumentArgs(documentArgs.databaseName, documentArgs.docid);
        } catch (JSONException exception) {
            return response = responseStrings.invalidArgs;
        }


        // Check id
        if (dars.docid.isEmpty()) {
            return response = responseStrings.MissingargsDCID;
        }


        Document document = null;
        try {
            if (databases.get(documentArgs.databaseName) != null) {
                Database db = databases.get(documentArgs.databaseName).getDatabase();
                document = db.getDocument(dars.docid);
                db.delete(document);
                return responseStrings.SuccessCode;
            } else {
                return responseStrings.DBnotfound;
            }
        } catch (CouchbaseLiteException ex) {
            return responseStrings.Exception + ex;
        }

    }

    public String getDocument(DocumentArgs dars) throws Exception {

        String response;
        String dbName = dars.getDatabaseName();
        String docId = dars.getDocid();

        if (!databases.containsKey(dbName)) {
            return responseStrings.DBnotfound;
        }


        Document document = null;

        if (databases.get(dbName) != null) {
            document = databases.get(dbName).getDatabase().getDocument(dars.docid);
        } else {
            return responseStrings.DBnotfound;
        }

        if (document == null)
            return responseStrings.Docnotfound;
        else
            return document.toJSON();

    }

    public String setDocument(DocumentArgs dars) {
        MutableDocument mutableDocument = new MutableDocument(dars.docid, dars.data);
        try {
            if (databases.get(dars.getDatabaseName()) != null) {
                Database database = databases.get(dars.getDatabaseName()).getDatabase();
                database.save(mutableDocument);
                return responseStrings.SuccessCode;

            } else {
                return responseStrings.DBnotfound;
            }

        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
            return responseStrings.ExceptionDOC + e.getMessage();
        }

    }


    public String setBlob(String dbname, String type, String blobdata) {


        Database database;
        if (databases.get(dbname) == null) {
            return responseStrings.DBnotfound;
        } else {
            database = databases.get(dbname).getDatabase();
        }

        Blob blob = new Blob(type, Base64.decode(blobdata, Base64.DEFAULT));
        database.saveBlob(blob);
        return blob.toJSON();

    }

    public String getBlob(String dbname, String blobdata) {


        JSONObject blob = null;
        Database database;
        if (databases.get(dbname) == null) {
            return responseStrings.DBnotfound;
        } else {
            database = databases.get(dbname).getDatabase();
        }


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
    public String registerForDatabaseChanges(String dbname, final String jsListener) {


        if (!databases.containsKey(dbname)) {
            return responseStrings.DBnotfound;
        }

        DatabaseResource dbResource = databases.get(dbname);
        final Database db = dbResource.getDatabase();
        ListenerToken listenerToken = db.addChangeListener(new DatabaseChangeListener() {
            @Override
            public void changed(DatabaseChange change) {

                WritableMap changeDocMap = new WritableNativeMap();
                WritableMap deletedDocMap = new WritableNativeMap();

                WritableMap finalmap = new WritableNativeMap();

                Boolean hasmodified = false;
                Boolean hasdeleted = false;

                if (change != null) {

                    for (String docId : change.getDocumentIDs()) {
                        Document doc = db.getDocument(docId);
                        if (doc != null) {
                            Log.i("DatabaseChangeEvent", "Document: " + doc.getId() + " was modified");
                            changeDocMap.putString(doc.getId(),doc.toJSON());
                            hasmodified = true;
                        } else {
                            Log.i("DatabaseChangeEvent", "Document: " + doc.getId() + " was deleted");
                            hasdeleted = true;
                            deletedDocMap.putString(doc.getId(),doc.toJSON());
                        }
                    }


                    if(hasmodified)
                    {
                        finalmap.putMap("Modified",changeDocMap);
                    }

                    if(hasdeleted)
                    {
                        finalmap.putMap("Deleted",deletedDocMap);
                    }


                    context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("DatabaseChangeEvent", finalmap);

                }

            }
        });

        dbResource.setListenerToken(listenerToken);


        return responseStrings.SuccessCode;
    }

    public String deregisterForDatabaseChanges(String dbname) {

        if (!databases.containsKey(dbname)) {
            return responseStrings.DBnotfound;
        }

        DatabaseResource dbResource = databases.get(dbname);
        Database db = dbResource.getDatabase();

        if (dbResource.getListenerToken() != null) {
            db.removeChangeListener(dbResource.getListenerToken());
        }

        return responseStrings.SuccessCode;
    }
}