package com.couchbase.cblitereact.Args;

import com.couchbase.lite.ListenerToken;
import com.couchbase.lite.Replicator;

import java.util.ArrayList;

public class ReplicatorResource {
    private Replicator replicator;
    private ListenerToken replicatorChangeListenerToken;
    private String replicatorChangeListenerJSFunction;

    public Replicator getReplicator() {
        return replicator;
    }

    public void setReplicator(Replicator replicator) {
        this.replicator = replicator;
    }

    public ListenerToken getReplicatorChangeListenerToken() {
        return replicatorChangeListenerToken;
    }

    public void setReplicatorChangeListenerToken(ListenerToken replicatorChangeListenerToken) {
        this.replicatorChangeListenerToken = replicatorChangeListenerToken;
    }

    public String getReplicatorChangeListenerJSFunction() {
        return this.replicatorChangeListenerJSFunction;
    }

    public void setReplicatorChangeListenerJSFunction(String replicatorChangeListenerJSFunction) {
        this.replicatorChangeListenerJSFunction = replicatorChangeListenerJSFunction;
    }


}
