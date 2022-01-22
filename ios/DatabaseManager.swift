//
//  DatabaseManager.swift
//  RNReactNativeCblite
//
//  Created by Umer on 02/12/2021.
//

//import UIKit
import CouchbaseLiteSwift

class DatabaseManager: RCTEventEmitter {
    public static var shared = DatabaseManager()
    private var _databases = [String:DatabaseResource]()
    var databases:[String:DatabaseResource] {
        get { return _databases }
        set { _databases = newValue }
    }
    private var _listenerToken: ListenerToken?
    var listenerToken:ListenerToken? {
        get { return _listenerToken }
        set { _listenerToken = newValue }
    }
    private var _hasListeners = false
    var hasListeners:Bool {
        get { return _hasListeners }
        set { _hasListeners = newValue }
    }
    
    func getDatabaseConfig(args: DatabaseArgs) -> DatabaseConfiguration? {
        let directory = args.directory
        let encryptionKey = args.encryptionKey
        
        if (directory == nil && encryptionKey == nil) || (directory!.isEmpty && encryptionKey!.isEmpty) {
            return nil
        }
        
        var dbConfig = DatabaseConfiguration()
        if directory != nil && !directory!.isEmpty{
            dbConfig.directory = directory!
        }
        
        return dbConfig;
    }
    
    func openOrCreateDatabase(args: DatabaseArgs) throws -> String {
        let dbConfig = getDatabaseConfig(args: args)
        
        if let _ = args.dbName {
            if self.databases.keys.contains(args.dbName!) {
                return ResponseStrings.DBExists
            }
        }
        do {
            if let dbName = args.dbName, let conf = dbConfig {
                let database = try Database(name: dbName, config: conf)
                self.databases[dbName] = DatabaseResource(db: database, config: conf)
                return ResponseStrings.SuccessCode
                
            } else if let dbName = args.dbName {
                let database = try Database(name: dbName)
                self.databases[dbName] = DatabaseResource(db: database)
                return ResponseStrings.SuccessCode
                
            } else {
                return ResponseStrings.MissingargsDBN
            }
        }
        catch let error {
            throw error
        }
    }
    
    func closeDatabase(dbName: String) throws -> String {
        do {
            if !self.databases.keys.contains(dbName) {
                return ResponseStrings.DBnotfound
            }
            
            if !dbName.isEmpty {
                let resource = self.databases[dbName]
                try resource?.database?.close()
                self.databases.removeValue(forKey: dbName)
                return ResponseStrings.SuccessCode
            } else {
                return ResponseStrings.MissingargsDBN
            }
        }
        catch let error {
            throw error
        }
    }
    
    func deleteDatabase(dbName: String) throws -> String {
        do {
            if !self.databases.keys.contains(dbName) {
                return ResponseStrings.DBnotfound
            }
            
            if !dbName.isEmpty {
                let resource = self.databases[dbName]
                try resource?.database?.delete()
                self.databases.removeValue(forKey: dbName)
                return ResponseStrings.SuccessCode
            } else {
                return ResponseStrings.MissingargsDBN
            }
        }
        catch let error {
            throw error
        }
    }
    
    func copyDatabase(cargs: DatabaseArgs, nargs: DatabaseArgs) throws -> String {
        do {
            let responsecreate = try self.openOrCreateDatabase(args: cargs)
            if responsecreate != ResponseStrings.SuccessCode {
                return responsecreate
            }
            
            if let dbName = cargs.dbName, let db = databases[dbName], let database = db.database, let newDbName = nargs.dbName  {
                let dbPath = database.path
                let isDirectory = UnsafeMutablePointer<ObjCBool>.allocate(capacity: 1)
                isDirectory[0] = true
                if FileManager.default.fileExists(atPath: dbPath ?? "", isDirectory: isDirectory) {
                    let newdbConfig = getDatabaseConfig(args: nargs)
                    try Database.copy(fromPath: dbPath!, toDatabase: newDbName, withConfig: newdbConfig)
                    return ResponseStrings.SuccessCode
                } else {
                    return ResponseStrings.ErrorCode
                }
                
            } else {
                return ResponseStrings.DBNotExists
            }
            
        }
        catch let error {
            throw error
        }
    }
    
    func databaseExists(args: DatabaseArgs) -> String {
        if let dbName = args.dbName, let directory = args.directory {
            if Database.exists(withName: dbName, inDirectory: directory) {
                return ResponseStrings.DBExists
            } else {
                return ResponseStrings.DBNotExists
            }
        } else {
            return ResponseStrings.ErrorCode
        }
    }
    
    func deleteDocument(documentArgs: DocumentArgs) throws -> String{
        if let dbname = documentArgs.databaseName, let docid = documentArgs.docid {
            let args = DocumentArgs(dbname: dbname, docid: docid)
            if let dId = args.docid {
                if !dId.isEmpty{
                    do {
                        if let db = databases[dbname]?.database {
                            try db.delete()
                            return ResponseStrings.SuccessCode
                        }
                    } catch let error {
                        throw error
                    }
                }
                else {
                    return ResponseStrings.MissingargsDCID
                }
            } else {
                return ResponseStrings.MissingargsDCID
            }
        }
        else{
            return ("\(ResponseStrings.DBnotfound) OR \(ResponseStrings.invalidArgs)")
        }
        return ResponseStrings.ErrorCode
    }
    
    func getDocument(args: DocumentArgs) throws -> String? {
        if let dbName = args.databaseName, let docId = args.docid {
            if !databases.keys.contains(dbName) {
                return ResponseStrings.DBnotfound
            }
            if let parentDb = databases[dbName], let db = parentDb.database {
                if let document = db.document(withID: docId) {
                    do {
                        return document.toJSON();
                    } catch let error {
                        throw error
                    }
                } else {
                    return ResponseStrings.Docnotfound
                }
            } else {
                return ResponseStrings.DBnotfound
            }
        }
        else {
            return "\(ResponseStrings.DBnotfound) OR \(ResponseStrings.invalidArgs)"
        }
    }
    
    func setDocument(args: DocumentArgs) -> String {
        if let docId = args.docid, let data = args.data {
            do {
                let mutableDocument = try MutableDocument.init(id: docId, json: data)
                if let dbname = args.databaseName, let db = databases[dbname], let database = db.database {
                    try database.saveDocument(mutableDocument)
                    return ResponseStrings.SuccessCode
                } else {
                    return ResponseStrings.DBnotfound
                }
            } catch let error {
                return ResponseStrings.ExceptionDOC + error.localizedDescription
            }
            
        } else {
            return ResponseStrings.ExceptionDOC
        }
    }
    
    func setBlob(dbname: String, type: String, blobdata: String) throws -> String {
        if let db = databases[dbname], let database = db.database {
            if let data = Data(base64Encoded: blobdata) {
                let blob = Blob.init(contentType: type, data: data)
                
                do {
                    try database.saveBlob(blob:blob);
                    return blob.toJSON();
                } catch let error {
                    return ResponseStrings.ExceptionBLOB
                }
            } else {
                return ResponseStrings.invaliddata
            }
            
        } else {
            return ResponseStrings.DBnotfound
        }
    }
    
    func getBlob(dbname: String, key: String) throws -> String {
        do {
            if let db = databases[dbname], let database = db.database {
                var properties : [String : Any] = [String : Any]()
                if let data  = key.data(using: .utf8) {
                    properties =  try JSONSerialization.jsonObject(with: data, options: []) as? [String: Any] ?? [String : Any]()
                } else {
                    return ResponseStrings.invalidblob
                }
                
                var blob = try database.getBlob(properties: properties)
                return (blob?.content?.base64EncodedString())!
                
            } else {
                return ResponseStrings.Blobnotfound
            }
        } catch let error {
            throw error
        }
    }
    
    
    func createValueIndex(args: IndexArgs) throws -> String {
        do {
            if let database = args.dbName, let indexName = args.indexName, let indexExpressionList = args.indexExpressions {
                if let dbResource = databases[database], let db = dbResource.database {
                    var indexExpression = ""
                    indexExpressionList.forEach { item in
                        indexExpression.append("\(item),")
                    }
                    indexExpression = String(indexExpression.dropLast())
                    let index = IndexBuilder.valueIndex(items: ValueIndexItem.property(indexExpression))
                    try db.createIndex(index, withName: indexName)
                    return ResponseStrings.SuccessCode
                } else {
                    return ResponseStrings.DBnotfound
                }
            } else {
                return ResponseStrings.DBnotfound
            }
        } catch let error {
            throw error
        }
    }
    
    func createFTSIndex(args: IndexArgs) throws -> String {
        do {
            if let database = args.dbName, let indexName = args.indexName, let ignoreAccents = args.ignoreAccents, let language = args.language, let indexExpressionList = args.indexExpressions {
                if let dbResource = databases[database], let db = dbResource.database {
                    var indexExpression = ""
                    indexExpressionList.forEach { item in
                        indexExpression.append("\(item ),")
                    }
                    indexExpression = String(indexExpression.dropLast())
                    
                    var index = IndexBuilder.fullTextIndex(items: FullTextIndexItem.property(indexExpression))
                    if ignoreAccents {
                        index = index.ignoreAccents(ignoreAccents)
                    }
                    if !language.isEmpty {
                        index = index.language(language)
                    }
                    try db.createIndex(index, withName: indexName)
                    return ResponseStrings.SuccessCode
                } else {
                    return ResponseStrings.DBnotfound
                }
            } else {
                return ResponseStrings.DBnotfound
            }
        } catch let error {
            throw error
        }
    }
    
    func deleteIndex(args: IndexArgs) throws -> String {
        do {
            if let database = args.dbName, let indexName = args.indexName {
                if !databases.keys.contains(database) {
                    return ResponseStrings.DBnotfound
                }
                if let dbResource = databases[database], let db = dbResource.database {
                    try db.deleteIndex(forName: indexName)
                    return ResponseStrings.SuccessCode
                } else {
                    return ResponseStrings.DBnotfound
                }
            } else {
                return ResponseStrings.DBnotfound
            }
        } catch let error {
            throw error
        }
    }
    
    func registerForDatabaseChanges(dbname: String, jsListener: String) -> String{
        if !databases.keys.contains(dbname) {
            return ResponseStrings.DBnotfound
        }
        if let dbResource = databases[dbname], let db = dbResource.database {
            if dbResource.listenerToken == nil {
                let token = db.addChangeListener { change in
                    var changeDocMap = [String:Any]()
                    var deletedDocMap = [String:Any]()
                    var finalmap = [String:Any]()
                    var hasmodified = false
                    var hasdeleted = false
                    change.documentIDs.forEach { docId in
                        let document = db.document(withID: docId)
                        
                        if let doc = document {
                            changeDocMap[docId] = doc.toDictionary()
                            hasmodified = true
                        }
                        else {
                            hasdeleted = true
                            deletedDocMap[docId] = ""
                        }
                    }
                    if hasmodified {
                        finalmap["Modified"] = changeDocMap
                    }
                    if hasdeleted {
                        finalmap["Deleted"] = deletedDocMap
                    }
                    if self.hasListeners {
                        self.sendEvent(withName: jsListener, body: finalmap)
                    }
                }
                dbResource.listenerToken = token
                return ResponseStrings.SuccessCode
            } else {
                return ResponseStrings.listenerTokenExists
            }
        } else {
            return ResponseStrings.DBnotfound
        }
    }
    
    func deregisterForDatabaseChanges(dbname: String) -> String{
        if !databases.keys.contains(dbname) {
            return ResponseStrings.DBnotfound
        }
        if let dbResource = databases[dbname], let db = dbResource.database {
            if let token = dbResource.listenerToken {
                db.removeChangeListener(withToken: token)
                dbResource.listenerToken = nil
                return ResponseStrings.SuccessCode
            } else {
                return ResponseStrings.listenerTokenNotExists
            }
        } else {
            return ResponseStrings.DBnotfound
        }
    }
    
    func enableLogging(domain: String, logLevel: String) -> String {
        if domain.isEmpty {
            Database.log.console.domains = .all
        }
        else if let d = Int(domain) {
            Database.log.console.domains = LogDomains(rawValue: d)
        }
        else {
            return "Domain is invalid"
        }
        
        if logLevel.isEmpty {
            Database.log.console.level = .debug
        }
        else if let l = UInt8(logLevel), let level = LogLevel.init(rawValue: l) {
            Database.log.console.level = level
        }
        else {
            return "Console level is invalid"
        }
        return ResponseStrings.SuccessCode
    }
    
    func createQuery(args: QueryArgs) throws -> String {
        do {
            if let dbname = args.dbName, let queryString = args.query {
                if let dbResource = databases[dbname] {
                    if !databases.keys.contains(dbname) {
                        return ResponseStrings.DBnotfound
                    }
                    
                    if let query = dbResource.database?.createQuery(query: queryString){
                        let queryID = try dbResource.setQuery(query: query)
                        return "\(queryID)"
                    }
                    else
                    {
                        return ResponseStrings.ExceptionInvalidQuery
                    }
                } else {
                    return ResponseStrings.DBnotfound
                }
            } else {
                return ResponseStrings.NoArgs
            }
        } catch let error {
            throw error
        }
    }
    
    func queryDb(args: QueryArgs) throws -> String {
        do {
            if let dbname = args.dbName, let queryString = args.query {
                if let dbResource = databases[dbname] {
                    if !databases.keys.contains(dbname) {
                        return ResponseStrings.DBnotfound
                    }
                    if let mquery = dbResource.database?.createQuery(query: queryString)
                    {
                        let mqueryID = try dbResource.setQuery(query: mquery)
                        if (try dbResource.getQuery(queryID: mqueryID)?.explain().hashValue != nil) {
                            
                        }
                        else{
                            _ = try dbResource.setQuery(query: mquery)
                        }
                        
                        
                        do {
                            var resultString = "["
                            if let queryResource = dbResource.getQuery(queryID: mqueryID) {
                                for result in try queryResource.execute()
                                {
                                    resultString += result.toJSON()
                                    resultString += ","
                                }
                                if(resultString.count>1)
                                {
                                    resultString = String(resultString.dropLast())
                                }
                            }
                            
                            resultString += "]"
                            return resultString
                            
                        } catch {
                            return ResponseStrings.ExceptionInvalidQuery
                        }
                    }
                    else
                    {
                        return ResponseStrings.ExceptionInvalidQuery
                    }
                    
                    
                    
                    
                    
                } else {
                    return ResponseStrings.DBnotfound
                }
            } else {
                return ResponseStrings.NoArgs
            }
        } catch let error {
            throw error
        }
    }
    
    func registerForQueryChanges(dbname : String, query: String, jsListener: String) throws -> String {
        do {
            if let dbResource = databases[dbname] {
                if !databases.keys.contains(dbname) {
                    return ResponseStrings.DBnotfound
                }
                let select = SelectResult.property(query)
                let q = QueryBuilder.select(select)
                let queryID = try dbResource.setQuery(query: q)
                if dbResource.getQuery(queryID: queryID) == nil {
                    _ = try dbResource.setQuery(query: q)
                }
                
                if dbResource.getQuery(queryID: queryID) != nil
                    && dbResource.getQueryChangeListenerToken(queryId: queryID) != nil {
                    return ResponseStrings.QueryListenerExists;
                } else {
                    dbResource.setQueryChangeListenerJSFunction(queryChangeListenerJSFunction: jsListener, queryID: queryID)
                    let queryListenerToken = dbResource.getQuery(queryID: queryID)?.addChangeListener({change in
                        let jsCallback = dbResource.getQueryChangeListenerJSFunction(queryID: queryID)
                        if change.error != nil {
                            var listenerResults = [String:Any]()
                            listenerResults["message"] = change.error?.localizedDescription
                            
                            if !jsCallback!.isEmpty {
                                let params = listenerResults.description
                                if self.hasListeners {
                                    self.sendEvent(withName: jsCallback!, body: params)
                                }
                            }
                        } else {
                            do {
                                var row: Result?
                                var json = [Any]()
                                if change.results != nil {
                                    while (row = change.results?.next()) != nil {
                                        let jsonData = try? JSONSerialization.data(withJSONObject: row!, options: .prettyPrinted)
                                        let rowObject = try JSONSerialization.jsonObject(with: jsonData!, options: .allowFragments)
                                        json.append(rowObject)
                                    }
                                }
                                if !jsCallback!.isEmpty {
                                    let params = json.description
                                    if self.hasListeners {
                                        self.sendEvent(withName: jsCallback!, body: params)
                                    }
                                }
                            } catch _ {
                            }
                        }
                        
                    })
                    if let token = queryListenerToken {
                        dbResource.setQueryChangeListenerToken(queryChangeListenerToken: token, queryID: queryID)
                    } else {
                        return "Listenertoken is empty"
                    }
                    return ResponseStrings.SuccessCode
                }
                
            } else {
                return ResponseStrings.DBnotfound
            }
        } catch let error {
            throw error
        }
    }
    
    func deregisterForQueryChanges(dbname : String, queryString: String) throws -> String {
        do {
            if let dbResource = databases[dbname] {
                let select = SelectResult.property(queryString)
                let q = QueryBuilder.select(select)
                let queryID = try dbResource.setQuery(query: q)
                if (dbResource.getQuery(queryID: queryID) != nil) {
                    if let token = dbResource.getQueryChangeListenerToken(queryId: queryID) {
                        dbResource.getQuery(queryID: queryID)?.removeChangeListener(withToken: token)
                        dbResource.setQueryChangeListenerToken(queryChangeListenerToken: nil, queryID: queryID)
                    } else {
                        return ResponseStrings.QueryNotListenerExists
                    }
                    
                } else {
                    return ResponseStrings.QueryNotListenerExists
                }
            } else {
                return ResponseStrings.DBNotExists
            }
        } catch let error {
            throw error
        }
        return ResponseStrings.SuccessCode
    }
    func replicatorStart(dbname:String,id:String) throws -> String {
        do {
            
            if let dbResource = databases[dbname] {
                if !databases.keys.contains(dbname) {
                    return ResponseStrings.DBnotfound
                }
                if dbResource.getReplicator(replicatorID: id) != nil {
                    dbResource.getReplicator(replicatorID: id)!.start()
                }else{
                    return ResponseStrings.ReplicatorNotExists
                }
                
            } else {
                return ResponseStrings.DBnotfound
            }
            
        } catch let error {
            throw error
        }
        return ResponseStrings.ErrorCode
    }
    
    func replicatorStop(dbname:String,id:String) throws -> String {
        do {
            
            if let dbResource = databases[dbname] {
                if !databases.keys.contains(dbname) {
                    return ResponseStrings.DBnotfound
                }
                if dbResource.getReplicator(replicatorID: id) != nil {
                    dbResource.getReplicator(replicatorID: id)!.stop()
                    if dbResource.getReplicatorChangeListenerToken(replicatorId: id) == nil {
                        dbResource.removeReplicator(replicatorId: id)
                    }
                    
                }else{
                    return ResponseStrings.ReplicatorNotExists
                }
                
            } else {
                return ResponseStrings.DBnotfound
            }
            
        } catch let error {
            throw error
        }
        return ResponseStrings.ErrorCode
    }
    
    func replicationRemoveChangeListener(dbname:String,id:String) throws -> String {
        
        do {
            if let dbResource = databases[dbname] {
                if !databases.keys.contains(dbname) {
                    return ResponseStrings.DBnotfound
                }
                if let replicator = dbResource.getReplicator(replicatorID: id){
                    
                    if let token = dbResource.getReplicatorChangeListenerToken(replicatorId: id) {
                        replicator.removeChangeListener(withToken: token)
                        dbResource.setReplicatorChangeListenerToken(replicatorId: id, replicatorChangeListenerToken: nil)
                        if replicator.status.activity == Replicator.ActivityLevel.stopped {
                            dbResource.removeReplicator(replicatorId: id)
                        }
                    }else {
                        return ResponseStrings.ReplicatorListenerNotExists
                    }
                    
                }else // replicator else
                {
                    return ResponseStrings.ReplicatorNotExists
                }
                
            }// dbresource if let
            else {
                ResponseStrings.DBnotfound
            }
            
            
            
        } catch let error {
            throw error
        }
        return ResponseStrings.SuccessCode
    }
    
    
    func replicationAddChangeListener(dbname:String,id:String,listner:String) throws -> String{
        do {
            if let dbResource = databases[dbname] {
                if !databases.keys.contains(dbname) {
                    return ResponseStrings.DBnotfound
                }
                
                if let replicator = dbResource.getReplicator(replicatorID: id){
                    
                    dbResource.setReplicatorChangeListenerToken(replicatorId: id, replicatorChangeListenerJSFunction: listner)
                    
                    if let token = dbResource.getReplicatorChangeListenerToken(replicatorId: id) {
                        // if token is not null
                        
                    }else {
                        //if token is null
                        
                        let blockToken =   dbResource.getReplicator(replicatorID: id)!.addChangeListener { (change) in
                            var changeObject : [String:String] = [:]
                            
                            switch change.status.activity {
                            case Replicator.ActivityLevel.stopped:
                                changeObject.updateValue("stoped", forKey: "status")
                                break
                            case Replicator.ActivityLevel.busy:
                                changeObject.updateValue("busy", forKey: "status")
                                break
                            case Replicator.ActivityLevel.connecting:
                                changeObject.updateValue("connecting", forKey: "status")
                                break
                            case Replicator.ActivityLevel.offline:
                                changeObject.updateValue("offile", forKey: "status")
                                break
                            default:
                                changeObject.updateValue("idle", forKey: "status")
                                break
                            }
                            
                            if change.status.error != nil {
                                changeObject.updateValue(change.status.error!.localizedDescription, forKey: "error")
                                // changeObject.updateValue(change.status.error., forKey: <#T##String#>)
                            }
                            changeObject.updateValue("\(change.status.progress.completed)", forKey: "completed")
                            changeObject.updateValue("\(change.status.progress.total)", forKey: "total")
                            
                            if let jsCallBackFunc = dbResource.getReplicatorChangeListenerJSFunction(replicatorId: id) {
                                if jsCallBackFunc != nil && !jsCallBackFunc.isEmpty {
                                    
                                    if self.hasListeners {
                                        self.sendEvent(withName: jsCallBackFunc, body: changeObject)
                                    }
                                }
                            }
                            
                        }
                        
                        dbResource.setReplicatorChangeListenerToken(replicatorId: id, replicatorChangeListenerToken: blockToken)
                        
                        
                    }// token else end.
                    
                }else // replicator else
                {
                    return ResponseStrings.ReplicatorNotExists
                }
                
            }// dbresource if let
            else {
                ResponseStrings.DBnotfound
            }
            
        } catch let error {
            throw error
        }
        
        return ResponseStrings.SuccessCode
    }
    
    
    override func supportedEvents() -> [String]! {
        return [""]
    }
    override func startObserving() {
        self.hasListeners = true
    }
    
    override func stopObserving() {
        self.hasListeners = false
    }
}

