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

import javax.annotation.Nullable;

public class CbliteAndroidModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;
    DatabaseManager dbMgr;
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
    public void createDatabase(String dbname,String directory,@Nullable String encryptionKey, @Nullable Callback onDatabaseChanged) {
        try{
            DatabaseArgs databaseArgs = null;

            if(encryptionKey==null)
            databaseArgs = new DatabaseArgs(dbname,directory);
            else
            databaseArgs = new DatabaseArgs(dbname,directory,encryptionKey);

            onDatabaseChanged.invoke(null, _createDatabase(databaseArgs));

        }catch (Exception e){
            onDatabaseChanged.invoke(responseStrings.ExceptionDB + e.getMessage(), null);
        }
    }

    @ReactMethod
    public void getDocument(String dbname,String docid,Callback cb) {
        try{
            DocumentArgs documentArgs = null;

            if(docid.isEmpty())
            {
                cb.invoke(responseStrings.MissingargsDCID, null);
            }
            else if (dbname.isEmpty())
            {
                cb.invoke(responseStrings.MissingargsDBN, null);
            }
            else {
                documentArgs = new DocumentArgs(dbname, docid);
                cb.invoke(null, _getDocument(documentArgs));
            }
        }catch (Exception e){
            cb.invoke("Error while get document : "+e.getMessage(), null);
        }
    }

    @ReactMethod
    public void setDocument(String dbname,String docid,String data,Callback cb) {
        try{
            DocumentArgs documentArgs = null;

            if(dbname.isEmpty())
            {
                cb.invoke(responseStrings.MissingargsDBN, null);
            }
            else if(docid.isEmpty())
            {
                cb.invoke(responseStrings.MissingargsDCID, null);
            }
            else if(data.isEmpty())
            {
                cb.invoke(responseStrings.MissingargsDCData, null);
            }
            else {
                try {
                    documentArgs = new DocumentArgs(dbname, docid, data);
                    cb.invoke(null, _setdocument(documentArgs));
                }catch (JSONException exception)
                {
                    cb.invoke(responseStrings.invalidargsDCData, null);
                }

            }
        }catch (Exception e){
            cb.invoke(responseStrings.ExceptionDOCGet+e.getMessage(), null);
        }
    }

    @ReactMethod (isBlockingSynchronousMethod = true)
    public String closeDatabase(String dbname) {
        return  _closeDatabase(dbname);
    }

    @ReactMethod
    public void getBlob(String data,Callback cb) {
        try{
            cb.invoke(null, _getBlob(data));
        }catch (Exception e){
            cb.invoke(responseStrings.ExceptionBLOBget+e.getMessage(), null);
        }
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public String setBlob(String type,String docObject) {
        return _setBlob(type, docObject);
    }




    private String _createDatabase(DatabaseArgs databaseArgs)
    {
        String response;

        if(databaseArgs.getDbName().isEmpty())
        {
            response = responseStrings.MissingargsDBN;
        }
        else if(databaseArgs.getDirectory().isEmpty())
        {
            response = responseStrings.MissingargsDBD;
        }
        else
        {
            response = dbMgr.openOrCreateDatabase(databaseArgs);
        }

        //if exception
        //response = "There was an exception opening database. error: "+couchbaseLiteException.getMessage();

        return response;
    }

    //Args for later "list of database"
    private String _closeDatabase(String dbname)
    {
        String response;

        if(!dbname.isEmpty())
        {
            dbMgr.closeDatabase();
            response = "Database Closed";
        }
        else
        {
            response = responseStrings.MissingargsDBN;
            return response;
        }

        return response;
    }

    private String _getDocument(DocumentArgs docargs)
    {
        String response = null;

        String document = dbMgr.getDocument(docargs);
        if(!document.isEmpty())
        {
            response = document;
        }
        else {
            response = responseStrings.NullDoc;
        }

        return response;
    }

    private String _setdocument(DocumentArgs data)
    {
        String response = null;
        response = dbMgr.setDocument(data);
        return response;
    }

    private String _getBlob(String data)
    {

        // Check id
        if(data.isEmpty())
        {
            return responseStrings.Missingargs+"Blob Data";
        }
        else {

            String document = dbMgr.getBlob(data);

            if (!document.isEmpty()) {
                return document;
            } else {
                return responseStrings.Blobnotfound;
            }
        }

    }

    private String _setBlob(String type,String data)
    {
        // Check id
        if(type.isEmpty())
        {
            return responseStrings.Missingargs+"Content type";
        }
        else if(data.isEmpty())
        {
            return responseStrings.Missingargs+"Blob Data";
        }
       else {
            try {
                return dbMgr.setBlob(type, data);
            } catch (Exception exception) {

                return responseStrings.ExceptionBLOB + exception.getMessage();
            }
        }
    }

    private String _removeListener(String JSONdatabaseArgs)
    {
        String response;

        if(!JSONdatabaseArgs.isEmpty())
        {
            dbMgr.deregisterForDatabaseChanges();
            response = "Removed Listeners";
        }
        else
        {
            response = responseStrings.NoArgs;
            return response;
        }

        return response;
    }

}
