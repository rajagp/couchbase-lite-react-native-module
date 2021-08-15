# Overview
A reference implementation of a [React Native Module](https://reactnative.dev/docs/native-modules-intro) for couchbase lite on Android. 

In order to use Couchbase Lite as embedded database within your React Native app, you will need a way to access Couchbase Lite’s native APIs from within your React Native JS application. React Native Modules allow mobile apps written in React Native to access native platform APIs.


The React Native Module example exports a subset of native Couchbase Lite API functionality and makes it available to React native JS apps. This is intended to be used as a reference. You can extend this module to expose other relevant APIs per [module development guide](https://reactnative.dev/docs/native-modules-ios) 

*NOTE*: The plugin **does not** bundle Couchbase Lite native framework. You will include Couchbase Lite library when building your React Native app. The Getting Started instructions below describe the same.

![](https://i2.wp.com/blog.couchbase.com/wp-content/uploads/2018/10/ReactNativeModule.jpg?w=900)

## Exported APIs
The following is list of Couchbase Lite(Android) APIs exported by the plugin. 

This is WIP

| API methods | Native Class |
| :---: | :---: |
| createDatabase (with specified Configuration) | Database |
| closeDatabase | Database |
| saveDocument (With JSON OBJECT) | MutableDocument |
| getDocument | MutableDocument |
| setBlob | Database |
| getBlob  | Database |

## Getting Started

### Integrating the native module into your React Native App


The step-by-step instructions below illustrates how you can integrate and use the react native module within a sample React Native app for Android platform. You will do something similar when building your own app. 

*  Create a sample React Native app named "AwesomeProject" per instructions in the [Starter's Guide](https://reactnative.dev/docs/environment-setup). The instructions also guide you through the steps to set up your environment for React Native app development for Android (and iOS). For the rest of the instructions, we will assume that you have created a sample React Native project named "AwesomeProject". **NOTE** You will need node version 12+ for Android app development. 

*  Install yarn from within your root folder

```bash
cd  /path/to/AwesomeProject
npm install yarn

```

* Install the plugin by adding the appropriate Github repo. If you fork the repo and modify it, then be sure to point it to the right URL!

```bash
yarn add https://github.com/rajagp/couchbase-lite-react-native-module
```


### Adding couchbase-lite-android framework as a dependency

The module does not come bundled with the couchbase lite framework. You will have to include the appropriately licensed Couchbase Lite Android library as dependency within your app. The React native reference module requires minimal version of **Couchbase Lite v3.0.0**. 

Couchbase Lite can be downloaded from Couchbase [downloads](https://www.couchbase.com/downloads) page or can be pulled in via maven as described in [Couchbase Lite Android Getting Started Guides](https://docs.couchbase.com/couchbase-lite/current/android/gs-install.html).

We discuss the steps to add the Couchbase Lite framework dependency depending on how you downloaded the framework. 

* Open the Android project located inside your React Native project under directory: `/path/to/AwesomeProject/android` using Android Studio.

**To add couchbase-lite-android as an .aar file**

* Create a a new directory called 'libs' under your "android/app" folder
* Copy the .aar files from within your downloaded Couchbase Lite package to the 'libs' folder 


![](https://blog.couchbase.com/wp-content/uploads/2021/08/react-native-app-couchbase-lite.png)

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
    implementation files('libs/couchbase-lite-android-ee-3.0.0.aar')
}
```


**Include couchbase-lite-android sdk from maven**

- In your 'app' level `build.gradle` file, add your library file path. Follow the instructions in [Couchbase Lite Android Getting Started Guides](https://docs.couchbase.com/couchbase-lite/current/android/gs-install.html) for URL or maven repository etc.
 ```
 dependencies {
    implementation 'com.couchbase.lite:couchbase-lite-android:${version}'
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
    }

```

### Build and Run your React Native project

You can run the app directly from Android Studio or per instructions in [Gettig Started Guide]()"https://reactnative.dev/docs/environment-setup")


## Usage

Here are a few examples of using the native module in your app

To use the module, open your react-native app project using a suitable IDE and declare the plugin at the on top of your `app.js` file.

```
import CBL from 'react-native-cblite-android';
```

**Create Database**
```
let config = {
    encryptionKey: "{{ENCRYPTION_KEY}}",
    directory: "{{DIRECTORY}}"
};

let dbName = '{{DATABASE_NAME}}'
CBL.createDatabase(dbName,config ,function(rs) { console.log("database "+ dbName + " creation: "+ rs.toString())}, function(error) { console.log(error.toString())});

```

_Params_

 * dbName: Name of the Database as string.

 * config: Couchbase Database configuration JSONobject containing following.

    * directory: Path of the database directory as string.
    * encryptionKey: Encryption key as string.
    
 * Error Callback: Asynchronously triggers when the function fails execution. Contains Error string as param, If there is an exception while execution the param will have the string exception.
 * Success Callback:Asynchronously triggers when the function succeeds execution. Contains string Response as param, If there is no exception while execution the param can contain one of the following responses.

_Example Response_

 * _"Database Created"_
 * _"Missing Arguments: Database Name"_
 * _"Missing Arguments: Directory"_
 * _"Error while Creating Database: \{exception\}"_



**Close Database**

```
let response = CBL.closeDatabase(dbName);
console.log("close" + dbName+ " database reponse is :" + response);
```

_Params_

 * dbName:  Name of the Database as string.

_Example Response_

 * _"Database Closed"_
 * _"Error while Closing Database : \{exception\}"_


**Create/Update Document**

```
let docid = "{{DOCUMENT_ID}}";
let data = "{{JSON_OBJECT}}"; e.g { foo : 'bar', adam : 'eve' }
let dbName = "{{DATABASE_NAME}}";

CBL.setDocument(dbName,docid, JSON.stringify(data), function(rs) { console.log("Added document with body"+JSON.stringify(data) +" to db " + dbName + " "+ rs.toString()) }, function(error) {console.log(error.toString()) });

```

_Params_

 * dbName: Name of the Database as string.
 * docid: Unique id of the document as string.
 * data: A JSON object containing data to be saved in document.
 * Error Callback: Asynchronously triggers when the function fails execution. Contains Error string as param, If there is an exception while execution the param will have the string exception.
 * Success Callback: Asynchronously triggers when the function succeeds execution. Contains string Response as param, If there is no exception while execution the param can contain one of the following responses.

_Example Response_

 * _"Document Created"_
 * _"Document is Null"_
 * _"Document not found"_
 * _"Database not found"_
 * _"Missing Arguments: Database Name"_
 * _"Missing Arguments: Document ID"_
 * _"Missing Arguments: Document Data"_
 * _"Invalid Arguments: Document data is not in proper JSON format"_
 * _"Error while Creating Document: \{exception\}"_



**Get Document**

```
let docid = "{{DOCUMENT_ID}}";
let dbName = "{{DATABASE_NAME}}";
CBL.getDocument(dbName,docid,function(rs) {console.log("Fetched document "+docid+ " from db " + dbName + " " + rs.toString()) }, function(error) { console.log(error.toString())});

```

_Params_

 * dbName: Name of the Database as string.
 * docid: Unique id of the document as string.
 * Error Callback: Asynchronously triggers when the function fails execution. Contains Error string as param, If there is an exception while execution the param will have the string exception.
 * Success Callback: Asynchronously triggers when the function succeeds execution. Contains string Response as param, If there is no exception while execution the param can contain one of the following responses.

_Example Response_

 * _"\{Document as JSON\}"_
 * _"Missing Arguments : Database Name"_
 * _"Missing Arguments : Document ID"_
 * _"Error while Fetching Document : \{exception\}"_


**Save Blob**

```
var blobMeta = CBL.setBlob(contentType,blob);
```

_Params_

 * contentType: MIME content type of the binary data
 * blob:  Base64 encoded string corresponding to binary data to be stored

_Example Response_

 * _"\{BLOB Meta Data\}"_: Synchronously returns a String of JSON Object of blob Meta Data which can be used retrieve blob by passing object to getBlob function.
 * _"Missing Arguments : Content Type"_
 * _"Missing Arguments : Blob Data"_
 * _"Error while Creating Blob : \{exception\}"_


**Get Blob**

```

CBL.getBlob(blobMeta,this.success_callback,this.error_callback);
```

_Params_

  * _blobMeta_: Meta Data JSON object of Blob which is returned from save blob function.
  * _Error Callback_: Asynchronously triggers when the function fails execution. Contains Error string as param, If there is an exception while execution the param will have the string exception.
  * _Success Callback_: Asynchronously triggers when the function succeeds execution. Contains string Response as param, If there is no exception while execution the param can contain one of the following responses.


#### Example Response from Get Blob :
> * _"\{Base64 encoded Blob String\}"_
> * _"Blob not found"_
> * _"Missing Arguments : BlobObject"_
> * _"Invalid Arguments : Blob Object is not in proper JSON format"_
> * _"Error while Fetching Blob : \{exception\}"_



## Updates to Native Module

If you update the plugin such as adding a new API, don't forget to  remove the plugin and re-add it to the app
