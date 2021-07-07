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
TBD

# Sample Usage Instructions
TBD

# Sample App
TBD (Add reference to user profile sample app)

# Couchbase Lite Version
This version of React Native Module requires Couchbase Lite 3.0.0(Beta).

