## Getting Started

We will look at the steps to integrate and use the react native module within a sample React Native Android app. The instructions assume some familiarity with [React Native app development](https://reactnative.dev/docs/environment-setup). You will do something similar when integrating into your own app. 

### Step 1: Add the module into your App

*  Create a sample React Native app named "AwesomeProject" for Android as per instructions in the [Starter's Guide](https://reactnative.dev/docs/environment-setup). If you have never done React Native development, follow the instructions on the same page to set up your environment for React Native app development for Android.
 
 For the rest of the instructions, we will assume that you have created a sample React Native project named "AwesomeProject" for Android.
**NOTE:** You will need node version 12+ for Android app development. 

*  Install yarn from within your root folder (if not installed already)

```bash
cd  /path/to/AwesomeProject
npm install yarn
```

* Install the plugin by adding the appropriate Github repo. If you fork the repo and modify it, then be sure to point it to the right URL!

```bash
yarn add https://github.com/couchbaselabs/couchbase-lite-react-native-module
```

* Install the relevant dependencies

```bash
npm install
```

### Step 2: Add couchbase-lite-android framework as a dependency

The module does not come bundled with the couchbase lite framework. You will have to separately include the appropriately licensed Couchbase Lite Android library as dependency within your app.
 
The reference module requires minimal version of **Couchbase Lite Android v3.0.0**. 

Couchbase Lite can be downloaded from Couchbase [downloads](https://www.couchbase.com/downloads) page or can be pulled in via maven as described in [Couchbase Lite Android Getting Started Guides](https://docs.couchbase.com/couchbase-lite/current/android/gs-install.html).

We discuss the steps to add the Couchbase Lite framework dependency depending on how you downloaded the framework. 

* Open the Android project located inside your React Native project under directory: `/path/to/AwesomeProject/android` using Android Studio ().

**Confirm Build Environment for App**

Recommending min version of Android Studio 4.2

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

**To include Couchbase Lite Android SDK from maven**

This is the preferred option. Follow the instructions in [Couchbase Lite Android Getting Started Guides](https://docs.couchbase.com/couchbase-lite/current/android/gs-install.html) 

- In your 'app' level `build.gradle` file, add your library file path.

```bash
 dependencies {
    implementation 'com.couchbase.lite:couchbase-lite-android:${version}'
 }
 
```

- In your 'project' level `build.gradle` file, add your library file path. 

```bash
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


**To add Couchbase Lite-Android as an .aar file**

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

### Step 3: Using the module

To use the module, declare the plugin at the on top of your `app.js` file and you can invoke the CBL APIs exported by the plugin.The React Native project can be opened using any suitable IDE for JS apps.

Here's an example

```bash
import * as CBL from 'react-native-cblite';
CBL.CreateOrOpenDatabase(dbName,config, function(rs) { 
  console.log("database "+ dbName + " creation: "+ rs.toString())
  }, function(error) { 
    console.log(error.toString())
    });
```

### Step 4: Build and Run

Build and run the app per instructions in [Getting Started Guide](https://reactnative.dev/docs/environment-setup). You can run the app direcly from Android Studio or from command line.

Don't forget to start the Metro bundler before running your app!

```bash
npx react-native start
```


## Updates to Native Module

If you update the plugin such as adding a new API, don't forget to remove the plugin and re-add it to the app. 

### Removing the module
```bash
yarn remove react-native-cblite
```

### Adding the module
```bash
yarn add https://github.com/couchbaselabs/couchbase-lite-react-native-module
```

*Troubleshooting Tip*:
If the app isn't recognizing the latest plugin changes it may help to do a complete clean
  - remove the root level `node_modules` folder
  - Run "npm install"
  - Repeat the steps to add the module and couchbase lite package.
