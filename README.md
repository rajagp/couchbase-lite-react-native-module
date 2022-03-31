# Overview
A reference implementation of a [React Native Module](https://reactnative.dev/docs/native-modules-intro) for couchbase lite on Android. 

**NOTE**: The plugin implementation is not officially supported by Couchbase and there are no guarantees that the APIs exported by the module are up to date with the latest version of Couchbase Lite. The module implementation is available as an open source reference implementation for developers to use as a starting point.

In order to use Couchbase Lite as embedded database within your React Native app, you will need a way to access Couchbase Lite’s native APIs from within your React Native JS application. React Native Modules allow mobile apps written in React Native to access native platform APIs.

The React Native Module example exports a subset of native Couchbase Lite API functionality and makes it available to React native JS apps. This is intended to be used as a reference. You can extend this module to expose other relevant APIs per [module development guide](https://reactnative.dev/docs/native-modules-ios) 

*NOTE*: The plugin **does not** bundle Couchbase Lite native framework. You will include Couchbase Lite library when building your React Native app. The Getting Started instructions below describe the same.

![](https://i2.wp.com/blog.couchbase.com/wp-content/uploads/2018/10/ReactNativeModule.jpg?w=900)

## Exported APIs

The following is a list of APIs (and features) exported by the react-native plugin. See the description of Couchbase Lite Native [API Specifications](https://docs.couchbase.com/mobile/3.0.0-beta02/couchbase-lite-android/com/couchbase/lite/package-summary.html) for an authoritative description of the API functionality. As mentioned above, in some cases, the plugin isn't a simple passthrough. The plugin implements additional logic, so there will not be an exact 1:1 mapping to the original API definition.

| API methods | Native Class |
| :---: | :---: |
| CreateOrOpenDatabase (with specified Configuration) | Database |
| closeDatabase | Database |
| deleteDatabase | Database |
| copyDatabase | Database |
| databaseExists | Database |
| addDatabaseChangeListener | Database |
| removeDatabaseChangeListener | Database |
| setDocument (With JSON OBJECT) | MutableDocument |
| getDocument | MutableDocument |
| deleteDocument | MutableDocument |
| setBlob | Database |
| getBlob  | Database |
| createValueIndex  | Database |
| createFTSIndex  | Database |
| deleteIndex  | Database |
| query  | Query |
| queryWithChangeListener  | Query |
| removeQueryChangeListener  | Query |
| enableConsoleLogging  | Database |
| createReplicator  | Replicator |
| replicatorStart  | Replicator |
| replicatorStop  | Replicator |
| replicationAddChangeListener  | Replicator |
| replicationRemoveChangeListener  | Replicator |

## Getting Started

The instructions assume some familiarity with [React Native app development](https://reactnative.dev/docs/environment-setup).

### Integrating the native module into your React Native App


The step-by-step instructions below illustrates how you can integrate and use the react native module within a sample React Native app for Android platform. You will do something similar when integrating into your own React Native app. 

*  Create a sample React Native app named "AwesomeProject" for Android as per instructions in the [Starter's Guide](https://reactnative.dev/docs/environment-setup). The instructions also guide you through the steps to set up your environment for React Native app development for Android (and iOS).
 
 For the rest of the instructions, we will assume that you have created a sample React Native project named "AwesomeProject" for Android. **NOTE:** You will need node version 12+ for Android app development. 

*  Install yarn from within your root folder

```bash
cd  /path/to/AwesomeProject
npm install yarn

```

* Install the plugin by adding the appropriate Github repo. If you fork the repo and modify it, then be sure to point it to the right URL!

```bash
yarn add https://github.com/rajagp/couchbase-lite-react-native-module
```

## iOS setup

The module does not come bundled with the couchbase lite framework. You will have to include the appropriately licensed Couchbase Lite iOS library as pod within your app.
The React native reference module requires minimal version of **CouchbaseLite-Swift v3.0.0**. 

```bash
cd ios
pod install
```

## Android setup

### Adding couchbase-lite-android framework as a dependency

The module does not come bundled with the couchbase lite framework. You will have to include the appropriately licensed Couchbase Lite Android library as dependency within your app.
 
The React native reference module requires minimal version of **Couchbase Lite v3.0.0**. 

Couchbase Lite can be downloaded from Couchbase [downloads](https://www.couchbase.com/downloads) page or can be pulled in via maven as described in [Couchbase Lite Android Getting Started Guides](https://docs.couchbase.com/couchbase-lite/current/android/gs-install.html).

We discuss the steps to add the Couchbase Lite framework dependency depending on how you downloaded the framework. 

* Open the Android project located inside your React Native project under directory: `/path/to/AwesomeProject/android` using Android Studio.

**Include couchbase-lite-android sdk from maven**

Follow the instructions in [Couchbase Lite Android Getting Started Guides](https://docs.couchbase.com/couchbase-lite/current/android/gs-install.html) for URL or maven repository etc.

- In your 'app' level `build.gradle` file, add your library file path. 
 ```
 dependencies {
    implementation 'com.couchbase.lite:couchbase-lite-android:${version}'
 }
```


- In your 'project' level `build.gradle` file, add your library file path. 

```
 buildscript {
    ...
    ext {
        ...
        // Add this line
        cblVersion = 'com.couchbase.lite:couchbase-lite-android:${version}'
        ...
        }
    ...
}
```


**To add couchbase-lite-android as an .aar file**

* Create a a new directory called 'libs' under your "**/path/to/AwesomeProject/node_modules/react-native-cblite/android**" folder
* Copy the .aar files from within your downloaded Couchbase Lite package into the newly created'libs' folder
```bash
cd /path/to/AwesomeProject/node_modules/react-native-cblite/android

mkdir libs

cp ~/path/to/couchbase-lite-android-ee-3.0.0.aar libs/ 
```


* In Android Studio, navigate to the "project structure" in order to add  couchbase lite library as a dependency.

![](https://blog.couchbase.com/wp-content/uploads/2021/09/project-structure.png)

* Add "lib/couchbase-lite-android-ee-3.0.0.aar" as dependency to the couchbase lite React native module

![](https://blog.couchbase.com/wp-content/uploads/2021/09/adding-library-react-native.png)

* Your dependency tree would look something like this

![](https://blog.couchbase.com/wp-content/uploads/2021/09/dependency-tree.png)

* In your 'Project' level `build.gradle` file, add the "libs" directory path using "flatDir"
```
allprojects {
    repositories {
        mavenLocal()
        maven {
            // All of React Native (JS, Obj-C sources, Android binaries) is installed from npm
            url("$rootDir/../node_modules/react-native/android")
        }
        maven {
            // Android JSC is installed from npm
            url("$rootDir/../node_modules/jsc-android/dist")
        }

        google()
        jcenter()
        maven { url 'https://www.jitpack.io' }
        flatDir {
            dirs 'libs'
        }
    }
}
```

* In your 'app' level `build.gradle` file, add Couchbase Lite library under dependencies. 
```bash
dependencies {
    implementation fileTree(dir: "libs", include: ["*.jar"])
    implementation files('com.couchbase.couchbase-lite-android-ee-3.0.0')

}
```


**Confirm minimum SDK version**

Couchbase Lite required min SDK version of API 22. So confirm that the React Native app has minimum SDK of API22.

For this, in your project level 'build.gradle' file, confirm that the `minSdkVersion` is greater than or equal to API 22

```bash
buildscript {
    ext {
        buildToolsVersion = "29.0.3"
        minSdkVersion = 22
        compileSdkVersion = 29
        targetSdkVersion = 29
        ndkVersion = "20.1.5948944"
        cblVersion = 'com.couchbase.lite:couchbase-lite-android:${version}'
    }

```

### Build and Run your React Native project

Build and run the app per instructions in [Getting Started Guide]("https://reactnative.dev/docs/environment-setup"). You can run the app direcly from Android Studio or from command line.

Don't forget to start the Metro bundler before running your app!

```bash
npx react-native start
```


## Usage

Here are a few examples of using the native module in your app

To use the module, open your react-native app project using a suitable IDE and declare the plugin at the on top of your `app.js` file.

```
import * as CBL from 'react-native-cblite';
```

### Create Database
```
let config = {
    encryptionKey: "{{ENCRYPTION_KEY}}",
    directory: "{{DIRECTORY}}"
};

let dbName = '{{DATABASE_NAME}}'
CBL.CreateOrOpenDatabase(dbName,config, function(rs) { 
  console.log("database "+ dbName + " creation: "+ rs.toString())
  }, function(error) { 
    console.log(error.toString())
    });

```

_Params_

 * dbName: Name of the Database as string.

 * config: Couchbase Database configuration JSONobject containing following.

    * directory: Path of the database directory as string.
    * encryptionKey: Encryption key as string.
    
 * Error Callback: Asynchronously triggers when the function fails execution. Contains Error string as param, If there is an exception while execution the param will have the string exception.
 * Success Callback:Asynchronously triggers when the function succeeds execution. Contains string Response as param, If there is no exception while execution the param can contain one of the following responses.

_Example Response_

 * _"Success"_
 * _"Database already exists"_
 * _"Missing Arguments: Database Name"_
 * _"Missing Arguments: Directory"_
 * _"Error while Creating Database: \{exception\}"_



### Close Database

```
let response = CBL.closeDatabase(dbName,function(rs) { 
  console.log("database "+ dbName + " closing : "+ rs.toString())
  }, function(error) {
     console.log(error.toString())
     });
```

_Params_

 * dbName:  Name of the Database as string.
 * Error Callback: Asynchronously triggers when the function fails execution. Contains Error string as param, If there is an exception while execution the param will have the string exception.
 * Success Callback:Asynchronously triggers when the function succeeds execution. Contains string Response as param, If there is no exception while execution the param can contain one of the following responses.
 
_Example Response_

 * _"Success"_
 * _"Database not found"_
 * _"Error while Closing Database : \{exception\}"_



### Delete Database

```
let response = CBL.deleteDatabase(dbName);
console.log("close" + dbName+ " database reponse is :" + response);
```

_Params_

 * dbName:  Name of the Database as string.

_Example Response_

 * _"Success"_
 * _"Database not found"_
 * _"Error while Deleting Database : \{exception\}"_



### Database Exists

```

 var dbexists = CouchbaseNativeModule.databaseExists(dbName, dbConfig);
        
```

_Params_

  * dbName: Name of the Database as string.
  * config: Couchbase Database configuration JSONobject containing following.

    * directory: Path of the database directory as string.
    * encryptionKey: Encryption key as string.

_Example Response_

 * _"Database already exists"_
 * _"Database not exists"_
 * _"Error"_
 * _"Missing Arguments : Database Name"_



### Create/Update Document

```
let docid = "{{DOCUMENT_ID}}";
let data = "{{JSON_OBJECT}}"; e.g { foo : 'bar', adam : 'eve' }
let dbName = "{{DATABASE_NAME}}";

CBL.setDocument(dbName,docid, JSON.stringify(data), function(rs) {
   console.log("Added document with body"+JSON.stringify(data) +" to db " + dbName + " "+ rs.toString())
   }, function(error) {
     console.log(error.toString()) 
     });

```

_Params_

 * dbName: Name of the Database as string.
 * docid: Unique id of the document as string.
 * data: A JSON object containing data to be saved in document.
 * Error Callback: Asynchronously triggers when the function fails execution. Contains Error string as param, If there is an exception while execution the param will have the string exception.
 * Success Callback: Asynchronously triggers when the function succeeds execution. Contains string Response as param, If there is no exception while execution the param can contain one of the following responses.

_Example Response_

 * _"Success"_
 * _"Document is Null"_
 * _"Document not found"_
 * _"Database not found"_
 * _"Missing Arguments: Database Name"_
 * _"Missing Arguments: Document ID"_
 * _"Missing Arguments: Document Data"_
 * _"Invalid Arguments: Document data is not in proper JSON format"_
 * _"Error while Creating Document: \{exception\}"_



### Get Document

```
let docid = "{{DOCUMENT_ID}}";
let dbName = "{{DATABASE_NAME}}";
CBL.getDocument(dbName,docid,function(rs) {
  console.log("Fetched document "+docid+ " from db " + dbName + " " + rs.toString()) 
  }, function(error) { 
    console.log(error.toString())
    });

```

_Params_

 * dbName: Name of the Database as string.
 * docid: Unique id of the document as string.
 * Error Callback: Asynchronously triggers when the function fails execution. Contains Error string as param, If there is an exception while execution the param will have the string exception.
 * Success Callback: Asynchronously triggers when the function succeeds execution. Contains string Response as param, If there is no exception while execution the param can contain one of the following responses.

_Example Response_

 * _"\{Document as JSON\}"_
 * _"Database not found"_
 * _"Document not found"_
 * _"Missing Arguments : Database Name"_
 * _"Missing Arguments : Document ID"_
 * _"Error while Fetching Document : \{exception\}"_


### Save Blob

```
var blobMeta = CBL.setBlob(dbName,contentType,blob);
```

_Params_

 * dbName: Name of the Database as string.
 * contentType: MIME content type of the binary data
 * blob:  Base64 encoded string corresponding to binary data to be stored

_Example Response_

 * _"\{BLOB Meta Data\}"_: Synchronously returns a String of JSON Object of blob Meta Data which can be used retrieve blob by passing object to getBlob function.
 * _"Database not found"_
 * _"Missing Arguments : Content Type"_
 * _"Missing Arguments : Blob Data"_
 * _"Error while Creating Blob : \{exception\}"_


### Get Blob

```
CBL.getBlob(dbName,blobMeta,this.success_callback,this.error_callback);
```

_Params_

  * dbName: Name of the Database as string.
  * _blobMeta_: Meta Data JSON object of Blob which is returned from save blob function.
  * _Error Callback_: Asynchronously triggers when the function fails execution. Contains Error string as param, If there is an exception while execution the param will have the string exception.
  * _Success Callback_: Asynchronously triggers when the function succeeds execution. Contains string Response as param, If there is no exception while execution the param can contain one of the following responses.


_Example Response_

 * _"\{Base64 encoded Blob String\}"_
 * _"Blob not found"_
 * _"Database not found"_
 * _"Missing Arguments : BlobObject"_
 * _"Invalid Arguments : Blob Object is not in proper JSON format"_
 * _"Error while Fetching Blob : \{exception\}"_




### Add Database Change Listener

```

var JSListenerEvent = 'OnDatabaseChanged'

var response = CBL.EventsListeners.addDatabaseChangeListener(dbName,JSListenerEvent);

if(response=='Success') {
    CBL.EventsListeners.addListener(JSListener, (eventResponse) => { console.log(eventResponse) });
    }
    else {
        console.log("ERROR: " + response);
    }


```

_Params_

  * dbName: Name of the Database as string.
  * _JSListenerEvent_: String name of the Javascript listener event.


_Example Response for addChangeListener_
   * _"Success"_
   * _"Database not found"_
   * _"Missing Arguments : Database Name"_
   * _"Missing Arguments : JSListener"_
   * _"Database listener already registered with database. Please remove the database listener before registering new one."_


_Example Response in eventResponse_
   * _{"Modified": {"DocumentID": "DocumentJson"},"Deleted": {"DocumentID": "DocumentJson"}}_
   * _{"Deleted": {"DocumentID1": "Document1Json","DocumentID2": "Document2Json"...}}_
   * _{"Modified": {"DocumentID1": "Document2Json","DocumentID2": "Document2Json"...}}_



### Remove Database Change Listener

```

var JSListenerEvent = 'OnDatabaseChanged'
.....

var response = CBL.removeDatabaseChangeListener(dbName);

if(response=='Success') {
     CBL.EventsListeners.removeAllListeners(JSListenerEvent);
     }
     else {
        console.log("ERROR: " + response);
     }
```

_Params_

  * dbName: Name of the Database as string.
  * _JSListenerEvent_: String name of the Javascript listener event.


_Example Response_

 * _"Success"_
 * _"Database not found"_
 * _"Database listener not registered with database."_
 * _"Missing Arguments : Database Name"_



### Create Value Index

```

let indexExpressions = ['name', 'location'];
let indexName = "nameLocationIndex";

 var response = CouchbaseNativeModule.createValueIndex(dbName, indexName, indexExpressions);
        
```

_Params_

  * dbName: Name of the Database as string.
  * indexName: String name of index to be created.
  * indexExpressions: Array of Expressions of index to be created.
 

_Example Response_

 * _"Success"_
 * _"Database not found"_
 * _"Missing Arguments : Database Name"_
 * _"Missing Arguments : Index Name"_
 * _"Missing Arguments : Index Expressions"_



### Create FTS Index

```

let indexExpressions = ['name', 'location'];
let indexName = "nameLocationIndex";
boolean ignoreAccents = true;
let language = "English";

 var response = CouchbaseNativeModule.createValueIndex(dbName, indexName, ignoreAccents, language, indexExpressions);
        
```

_Params_

  * dbName: Name of the Database as string.
  * indexName: String name of index to be created.
  * ignoreAccents (nullable) : Boolean value for ignoreAccents of index to be created.
  * language (nullable) : String language for index to be created.
  * indexExpressions: Array of Expressions of index to be created.
 

_Example Response_

 * _"Success"_
 * _"Database not found"_
 * _"Missing Arguments : Database Name"_
 * _"Missing Arguments : Index Name"_
 * _"Missing Arguments : Index Expressions"_




### Delete Index

```

 let indexName = "nameLocationIndex";

 var response = CouchbaseNativeModule.deleteIndex(dbName, indexName);
        
```

_Params_

  * dbName: Name of the Database as string.
  * indexName: String name of index to be deleted.
 

_Example Response_

 * _"Success"_
 * _"Database not found"_
 * _"Missing Arguments : Database Name"_
 * _"Missing Arguments : Index Name"_




### Enable Logging

```
let domain = "REPLICATOR"; // e.g for ALL_DOMAINS enter null }
let logLevel = "verbose";
 
var response = await CouchbaseNativeModule.enableLogging(domain,logLevel);
```

_Params_

  * domain: String value of log domain.
  * logLevel: String value of logLevel.
 
_Example Response_

 * _"Success"_
 * _"Error"_




### Query

```

  let query = "{{QUERY_STRING}}"; //e.g "select * from users"

  CouchbaseNativeModule.query(dbName, query,function(rs) {
     console.log("Query result "+ rs.toString())
     }, function(error) { 
       console.log(error.toString())
       }););
        
```

_Params_

  * dbName: Name of the Database as string.
  * query: String query to be executed.
  * Error Callback: Asynchronously triggers when the function fails execution. Contains Error string as param, If there is an exception while execution the param will have the string exception.
  * Success Callback:Asynchronously triggers when the function succeeds execution. Contains string Response as param, If there is no exception while execution the param can contain one of the following responses.


_Example Response_

 * _"[Query response]"_
 * _"Database not found"_
 * _"Missing Arguments : Database Name"_
 * _"Missing Arguments : Query"_




### Live Query

```

  let query = "{{QUERY_STRING}}"; //e.g "select * from users"
  let JSListenerEvent = "OnQueryChanged"

  let listenerAddResponse = await CouchbaseNativeModule.queryWithChangeListener(dbName, query, JSListenerEvent);

  if (listenerAddResponse == "Success") {
      CBL.EventsListeners.addListener(JsListener, function(response){
        conosole.log("Query Response : ", response);
      });  
  }


        
```

_Params_

  * dbName: Name of the Database as string.
  * query: String query to be executed.
  * _JSListenerEvent_: String name of the Javascript listener event.


_Example Response_

 * _"Success"_
 * _"Database not found"_
 * _"Missing Arguments : Database Name"_
 * _"Missing Arguments : Query"_


_Example Response in eventResponse_
   * _{{"DATABASE_NAME}": {{DocumentJson}}}_
   


### Stop Live Query

```
  let query = "{{QUERY_STRING}}"; //e.g "select * from users"
  let JSListenerEvent = "OnQueryChanged"

  var stopQueryListener = await CouchbaseNativeModule.removeQueryChangeListener(dbname, query);

  if (stopQueryListener == "Success") {
      CBL.EventsListeners.removeAllListeners(stopQueryListener);
  }
        
```

_Params_

  * dbName: Name of the Database as string.
  * query: String query to be executed.


_Example Response_

 * _"Success"_
 * _"Database not found"_
 * _"Query not found"_
 * _"Missing Arguments : Database Name"_
 * _"Missing Arguments : Query"_



### Create Replicator

```
    var config = {
            databaseName: "{{DATABASE_NAME}}",
            target: "{{STRING_URI}}", // e.g "ws://10.0.2.2:4984/",
            authenticator: {
                authType: "{{AUTH_TYPE}}", // e.g. "Basic"
                username: "{{AUTH_USERNAME}}", // e.g. "user@example.com"
                password: "{{AUTH_PASSWORD}}" // e.g. "examplePassword"
            }, //optional
            continuous: {{BOOLEAN}}, //optional
            headers: [{HEADER_ARRAY}], //optional
            channels: [{CHANNELS_LIST}], //optional
            documentIds: [{DOCUMENT_ID_LIST}], //optional
            acceptOnlySelfSignedServerCertificate: {{BOOLEAN}}, //optional
            pinnedServerCertificateUri: {{STRING_URI}}, //optional
            heartbeat:{{HEARTBEAT_INT}}, //optional
          
        }

  let ReplicatorID = await CouchbaseNativeModule.createReplicator(dbname, config);
  
  console.log("ReplicatorID", ReplicatorID);

        
```

_Params_

  * dbName: Name of the Database as string.
  * config: Configuration object for replicator.


_Example Response_

 * _"{{REPLICATOR_ID}}"_
 * _"Database not found"_
 * _"Missing Arguments : Database Name"_
 * _"Missing Arguments : Config"_




### Start Replicator

```
  let startReplicatorResponse = await CouchbaseNativeModule.replicatorStart(dbname, ReplicatorID);
     
  console.log("Replicator Started", startReplicatorResponse)
        
```

_Params_

  * dbName: Name of the Database as string.
  * ReplicatorID: String ID of replicator, obtained from CreateReplicator function.


_Example Response_

 * _"Success"_
 * _"Failed"_
 * _"Database not found"_
 * _"Replicator not found"_
 * _"Missing Arguments : Database Name"_
 * _"Missing Arguments : ReplicatorID"_




### Stop Replicator

```
  let stopReplicatorResponse = await CouchbaseNativeModule.replicatorStop(dbname, ReplicatorID);
     
  console.log("Replicator Stopped", stopReplicatorResponse)
    
```

_Params_

  * dbName: Name of the Database as string.
  * ReplicatorID: String ID of replicator, obtained from CreateReplicator function.


_Example Response_

 * _"Success"_
 * _"Failed"_
 * _"Database not found"_
 * _"Replicator not found"_
 * _"Missing Arguments : Database Name"_
 * _"Missing Arguments : ReplicatorID"_




### Create Replicator Listener

```
  let JSListenerEvent = "OnReplicatorChanged"

  let ReplicatorListenerResponse = await CouchbaseNativeModule.replicationAddChangeListener(dbname, ReplicatorID, JSListenerEvent);
     
   if (ReplicatorListenerResponse == "Success") {
      CBL.EventsListeners.addListener(JSListenerEvent, function(response){
        conosole.log("Replicator Status Response : ", response);
      });  
  }
        
```

_Params_

  * dbName: Name of the Database as string.
  * ReplicatorID: String ID of replicator, obtained from CreateReplicator function.
  * _JSListenerEvent_: String name of the Javascript listener event.


_Example Response_

 * _"Success"_
 * _"Database not found"_
 * _"Replicator not found"_
 * _"Missing Arguments : Database Name"_
 * _"Missing Arguments : ReplicatorID"_
 * _"Missing Arguments : JSListener"_


_Example Response in eventResponse_

   * _{"status": "{STATUS}", "completed":{COMPLETED_TASKS}, "total":{TOTAL_TASKS}}_
   * _{"status": "{STATUS}", "error":"{ERROR_MESSAGE}", "errorCode":"{ERROR_CODE}", "completed":{COMPLETED_TASKS}, "total":{TOTAL_TASKS}}_
   



### Remove Replicator Listener

```
  let JSListenerEvent = "OnReplicatorChanged"

  var stopReplicatorListener = await CouchbaseNativeModule.replicationRemoveChangeListener(dbname, ReplicatorID);

  if (stopReplicatorListener == "Success") {
      CBL.EventsListeners.removeAllListeners(JSListenerEvent);
  }
        
```

_Params_

  * dbName: Name of the Database as string.
  * ReplicatorID: String ID of replicator, obtained from CreateReplicator function.


_Example Response_

 * _"Success"_
 * _"Database not found"_
 * _"Replicator not found"_
 * _"Missing Arguments : Database Name"_
 * _"Missing Arguments : ReplicatorID"_



## Updates to Native Module

If you update the plugin such as adding a new API, don't forget to remove the plugin and re-add it to the app. 

### Removing the module
```bash
yarn remove react-native-cblite
```

### Adding the module
```bash
yarn add https://github.com/rajagp/couchbase-lite-react-native-module
```

*Troubleshooting Tip*:
 On occasion.  if the app isn't recognizing the latest plugin changes it may help to do a complete clean
  - remove the root level `node_modules` folder
  - Run "npm install"
  - Repeat the steps to add the module and couchbase lite package.
