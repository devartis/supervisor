package com.devartis.supervisor;

import java.util.UUID;

/**
 * Created by german on 4/22/15.
 */
public class TaskContext {
    private final UUID uuid;
    private final String name;
    private final Runnable task;
    private final boolean keepAlive;

    private Thread thread;
    private boolean started = false;

    public TaskContext(UUID uuid, String name, Runnable task, boolean keepAlive) {
        this.uuid = uuid;
        this.name = name;
        this.task = task;
        this.keepAlive = keepAlive;
    }

    public synchronized void start() {
        if (!started || (keepAlive && !this.isRunning())) {
            this.thread = new Thread() {
                public void run() {
                    task.run();
                }
            };
            this.thread.start();
            this.started = true;
        }
    }

    public synchronized boolean isRunning() {
        return this.thread != null && this.thread.isAlive();
    }

    public synchronized void stop() {
        this.thread.interrupt();
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }
}
