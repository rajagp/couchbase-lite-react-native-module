# overview
A reference implementation of a [React Native Module](https://reactnative.dev/docs/native-modules-intro) for couchbase lite on Android. 

In order to use Couchbase Lite as embedded database within your React Native app, you will need a way to access Couchbase Lite’s native APIs from within your React Native JS application. React Native Modules allow mobile apps written in React Native to access native platform APIs.


The React Native Module example exports a subset of native Couchbase Lite API functionality. This is intended to be used as a reference. You can extend this module to expose other relevant APIs per [module development guide](https://reactnative.dev/docs/native-modules-ios) 

![](https://i2.wp.com/blog.couchbase.com/wp-content/uploads/2018/10/ReactNativeModule.jpg?w=900)
# Exported APIs
The following list of Couchbase Lite (Android) APIs is exported by the plugin. 

This is WIP

| Create Database with specified Configuration | Database |
|-|-|
| Create DatabaseConfiguration | DatabaseConfiguration |
| Close Database | close |
| copyDatabase | copy |
| AddChangeListener to listen for database changes | addChangeListener |
| RemoveChangeListener | removeChangeListener |
| saveDocumentWith JSONString | Will be provided during project dev (pre-release) |
| Init mutableDocument | MutableDocument |
| mutableDocument type set for string | setString |
| Mutable Document type set for Blob | setBlob |
| saveBlob  | pre-release) |
| deleteDocument | deleteDocument(_:) |
| getDocument | getDocument |

# Build Instructions

A step by step installation guide of React Native Module of Couchbase Lite in Android.

## Installation 

### Yarn
```
Yarn add “<package path>”
```

### NPM
```
npm install “<package path>”
```


# Sample Usage Instructions
```
import CbliteAndroid from 'react-native-cblite-android’;
```

## Create Database
```
CbliteAndroid.createDatabase(dbName,directory,encrytion,(error, response)=>callback);
```

### Params

**dbName :** Name of the Database as string.
**directory :** Path of the database directory as string.
**Encryption :** Encryption key as string.
**Callback :** Asynchronously triggers when the function completes execution. Contains Error and Response params, If there is an exception while execution the Error param will have the string exception if there is no exception then the response param will contains following responses.

#### Example Response from Create Database :
> * _"Database Created"_
> * _"Missing Arguments : Database Name"_
> * _"Missing Arguments : Directory"_
> * _"Error while Creating Database : <exception>"_




## Close Database
```
CbliteAndroid.closeDatabase(dbName);
```

### Params

**dbName :** Name of the Database as string.

#### Example Response from Create Database :
> * _"Database Closed"_
> * _"Error while Closing Database : <exception>"_




## Create Document
```
CbliteAndroid.setDocument(dbName,docid,data,(error, response)=>callback);
```

### Params

**dbName :** Name of the Database as string.
**docid :** Unique id of the document as string.
**data :** A JSON object containing data to be saved in document.
**Callback :** Asynchronously triggers when the function completes execution. Contains Error and Response params, If there is an exception while execution the Error param will have the string exception if there is no exception then the response param will contains following responses.

#### Example Response from Create Document :
> * _"Document Created"_
> * _"Document is Null"_
> * _"Document not found"_
> * _"Database not found"_
> * _"Missing Arguments : Database Name"_
> * _"Missing Arguments : Document ID"_
> * _"Missing Arguments : Document Data"_
> * _"Invalid Arguments : Document data is not in proper JSON format"_
> * _"Error while Creating Document : <exception>"_





## Get Document
```
CbliteAndroid.getDocument(dbname,docid,(error, result) => callback);
```

### Params

**dbName :** Name of the Database as string.
**docid :** Unique id of the document as string.
**Callback :** Asynchronously triggers when the function completes execution. Contains Error and Response params, If there is an exception while execution the Error param will have the string exception if there is no exception then the response param will contains following responses.

#### Example Response from Get Document :
> * _"<Document as JSON>"_
> * _"Missing Arguments : Database Name"_
> * _"Missing Arguments : Document ID"_
> * _"Error while Fetching Document : <exception>"_






## Save Blob
```
var BlobJSON = CbliteAndroid.setBlob(ContentType,Blob);
```

### Params

**ContentType :** Content type of the Blob object as string.
**Blob :** Base64 encoded blob string.
**Retrun :** Synchronously returns a String of JSON Object of blob Meta Data which can be used retrieve blob by passing object to getBlob function.

#### Example Response from Save Blob :
> * _"<BLOB Meta Data>"_
> * _"Missing Arguments : Content Type"_
> * _"Missing Arguments : Blob Data"_
> * _"Error while Creating Blob : <exception>"_





## Get Blob
```
CbliteAndroid.getBlob(blobMeta,(error, result) => callback);
```

### Params

**blobMeta :** Meta Data JSON object of Blob which is returned from save blob function.
**Callback :** Asynchronously triggers when the function completes execution. Contains Error and Response params, If there is an exception while execution the Error param will have the string exception if there is no exception then the response param will contains following responses.

#### Example Response from Get Blob :
> * _"<Base64 encoded Blob String>"_
> * _"Blob not found"_
> * _"Missing Arguments : BlobObject"_
> * _"Invalid Arguments : Blob Object is not in proper JSON format"_
> * _"Error while Fetching Blob : <exception>"_





# Sample App
TBD (Add reference to user profile sample app)

# Couchbase Lite Version
This version of React Native Module requires Couchbase Lite 3.0.0(Beta).

