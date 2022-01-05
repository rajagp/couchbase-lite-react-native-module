//
//  QueryResource.swift
//  RNReactNativeCblite
//
//  Created by Umer on 05/12/2021.
//  Copyright © 2021 Facebook. All rights reserved.
//

//import UIKit
import CouchbaseLiteSwift

class QueryResource: NSObject {
    private var _query: Query?
    var query: Query? {
        get { return _query }
        set { _query = newValue }
    }
    private var _queryChangeListenerToken: ListenerToken?
    var queryChangeListenerToken: ListenerToken? {
        get { return _queryChangeListenerToken }
        set { _queryChangeListenerToken = newValue }
    }
    private var _queryChangeListenerJSFunction: String?
    var queryChangeListenerJSFunction: String? {
        get { return _queryChangeListenerJSFunction }
        set { _queryChangeListenerJSFunction = newValue }
    }
    
    override init() {
        super.init()
    }
}
