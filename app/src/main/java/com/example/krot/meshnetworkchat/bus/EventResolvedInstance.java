package com.example.krot.meshnetworkchat.bus;

import com.hypelabs.hype.Instance;

/**
 * Created by Krot on 3/21/18.
 */

public class EventResolvedInstance {

    private Instance resolvedInstance;

    public EventResolvedInstance(Instance resolvedInstance) {
        this.resolvedInstance = resolvedInstance;
    }

    public Instance getResolvedInstance() {
        return resolvedInstance;
    }
}
