//
//  ReplicatorResource.swift
//  RNReactNativeCblite
//
//  Created by Umer on 05/12/2021.
//  Copyright © 2021 Facebook. All rights reserved.
//

//import UIKit
import CouchbaseLiteSwift

class ReplicatorResource: NSObject {
    private var _replicator: Replicator?
    var replicator: Replicator? {
        get { return _replicator }
        set { _replicator = newValue }
    }
    private var _replicatorChangeListenerToken: ListenerToken?
    var replicatorChangeListenerToken: ListenerToken? {
        get { return _replicatorChangeListenerToken }
        set { _replicatorChangeListenerToken = newValue }
    }
    private var _replicatorChangeListenerJSFunction: String?
    var replicatorChangeListenerJSFunction: String? {
        get { return _replicatorChangeListenerJSFunction }
        set { _replicatorChangeListenerJSFunction = newValue }
    }
    
    override init() {
        super.init()
    }
}
