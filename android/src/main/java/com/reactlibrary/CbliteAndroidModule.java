// CbliteAndroidModule.java

package com.reactlibrary;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.reactlibrary.util.DatabaseManager;

import org.json.JSONException;
import org.json.JSONObject;

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
            cb.invoke(null, _createDatabase(args));
        }catch (Exception e){
            cb.invoke(e.toString(), null);
        }
    }

    @ReactMethod
    public void closeDatabase(String args, @Nullable Callback cb) {
        try{
            cb.invoke(null, _closeDatabase("nan"));
        }catch (Exception e){
            cb.invoke(e.toString(), null);
        }
    }

    @ReactMethod
    public void getDocument(String id, Callback cb) {
        try{
            cb.invoke(null, _getDocument(id));
        }catch (Exception e){
            cb.invoke(e.toString(), null);
        }
    }

    @ReactMethod
    public void setDocument(String id, String JsonObject, Callback cb) {

        Boolean objectIsValid;

        try{
            JSONObject checkobject = new JSONObject(JsonObject);
            objectIsValid = true;
        }catch (JSONException exception){
            objectIsValid = false;
        }

        try{
            if(objectIsValid)
                cb.invoke(null, _setdocument(id,JsonObject));
            else
                cb.invoke(null, "Invalid Json Object");

        }catch (Exception e){
            cb.invoke(e.toString(), null);
        }
    }

    private void _init()
    {
        //initialize Couchbase Lite
        dbMgr.initCouchbaseLite(reactContext);
    }

    private String _createDatabase(String JSONdatabaseArgs)
    {
        String response;

        if(!JSONdatabaseArgs.isEmpty())
        {
            response = dbMgr.openOrCreateDatabase(reactContext,JSONdatabaseArgs);
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
           response = dbMgr.getDocument(id);
        }
        else {
            response = "Document not found";
        }

        return response;
    }

    private String _setdocument(String id, String data)
    {
        String response = null;

        response = dbMgr.setDocument(id,data);


        return response;
    }

}
