//
//  RNEventEmitter.swift
//  Cblite
//
//  Created by Ramiz on 22/02/2022.
//  Copyright © 2022 Facebook. All rights reserved.
//

@objc(RNEventEmitter)
open class RNEventEmitter: RCTEventEmitter {
  public static var emitter: RCTEventEmitter!
   
    public static var mevents:[String] = []
  
    public static var hasListeners:Bool = false
    
    public func addevents(name : String)
    {
        RNEventEmitter.mevents.append(name)
    }
    
  override init() {
    super.init()
    RNEventEmitter.emitter = self
   
  }

    
    open override func startObserving() {
        RNEventEmitter.hasListeners = true
    }
    
    
    open override func stopObserving() {
        RNEventEmitter.hasListeners = false
    }
    
  open override func supportedEvents() -> [String] {
    return RNEventEmitter.mevents;    // etc.
  }
}
