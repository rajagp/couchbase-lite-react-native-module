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
        dbMgr = DatabaseManager.getSharedInstance();
    }

    @Override
    public String getName() {
        return TAG;
    }

    @ReactMethod
    public void init() {
        _init();
    }

    @ReactMethod
    public void createDatabase(String args, @Nullable Callback cb) {
        try{
            cb.invoke(null, _createDatabase(args,cb));
        }catch (Exception e){
            cb.invoke(e.toString(), null);
        }
    }

    @ReactMethod
    public void closeDatabase(String args, @Nullable Callback cb) {
        try{
            cb.invoke(null, _closeDatabase(args));
        }catch (Exception e){
            cb.invoke(e.toString(), null);
        }
    }

    @ReactMethod
    public void getDocument(String id,Callback cb) {
        try{
            cb.invoke(null, _getDocument(id));
        }catch (Exception e){
            cb.invoke(e.toString(), null);
        }
    }

    @ReactMethod
    public void setDocument(String docObject,Callback cb) {

        try{

            if(!docObject.isEmpty())
                cb.invoke(null, _setdocument(docObject));
            else
                cb.invoke(null, "Invalid Json Object");

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

    @ReactMethod
    public void setBlob(String type,String docObject,Callback cb) {
        try{

            if(!docObject.isEmpty())
                cb.invoke(null, _setBlob(type,docObject));
            else
                cb.invoke("Invalid data",null );

        }catch (Exception e){
            cb.invoke(e.toString(), null);
        }
    }

    private void _init()
    {
        //initialize Couchbase Lite
        dbMgr.initCouchbaseLite(reactContext);
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

    private String _getDocument(String id)
    {
        String response = null;

        String document = dbMgr.getDocument(id);
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
