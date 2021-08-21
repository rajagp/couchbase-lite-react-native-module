// CbliteAndroidModule.java

package com.reactlibrary;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.PromiseImpl;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableMap;
import com.reactlibrary.strings.*;
import com.reactlibrary.Args.*;
import com.reactlibrary.util.DatabaseManager;


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
    public void createDatabase(String dbname, @Nullable ReadableMap config, Callback OnSuccessCallback, Callback OnErrorCallback) {
        try {

            String response;
            DatabaseArgs databaseArgs = null;

            if (dbname != null) {

                if(config!=null) { databaseArgs = new DatabaseArgs(dbname, config);}
                else { databaseArgs = new DatabaseArgs(dbname);}
                response = dbMgr.openOrCreateDatabase(databaseArgs);

                if (response.equals(responseStrings.DBExists) || response.equals(responseStrings.SuccessCode)) {
                    OnSuccessCallback.invoke(response);
                } else {
                    OnErrorCallback.invoke(response);
                }

            } else {
                OnErrorCallback.invoke(responseStrings.MissingargsDBN);
            }

        } catch (Exception e) {
            OnErrorCallback.invoke(responseStrings.ExceptionDB + e.getMessage());
        }
    }

    @ReactMethod
    public void copyDatabase(String currentdbname, String newDBName,@Nullable ReadableMap currentConfig,@Nullable ReadableMap newConfig, Callback OnSuccessCallback, Callback OnErrorCallback) {
        try {

            String response;
            DatabaseArgs currentDatabaseArgs = new DatabaseArgs(currentdbname, currentConfig);;
            DatabaseArgs newDatabaseArgs = new DatabaseArgs(newDBName, newConfig);;

            if(currentdbname==null||currentdbname.isEmpty())
            {
                OnErrorCallback.invoke(responseStrings.Missingargs+"Current Database Name");
            }
            else if(newDBName==null||newDBName.isEmpty())
            {
                OnErrorCallback.invoke(responseStrings.Missingargs+"New Database Name");
            }
            else if(currentDatabaseArgs.databaseConfig==null)
            {
                OnErrorCallback.invoke(responseStrings.Missingargs+"Current Database Config");
            }
            else if(newDatabaseArgs.databaseConfig==null)
            {
                OnErrorCallback.invoke(responseStrings.Missingargs+"New Database Config");
            }
            else {
                response = dbMgr.copyDatabase(currentDatabaseArgs,newDatabaseArgs);

                if(response.equals(responseStrings.SuccessCode))
                {
                    OnSuccessCallback.invoke(response);
                }
                else
                {
                    OnErrorCallback.invoke(response);
                }
            }



        } catch (Exception e) {
            OnErrorCallback.invoke(responseStrings.ExceptionDBcopy + e.getMessage());
        }
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public String closeDatabase(String dbname) {

        if (dbname!=null&&!dbname.isEmpty()) {
            return dbMgr.closeDatabase(dbname);
        } else {
            return responseStrings.MissingargsDBN;
        }

    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public String deleteDatabase(String dbname) {

        if (dbname!=null&&!dbname.isEmpty()) {
            return dbMgr.deleteDatabase(dbname);
        } else {
            return responseStrings.MissingargsDBN;
        }

    }



    @ReactMethod
    public void getDocument(String dbname, String docid, Callback OnSuccessCallback, Callback OnErrorCallback) {
        try {
            DocumentArgs documentArgs = null;

            if (docid==null||docid.isEmpty()) {
                OnErrorCallback.invoke(responseStrings.MissingargsDCID);
            } else if (dbname==null||dbname.isEmpty()) {
                OnErrorCallback.invoke(responseStrings.MissingargsDBN);
            } else {
                documentArgs = new DocumentArgs(dbname, docid);

                String documentResponse = dbMgr.getDocument(documentArgs);
                if (!documentResponse.isEmpty()) {

                    if(!documentResponse.equals(responseStrings.DBnotfound)&&!documentResponse.equals(responseStrings.Docnotfound))
                        OnSuccessCallback.invoke(documentResponse);
                    else
                        OnErrorCallback.invoke(documentResponse);


                } else {
                    OnErrorCallback.invoke(responseStrings.NullDoc);
                }

            }
        } catch (Exception e) {
            OnErrorCallback.invoke(responseStrings.ExceptionDOCGet + e.getMessage());
        }
    }

    @ReactMethod
    public void setDocument(String dbname, String docid, String data, Callback OnSuccessCallback, Callback OnErrorCallback) {
        try {
            DocumentArgs documentArgs = null;

            if (dbname==null||dbname.isEmpty()) {
                OnErrorCallback.invoke(responseStrings.MissingargsDBN);
            } else if (docid==null||docid.isEmpty()) {
                OnErrorCallback.invoke(responseStrings.MissingargsDCID);
            } else if (data==null||data.isEmpty()) {
                OnErrorCallback.invoke(responseStrings.MissingargsDCData);
            } else {
                try {
                    documentArgs = new DocumentArgs(dbname, docid, data);
                    String response = dbMgr.setDocument(documentArgs);

                    if (response.equals(responseStrings.SuccessCode))
                        OnSuccessCallback.invoke(response);
                    else
                        OnErrorCallback.invoke(response);

                } catch (JSONException exception) {
                    OnErrorCallback.invoke(responseStrings.invalidargsDCData);
                }

            }
        } catch (Exception e) {
            OnErrorCallback.invoke(responseStrings.ExceptionDOC + e.getMessage());
        }
    }

    @ReactMethod
    public void deleteDocument(String dbname, String docid, Callback OnSuccessCallback, Callback OnErrorCallback) {
        try {
            DocumentArgs documentArgs = null;

            if (docid==null||docid.isEmpty()) {
                OnErrorCallback.invoke(responseStrings.MissingargsDCID);
            } else if (dbname==null||dbname.isEmpty()) {
                OnErrorCallback.invoke(responseStrings.MissingargsDBN);
            } else {
                documentArgs = new DocumentArgs(dbname, docid);

                String documentResponse = dbMgr.deleteDocument(documentArgs);
                if (!documentResponse.isEmpty()) {

                    if(documentResponse.equals(responseStrings.SuccessCode))
                        OnSuccessCallback.invoke(documentResponse);
                    else
                        OnErrorCallback.invoke(documentResponse);


                } else {
                    OnErrorCallback.invoke(responseStrings.NullDoc);
                }

            }
        } catch (Exception e) {
            OnErrorCallback.invoke(responseStrings.Exception + e.getMessage());
        }
    }


    @ReactMethod
    public void getBlob(String dbname,String data, Callback OnSuccessCallback, Callback OnErrorCallback) {
        try {

            if(dbname==null||dbname.isEmpty())
            {
                OnErrorCallback.invoke(responseStrings.MissingargsDBN);
            }
            else
            {

                if (data==null||data.isEmpty()) {
                    OnErrorCallback.invoke(responseStrings.Missingargs + "Blob Data");
                } else {

                    String Blobresponse = dbMgr.getBlob(dbname,data);

                    if (!Blobresponse.equals(responseStrings.invalidblob)&&!Blobresponse.equals(responseStrings.invalidblob)) {
                        OnSuccessCallback.invoke(Blobresponse);
                    } else {
                        OnErrorCallback.invoke(Blobresponse);
                    }
                }


            }


        } catch (Exception e) {
            OnErrorCallback.invoke(responseStrings.ExceptionBLOBget + e.getMessage());
        }
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public String setBlob(String dbname,String type, String docObject) {

        if(dbname==null||dbname.isEmpty())
        {
            return responseStrings.MissingargsDBN;
        }
        else
        {
            if (type.isEmpty()) {
                return responseStrings.Missingargs + "Content type";
            } else if (docObject.isEmpty()) {
                return responseStrings.Missingargs + "Blob Data";
            } else {
                try {
                    return dbMgr.setBlob(dbname,type, docObject);
                } catch (Exception exception) {
                    return responseStrings.ExceptionBLOB + exception.getMessage();
                }
            }
        }
    }

    @ReactMethod (isBlockingSynchronousMethod = true)
    private String addChangeListener(String dbname, String listener) {

        try {

            if(dbname==null||dbname.isEmpty())
            {
                return responseStrings.MissingargsDBN;
            }
            else if(listener==null||listener.isEmpty())
            {
                return responseStrings.Missingargs+"JSListener";
            }
            else
            {
                String result = dbMgr.registerForDatabaseChanges(dbname, listener);
                return result;
            }


        } catch (Exception e) {
            return responseStrings.Exception + e.getMessage();
        }

    }

    @ReactMethod (isBlockingSynchronousMethod = true)
    private String removeChangeListener(String dbname) {

        try {

            if(dbname==null||dbname.isEmpty())
            {
                return responseStrings.MissingargsDBN;
            }

            else
            {
                String result = dbMgr.deregisterForDatabaseChanges(dbname);
                return result;
            }


        } catch (Exception e) {
            return responseStrings.ExceptionDOC + e.getMessage();
        }

    }


}
