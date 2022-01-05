//
//  DatabaseArgs.swift
//  RNCouchbaseLiteSwift
//
//  Created by Umer on 05/12/2021.
//

//import UIKit

class DatabaseArgs: NSObject {
    private var _directory:String?
    var directory:String? {
        get { return _directory }
        set { _directory = newValue }
    }
    private var _dbName:String?
    var dbName:String? {
        get { return _dbName }
        set { _dbName = newValue }
    }
    private var _encryptionKey:String?
    var encryptionKey:String? {
        get { return _encryptionKey }
        set { _encryptionKey = newValue }
    }
    private var _databaseConfig:[String:Any]?
    var databaseConfig:[String:Any]? {
        get { return _databaseConfig }
        set { _databaseConfig = newValue }
    }
    
    override init() {
        super.init()
    }
    
    init(dbname: String, directory: String, encryptionKey:String) {
        super.init()
        self.dbName = dbname
        self.directory = directory
        self.encryptionKey = encryptionKey
    }
    
    init(dbname: String, directory: String) {
        super.init()
        self.dbName = dbname
        self.directory = directory
    }
    
    init(dbname: String) {
        super.init()
        self.dbName = dbname
    }
    
    init(dbname: String, databaseConfig: [String:Any]) {
        super.init()
        self.dbName = dbname
        self.databaseConfig = databaseConfig
        self.directory = databaseConfig.keys.contains("Directory") ? databaseConfig["Directory"] as? String : nil
        self.encryptionKey = databaseConfig.keys.contains("encryptionKey") ? databaseConfig["encryptionKey"] as? String : nil
    }
}
