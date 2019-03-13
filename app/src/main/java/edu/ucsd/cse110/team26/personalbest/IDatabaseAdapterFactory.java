package edu.ucsd.cse110.team26.personalbest;

import android.content.Context;

class IDatabaseAdapterFactory {
    private static final String TAG = "IDatabaseAdapterFactory";

    static IDataAdapter create(boolean debug, Context context) {
        if (debug) {
            return new MockDataAdapter();
        } else {
            return new FirestoreAdapter(context, new ConcreteTimeStamper());
        }
    }
}
