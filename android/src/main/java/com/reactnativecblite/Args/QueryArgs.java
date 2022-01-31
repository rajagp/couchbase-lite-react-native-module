package com.reactnativecblite.Args;

public class QueryArgs {
    private String dbName;
    private String query;

    public QueryArgs(String dbname,String query) {
        this.dbName = dbname;
        this.query = query;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String databaseName) {
        this.dbName = databaseName;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
