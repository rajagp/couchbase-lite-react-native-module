//
//  QueryArgs.swift
//  RNReactNativeCblite
//
//  Created by Umer on 05/12/2021.
//  Copyright © 2021 Facebook. All rights reserved.
//

//import UIKit

class QueryArgs: NSObject {
    private var _dbName:String?
    var dbName:String? {
        get { return _dbName }
        set { _dbName = newValue }
    }
    private var _query:String?
    var query:String? {
        get { return _query }
        set { _query = newValue }
    }
    
    init(dbName: String, query: String) {
        super.init()
        self.dbName = dbName
        self.query = query
    }
}
