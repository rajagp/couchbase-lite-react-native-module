//
//  DatabaseResource.swift
//  RNReactNativeCblite
//
//  Created by Umer on 05/12/2021.
//  Copyright © 2021 Facebook. All rights reserved.
//

//import UIKit
import CouchbaseLiteSwift


class DatabaseResource: NSObject {
    private var _database: Database?
    var database: Database? {
        get { return _database }
        set { _database = newValue }
    }
    private var _configuration: DatabaseConfiguration?
    var configuration: DatabaseConfiguration? {
        get { return _configuration }
        set { _configuration = newValue }
    }
    private var _listenerToken: ListenerToken?
    var listenerToken: ListenerToken? {
        get { return _listenerToken }
        set { _listenerToken = newValue }
    }
    private var _replicators:[String:ReplicatorResource]?
    var replicators:[String:ReplicatorResource]? {
        get { return _replicators }
        set { _replicators = newValue }
    }
    private var _querys:[Int:QueryResource]?
    var querys:[Int:QueryResource]? {
        get { return _querys }
        set { _querys = newValue }
    }
    
    override init() {
        super.init()
    }
    
    init(db: Database, config: DatabaseConfiguration) {
        super.init()
        self.database = db
        self.configuration = config
    }
    
    init(db: Database) {
        super.init()
        self.database = db
    }
    
    func getReplicator(replicatorID: String) -> Replicator? {
        return self.replicators?[replicatorID]?.replicator
    }
    
    func setReplicator(replicator: Replicator) -> String? {
        let setReplicator = ReplicatorResource()
        setReplicator.replicator = replicator
        let hash = generateReplicatorConfigHash(replicator: replicator)
        if let _ = self.replicators, let _ = hash {
            self.replicators![hash!] = setReplicator
        }
        return hash
    }
    
    func generateReplicatorConfigHash(replicator: Replicator) -> String? {
        let replicatorConfiguration = replicator.config
        var sbhash = ""
        sbhash.append(replicatorConfiguration.database.name)
        sbhash.append("\(replicatorConfiguration.continuous)")
        sbhash.append("\(replicatorConfiguration.replicatorType)")
        replicatorConfiguration.channels?.forEach({ channel in
            sbhash.append(channel)
        })
        replicatorConfiguration.documentIDs?.forEach({ id in
            sbhash.append(id)
        })
        let md = sbhash.utf8.md5.rawValue
        return md
    }
    
    func removeReplicator(replicatorId: String) {
        self.replicators?.removeValue(forKey: replicatorId)
    }
    
    func setReplicatorChangeListenerToken(replicatorId: String, replicatorChangeListenerToken: ListenerToken) {
        if let _ = self.replicators , let _ = self.replicators![replicatorId] {
            self.replicators![replicatorId]!.replicatorChangeListenerToken = replicatorChangeListenerToken
        }
    }
    
    func getReplicatorChangeListenerToken(replicatorId: String) -> ListenerToken? {
        if let _ = self.replicators , let _ = self.replicators![replicatorId], let _ = self.replicators![replicatorId]!.replicatorChangeListenerToken {
            return self.replicators![replicatorId]!.replicatorChangeListenerToken!
        }
        return nil
    }
    
    func setReplicatorChangeListenerToken(replicatorId: String, replicatorChangeListenerJSFunction: String) {
        if let _ = self.replicators , let _ = self.replicators![replicatorId] {
            self.replicators![replicatorId]!.replicatorChangeListenerJSFunction = replicatorChangeListenerJSFunction
        }
    }
    
    func getReplicatorChangeListenerJSFunction(replicatorId: String) -> String? {
        if let _ = self.replicators , let _ = self.replicators![replicatorId], let _ = self.replicators![replicatorId]!.replicatorChangeListenerToken {
            return self.replicators![replicatorId]!.replicatorChangeListenerJSFunction!
        }
        return nil
    }
    
    func getQuery(queryID: Int) -> Query? {
        if let _ = self.querys, let _ = self.querys![queryID], let _ = self.querys![queryID]!.query {
            return self.querys![queryID]!.query!
        }
        return nil
    }
    
    func setQuery(query: Query) throws -> Int {
        let queryResource = QueryResource()
        queryResource.query = query
        var hash = 0
        do {
            hash = try query.explain().hash
            if ((self.querys?.keys.contains(hash)) == nil) {
                self.querys?[hash] = queryResource
            }
        }
        catch let error {
            throw error
        }
        return hash
    }
    
    func removeQuery(queryId: Int) {
        self.querys?.removeValue(forKey: queryId)
    }
    
    func getQueryChangeListenerToken(queryId: Int) -> ListenerToken? {
        return self.querys?[queryId]?.queryChangeListenerToken
    }
    
    func setQueryChangeListenerToken(queryChangeListenerToken: ListenerToken?, queryID: Int) {
        self.querys?[queryID]?.queryChangeListenerToken = queryChangeListenerToken
    }
    
    func getQueryChangeListenerJSFunction(queryID: Int) -> String? {
        return self.querys?[queryID]?.queryChangeListenerJSFunction
    }
    
    func setQueryChangeListenerJSFunction(queryChangeListenerJSFunction: String, queryID: Int) {
        self.querys?[queryID]?.queryChangeListenerJSFunction = queryChangeListenerJSFunction
    }
}
