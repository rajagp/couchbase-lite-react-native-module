// CbliteAndroidModule.java

package com.reactlibrary;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.reactlibrary.strings.ResponseStrings;
import com.reactlibrary.Args.DatabaseArgs;
import com.reactlibrary.util.DatabaseManager;

import com.reactlibrary.Args.DocumentArgs;

import org.json.JSONException;
import org.json.JSONObject;

import javax.annotation.Nullable;

public class CbliteAndroidModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;
    private DatabaseManager dbMgr;
    private ResponseStrings responseStrings;
    private static final String TAG = "CbliteAndroid";


    //init
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
    public void createDatabase(String dbname, JSONObject config, Callback OnSuccessCallback, Callback OnErrorCallback) {
        try {
            DatabaseArgs databaseArgs = null;

            if(config.length()>0) {

            databaseArgs = new DatabaseArgs(dbname, config);

            String response;

            if (databaseArgs.getDbName().isEmpty()) {
                OnErrorCallback.invoke(responseStrings.MissingargsDBN);
            } else if (databaseArgs.getDirectory().isEmpty()) {
                OnErrorCallback.invoke(responseStrings.MissingargsDBD);
            } else {
                OnSuccessCallback.invoke(dbMgr.openOrCreateDatabase(databaseArgs));
            }


            }else
            {
                OnErrorCallback.invoke(responseStrings.Missingargs +"Database Configuration");
            }

        } catch (Exception e) {
            OnErrorCallback.invoke(responseStrings.ExceptionDB + e.getMessage());
        }
    }

    @ReactMethod
    public void getDocument(String dbname, String docid, Callback OnSuccessCallback, Callback OnErrorCallback) {
        try {
            DocumentArgs documentArgs = null;

            if (docid.isEmpty()) {
                OnErrorCallback.invoke(responseStrings.MissingargsDCID);
            } else if (dbname.isEmpty()) {
                OnErrorCallback.invoke(responseStrings.MissingargsDBN);
            } else {
                documentArgs = new DocumentArgs(dbname, docid);

                String document = dbMgr.getDocument(documentArgs);
                if (!document.isEmpty()) {
                    OnSuccessCallback.invoke(document);
                } else {
                    OnErrorCallback.invoke(responseStrings.NullDoc);
                }

            }
        } catch (Exception e) {
            OnErrorCallback.invoke(responseStrings.ExceptionDOCGet + e.getMessage());
        }
    }

    @ReactMethod
    public void setDocument(String dbname, String docid, JSONObject data, Callback OnSuccessCallback, Callback OnErrorCallback) {
        try {
            DocumentArgs documentArgs = null;

            if (dbname.isEmpty()) {
                OnErrorCallback.invoke(responseStrings.MissingargsDBN);
            } else if (docid.isEmpty()) {
                OnErrorCallback.invoke(responseStrings.MissingargsDCID);
            } else if (data.length()<1) {
                OnErrorCallback.invoke(responseStrings.MissingargsDCData);
            } else {
                try {
                    documentArgs = new DocumentArgs(dbname, docid, data);
                    String response = dbMgr.setDocument(documentArgs);

                    if (response.equals(responseStrings.DocCreated))
                        OnSuccessCallback.invoke(response);
                    else
                        OnErrorCallback.invoke(response);

                } catch (JSONException exception) {
                    OnErrorCallback.invoke(responseStrings.invalidargsDCData);
                }

            }
        } catch (Exception e) {
            OnErrorCallback.invoke(responseStrings.ExceptionDOCGet + e.getMessage());
        }
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public String closeDatabase(String dbname) {
        String response;

        if (!dbname.isEmpty()) {
            dbMgr.closeDatabase();
            response = "Database Closed";
        } else {
            response = responseStrings.MissingargsDBN;
            return response;
        }

        return response;
    }

    @ReactMethod
    public void getBlob(String data, Callback OnSuccessCallback, Callback OnErrorCallback) {
        try {
            String response;
            if (data.isEmpty()) {
                OnErrorCallback.invoke(responseStrings.Missingargs + "Blob Data");
            } else {

                String document = dbMgr.getBlob(data);

                if (!document.isEmpty()) {
                    OnSuccessCallback.invoke(document);
                } else {
                    OnErrorCallback.invoke(responseStrings.Blobnotfound);
                }
            }


        } catch (Exception e) {
            OnErrorCallback.invoke(responseStrings.ExceptionBLOBget + e.getMessage());
        }
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public String setBlob(String type, String docObject) {
        if (type.isEmpty()) {
            return responseStrings.Missingargs + "Content type";
        } else if (docObject.isEmpty()) {
            return responseStrings.Missingargs + "Blob Data";
        } else {
            try {
                return dbMgr.setBlob(type, docObject);
            } catch (Exception exception) {

                return responseStrings.ExceptionBLOB + exception.getMessage();
            }
        }
    }


}
