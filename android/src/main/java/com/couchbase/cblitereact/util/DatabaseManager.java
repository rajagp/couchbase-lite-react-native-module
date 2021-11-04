package com.couchbase.cblitereact.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.couchbase.lite.BasicAuthenticator;
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
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.couchbase.lite.Query;
import com.couchbase.lite.QueryChange;
import com.couchbase.lite.QueryChangeListener;
import com.couchbase.lite.Replicator;
import com.couchbase.lite.ReplicatorChange;
import com.couchbase.lite.ReplicatorChangeListener;
import com.couchbase.lite.ReplicatorConfiguration;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SessionAuthenticator;
import com.couchbase.lite.URLEndpoint;
import com.couchbase.lite.ValueIndexConfiguration;
import com.couchbase.lite.internal.utils.JSONUtils;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;

import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.couchbase.cblitereact.Args.*;
import com.couchbase.cblitereact.strings.*;

public class DatabaseManager {

    private static Map<String, DatabaseResource> databases;
    private static Map<Integer, QueryListenerResource> queryResources;

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
        queryResources = new HashMap<>();
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

    public String databaseExists(DatabaseArgs args) {

        String response;
        DatabaseConfiguration dbConfig = getDatabaseConfig(args);
        String dbName = args.getDbName();

        if (dbName != null && dbConfig != null) {

            boolean exists = Database.exists(dbName, new File(dbConfig.getDirectory()));

            if (exists) {
                return responseStrings.DBExists;
            } else {
                return responseStrings.DBNotExists;
            }

        } else {
            return responseStrings.ErrorCode;
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
        if (dbResource.getListenerToken() == null) {
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
                                changeDocMap.putString(doc.getId(), doc.toJSON());
                                hasmodified = true;
                            } else {
                                Log.i("DatabaseChangeEvent", "Document: " + doc.getId() + " was deleted");
                                hasdeleted = true;
                                deletedDocMap.putString(doc.getId(), doc.toJSON());
                            }
                        }


                        if (hasmodified) {
                            finalmap.putMap("Modified", changeDocMap);
                        }

                        if (hasdeleted) {
                            finalmap.putMap("Deleted", deletedDocMap);
                        }


                        context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(jsListener, finalmap);

                    }

                }
            });

            dbResource.setListenerToken(listenerToken);
        } else {
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
        if (dbResource.getListenerToken() != null) {
            db.removeChangeListener(dbResource.getListenerToken());
            databases.get(dbname).setListenerToken(null);
        } else {
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

            if (ignoreAccents != null) {
                indexConfig.ignoreAccents(ignoreAccents);
            }
            if (language != null & language.isEmpty()) {
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

    public String queryDb(QueryArgs args) {

        String database = args.getDbName();
        String queryString = args.getQuery();

        if (!databases.containsKey(database)) {
            return responseStrings.DBnotfound;
        }

        DatabaseResource dbResource = databases.get(database);
        Database db = dbResource.getDatabase();
        Query query = db.createQuery(queryString);
        ResultSet rows = null;
        try {
            rows = query.execute();
        } catch (CouchbaseLiteException e) {
            return responseStrings.ExceptionInvalidQuery;
        }
        Result row;
        JSONArray json = new JSONArray();
        while ((row = rows.next()) != null) {
            JSONObject rowObject = null;
            try {
                rowObject = new JSONObject(row.toJSON());
            } catch (JSONException e) {
                return responseStrings.invaliddata;
            }
            json.put(rowObject);
        }

        return json.toString();

    }

    public String registerForQueryChanges(String dbname, String query, final String jsListener) {

        if (!databases.isEmpty()) {
            String dbName = dbname;
            String callbackFn = jsListener;

            DatabaseResource dbr = databases.get(dbName);
            if (dbr != null) {
                Database db = dbr.getDatabase();
                Query newQuery = db.createQuery(query);
                try {
                    if (queryResources.get(newQuery.explain().hashCode()) != null && queryResources.get(newQuery.explain().hashCode()).getQueryChangeListenerToken() != null) {
                        return responseStrings.QueryListenerExists;
                    } else {
                        QueryListenerResource qlr = new QueryListenerResource();
                        qlr.setQuery(newQuery);
                        qlr.setQueryChangeListenerJSFunction(callbackFn);

                        ListenerToken queryListenerToken = newQuery.addChangeListener(new QueryChangeListener() {
                            @Override
                            public void changed(@NonNull QueryChange change) {
                                QueryListenerResource qr = null;
                                try {
                                    qr = queryResources.get(change.getQuery().explain().hashCode());
                                    String jsCallback = qr.getQueryChangeListenerJSFunction();

                                    if (change.getError() != null) {
                                        JSONObject listenerResults = new JSONObject();
                                        listenerResults.put("message", change.getError().getMessage());

                                        if (jsCallback != null && !jsCallback.isEmpty()) {
                                            String params = listenerResults.toString();
                                            context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(jsCallback, params);
                                        }
                                    } else {
                                        Result row;
                                        JSONArray json = new JSONArray();

                                        if (change.getResults() != null) {
                                            while ((row = change.getResults().next()) != null) {
                                                JSONObject rowObject = new JSONObject(row.toJSON());
                                                json.put(rowObject);
                                            }
                                        }

                                        if (jsCallback != null && !jsCallback.isEmpty()) {
                                            String params = json.toString();
                                            context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(jsCallback, params);
                                        }
                                    }
                                } catch (CouchbaseLiteException | JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                        qlr.setQueryChangeListenerToken(queryListenerToken);
                        queryResources.put(newQuery.explain().hashCode(), qlr);
                    }
                } catch (CouchbaseLiteException e) {
                    e.printStackTrace();
                    return responseStrings.ExceptionInvalidQuery;
                }
            } else {
                return responseStrings.DBNotExists;
            }
        } else {
            return responseStrings.DBNotExists;
        }
        return responseStrings.SuccessCode;
    }

    public String deregisterForQueryChanges(String dbname, String queryString) {

        if (queryResources.isEmpty()) {
            return responseStrings.DBorQueryNotListenerExists;
        } else {
            try {
                DatabaseResource dbr = databases.get(dbname);

                if (dbr != null) {
                    Database database = dbr.getDatabase();
                    Query query = database.createQuery(queryString);
                    QueryListenerResource qlr = queryResources.get(query.explain().hashCode());

                    if (qlr != null) {
                        ListenerToken token = qlr.getQueryChangeListenerToken();

                        if (token != null) {
                            qlr.getQuery().removeChangeListener(token);
                            qlr.setQueryChangeListenerToken(null);
                        } else {
                            return responseStrings.QueryNotListenerExists;
                        }
                    } else {
                        return responseStrings.QueryResourceNotExists;
                    }

                } else {
                    return responseStrings.DBNotExists;
                }
            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
            }

        }
        return responseStrings.SuccessCode;

    }


    public String enableLogging(String domain,String loglevel) {

        try {
            if(domain==null||domain.isEmpty())
                Database.log.getConsole().setDomains(LogDomain.ALL_DOMAINS);
            else
                Database.log.getConsole().setDomains(LogDomain.valueOf(domain));

            if(loglevel==null||loglevel.isEmpty())
                Database.log.getConsole().setLevel(LogLevel.DEBUG);
            else
                Database.log.getConsole().setLevel(LogLevel.valueOf(loglevel));


            return responseStrings.SuccessCode;

        } catch (Exception exception) {
            return responseStrings.Exception + exception.getMessage();
        }
    }


    public ReplicatorConfiguration getReplicatorConfig(Database database, ReadableMap repConfig) {


        ReplicatorConfiguration config = null;
        ReadableMap dictionary = repConfig;


        try {
            String dbName = dictionary.hasKey("databaseName") ? dictionary.getString("databaseName").toLowerCase() : null;
            String targetUrl = dictionary.hasKey("target") ? dictionary.getString("target") : null;


            URI url = new URI(targetUrl);

            if (database.getName().equals(dbName)) {
                config = new ReplicatorConfiguration(database, new URLEndpoint(url));

                if (dictionary.hasKey("continuous")) {
                    config.setContinuous(dictionary.getBoolean("continuous"));
                }

                if (dictionary.hasKey("headers")) {

                    ReadableArray headersArr = dictionary.getArray("headers");

                    if (headersArr.size() > 0) {
                        Map<String, String> headerMap = new HashMap<>();
                        for (int i = 0; i < headersArr.size(); i++) {
                            ReadableMap obj = headersArr.getMap(i);
                            String k = obj.keySetIterator().nextKey();
                            String v = obj.getString(k);
                            headerMap.put(k, v);
                        }
                        config.setHeaders(headerMap);
                    } else {
                        config.setHeaders(null);
                    }
                }

                if (dictionary.hasKey("channels")) {

                    ReadableArray channelsArr = dictionary.getArray("channels");

                    if (channelsArr.size() > 0) {

                        List<String> channels = new ArrayList<>();
                        for (int i = 0; i < channelsArr.size(); i++) {
                            channels.add(channelsArr.getString(i));
                        }
                        config.setChannels(channels);
                    } else {
                        config.setChannels(null);
                    }
                }

                if (dictionary.hasKey("documentIds")) {

                    ReadableArray documentIdsArr = dictionary.getArray("documentIds");

                    if (documentIdsArr.size() > 0) {

                        List<String> documentIds = new ArrayList<>();
                        for (int i = 0; i < documentIdsArr.size(); i++) {
                            documentIds.add(documentIdsArr.getString(i));
                        }
                        config.setDocumentIDs(documentIds);
                    } else {
                        config.setDocumentIDs(null);
                    }
                }

                if (dictionary.hasKey("acceptOnlySelfSignedServerCertificate")) {
                    config.setAcceptOnlySelfSignedServerCertificate(dictionary.getBoolean("acceptOnlySelfSignedServerCertificate"));
                }

                if (dictionary.hasKey("pinnedServerCertificateUri")) {
                    String pinnedServerCertificateUri = dictionary.getString("pinnedServerCertificateUri");
                    byte[] pinnedServerCert = this.getPinnedCertFile(context, pinnedServerCertificateUri);
                    // Set pinned certificate.
                    config.setPinnedServerCertificate(pinnedServerCert);
                }

                if (dictionary.hasKey("heartbeat")) {
                    config.setHeartbeat(dictionary.getInt("heartbeat"));
                }

                if (dictionary.hasKey("authenticator")) {
                    ReadableMap authObj = dictionary.getMap("authenticator");

                    if (authObj.hasKey("authType") && authObj.getString("authType").equalsIgnoreCase("Basic")) {
                        String username = authObj.hasKey("username") ? authObj.getString("username") : null;
                        String password = authObj.hasKey("password") ? authObj.getString("password") : null;

                        if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
                            char[] passwordCharArray = new char[password.length()];

                            for (int i = 0; i < password.length(); i++) {
                                passwordCharArray[i] = password.charAt(i);
                            }
                            config.setAuthenticator(new BasicAuthenticator(username, passwordCharArray));
                        }
                    } else if (authObj.hasKey("authType") && authObj.getString("authType").equalsIgnoreCase("Session")) {
                        String sessionId = authObj.getString("sessionId");
                        if (!sessionId.isEmpty()) {
                            String cookieName = authObj.getString("cookieName");
                            if (!cookieName.isEmpty()) {
                                config.setAuthenticator(new SessionAuthenticator(sessionId, cookieName));
                            } else {
                                config.setAuthenticator(new SessionAuthenticator(sessionId));
                            }
                        }
                    }
                }
            }

        } catch (URISyntaxException e) {
            config = null;
            e.printStackTrace();
        }
        return config;
    }

    public String replicatorStart(String dbname, ReadableMap replicatorConfig) {

        try {

            if (databases.isEmpty()) {
                return responseStrings.DBNotExists;
            }
            String response;
            Database database;

            if (dbname == null || dbname.isEmpty()) {
                return responseStrings.MissingargsDBN;
            }
            if (!replicatorConfig.hasKey("target") || replicatorConfig.getString("target") == null || replicatorConfig.getString("target").isEmpty()) {
                return responseStrings.Missingargs + "Target Url";
            }


            DatabaseResource dbr = getDatabases().get(dbname);
            if (dbr != null) {
                database = dbr.getDatabase();
            } else {
                DatabaseArgs dbArgs = new DatabaseArgs(dbname);

                response = openOrCreateDatabase(dbArgs);

                if (response != responseStrings.SuccessCode) {
                    return responseStrings.ErrorCode + " Message : couldn't open database for replication.";
                } else {
                    DatabaseResource dbr2 = getDatabases().get(dbname);
                    database = dbr2.getDatabase();
                }
            }


            ReplicatorConfiguration replicatorConfiguration = getReplicatorConfig(database, replicatorConfig);
            String ID = dbr.setReplicator(new Replicator(replicatorConfiguration));
            dbr.getReplicator(ID).start();

            JSONObject jon = new JSONObject();
            jon.put("status",responseStrings.SuccessCode);
            jon.put("ReplicatorID",ID);
            return jon.toString();


        } catch (Exception exception) {
            return responseStrings.Exception + exception.getMessage();
        }
    }

    public String replicatorStop(String dbName,String id){
        if (databases.isEmpty()) {
            return responseStrings.DBNotExists;
        } else {
            DatabaseResource dbr = databases.get(dbName);
            if (dbr != null) {
                Replicator rp = dbr.getReplicator(id);
                if (rp != null) {
                    rp.stop();
                    dbr.removeReplicator(id);
                    return responseStrings.SuccessCode;
                }
            } else {
                return responseStrings.ReplicatorNotExists;
            }
        }
        return responseStrings.ErrorCode;
    }

    public String replicationAddChangeListener(String dbname, final String replicatorId, final String JSListener) {

        if (!databases.isEmpty()) {
            DatabaseResource dbr = databases.get(dbname);
            dbr.setReplicatorChangeListenerJSFunction(JSListener,replicatorId);

            if (dbr.getReplicatorChangeListenerToken(replicatorId) == null) {

                Replicator replicator = dbr.getReplicator(replicatorId);

                if (replicator != null) {
                    ListenerToken replicationListenerToken = replicator.addChangeListener(new ReplicatorChangeListener() {
                        @Override
                        public void changed(@NonNull ReplicatorChange change) {
                            DatabaseResource ldbr = databases.get(change.getReplicator().getConfig().getDatabase().getName());

                            try {
                                JSONObject replicatorChange = new JSONObject();
                                switch (change.getStatus().getActivityLevel()) {
                                    case BUSY:
                                        replicatorChange.put("status", "busy");
                                        break;
                                    case CONNECTING:
                                        replicatorChange.put("status", "connecting");
                                        break;
                                    case OFFLINE:
                                        replicatorChange.put("status", "offline");
                                        break;
                                    case STOPPED:
                                        replicatorChange.put("status", "stopped");
                                        break;
                                    default:
                                        replicatorChange.put("status", "idle");
                                }

                                replicatorChange.put("completed", change.getStatus().getProgress().getCompleted());
                                replicatorChange.put("total", change.getStatus().getProgress().getTotal());

                                String jsCallbackFn = ldbr.getReplicatorChangeListenerJSFunction(replicatorId);

                                if (jsCallbackFn != null && !jsCallbackFn.isEmpty()) {
                                    String params = replicatorChange.toString();
                                    context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(JSListener, params);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    dbr.setReplicatorChangeListenerToken(replicationListenerToken,replicatorId);
                } else {
                    return responseStrings.ReplicatorNotExists;
                }

            } else {
                return responseStrings.ReplicatorListenerExists;
            }

        } else {
            return responseStrings.DBNotExists;
        }

        return responseStrings.SuccessCode;
    }

    public String replicationRemoveChangeListener(String dbname,String replicatorId) {

        if (!databases.containsKey(dbname)) {
            return responseStrings.DBNotExists;
        }

        DatabaseResource dbResource = databases.get(dbname);
        Replicator rp = dbResource.getReplicator(replicatorId);

        if (dbResource.getReplicatorChangeListenerToken(replicatorId) != null) {
            rp.removeChangeListener(dbResource.getReplicatorChangeListenerToken(replicatorId));
            dbResource.setReplicatorChangeListenerToken(null,replicatorId);
        } else {
            return responseStrings.ReplicatorListenerNotExists;
        }

        return responseStrings.SuccessCode;
    }


    private byte[] getPinnedCertFile(Context context, String resource) {
        AssetManager assetManager = context.getAssets();
        InputStream is = null;
        try {
            is = assetManager.open(resource + ".cer");
            return new byte[is.available()];
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}