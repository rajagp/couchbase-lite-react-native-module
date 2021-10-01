// CbliteAndroidModule.java

package com.couchbase.cblitereact;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.couchbase.cblitereact.strings.*;
import com.couchbase.cblitereact.Args.*;
import com.couchbase.cblitereact.util.DatabaseManager;


import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    public void CreateOrOpenDatabase(String dbname, @Nullable ReadableMap config, Callback OnSuccessCallback, Callback OnErrorCallback) {
        try {

            String response;
            DatabaseArgs databaseArgs = null;

            if (dbname != null) {

                if (config != null) {
                    databaseArgs = new DatabaseArgs(dbname, config);
                } else {
                    databaseArgs = new DatabaseArgs(dbname);
                }
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
    public void copyDatabase(String currentdbname, String newDBName, @Nullable ReadableMap currentConfig, @Nullable ReadableMap newConfig, Callback OnSuccessCallback, Callback OnErrorCallback) {
        try {

            String response;
            DatabaseArgs currentDatabaseArgs = new DatabaseArgs(currentdbname, currentConfig);
            ;
            DatabaseArgs newDatabaseArgs = new DatabaseArgs(newDBName, newConfig);
            ;

            if (currentdbname == null || currentdbname.isEmpty()) {
                OnErrorCallback.invoke(responseStrings.Missingargs + "Current Database Name");
            } else if (newDBName == null || newDBName.isEmpty()) {
                OnErrorCallback.invoke(responseStrings.Missingargs + "New Database Name");
            } else if (currentDatabaseArgs.databaseConfig == null) {
                OnErrorCallback.invoke(responseStrings.Missingargs + "Current Database Config");
            } else if (newDatabaseArgs.databaseConfig == null) {
                OnErrorCallback.invoke(responseStrings.Missingargs + "New Database Config");
            } else {
                response = dbMgr.copyDatabase(currentDatabaseArgs, newDatabaseArgs);

                if (response.equals(responseStrings.SuccessCode)) {
                    OnSuccessCallback.invoke(response);
                } else {
                    OnErrorCallback.invoke(response);
                }
            }


        } catch (Exception e) {
            OnErrorCallback.invoke(responseStrings.ExceptionDBcopy + e.getMessage());
        }
    }

    @ReactMethod
    public void closeDatabase(String dbname, Callback OnSuccessCallback, Callback OnErrorCallback) {

        try {
            if (dbname != null && !dbname.isEmpty()) {
                String response = dbMgr.closeDatabase(dbname);
                if (response.equals(responseStrings.SuccessCode)) {
                    OnSuccessCallback.invoke(response);
                } else {
                    OnErrorCallback.invoke(response);
                }
            } else {
                OnErrorCallback.invoke(responseStrings.MissingargsDBN);
            }
        } catch (Exception e) {
            OnErrorCallback.invoke(responseStrings.ExceptionDBclose + e.getMessage());
        }
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public String deleteDatabase(String dbname) {
        try {
            if (dbname != null && !dbname.isEmpty()) {
                return dbMgr.deleteDatabase(dbname);
            } else {
                return responseStrings.MissingargsDBN;
            }
        } catch (Exception e) {
            return (responseStrings.ExceptionDBdelete + e.getMessage());
        }
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public String databaseExists(String dbname, ReadableMap config) {
        try {
            if (dbname != null) {
                DatabaseArgs databaseArgs = null;
                if (config != null) {
                    databaseArgs = new DatabaseArgs(dbname, config);
                } else {
                    databaseArgs = new DatabaseArgs(dbname);
                }

                return dbMgr.databaseExists(databaseArgs);

            } else {
                return responseStrings.MissingargsDBN;
            }
        } catch (Exception e) {
            return (responseStrings.ExceptionDBdelete + e.getMessage());
        }
    }

    @ReactMethod
    public void getDocument(String dbname, String docid, Callback OnSuccessCallback, Callback OnErrorCallback) {
        try {


            DocumentArgs documentArgs = null;

            if (docid == null || docid.isEmpty()) {
                OnErrorCallback.invoke(responseStrings.MissingargsDCID);
            } else if (dbname == null || dbname.isEmpty()) {
                OnErrorCallback.invoke(responseStrings.MissingargsDBN);
            } else {
                documentArgs = new DocumentArgs(dbname, docid);

                String documentResponse = dbMgr.getDocument(documentArgs);
                if (!documentResponse.isEmpty()) {

                    if (!documentResponse.equals(responseStrings.DBnotfound) && !documentResponse.equals(responseStrings.Docnotfound))
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

            if (dbname == null || dbname.isEmpty()) {
                OnErrorCallback.invoke(responseStrings.MissingargsDBN);
            } else if (docid == null || docid.isEmpty()) {
                OnErrorCallback.invoke(responseStrings.MissingargsDCID);
            } else if (data == null || data.isEmpty()) {
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

            if (docid == null || docid.isEmpty()) {
                OnErrorCallback.invoke(responseStrings.MissingargsDCID);
            } else if (dbname == null || dbname.isEmpty()) {
                OnErrorCallback.invoke(responseStrings.MissingargsDBN);
            } else {
                documentArgs = new DocumentArgs(dbname, docid);

                String documentResponse = dbMgr.deleteDocument(documentArgs);
                if (!documentResponse.isEmpty()) {

                    if (documentResponse.equals(responseStrings.SuccessCode))
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


    @ReactMethod(isBlockingSynchronousMethod = true)
    public String createValueIndex(String dbname, String indexName, ReadableArray indexExpressions) {

        try {

            if (dbname == null || dbname.isEmpty()) {
                return responseStrings.MissingargsDBN;
            } else if (indexName == null || indexName.isEmpty()) {
                return responseStrings.MissingargsIN;
            } else if (indexExpressions == null || indexExpressions.size() < 1) {
                return responseStrings.MissingargsINEX;
            } else {
                List<String> parsedIndexExpressions = new ArrayList<>();
                for (int a = 0; a < indexExpressions.size(); a++) {
                    parsedIndexExpressions.add(indexExpressions.getString(a));
                }

                IndexArgs vargs = new IndexArgs();
                vargs.ValueIndexArgs(dbname, indexName, parsedIndexExpressions);
                String result = dbMgr.createValueIndex(vargs);
                return result;
            }


        } catch (Exception e) {
            return responseStrings.Exception + e.getMessage();
        }

    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public String createFTSIndex(String dbname, String indexName, @Nullable Boolean ignoreAccents, @Nullable String language, ReadableArray indexExpressions) {

        try {

            if (dbname == null || dbname.isEmpty()) {
                return responseStrings.MissingargsDBN;
            } else if (indexName == null || indexName.isEmpty()) {
                return responseStrings.MissingargsIN;
            } else if (indexExpressions == null || indexExpressions.size() < 1) {
                return responseStrings.MissingargsINEX;
            } else {
                List<String> parsedIndexExpressions = new ArrayList<>();
                for (int a = 0; a <= indexExpressions.size(); a++) {
                    parsedIndexExpressions.add(indexExpressions.getString(a));
                }
                IndexArgs fargs = new IndexArgs();
                fargs.FTSIndexArgs(dbname, indexName, ignoreAccents, language, parsedIndexExpressions);
                String result = dbMgr.createFTSIndex(fargs);
                return result;
            }

        } catch (Exception e) {
            return responseStrings.Exception + e.getMessage();
        }

    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public String deleteIndex(String dbname, String indexName) {

        try {

            if (dbname == null || dbname.isEmpty()) {
                return responseStrings.MissingargsDBN;
            } else if (indexName == null || indexName.isEmpty()) {
                return responseStrings.MissingargsIN;
            } else {
                IndexArgs fargs = new IndexArgs();
                fargs.DeleteIndexArgs(dbname, indexName);
                String result = dbMgr.deleteIndex(fargs);
                return result;
            }


        } catch (Exception e) {
            return responseStrings.Exception + e.getMessage();
        }

    }


    @ReactMethod
    public void getBlob(String dbname, String data, Callback OnSuccessCallback, Callback OnErrorCallback) {
        try {

            if (dbname == null || dbname.isEmpty()) {
                OnErrorCallback.invoke(responseStrings.MissingargsDBN);
            } else {

                if (data == null || data.isEmpty()) {
                    OnErrorCallback.invoke(responseStrings.Missingargs + "Blob Data");
                } else {

                    String Blobresponse = dbMgr.getBlob(dbname, data);

                    if (!Blobresponse.equals(responseStrings.DBnotfound) && !Blobresponse.equals(responseStrings.invalidblob)) {
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
    public String setBlob(String dbname, String type, String docObject) {

        if (dbname == null || dbname.isEmpty()) {
            return responseStrings.MissingargsDBN;
        } else {
            if (type.isEmpty()) {
                return responseStrings.Missingargs + "Content type";
            } else if (docObject.isEmpty()) {
                return responseStrings.Missingargs + "Blob Data";
            } else {
                try {
                    return dbMgr.setBlob(dbname, type, docObject);
                } catch (Exception exception) {
                    return responseStrings.ExceptionBLOB + exception.getMessage();
                }
            }
        }
    }


    @ReactMethod(isBlockingSynchronousMethod = true)
    public String addDatabaseChangeListener(String dbname, String listener) {

        try {

            if (dbname == null || dbname.isEmpty()) {
                return responseStrings.MissingargsDBN;
            } else if (listener == null || listener.isEmpty()) {
                return responseStrings.Missingargs + "JSListener";
            } else {
                String result = dbMgr.registerForDatabaseChanges(dbname, listener);
                return result;
            }


        } catch (Exception e) {
            return responseStrings.Exception + e.getMessage();
        }

    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public String removeDatabaseChangeListener(String dbname) {

        try {

            if (dbname == null || dbname.isEmpty()) {
                return responseStrings.MissingargsDBN;
            } else {
                String result = dbMgr.deregisterForDatabaseChanges(dbname);
                return result;
            }


        } catch (Exception e) {
            return responseStrings.Exception + e.getMessage();
        }

    }


    @ReactMethod(isBlockingSynchronousMethod = true)
    public String enableLogging() {

        try {
            return dbMgr.enableLogging();
        } catch (Exception e) {
            return responseStrings.ExceptionEnableLogging + e.getMessage();
        }

    }


    @ReactMethod
    public void query(String dbname, String query, Callback OnSuccessCallback, Callback OnErrorCallback) {

        try {
            if (dbname == null || dbname.isEmpty()) {
                OnErrorCallback.invoke(responseStrings.MissingargsDBN);
            } else if (query == null || query.isEmpty()) {
                OnErrorCallback.invoke(responseStrings.Missingargs + "Query");
            } else {
                QueryArgs qArgs = new QueryArgs(dbname, query);
                String response = dbMgr.queryDb(qArgs);
                if (!response.equals(responseStrings.DBnotfound) && !response.equals(responseStrings.ExceptionInvalidQuery) && !response.equals(responseStrings.invaliddata)) {
                    OnSuccessCallback.invoke(response);
                } else {
                    OnErrorCallback.invoke(response);
                }
            }


        } catch (Exception e) {
            OnErrorCallback.invoke(responseStrings.ExceptionQuery + e.getMessage());
        }

    }


    @ReactMethod
    public void replicatorStart(String dbname, ReadableMap replicatorConfig, Callback OnSuccessCallback, Callback OnErrorCallback) {

        try {

            String response;
            if (replicatorConfig == null) {
                OnErrorCallback.invoke(responseStrings.Missingargs + "ReplicatorConfig");
                return;
            }
            if (dbname != null) {

                response = dbMgr.replicatorStart(dbname, replicatorConfig);

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

    @ReactMethod(isBlockingSynchronousMethod = true)
    public String replicatorStop(String dbname) {
        try {

            if (dbname != null) {
                return dbMgr.replicatorStop(dbname);
            } else {
                return responseStrings.MissingargsDBN;
            }

        } catch (Exception e) {
            return responseStrings.ExceptionDB + e.getMessage();
        }
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public String replicationRemoveListener(String dbname) {
        try {

            if (dbname != null) {
                return dbMgr.replicationRemoveChangeListener(dbname);
            } else {
                return responseStrings.MissingargsDBN;
            }

        } catch (Exception e) {
            return responseStrings.ExceptionDB + e.getMessage();
        }
    }


    @ReactMethod(isBlockingSynchronousMethod = true)
    public String replicationAddListener(String dbname, String JSListener) {
        try {

            if (dbname != null) {
                return dbMgr.replicationAddChangeListener(dbname, JSListener);
            } else {
                return responseStrings.MissingargsDBN;
            }

        } catch (Exception e) {
            return responseStrings.ExceptionDB + e.getMessage();
        }
    }


}
