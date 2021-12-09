//
//  Databasemanager.swift
//  RNCouchbaseLiteReactNativeModuleIos
//
//  Created by Umer on 12/5/21.
//  Copyright © 2021 Facebook. All rights reserved.
//

import Foundation
import CouchbaseLiteSwift


class DatabaseManager {
    
    var database : Dictionary<String, DatabaseResources>
    var queryDb: Dictionary<QueryHash, QueryListenerResource>
    var databaseResource: Dictionary<ReplicatorConfigHash, ReplicatorResource>
    
    public var db :Dictionary<String, DatabaseResources> {
        get {
            return database
        }
    }

    
    private init () {
        database = Dictionary<String, DatabaseResources>()
        
        databaseResource = Dictionary<ReplicatorConfigHash, ReplicatorResource>()
    }
    
    func initialize(){
    }
    
    deinit {
        do {
            for (_, resources) in database {
                try resources.database?.close()
            }
        }
        catch {
            
        }
    }
    
    static let shared : DataBaseManager = {
        let instance = DataBaseManager()
        instance.initialize()
        return instance
    }()
    
    func openOrCreateDatabase(arguments: DatabaseArguments) -> String {
        do {
            let dbConfiguration = arguments.databaseConfig
            
            if (database[arguments.name!] != nil){
                
                return "database \(arguments.name!) already open and available for use."
                
            }
            
            if let config = dbConfiguration, let dbName = arguments.name {
                let db = try Database(name: dbName, config: config)
                database[dbName] = DatabaseResources(fromDatabase: db, fromDatabaseConfiguration: config)
                if let path = database[dbName]?.database?.path {
                    return getDatabasePathJson(path: path)
                }
            }
            return "Success"
        } catch {
            return "error"
        }
    }
    
    
    
    func closeDatabase(name: String) -> String {
        do {
            let db = self.database[name]?.database
            try db?.close()
            database.removeValue(forKey: name)
            return "Success"
        } catch{
            return getJsonMessage(fromMessage: "error = \(name) throws error \(error).")
        }
    }
    
    func createDocument(value:String,key:String,bdname:String){
        let document = MutableDocument().setString(value, forKey: key)
        do {
            try database[bdname]?.database?.saveDocument(document)
        } catch {
            fatalError("Error saving document")
        }
    }
    
   

    
    
    private func getDatabasePathJson(path: String) -> String {
        return "{\"path\": \"\(path))\"}"
    }
    
    func getJsonMessage(fromMessage: String) -> String {
        return "{\"message\": \"\(fromMessage)\"}"
    }
    
}



