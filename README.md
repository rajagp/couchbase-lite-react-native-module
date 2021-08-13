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

## Getting started
The plugin can be integrated within react-native projects.

The step-by-step instructions below illustrates how you can integrate and use the plugin within a blank React Native Project for Android platform. You will do something similar when building your own app.
 
* Create a blank react-native app and inside your react-native project folder install yarn. 
* Install the plugin by using yarn.
* Add Couchbase lite android framework as a dependency.

***

First you will need to download the react-native module from github by clicking on download -> download zip.

Extract the downloaded zip file in any folder and copy the path e.g. `desktop\react\plugin`.

For start create a blank react-native project using `react-native init awesomeproject` command in terminal.

Then open your project folder in terminal using `cd awesomeproject` command.

Make sure you already have npm installed in your system if not then first install npm.

After installing npm follow steps stated below for plugin installation :

### Install yarn using NPM
```
npm install yarn
```


### install plugin using Yarn
```
Yarn add “<react native module path>”
```

### Adding couchbase-lite-android framework as a dependency

The plugin does not come bundled with the react-native plugin. You will have to include the appropriately licensed Couchbase Lite Android library as dependency within your app. The React Native reference plugin requires minimal version of **Couchbase Lite v3.0.0**. 

Couchbase Lite can be downloaded from Couchbase [downloads](https://www.couchbase.com/downloads) page or can be pulled in via maven as described in [Couchbase Lite Android Getting Started Guides](https://docs.couchbase.com/couchbase-lite/current/android/gs-install.html).

We discuss the steps to add the Couchbase Lite framework dependency depending on how you downloaded the framework. 

* Open the Android project located inside your React Native project under directory: `/path/to/react-native-app/android` using Android Studio.

**To add couchbase-lite-android as an .aar file**

* Create a new directory called 'libs' under your /android/app/ folder
* Copy the .aar file within that 'libs' folder 

* In your 'Project' level `build.gradle` file and add `flatDir{dirs 'libs'}` like did below. 
```
allprojects {
   repositories {
      jcenter()
      flatDir {
        dirs 'libs'
      }
   }
}
```

* In your 'app' level `build.gradle` file and add your library file under dependencies. 
```bash
dependencies {
    implementation(name:'couchbase-lite-android-ee-3.0.0', ext:'aar')
    .......
}
```



# Sample Usage Instructions

Here are a few examples of using the plugin in your app

To use the plugin, open your react-native app and declare the plugin at the on top of your app.js file.

```
import CbliteAndroid from 'react-native-cblite-android';
```

## Create Database
```
CbliteAndroid.createDatabase(dbName,config,this.success_callback,this.error_callback);
```

### Params

1. **dbName :** Name of the Database as string.
2. **config :** Couchbase Database configuration JSONobject containing following.
    * **directory :** Path of the database directory as string.
    * **Encryption :** Encryption key as string.
3. **Error Callback :** Asynchronously triggers when the function fails execution. Contains Error string as param, If there is an exception while execution the param will have the string exception.
4. **Success Callback :** Asynchronously triggers when the function succeeds execution. Contains string Response as param, If there is no exception while execution the param can contain one of the following responses.

#### Example Response from Create Database :
> * _"Database Created"_
> * _"Missing Arguments : Database Name"_
> * _"Missing Arguments : Directory"_
> * _"Error while Creating Database : \{exception\}"_




## Close Database
```
CbliteAndroid.closeDatabase(dbName);
```

### Params

1. **dbName :** Name of the Database as string.

#### Example Response from Create Database :
> * _"Database Closed"_
> * _"Error while Closing Database : \{exception\}"_




## Create Document
```
CbliteAndroid.setDocument(dbName,docid,data,this.success_callback,this.error_callback);
```

### Params

1. **dbName :** Name of the Database as string.
2. **docid :** Unique id of the document as string.
3. **data :** A JSON object containing data to be saved in document.
4. **Error Callback :** Asynchronously triggers when the function fails execution. Contains Error string as param, If there is an exception while execution the param will have the string exception.
5. **Success Callback :** Asynchronously triggers when the function succeeds execution. Contains string Response as param, If there is no exception while execution the param can contain one of the following responses.

#### Example Response from Create Document :
> * _"Document Created"_
> * _"Document is Null"_
> * _"Document not found"_
> * _"Database not found"_
> * _"Missing Arguments : Database Name"_
> * _"Missing Arguments : Document ID"_
> * _"Missing Arguments : Document Data"_
> * _"Invalid Arguments : Document data is not in proper JSON format"_
> * _"Error while Creating Document : \{exception\}"_





## Get Document
```
CbliteAndroid.getDocument(dbname,docid,this.success_callback,this.error_callback);
```

### Params

1. **dbName :** Name of the Database as string.
2. **docid :** Unique id of the document as string.
3. **Error Callback :** Asynchronously triggers when the function fails execution. Contains Error string as param, If there is an exception while execution the param will have the string exception.
4. **Success Callback :** Asynchronously triggers when the function succeeds execution. Contains string Response as param, If there is no exception while execution the param can contain one of the following responses.

#### Example Response from Get Document :
> * _"\{Document as JSON\}"_
> * _"Missing Arguments : Database Name"_
> * _"Missing Arguments : Document ID"_
> * _"Error while Fetching Document : \{exception\}"_






## Save Blob
```
var BlobJSON = CbliteAndroid.setBlob(ContentType,Blob);
```

### Params

1. **ContentType :** Content type of the Blob object as string.
2. **Blob :** Base64 encoded blob string.
3. **Retrun :** Synchronously returns a String of JSON Object of blob Meta Data which can be used retrieve blob by passing object to getBlob function.

#### Example Response from Save Blob :
> * _"\{BLOB Meta Data\}"_
> * _"Missing Arguments : Content Type"_
> * _"Missing Arguments : Blob Data"_
> * _"Error while Creating Blob : \{exception\}"_





## Get Blob
```
CbliteAndroid.getBlob(blobMeta,this.success_callback,this.error_callback);
```

### Params

1. **blobMeta :** Meta Data JSON object of Blob which is returned from save blob function.
2. **Error Callback :** Asynchronously triggers when the function fails execution. Contains Error string as param, If there is an exception while execution the param will have the string exception.
3. **Success Callback :** Asynchronously triggers when the function succeeds execution. Contains string Response as param, If there is no exception while execution the param can contain one of the following responses.


#### Example Response from Get Blob :
> * _"\{Base64 encoded Blob String\}"_
> * _"Blob not found"_
> * _"Missing Arguments : BlobObject"_
> * _"Invalid Arguments : Blob Object is not in proper JSON format"_
> * _"Error while Fetching Blob : \{exception\}"_




# Sample App
TBD (Add reference to user profile sample app)

# Couchbase Lite Version
This version of React Native Module requires Couchbase Lite 3.0.0(Beta).

