import { NativeModules } from 'react-native';

type CbliteAndroidModuleType = {
  multiply(a: number, b: number): Promise<number>;
};

const { CbliteAndroidModule } = NativeModules;

export default CbliteAndroidModule as CbliteAndroidModuleType;
