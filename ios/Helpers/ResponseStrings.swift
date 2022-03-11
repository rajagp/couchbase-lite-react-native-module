//
//  responselet.swift
//  RNCouchbaseLiteSwift
//
//  Created by Hackintosh1 on 18/12/2021.
//

import Foundation

public class ResponseStrings {

    public static let MissingargsDBN="Missing Arguments : Database Name";
    public static let MissingargsDBD="Missing Arguments : Directory";
    public static let MissingargsDCID="Missing Arguments : Document ID";
    public static let MissingargsDCData="Missing Arguments : Document Data";
    public static let MissingargsIN="Missing Arguments : Index Name";
    public static let MissingargsINEX="Missing Arguments : Index Expressions";
    public static let NullDoc="Document is Null";
    public static let DBCreated="Database Created";
    public static let DBClosed="Database Closed";
    public static let DBDeleted="Database Deleted";
    public static let DBExists="Database already exists";
    public static let SuccessCode="Success";
    public static let ErrorCode="Error";
    public static let DBNotExists="Database not exists";
    public static let ReplicatorNotExists="Replicator does not exists";
    public static let ReplicatorListenerExists="Replicator listener already exists";
    public static let QueryListenerExists="Query listener already exists";
    public static let QueryNotListenerExists="Query listener does not exists";
    public static let QueryResourceNotExists="Query resource does not exists";
    public static let DBorQueryNotListenerExists="Database or Query Resource not exists";
    public static let ReplicatorListenerNotExists="Replicator listener does not exists";
    public static let DocCreated="Document Created";
    public static let DocDeleted="Document Deleted";
    public static let listenerTokenExists="Database listener already registered with database. Please remove the database listener before registering new one.";
    public static let listenerTokenNotExists="Database listener not registered with database.";

    public static let ExceptionDB="Error while Creating Database : ";
    public static let ExceptionQuery="Error while executing query : ";
    public static let ExceptionInvalidQuery="Error while executing query : Invalid Query";
    public static let ExceptionQuerynotExists="Query not exists, Please create or exceute this query fist.";
    public static let ExceptionDBclose="Error while Closing Database : ";
    public static let ExceptionDBdelete="Error while Deleting Database : ";
    public static let ExceptionDBcopy="Error while Copying Database : ";
    public static let ExceptionDBfetch="Error while Fetching Database : ";
    public static let ErrDBdelete="Error while Deleting Database";
    public static let ErrDBcopy="Error while Deleting Database";
    public static let ExceptionDOC="Error while Creating Document : ";
    public static let Exception="Error found : ";
    public static let ExceptionEnableLogging="Error found while enabling logger for the database : ";
    public static let ExceptionDOCGet="Error while Fetching Document : ";
    public static let ExceptionBLOB="Error while Creating Blob : ";
    public static let ExceptionBLOBget="Error while Fetching Blob : ";
    public static let invalidArgs="Invalid Arguments Passed";
    public static let invalidargsDCData="Invalid Arguments : Document data is not in proper JSON format";
    public static let NoArgs="No Arguments Passed";
    public static let Docnotfound="Document not found";
    public static let Blobnotfound="Blob not found";
    public static let DBnotfound="Database not found";
    public static let Missingargs="Missing Arguments : ";
    public static let invalidblob="Invalid Object : Blob Data";
    public static let invaliddata="Invalid data from query response.";
}
