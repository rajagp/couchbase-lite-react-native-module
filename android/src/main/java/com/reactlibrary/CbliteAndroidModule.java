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

public class CbliteAndroidModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;

    public CbliteAndroidModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "CbliteAndroid";
    }

    @ReactMethod
    public void LoginUser(String username, String password, Callback cb) {
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
        System.out.println("logiinni");
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

}
