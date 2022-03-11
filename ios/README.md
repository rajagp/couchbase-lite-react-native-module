## Getting Started

We will look at the steps to integrate and use the react native module within a sample React Native iOS app. The instructions assume some familiarity with [React Native app development](https://reactnative.dev/docs/environment-setup). You will do something similar when integrating into your own app. 

The plugin will be integrated into your using Cocoapods.

### Step 1: Add the module into your App

*  Create a sample React Native app named "AwesomeProject" for iOS as per instructions in the [Starter's Guide](https://reactnative.dev/docs/environment-setup). If you have never done React Native development, follow the instructions on the same page to set up your environment for React Native app development for iOS.
 
 For the rest of the instructions, we will assume that you have created a sample React Native project named "AwesomeProject" for iOS.
**NOTE:** You will need node version 12+ for iOS app development. 

*  Install yarn from within your root folder (if not installed already)

```bash
cd  /path/to/AwesomeProject
npm install yarn
```

* Add the plugin by adding the appropriate Github repo. If you fork the repo and modify it, then be sure to point it to the right URL!

```bash
yarn add https://github.com/coucbaselabs/couchbase-lite-react-native-module
```


### Step 2: Install the plugin with Couchbase Lite-iOS framework as a dependency

The module includes couchbase lite iOS framework as a depedency as specified in the `react-native-cblite.podspec` file located in the root folder of the repo. The module pulls down the framework using Cocoapods. 
 
The module requires minimal version of **Couchbase Lite iOS v3.0.1**. You can edit the podspec file to change the edition (Community or Enterprise) and/or version of Couchbase Lite.

```bash
cd /path/to/AwesomeProject/ios
pod install
```

* Open the `.xcframework` project using Xcode. We recommend min Xcode v12.4

```bash
cd /path/to/AwesomeProject/ios
open AwesomeProject.xcworkspace
```

### Step 3: Using the module

To use the module, declare the plugin at the start of the `app.js` file and you can invoke the CBL APIs exported by the plugin. The `AwesomeProject` folder can be opened using any suitable IDE for JS apps.

Here's an example

```bash
import React from 'react';
import type {Node} from 'react';
import * as CBL from 'react-native-cblite';

....
CBL.CreateOrOpenDatabase(dbName,config, function(rs) { 
  console.log("database "+ dbName + " creation: "+ rs.toString())
  }, function(error) { 
    console.log(error.toString())
    });
```

### Step 4: Build and Run

Build and run the app per instructions in [Getting Started Guide](https://reactnative.dev/docs/environment-setup). You can run the app direcly from Xcode or from command line.

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
