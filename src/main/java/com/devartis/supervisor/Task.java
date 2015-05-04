package com.devartis.supervisor;

/**
 * Created by german on 4/30/15.
 */
public abstract class Task implements Runnable {

    @Override
    public void run() {
        try {
            doRun();
        } catch (InterruptedException e) {
            onInterrupt();
        }
    }

    protected void onInterrupt() {
    }

    protected void stopIfInterrupted() throws InterruptedException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException();
        }
    }

    public abstract void doRun() throws InterruptedException;
}
