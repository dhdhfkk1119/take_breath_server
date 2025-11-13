package com.take.take_breath.members.login;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class StateStore {

    private final Map<String, Boolean> store = new ConcurrentHashMap<>();

    public void save(String state) {
        store.put(state, true);
    }

    public boolean exists(String state) {
        return store.containsKey(state);
    }

    public void remove(String state) {
        store.remove(state);
    }
}
