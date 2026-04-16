package org.novak.statistics.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;


@Service
public class ImportLockService {

    private final AtomicBoolean isImporting = new AtomicBoolean(false);


    public void lock() {
        isImporting.set(true);
    }


    public void unlock() {
        isImporting.set(false);
    }


    public boolean isLocked() {
        return isImporting.get();
    }
}
