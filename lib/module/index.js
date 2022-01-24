import { NativeModules, Platform } from 'react-native';
const LINKING_ERROR =
  `The package 'react-native-cblite' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({
    ios: "- You have run 'pod install'\n",
    default: '',
  }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo managed workflow\n';
const Cblite = NativeModules.Cblite
  ? NativeModules.Cblite
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );
export function multiply(a, b) {
  return Cblite.multiply(a, b);
}
export function CreateOrOpenDatabase(
  dbname,
  config,
  OnSuccessCallback,
  OnErrorCallback
) {
  Cblite.CreateOrOpenDatabase(
    dbname,
    config,
    OnSuccessCallback,
    OnErrorCallback
  );
}
//# sourceMappingURL=index.js.map
