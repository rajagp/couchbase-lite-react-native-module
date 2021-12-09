import CouchbaseLiteSwift

class DatabaseResources {
    var database :Database?
    var databaseConfig :DatabaseConfiguration?
    var dbChangeListenerToken: ListenerToken?
    var dbChangeListenerJSFunction: String?
    var commandDelegate: CDVCommandDelegate?
    
    init(fromDatabase: Database, fromDatabaseConfiguration: DatabaseConfiguration){
        database = fromDatabase
        databaseConfig = fromDatabaseConfiguration
    }
    
    init(fromDatabase: Database){
        database = fromDatabase
    }
}
