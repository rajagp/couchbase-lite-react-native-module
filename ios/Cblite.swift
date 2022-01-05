import CouchbaseLiteSwift

@objc(Cblite)
class Cblite: NSObject {
    private var _TAG = "CBlite"
    var TAG: String {
        get { return _TAG }
    }
    
    @objc
    func CreateOrOpenDatabase(_ dbname: String,
                              config: [String:Any],
                              OnSuccessCallback:RCTResponseSenderBlock,
                              OnErrorCallback: RCTResponseErrorBlock) {
        var databaseArgs = DatabaseArgs()
        do {
            if !dbname.isEmpty {
                if config.count > 0 {
                    databaseArgs = DatabaseArgs(dbname: dbname, databaseConfig: config)
                } else {
                    databaseArgs = DatabaseArgs(dbname: dbname)
                }
                let response = try DatabaseManager.shared.openOrCreateDatabase(args: databaseArgs)
                if response == ResponseStrings.DBExists || response == ResponseStrings.SuccessCode {
                    OnSuccessCallback([response])
                } else {
                    let error = NSError(domain: "", code: 0, userInfo: ["Error":response])
                    OnErrorCallback(error)
                }
            } else {
                let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.MissingargsDBN])
                OnErrorCallback(error)
            }
        } catch let error {
            let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.ExceptionDB + error.localizedDescription])
            OnErrorCallback(error)
        }
    }
    
    @objc
    func closeDatabase(_ dbname: String,
                       OnSuccessCallback:RCTResponseSenderBlock,
                       OnErrorCallback: RCTResponseErrorBlock) {
        do {
            if !dbname.isEmpty {
                let response = try DatabaseManager.shared.closeDatabase(dbName: dbname)
                if response == ResponseStrings.SuccessCode {
                    OnSuccessCallback([response])
                } else {
                    let error = NSError(domain: "", code: 0, userInfo: ["Error":response])
                    OnErrorCallback(error)
                }
            } else {
                let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.MissingargsDBN])
                OnErrorCallback(error)
            }
        } catch let error {
            let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.ExceptionDBclose + error.localizedDescription])
            OnErrorCallback(error)
        }
    }
    
    @objc
    func deleteDatabase(_ dbname: String,
                        resolve:@escaping RCTPromiseResolveBlock,
                        reject:@escaping RCTPromiseRejectBlock) {
        do {
            if !dbname.isEmpty {
                resolve(try DatabaseManager.shared.deleteDatabase(dbName: dbname))
                return;
            } else {
                let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.MissingargsDBN])
                reject("0", "Error", error)
                return;
            }
        } catch let error {
            let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.ExceptionDBdelete + error.localizedDescription])
            reject("0", "Error", error)
        }
    }
    
    @objc
    func copyDatabase(_ currentdbname: String,
                      newDBName: String,
                      currentConfig: [String:Any],
                      newConfig: [String:Any],
                      OnSuccessCallback:RCTResponseSenderBlock,
                      OnErrorCallback: RCTResponseErrorBlock) {
        do {
            let currentDatabaseArgs = DatabaseArgs(dbname: currentdbname, databaseConfig: currentConfig)
            let newDatabaseArgs = DatabaseArgs(dbname: newDBName, databaseConfig: newConfig)
            
            if currentdbname.isEmpty {
                let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.Missingargs + "Current Database Name"])
                OnErrorCallback(error)
            } else if newDBName.isEmpty {
                let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.Missingargs + "New Database Name"])
                OnErrorCallback(error)
            } else if currentConfig.count < 1 {
                let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.Missingargs + "Current Database Config"])
                OnErrorCallback(error)
            } else if newConfig.count < 1 {
                let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.Missingargs + "New Database Config"])
                OnErrorCallback(error)
            } else {
                let response = try DatabaseManager.shared.copyDatabase(cargs: currentDatabaseArgs, nargs: newDatabaseArgs)
                if response == ResponseStrings.SuccessCode {
                    OnSuccessCallback([response])
                } else {
                    let error = NSError(domain: "", code: 0, userInfo: ["Error":response])
                    OnErrorCallback(error)
                }
            }
        } catch let error {
            let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.ExceptionDBcopy + error.localizedDescription])
            OnErrorCallback(error)
        }
    }
    
    @objc
    func databaseExists(_ dbname: String,
                        config: [String:Any],
                        resolve:@escaping RCTPromiseResolveBlock,
                        reject:@escaping RCTPromiseRejectBlock) {
        if !dbname.isEmpty {
            var databaseArgs = DatabaseArgs()
            if config.count > 0 {
                databaseArgs = DatabaseArgs(dbname: dbname, databaseConfig: config)
            } else {
                databaseArgs = DatabaseArgs(dbname: dbname)
            }
            resolve(DatabaseManager.shared.databaseExists(args: databaseArgs))
        } else {
            let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.MissingargsDBN])
            reject("0", "Error", error)
        }
    }
    
    @objc
    func deleteDocument(_ dbname: String,
                        docid: String,
                       OnSuccessCallback:RCTResponseSenderBlock,
                       OnErrorCallback: RCTResponseErrorBlock) {
        do {
            var documentArgs = DocumentArgs()
            if dbname.isEmpty {
                let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.MissingargsDBN])
                OnErrorCallback(error)
            } else if docid.isEmpty {
                let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.MissingargsDCID])
                OnErrorCallback(error)
            } else {
                documentArgs = DocumentArgs(dbname: dbname, docid: docid)
                let response = try DatabaseManager.shared.deleteDocument(documentArgs: documentArgs)
                if !response.isEmpty {
                    if response == ResponseStrings.SuccessCode {
                        OnSuccessCallback([response])
                    } else {
                        let error = NSError(domain: "", code: 0, userInfo: ["Error":response])
                        OnErrorCallback(error)
                    }
                }
            }
        } catch let error {
            let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.Exception + error.localizedDescription])
            OnErrorCallback(error)
        }
    }
    
    @objc
    func getDocument(_ dbname: String,
                     docid: String,
                     OnSuccessCallback:RCTResponseSenderBlock,
                     OnErrorCallback: RCTResponseErrorBlock) {
        do {
            var documentArgs = DocumentArgs()
            if dbname.isEmpty {
                let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.MissingargsDBN])
                OnErrorCallback(error)
            } else if docid.isEmpty {
                let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.MissingargsDCID])
                OnErrorCallback(error)
            } else {
                documentArgs = DocumentArgs(dbname: dbname, docid: docid)
                if let response = try DatabaseManager.shared.getDocument(args: documentArgs) {
                    if !response.isEmpty {
                        if response != ResponseStrings.DBnotfound && response != ResponseStrings.Docnotfound {
                            OnSuccessCallback([response])
                        } else {
                            let error = NSError(domain: "", code: 0, userInfo: ["Error":response])
                            OnErrorCallback(error)
                        }
                    } else {
                        let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.NullDoc])
                        OnErrorCallback(error)
                    }
                }
            }
        } catch let error {
            let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.ExceptionDOCGet + error.localizedDescription])
            OnErrorCallback(error)
        }
    }
    
    @objc
    func setDocument(_ dbname: String,
                     docid: String,
                     data: [String:Any],
                     OnSuccessCallback:RCTResponseSenderBlock,
                     OnErrorCallback: RCTResponseErrorBlock) {
        var documentArgs = DocumentArgs()
        if dbname.isEmpty {
            let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.MissingargsDBN])
            OnErrorCallback(error)
        } else if docid.isEmpty {
            let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.MissingargsDCID])
            OnErrorCallback(error)
        } else if data.isEmpty {
            let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.MissingargsDCData])
            OnErrorCallback(error)
        } else {
            documentArgs = DocumentArgs(dbname: dbname, docid: docid, jsondata: data)
            let response = DatabaseManager.shared.setDocument(args: documentArgs)
            if !response.isEmpty {
                if response == ResponseStrings.SuccessCode {
                    OnSuccessCallback([response])
                } else {
                    let error = NSError(domain: "", code: 0, userInfo: ["Error":response])
                    OnErrorCallback(error)
                }
            } else {
                let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.ExceptionDOC])
                OnErrorCallback(error)
            }
        }
    }
    
    @objc
    func setBlob(_ dbname: String,
                 type: String,
                 docObject: String,
                 key: String,
                 config: [String:Any],
                 resolve:@escaping RCTPromiseResolveBlock,
                 reject:@escaping RCTPromiseRejectBlock) {
        if dbname.isEmpty {
            let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.MissingargsDBN])
            reject("0", "Error", error)
        } else if type.isEmpty {
            let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.Missingargs + "Content Type"])
            reject("0", "Error", error)
        } else if docObject.isEmpty {
            let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.Missingargs + "Blob Data"])
            reject("0", "Error", error)
        } else {
            do {
                resolve(try DatabaseManager.shared.setBlob(dbname: dbname,
                                                   type: type,
                                                   blobdata: docObject,
                                                   key: key))
            } catch let error {
                let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.ExceptionBLOB + error.localizedDescription])
                reject("0", "Error", error)
            }
        }
    }
    
    @objc
    func getBlob(_ dbname: String,
                 documentId: String,
                 key: String,
                 OnSuccessCallback:RCTResponseSenderBlock,
                 OnErrorCallback: RCTResponseErrorBlock) {
        if dbname.isEmpty {
            let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.MissingargsDBN])
            OnErrorCallback(error)
        } else if documentId.isEmpty {
            let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.MissingargsDCID])
            OnErrorCallback(error)
        } else if documentId.isEmpty {
            let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.Missingargs + "Missing Key"])
            OnErrorCallback(error)
        } else {
            do {
                let response = try DatabaseManager.shared.getBlob(dbname: dbname,
                                                                  documentId: documentId,
                                                                  key: key)
                if response != ResponseStrings.DBnotfound && response != ResponseStrings.invalidblob {
                    OnSuccessCallback([response])
                } else {
                    let error = NSError(domain: "", code: 0, userInfo: ["Error":response])
                    OnErrorCallback(error)
                }
            } catch let error {
                let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.ExceptionBLOBget + error.localizedDescription])
                OnErrorCallback(error)
            }
        }
    }
    
    @objc
    func createValueIndex(_ dbname: String, indexName: String, indexExpressions: [String],
                          resolve:@escaping RCTPromiseResolveBlock,
                          reject:@escaping RCTPromiseRejectBlock) {
        do {
        if dbname.isEmpty {
            let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.MissingargsDBN])
            reject("0", "Error", error)
        } else if indexName.isEmpty {
            let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.MissingargsIN])
            reject("0", "Error", error)
        } else if indexExpressions.count < 1 {
            let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.MissingargsINEX])
            reject("0", "Error", error)
        } else {
            var parsedIndexExpressions = [String]()
            indexExpressions.forEach { index in
                parsedIndexExpressions.append(index)
            }
            let vargs = IndexArgs()
            vargs.ValueIndexArgs(dbName: dbname, indexName: indexName, indexExpressions: parsedIndexExpressions)
            let result = try DatabaseManager.shared.createValueIndex(args: vargs)
            resolve(result)
        }
        } catch let error {
            let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.Exception + error.localizedDescription])
            reject("0", "Error", error)
        }
    }
    
    @objc
    func createFTSIndex(_ dbname: String, indexName: String, options: [String:Any], indexExpressions: [String],
                          resolve:@escaping RCTPromiseResolveBlock,
                          reject:@escaping RCTPromiseRejectBlock) {
        do {
            if dbname.isEmpty {
                let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.MissingargsDBN])
                reject("0", "Error", error)
            } else if indexName.isEmpty {
                let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.MissingargsIN])
                reject("0", "Error", error)
            } else if indexExpressions.count < 1 {
                let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.MissingargsINEX])
                reject("0", "Error", error)
            } else if options.count < 1 || !options.keys.contains("ignoreAccents") || !options.keys.contains("language"){
                let error = NSError(domain: "", code: 0, userInfo: ["Error":"Options must not be empty. It must have 'ignoreAccents' and 'language' as elements."])
                reject("0", "Error", error)
            } else {
                var iAccents = false
                var lang = ""
                if let ignoreAccents = options["ignoreAccents"] as? Bool {
                    iAccents = ignoreAccents
                } else {
                    let error = NSError(domain: "", code: 0, userInfo: ["Error":"'ignoreAccents'must be a 'boolean'"])
                    reject("0", "Error", error)
                }
                if let language = options["language"] as? String {
                    lang = language
                } else {
                    let error = NSError(domain: "", code: 0, userInfo: ["Error":"'language'must be a 'string'"])
                    reject("0", "Error", error)
                }
                var parsedIndexExpressions = [String]()
                indexExpressions.forEach { index in
                    parsedIndexExpressions.append(index)
                }
                let fargs = IndexArgs()
                fargs.FTSIndexArgs(dbName: dbname, indexName: indexName, ignoreAccents: iAccents, language: lang, indexExpressions: parsedIndexExpressions)
                let result = try DatabaseManager.shared.createValueIndex(args: fargs)
                resolve(result)
                
            }
        } catch let error {
            let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.Exception + error.localizedDescription])
            reject("0", "Error", error)
        }
    }
    
    @objc
    func deleteIndex(_ dbname: String, indexName: String, resolve:@escaping RCTPromiseResolveBlock, reject:@escaping RCTPromiseRejectBlock) {
        do {
            if dbname.isEmpty {
                let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.MissingargsDBN])
                reject("0", "Error", error)
            } else if indexName.isEmpty {
                let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.MissingargsIN])
                reject("0", "Error", error)
            } else {
                let fargs = IndexArgs()
                fargs.DeleteIndexArgs(dbName: dbname, indexName: indexName)
                let result = try DatabaseManager.shared.deleteIndex(args: fargs)
                resolve(result)
            }
        } catch let error {
            let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.Exception + error.localizedDescription])
            reject("0", "Error", error)
        }
    }
    
    @objc
    func addDatabaseChangeListener(_ dbname: String, listener: String, resolve:@escaping RCTPromiseResolveBlock, reject:@escaping RCTPromiseRejectBlock) {
        if dbname.isEmpty {
            let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.MissingargsDBN])
            reject("0", "Error", error)
        } else if listener.isEmpty {
            let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.Missingargs + "JSListener"])
            reject("0", "Error", error)
        } else {
            let result = DatabaseManager.shared.registerForDatabaseChanges(dbname: dbname, jsListener: listener)
            if result == ResponseStrings.SuccessCode {
                resolve(result)
            } else {
                let error = NSError(domain: "", code: 0, userInfo: ["Error":result])
                reject("0", "Error", error)
            }
        }
    }
    
    @objc
    func removeDatabaseChangeListener(_ dbname: String, resolve:@escaping RCTPromiseResolveBlock, reject:@escaping RCTPromiseRejectBlock) {
        if dbname.isEmpty {
            let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.MissingargsDBN])
            reject("0", "Error", error)
        } else {
            let result = DatabaseManager.shared.deregisterForDatabaseChanges(dbname: dbname)
            if result == ResponseStrings.SuccessCode {
                resolve(result)
            } else {
                let error = NSError(domain: "", code: 0, userInfo: ["Error":result])
                reject("0", "Error", error)
            }
        }
    }
    
    @objc
    func enableConsoleLogging(_ domain: String, logLevel: String, resolve:@escaping RCTPromiseResolveBlock, reject:@escaping RCTPromiseRejectBlock) {
        if domain.isEmpty {
            let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.Missingargs+"domain"])
            reject("0", "Error", error)
        } else if logLevel.isEmpty {
            let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.Missingargs+"logLevel"])
            reject("0", "Error", error)
        } else {
            let result = DatabaseManager.shared.enableLogging(domain: domain, logLevel: logLevel)
            if result == ResponseStrings.SuccessCode {
                resolve(ResponseStrings.SuccessCode)
            } else {
                let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.ExceptionEnableLogging])
                reject("0", "Error", error)
            }
        }
    }
    
    @objc
    func createQuery(_ dbname: String, query: String, resolve:@escaping RCTPromiseResolveBlock, reject:@escaping RCTPromiseRejectBlock) {
        do {
            if dbname.isEmpty {
                let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.MissingargsDBN])
                reject("0", "Error", error)
            } else if query.isEmpty {
                let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.Missingargs+"Query"])
                reject("0", "Error", error)
            } else {
                let qArgs = QueryArgs(dbName: dbname, query: query)
                let result = try DatabaseManager.shared.createQuery(args: qArgs)
                if result != ResponseStrings.DBnotfound
                    && result != ResponseStrings.NoArgs
                    && result != ResponseStrings.ExceptionInvalidQuery
                    && result != ResponseStrings.invaliddata {
                    resolve(result)
                } else {
                    let error = NSError(domain: "", code: 0, userInfo: ["Error":result])
                    reject("0", "Error", error)
                }
            }
        } catch let error {
            let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.ExceptionQuery+error.localizedDescription])
            reject("0", "Error", error)
        }
    }
    
    @objc
    func query(_ dbname: String, query: String, OnSuccessCallback:RCTResponseSenderBlock, OnErrorCallback: RCTResponseErrorBlock) {
        do {
            if dbname.isEmpty {
                let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.MissingargsDBN])
                OnErrorCallback(error)
            } else if query.isEmpty {
                let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.Missingargs + "Query"])
                OnErrorCallback(error)
            } else {
                let qArgs = QueryArgs(dbName: dbname, query: query)
                let result = try DatabaseManager.shared.queryDb(args: qArgs)
                if result != ResponseStrings.DBnotfound
                    && result != ResponseStrings.NoArgs
                    && result != ResponseStrings.ExceptionInvalidQuery
                    && result != ResponseStrings.invaliddata {
                    OnSuccessCallback([result])
                } else {
                    let error = NSError(domain: "", code: 0, userInfo: ["Error":result])
                    OnErrorCallback(error)
                }
            }
        } catch let error {
            let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.ExceptionQuery+error.localizedDescription])
            OnErrorCallback(error)
        }
    }
    
    @objc
    func queryWithChangeListener(_ dbname: String, query: String, listener: String, resolve:@escaping RCTPromiseResolveBlock, reject:@escaping RCTPromiseRejectBlock) {
        do {
        if dbname.isEmpty {
            let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.MissingargsDBN])
            reject("0", "Error", error)
        } else if query.isEmpty {
            let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.Missingargs+"Query"])
            reject("0", "Error", error)
        } else if listener.isEmpty {
            let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.Missingargs+"JSListener"])
            reject("0", "Error", error)
        } else {
            let result = try DatabaseManager.shared.registerForQueryChanges(dbname: dbname, query: query, jsListener: listener)
            resolve(result)
        }
        } catch let error {
            let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.Exception + error.localizedDescription])
            reject("0", "Error", error)
        }
    }
    
    @objc
    func removeQueryChangeListener(_ dbname: String, query: String, resolve:@escaping RCTPromiseResolveBlock, reject:@escaping RCTPromiseRejectBlock) {
        do {
            if dbname.isEmpty {
                let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.MissingargsDBN])
                reject("0", "Error", error)
            } else if query.isEmpty {
                let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.Missingargs+"Query"])
                reject("0", "Error", error)
            } else {
                let result = try DatabaseManager.shared.deregisterForQueryChanges(dbname: dbname, queryString: query)
                resolve(result)
            }
        } catch let error {
            let error = NSError(domain: "", code: 0, userInfo: ["Error":ResponseStrings.Exception + error.localizedDescription])
            reject("0", "Error", error)
        }
    }
    
    @objc
    static func requiresMainQueueSetup() -> Bool {
        return true
    }
}
