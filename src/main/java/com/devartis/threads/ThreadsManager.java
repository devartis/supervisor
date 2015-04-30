package com.devartis.threads;

import java.util.*;

public class ThreadsManager {

    private final Config config;

    private boolean started = false;

    private Map<UUID, TaskContext> threads = new HashMap<UUID, TaskContext>();
    private Thread mainThread;

    public ThreadsManager(Config config) {
        this.config = config;

        this.mainThread = new Thread() {
            public void run() {
                while (true) {
                    for (TaskContext thread : ThreadsManager.this.threads.values()) {
                        thread.start();
                    }
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }
        };
    }

    public synchronized void start() {
        if (!mainThread.isAlive()) {
            mainThread.start();
        }
    }

    public synchronized UUID add(Runnable task) {
        return this.add(task.toString(), task);
    }

    public synchronized UUID add(Runnable task, boolean keepAlive) {
        return this.add(task.toString(), task, keepAlive);
    }

    public synchronized UUID add(String name, Runnable task) {
        return this.add(name, task, false);
    }

    public synchronized UUID add(String name, Runnable task, boolean keepAlive) {
        UUID uuid = UUID.randomUUID();
        threads.put(uuid, new TaskContext(uuid, name, task, keepAlive));
        return uuid;
    }

    public synchronized boolean remove(UUID uuid) {
        this.stop(uuid);
        return threads.remove(uuid) != null;
    }


    public synchronized void stop(UUID uuid) {
        if (this.isRunning(uuid)) {
            threads.get(uuid).stop();
        }
    }

    public synchronized int getTaskCount() {
        return threads.size();
    }

    public synchronized boolean isRunning(UUID uuid) {
        return threads.containsKey(uuid) && threads.get(uuid).isRunning();
    }

    public synchronized UUID getUUID(String name) {
        for (TaskContext thread : this.threads.values()) {
            if (name.equals(thread.getName())) {
                return thread.getUUID();
            }
        }
        return null;
    }
}
