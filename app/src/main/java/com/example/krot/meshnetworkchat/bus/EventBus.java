package com.example.krot.meshnetworkchat.bus;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

/**
 * Created by Krot on 3/21/18.
 */

public class EventBus {

    private static EventBus eventBus;
    private Bus bus;
    private static final Handler handler = new Handler(Looper.getMainLooper());

    private EventBus() {
        bus = new Bus();
    }

    public static EventBus getInstance() {
        if (eventBus == null) {
            eventBus = new EventBus();
        }

        return eventBus;
    }


    public void doPost(final Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            bus.post(event);
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    bus.post(event);
                }
            });
        }
    }

    public void registerBus(Object object) {
        bus.register(object);
    }

    public void unregisterBus(Object object) {
        bus.unregister(object);
    }

}
