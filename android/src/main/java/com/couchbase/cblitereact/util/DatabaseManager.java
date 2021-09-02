package com.couchbase.cblitereact.util;

import android.os.Bundle;
import android.text.TextUtils;
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
import com.couchbase.lite.FullTextIndexConfiguration;
import com.couchbase.lite.ListenerToken;
import com.couchbase.lite.LogDomain;
import com.couchbase.lite.LogLevel;
import com.couchbase.lite.MutableDocument;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.couchbase.lite.Query;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.ValueIndexConfiguration;
import com.couchbase.lite.internal.utils.JSONUtils;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
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

    private static Map<String, DatabaseResource> getDatabases() {
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


    public String openOrCreateDatabase(DatabaseArgs args) {

        String response;
        try {
            DatabaseConfiguration dbConfig = getDatabaseConfig(args);

            if (databases.containsKey(args.dbName)) {
                return responseStrings.DBExists;
            }

            if (args.dbName != null && args.databaseConfig != null) {

                Database database = null;
                database = new Database(args.dbName, dbConfig);
                databases.put(args.dbName, new DatabaseResource(database, dbConfig));
                return responseStrings.SuccessCode;

            } else if (args.dbName != null) {

                Database database = new Database(args.dbName);
                databases.put(args.dbName, new DatabaseResource(database));
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
        DocumentArgs args = null;

        //Check args object

        try {
            args = new DocumentArgs(documentArgs.databaseName, documentArgs.docid);
        } catch (JSONException exception) {
            return response = responseStrings.invalidArgs;
        }


        // Check id
        if (args.docid.isEmpty()) {
            return response = responseStrings.MissingargsDCID;
        }


        Document document = null;
        try {
            if (databases.get(documentArgs.databaseName) != null) {
                Database db = databases.get(documentArgs.databaseName).getDatabase();
                document = db.getDocument(args.docid);
                db.delete(document);
                return responseStrings.SuccessCode;
            } else {
                return responseStrings.DBnotfound;
            }
        } catch (CouchbaseLiteException ex) {
            return responseStrings.Exception + ex;
        }

    }

    public String getDocument(DocumentArgs args) throws Exception {

        String response;
        String dbName = args.getDatabaseName();
        String docId = args.getDocid();

        if (!databases.containsKey(dbName)) {
            return responseStrings.DBnotfound;
        }


        Document document = null;

        if (databases.get(dbName) != null) {
            document = databases.get(dbName).getDatabase().getDocument(args.docid);
        } else {
            return responseStrings.DBnotfound;
        }

        if (document == null)
            return responseStrings.Docnotfound;
        else
            return document.toJSON();

    }

    public String setDocument(DocumentArgs args) {
        MutableDocument mutableDocument = new MutableDocument(args.docid, args.data);
        try {
            if (databases.get(args.getDatabaseName()) != null) {
                Database database = databases.get(args.getDatabaseName()).getDatabase();
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


    public String registerForDatabaseChanges(String dbname, final String jsListener) {

        if (!databases.containsKey(dbname)) {
            return responseStrings.DBnotfound;
        }

        DatabaseResource dbResource = databases.get(dbname);
        final Database db = dbResource.getDatabase();
        if(dbResource.getListenerToken()==null){
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


                    context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(jsListener, finalmap);

                }

            }
        });

        dbResource.setListenerToken(listenerToken);
        }
        else {
            return responseStrings.listenerTokenExists;
        }

        return responseStrings.SuccessCode;
    }

    public String deregisterForDatabaseChanges(String dbname) {

        if (!databases.containsKey(dbname)) {
            return responseStrings.DBnotfound;
        }

        DatabaseResource dbResource = databases.get(dbname);
        final Database db = dbResource.getDatabase();
        if(dbResource.getListenerToken()!=null){
            db.removeChangeListener(dbResource.getListenerToken());
            databases.get(dbname).setListenerToken(null);
        }
        else
        {
            return responseStrings.listenerTokenNotExists;
        }

        return responseStrings.SuccessCode;
    }


    public String createValueIndex(IndexArgs args) {

        String database = args.getDbName();
        String indexName = args.getIndexName();
        List<String> indexExpressionList = args.getIndexExpressions();

        try {

            if (!databases.containsKey(database)) {
                return responseStrings.DBnotfound;
            }
            DatabaseResource dbResource = databases.get(database);
            Database db = dbResource.getDatabase();

            String indexExpression = TextUtils.join(",", indexExpressionList);
            ValueIndexConfiguration indexConfig = new ValueIndexConfiguration(indexExpression);
            db.createIndex(indexName, indexConfig);

            return responseStrings.SuccessCode;

        } catch (CouchbaseLiteException exception) {
            return responseStrings.Exception + exception.getMessage();
        }
    }

    public String createFTSIndex(IndexArgs args) {

        String database = args.getDbName();
        String indexName = args.getIndexName();
        Boolean ignoreAccents = args.isIgnoreAccents();
        String language = args.getLanguage();
        List<String> indexExpressionList = args.getIndexExpressions();

        try {

            if (!databases.containsKey(database)) {
                return responseStrings.DBnotfound;
            }
            DatabaseResource dbResource = databases.get(database);
            Database db = dbResource.getDatabase();

            String indexExpressions = TextUtils.join(",", indexExpressionList);

            FullTextIndexConfiguration indexConfig = new FullTextIndexConfiguration(indexExpressions);

            if(ignoreAccents!=null)
            {
                indexConfig.ignoreAccents(ignoreAccents);
            }
            if(language!=null&language.isEmpty())
            {
                indexConfig.setLanguage(language);
            }

            db.createIndex(indexName, indexConfig);

            return responseStrings.SuccessCode;

        } catch (CouchbaseLiteException exception) {
            return responseStrings.Exception + exception.getMessage();
        }
    }

    public String deleteIndex(IndexArgs args) {

        String database = args.getDbName();
        String indexName = args.getIndexName();

       try {

            if (!databases.containsKey(database)) {
                return responseStrings.DBnotfound;
            }
           DatabaseResource dbResource = databases.get(database);
           Database db = dbResource.getDatabase();
           db.deleteIndex(indexName);

            return responseStrings.SuccessCode;

        } catch (CouchbaseLiteException exception) {
            return responseStrings.Exception + exception.getMessage();
        }
    }

    public void queryDb(QueryArgs args, Callback sucess,Callback error) {

        String database = args.getDbName();
        String queryString = args.getQuery();

       try {

            if (!databases.containsKey(database)) {
                error.invoke(responseStrings.DBnotfound);
            }

           DatabaseResource dbResource = databases.get(database);
           Database db = dbResource.getDatabase();
           Query query = db.createQuery(queryString);
           ResultSet rows = query.execute();
           Result row;
           WritableArray json = new WritableNativeArray();
           while ((row = rows.next()) != null) {
                json.pushString(row.toJSON());
           }

           sucess.invoke(json);

        } catch (Exception exception) {
           error.invoke(responseStrings.ExceptionQuery + exception.getMessage());
        }
    }


    public String enableLogging()
    {

        try {
            Database.log.getConsole().setDomains(LogDomain.ALL_DOMAINS);
            Database.log.getConsole().setLevel(LogLevel.DEBUG);

            return responseStrings.SuccessCode;

        } catch (Exception exception) {
            return responseStrings.Exception + exception.getMessage();
        }
    }



}