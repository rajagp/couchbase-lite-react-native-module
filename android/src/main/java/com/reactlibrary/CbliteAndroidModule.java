// CbliteAndroidModule.java

package com.reactlibrary;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.reactlibrary.util.DatabaseManager;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.MutableDocument;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class CbliteAndroidModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;
    DatabaseManager dbMgr;

    private static final String TAG = "CbliteAndroid";

    public CbliteAndroidModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        dbMgr = DatabaseManager.getSharedInstance(reactContext);
    }

    @Override
    public String getName() {
        return TAG;
    }

    @ReactMethod
    public void createDatabase(String args, @Nullable Callback onDatabaseChanged) {
        try{
            onDatabaseChanged.invoke(null, _createDatabase(args,onDatabaseChanged));
        }catch (Exception e){
            onDatabaseChanged.invoke(e.toString(), null);
        }
    }

    @ReactMethod (isBlockingSynchronousMethod = true)
    public String closeDatabase(String args) {
        return  _closeDatabase(args);
    }

    @ReactMethod
    public void getDocument(String docargs,Callback cb) {
        try{
            cb.invoke(null, _getDocument(docargs));
        }catch (Exception e){
            cb.invoke(e.toString(), null);
        }
    }

    @ReactMethod
    public void setDocument(String docObject,Callback cb) {

        try{
            if(docObject.length()>0)
                cb.invoke(null, _setdocument(docObject));
            else
                cb.invoke("Null Json Object",null);

        }catch (Exception e){
            cb.invoke(e.toString(), null);
        }
    }

    @ReactMethod
    public void getBlob(String data,Callback cb) {
        try{
            cb.invoke(null, _getBlob(data));
        }catch (Exception e){
            cb.invoke(e.toString(), null);
        }
    }


    @ReactMethod(isBlockingSynchronousMethod = true)
    public String setBlob(String type,String docObject) {
        if(!docObject.isEmpty())
            return _setBlob(type,docObject);
        else
            return "Invalid data";
    }

    private String _createDatabase(String JSONdatabaseArgs,Callback cb)
    {
        String response;

        if(!JSONdatabaseArgs.isEmpty())
        {
            response = dbMgr.openOrCreateDatabase(JSONdatabaseArgs,cb);
        }
        else
        {
            response = "No arguments passed.";
            return response;
        }

        //if exception
        //response = "There was an exception opening database. error: "+couchbaseLiteException.getMessage();

        return response;
    }

    //Args for later "list of database"
    private String _closeDatabase(String JSONdatabaseArgs)
    {
        String response;

        if(!JSONdatabaseArgs.isEmpty())
        {
            dbMgr.closeDatabase();
            response = "Database Closed";
        }
        else
        {
            response = "No arguments passed";
            return response;
        }

        return response;
    }

    private String _removeListener(String JSONdatabaseArgs)
    {
        String response;

        if(!JSONdatabaseArgs.isEmpty())
        {
            dbMgr.deregisterForDatabaseChanges();
            response = "Database Closed";
        }
        else
        {
            response = "No arguments passed";
            return response;
        }

        return response;
    }

    private String _getDocument(String docargs)
    {
        String response = null;

        String document = dbMgr.getDocument(docargs);
        if(!document.isEmpty())
        {
            response = document;
        }
        else {
            response = "Document not found";
        }

        return response;
    }

    private String _setdocument(String data)
    {
        String response = null;

        try {
            response = dbMgr.setDocument(data);
        } catch (CouchbaseLiteException couchbaseLiteException) {
            return response = "Error while updating document : "+couchbaseLiteException.getMessage();
        }


        return response;
    }

    private String _getBlob(String data)
    {
        String response = null;

        String document = dbMgr.getBlob(data);

        if(!document.isEmpty())
        {
            response = document;
        }
        else {
            response = "Blob not found";
        }

        return response;
    }

    private String _setBlob(String type,String data)
    {
        String response = null;
        response = dbMgr.setBlob(type,data);
        return response;
    }

}
