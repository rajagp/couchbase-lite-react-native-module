//
//  IndexArgs.swift
//  RNReactNativeCblite
//
//  Created by Umer on 05/12/2021.
//  Copyright © 2021 Facebook. All rights reserved.
//

//import UIKit

class IndexArgs: NSObject {
    private var _dbName:String?
    var dbName:String? {
        get { return _dbName }
        set { _dbName = newValue }
    }
    private var _indexName:String?
    var indexName:String? {
        get { return _indexName }
        set { _indexName = newValue }
    }
    private var _ignoreAccents:Bool?
    var ignoreAccents:Bool? {
        get { return _ignoreAccents }
        set { _ignoreAccents = newValue }
    }
    private var _language:String?
    var language:String? {
        get { return _language }
        set { _language = newValue }
    }
    private var _indexExpressions:[String]?
    var indexExpressions:[String]? {
        get { return _indexExpressions }
        set { _indexExpressions = newValue }
    }
    
    override init() {
        super.init()
    }
    
    func DeleteIndexArgs(dbName: String, indexName: String) {
        self.dbName = dbName
        self.indexName = indexName
    }
    
    func ValueIndexArgs(dbName: String, indexName: String, indexExpressions: [String]) {
        self.dbName = dbName
        self.indexName = indexName
        self.indexExpressions = indexExpressions
    }
    
    func FTSIndexArgs(dbName: String, indexName: String, ignoreAccents: Bool, language: String, indexExpressions: [String]) {
        self.dbName = dbName
        self.indexName = indexName
        self.ignoreAccents = ignoreAccents
        self.language = language
        self.indexExpressions = indexExpressions
    }
}
