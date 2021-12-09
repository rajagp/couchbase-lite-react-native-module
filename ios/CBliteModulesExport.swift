//
//  CBliteModulesExport.swift
//  RNCouchbaseLiteReactNativeModuleIos
//
//  Created by Umer on 09/12/2021.
//  Copyright © 2021 Facebook. All rights reserved.
//

import Foundation
import UIKit
import AVFoundation


@objc(CbliteiOS)
class CbliteiOS: NSObject {
    @objc func createDatabase(dbname : String, dbconfig : NSDictionary ,successCallback:
RCTResponseSenderBlock,errorCallback:
    RCTResponseSenderBlock) {
        
        // Assume name comes from the any native API side
        
        var dbargs = DatabaseArguments.init(name: dbname, databaseConfig:dbconfig);
        var response = DatabaseManager.openOrCreateDatabase(dbargs);
        if(response == "Success")
        {
            successCallback([response])
        }else{
            errorCallback([response])
        }
  }
    
    @objc func closeDatabase(dbname : String,successCallback:
RCTResponseSenderBlock,errorCallback:
    RCTResponseSenderBlock) {
    
        var response = DatabaseManager.closeDatabase(dbname)
        if(response == "Success")
        {
            successCallback([response])
        }else{
            errorCallback([response])
        }
  }
}
