package com.reactnativecblite.Args;

import java.util.List;

public class IndexArgs {
    private String dbName;
    private String indexName;
    private boolean ignoreAccents;
    private String language;
    private List<String> indexExpressions;


    public IndexArgs() {}

    public void DeleteIndexArgs(String dbName,String indexName) {
        this.dbName = dbName;
        this.indexName = indexName;
    }

    public void ValueIndexArgs(String dbName,String indexName,List<String> indexExpressions) {
        this.dbName = dbName;
        this.indexName = indexName;
        this.indexExpressions = indexExpressions;
    }

    public void FTSIndexArgs(String databaseName, String indexName, boolean ignoreAccents, String language, List<String> indexExpressions) {

        this.dbName = databaseName;
        this.indexName = indexName;
        this.ignoreAccents = ignoreAccents;
        this.language = language;
        this.indexExpressions = indexExpressions;

    }




    public boolean isIgnoreAccents() {
        return ignoreAccents;
    }

    public void setIgnoreAccents(boolean ignoreAccents) {
        this.ignoreAccents = ignoreAccents;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }



    public List<String> getIndexExpressions() {
        return indexExpressions;
    }

    public void setIndexExpressions(List<String> indexes) {
        this.indexExpressions = indexes;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }
}
