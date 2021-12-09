import Foundation

struct DbChangeListenerResults {
    var message: String
    var isError: Bool
    var docChanges: Dictionary<String, [String]>
    
    init(fromIsError: Bool, fromDocChanges: Dictionary<String, [String]>){
        self.isError = fromIsError
        self.docChanges = fromDocChanges
        self.message = ""
    }
    
    init(fromIsError: Bool, fromDocChanges: Dictionary<String, [String]>, fromMessage: String){
        self.isError = fromIsError
        self.docChanges = fromDocChanges
        self.message = fromMessage
    }
    
    enum CodingKeys: String, CodingKey {
          case message
          case isError
          case docChanges
    }
}

extension DbChangeListenerResults : Encodable {
    func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        try container.encode(message, forKey: .message)
        try container.encode(docChanges, forKey: .docChanges)
        try container.encode(isError, forKey: .isError)
    }
}
