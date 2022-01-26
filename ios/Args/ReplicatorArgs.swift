//
//  ReplicatorArgs.swift
//  RNReactNativeCblite
//
//  Created by Umer on 05/12/2021.
//  Copyright © 2021 Facebook. All rights reserved.
//

//import UIKit

class ReplicatorArgs: NSObject {
    private var _dbName:String?
    var dbName:String? {
        get { return _dbName }
        set { _dbName = newValue }
    }
    private var _replicatorConfig:[String:Any]?
    var replicatorConfig:[String:Any]? {
        get { return _replicatorConfig }
        set { _replicatorConfig = newValue }
    }
    
    override init() {
        super.init()
    }
    
    init(dbName: String, replicatorConfig: [String:Any]) {
        super.init()
        self.dbName = dbName
        self.replicatorConfig = replicatorConfig
    }
}
