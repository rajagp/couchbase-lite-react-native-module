import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-cblite' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", android: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo managed workflow\n';

const linkin_err = new Proxy(
  {},
  {
    get() {
      throw new Error(LINKING_ERROR);
    },
  }
);

const Cblite = Platform.OS == 'ios'? (NativeModules.Cblite
  ? NativeModules.Cblite :linkin_err) : NativeModules? NativeModules : linkin_err;

export function CreateOrOpenDatabase(
  dbname: string,
  config: Object,
  OnSuccessCallback: Function,
  OnErrorCallback: Function
): void {
  Cblite.CreateOrOpenDatabase(
    dbname,
    config,
    OnSuccessCallback,
    OnErrorCallback
  );
}
export function closeDatabase(
  dbname: string,
  OnSuccessCallback: Function,
  OnErrorCallback: Function
): void {
  Cblite.closeDatabase(dbname, OnSuccessCallback, OnErrorCallback);
}
export function deleteDatabase(dbname: string): string {
  return Cblite.deleteDatabase(dbname);
}
export function copyDatabase(
  currentdbname: string,
  newDBName: string,
  currentConfig: Object,
  newConfig: Object,
  OnSuccessCallback: Function,
  OnErrorCallback: Function
): void {
  Cblite.copyDatabase(
    currentdbname,
    newDBName,
    currentConfig,
    newConfig,
    OnSuccessCallback,
    OnErrorCallback
  );
}
export function databaseExists(
  dbname: string,
  config: Object
): string {
  return Cblite.databaseExists(dbname, config);
}
export function deleteDocument(
  dbname: string,
  docid: string,
  OnSuccessCallback: Function,
  OnErrorCallback: Function
): void {
  Cblite.deleteDocument(dbname, docid, OnSuccessCallback, OnErrorCallback);
}
export function getDocument(
  dbname: string,
  docid: string,
  OnSuccessCallback: Function,
  OnErrorCallback: Function
): void {
  Cblite.getDocument(dbname, docid, OnSuccessCallback, OnErrorCallback);
}
export function setDocument(
  dbname: string,
  docid: string,
  data: string,
  OnSuccessCallback: Function,
  OnErrorCallback: Function
): void {
  Cblite.setDocument(dbname, docid, data, OnSuccessCallback, OnErrorCallback);
}
export function setBlob(
  dbname: string,
  type: string,
  imagedata: string,
): string {
  return Cblite.setBlob(dbname, type, imagedata);
}
export function getBlob(
  dbname: string,
  blobData: object,
  OnSuccessCallback: Function,
  OnErrorCallback: Function
): void {
  Cblite.getBlob(dbname, blobData, OnSuccessCallback, OnErrorCallback);
}
export function createValueIndex(
  dbname: string,
  indexName: string,
  indexExpressions: [string]
): string {
  return Cblite.createValueIndex(dbname, indexName, indexExpressions);
}
export function createFTSIndex(
  dbname: string,
  indexName: string,
  options: Object,
  indexExpressions: [string]
): string {
  return Cblite.createFTSIndex(dbname, indexName, options, indexExpressions);
}
export function deleteIndex(
  dbname: string,
  indexName: string
): string {
  return Cblite.deleteIndex(dbname, indexName);
}
export function addDatabaseChangeListener(
  dbname: string,
  listener: string
): string {
  return Cblite.addDatabaseChangeListener(dbname, listener);
}
export function removeDatabaseChangeListener(dbname: string): string {
  return Cblite.removeDatabaseChangeListener(dbname);
}
export function enableConsoleLogging(
  domain: string,
  logLevel: string
): string {
  return Cblite.enableConsoleLogging(domain, logLevel);
}
export function createQuery(dbname: string, query: string): string {
  return Cblite.createQuery(dbname, query);
}
export function query(
  dbname: string,
  // eslint-disable-next-line no-shadow
  query: string,
  OnSuccessCallback: Function,
  OnErrorCallback: Function
): void {
  Cblite.query(dbname, query, OnSuccessCallback, OnErrorCallback);
}
export function queryWithChangeListener(
  dbname: string,
  // eslint-disable-next-line no-shadow
  query: string,
  listener: string
): string {
  return Cblite.queryWithChangeListener(dbname, query, listener);
}
export function removeQueryChangeListener(
  dbname: string,
  // eslint-disable-next-line no-shadow
  query: string
): string {
  return Cblite.removeQueryChangeListener(dbname, query);
}
