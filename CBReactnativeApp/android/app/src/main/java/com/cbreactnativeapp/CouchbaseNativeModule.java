package com.cbreactnativeapp;
import androidx.annotation.NonNull;

import com.couchbase.lite.CouchbaseLite;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.Document;
import com.couchbase.lite.MutableDocument;
import com.cbreactnativeapp.util.DatabaseManager;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.HashMap;

public class CouchbaseNativeModule extends ReactContextBaseJavaModule {

    private static ReactApplicationContext reactContext;

    @NonNull
    @Override
    public String getName() {
        return "CouchbaseNativeModule";
    }


    @ReactMethod
    public void LoginUser(String username, String password,Callback cb) {
        try{
            cb.invoke(null, mloginuser(username,password));
        }catch (Exception e){
            cb.invoke(e.toString(), null);
        }
    }

    @ReactMethod
    public void GetProfile(Callback cb) {
        try{
            cb.invoke(null, mGetUserProfile());
        }catch (Exception e){
            cb.invoke(e.toString(), null);
        }
    }

    @ReactMethod
    public void SetProfile(String JsonObject,Callback cb) {

        Boolean objectIsValid;

        try{
            JSONObject checkobject = new JSONObject(JsonObject);
            objectIsValid = true;
        }catch (JSONException exception){
            objectIsValid = false;
        }


        try{
            if(objectIsValid)
                cb.invoke(null, mSetUserProfile(JsonObject));
            else
                cb.invoke(null, "Invalid Json Object");

        }catch (Exception e){
            cb.invoke(e.toString(), null);
        }
    }


    private String mloginuser(String username, String password)
    {
            DatabaseManager dbMgr = DatabaseManager.getSharedInstance();
            dbMgr.initCouchbaseLite(reactContext);
            dbMgr.openOrCreateDatabaseForUser(reactContext, username);
            return "UserLoggedIn";
    }

    public String mGetUserProfile()
    {
        Database database = DatabaseManager.getDatabase();
        String docId = DatabaseManager.getSharedInstance().getCurrentUserDocId();
        Document document = null;

        if (database != null) {
            Map<String, Object> profile = new HashMap<>();
            profile.put("email", DatabaseManager.getSharedInstance().currentUser);
            document = database.getDocument(docId);
        }

        return document.toJSON();
    }

    public String mSetUserProfile(String profile)
    {

        String response = null;
        Database database = DatabaseManager.getDatabase();
        String docId = DatabaseManager.getSharedInstance().getCurrentUserDocId();
        MutableDocument mutableDocument = new MutableDocument(docId, profile);

        try {
            database.save(mutableDocument);
            response = "Profile Updated";
        } catch (CouchbaseLiteException ex) {
            ex.printStackTrace();
            response = ex.toString();
        }

        return response;
    }

    CouchbaseNativeModule(ReactApplicationContext context) {
        super(context);
        reactContext = context;
    }
}
