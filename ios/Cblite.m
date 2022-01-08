#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(Cblite, NSObject)

RCT_EXTERN_METHOD(multiply:(float)a withB:(float)b
                 withResolver:(RCTPromiseResolveBlock)resolve
                 withRejecter:(RCTPromiseRejectBlock)reject)
RCT_EXTERN_METHOD(CreateOrOpenDatabase:(NSString *)dbname config:(NSDictionary *)config OnSuccessCallback:(RCTResponseSenderBlock)OnSuccessCallback OnErrorCallback:(RCTResponseErrorBlock)OnErrorCallback)

RCT_EXTERN_METHOD(closeDatabase:(NSString *)dbname OnSuccessCallback:(RCTResponseSenderBlock)OnSuccessCallback OnErrorCallback:(RCTResponseErrorBlock)OnErrorCallback)

RCT_EXTERN_METHOD(deleteDatabase:(NSString *)dbname resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(copyDatabase:(NSString *)currentdbname newDBName:(NSString *)newDBName currentConfig:(NSDictionary *)currentConfig newConfig:(NSDictionary *)newConfig OnSuccessCallback:(RCTResponseSenderBlock)OnSuccessCallback OnErrorCallback:(RCTResponseErrorBlock)OnErrorCallback)

RCT_EXTERN_METHOD(databaseExists:(NSString *)dbname config:(NSDictionary *)config resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(deleteDocument:(NSString *)dbname docid:(NSString *)docid OnSuccessCallback:(RCTResponseSenderBlock)OnSuccessCallback OnErrorCallback:(RCTResponseErrorBlock)OnErrorCallback)

RCT_EXTERN_METHOD(getDocument:(NSString *)dbname docid:(NSString *)docid OnSuccessCallback:(RCTResponseSenderBlock)OnSuccessCallback OnErrorCallback:(RCTResponseErrorBlock)OnErrorCallback)

RCT_EXTERN_METHOD(setDocument:(NSString *)dbname docid:(NSString *)docid data:(NSString *)data OnSuccessCallback:(RCTResponseSenderBlock)OnSuccessCallback OnErrorCallback:(RCTResponseErrorBlock)OnErrorCallback)

RCT_EXTERN_METHOD(setBlob:(NSString *)dbname type:(NSString *)type docObject:(NSString *)docObject resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(getBlob:(NSString *)dbname key:(NSString *)key OnSuccessCallback:(RCTResponseSenderBlock)OnSuccessCallback OnErrorCallback:(RCTResponseErrorBlock)OnErrorCallback)

RCT_EXTERN_METHOD(createValueIndex:(NSString *)dbname indexName:(NSString *)indexName indexExpressions:(NSArray *)indexExpressions resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(createFTSIndex:(NSString *)dbname indexName:(NSString *)indexName options:(NSDictionary *)options indexExpressions:(NSArray *)indexExpressions resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(deleteIndex:(NSString *)dbname indexName:(NSString *)indexName resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(addDatabaseChangeListener:(NSString *)dbname listener:(NSString *)listener resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(removeDatabaseChangeListener:(NSString *)dbname resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(enableConsoleLogging:(NSString *)domain logLevel:(NSString *)logLevel resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(createQuery:(NSString *)dbname query:(NSString *)query resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(createQuery:(NSString *)dbname query:(NSString *)query OnSuccessCallback:(RCTResponseSenderBlock)OnSuccessCallback OnErrorCallback:(RCTResponseErrorBlock)OnErrorCallback)

RCT_EXTERN_METHOD(queryWithChangeListener:(NSString *)dbname query:(NSString *)query listener:(NSString *)listener resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(removeQueryChangeListener:(NSString *)dbname query:(NSString *)query resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject)
@end
