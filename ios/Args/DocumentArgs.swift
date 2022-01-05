//
//  DocumentArgs.swift
//  RNReactNativeCblite
//
//  Created by Umer on 04/12/2021.
//

//import UIKit

class DocumentArgs: NSObject {
    private var _docid:String?
    var docid:String? {
        get { return _docid }
        set { _docid = newValue }
    }
    private var _data:String?
    var data:String? {
        get { return _data }
        set { _data = newValue }
    }
    private var _databaseName:String?
    var databaseName:String? {
        get { return _databaseName }
        set { _databaseName = newValue }
    }
    private var _jsondata:[String:Any]?
    var jsondata:[String:Any]? {
        get { return _jsondata }
        set { _jsondata = newValue }
    }
    
    override init() {
        super.init()
    }
    
    init(dbname: String, docid: String, data: String) {
        super.init()
        self.docid = docid;
        self.databaseName = dbname;
        self.data = data;
    }
    
    init(dbname: String, docid: String, jsondata: [String:Any]) {
        super.init()
        self.docid = docid;
        self.databaseName = dbname;
        self.jsondata = jsondata;
    }
    
    init(dbname: String, docid: String) {
        super.init()
        self.docid = docid;
        self.databaseName = dbname;
    }
}
