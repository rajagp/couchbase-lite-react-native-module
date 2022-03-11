package com.reactnativecblite.Args;

import com.couchbase.lite.ListenerToken;
import com.couchbase.lite.Query;

public class QueryResource {
    private Query query;
    private ListenerToken queryChangeListenerToken;
    private String queryChangeListenerJSFunction;

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    public ListenerToken getQueryChangeListenerToken() {
        return queryChangeListenerToken;
    }

    public void setQueryChangeListenerToken(ListenerToken replicatorChangeListenerToken) {
        this.queryChangeListenerToken = replicatorChangeListenerToken;
    }

    public String getQueryChangeListenerJSFunction() {
        return this.queryChangeListenerJSFunction;
    }

    public void setQueryChangeListenerJSFunction(String replicatorChangeListenerJSFunction) {
        this.queryChangeListenerJSFunction = replicatorChangeListenerJSFunction;
    }

}
