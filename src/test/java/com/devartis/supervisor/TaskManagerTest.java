package com.devartis.supervisor;

import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class TaskManagerTest {

    private TaskManager manager;

    @Before
    public void setUp() {
        this.manager = new TaskManager(new Config());
    }

    @Test
    public void testAddRemoveThread() {
        Thread task1 = new Thread();
        Thread task2 = new Thread();

        UUID id1 = manager.add(task1);
        assertEquals(1, manager.getTaskCount());

        UUID id2 = manager.add(task2);
        assertEquals(2, manager.getTaskCount());

        boolean found = manager.remove(id1);
        assertTrue(found);
        assertEquals(1, manager.getTaskCount());
    }

    @Test
    public void testAddThreadWithName() {
        Thread task1 = new Thread();
        Thread task2 = new Thread();

        UUID id1 = manager.add("Stream1", task1);
        assertEquals(1, manager.getTaskCount());
        assertEquals(id1, manager.getUUID("Stream1"));

        UUID id2 = manager.add("Stream2", task2);
        assertEquals(2, manager.getTaskCount());
        assertEquals(id2, manager.getUUID("Stream2"));

        UUID id3 = manager.add("Stream1-1", task1);
        assertEquals(3, manager.getTaskCount());
        assertEquals(id3, manager.getUUID("Stream1-1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddRepeatedName() {
        addRepeatedTasks(true);
    }

    @Test
    public void testAddRepeatedNameWithoutValidation() {
        Config config = new Config();
        config.setValidateNameUniqueness(false);
        manager = new TaskManager(config);

        addRepeatedTasks(true);
    }

    @Test
    public void testAddRepeatedNameWithoutKeepAlive() {
        addRepeatedTasks(false);
    }

    @Test
    public void testRunThread() throws InterruptedException {
        final int[] result = {1};

        Runnable task1 = new Runnable() {
            public void run() {
                result[0] += 100;
            }
        };
        Runnable task2 = new Runnable() {
            public void run() {
                result[0] += 99;
            }
        };

        assertEquals(1, result[0]);

        manager.add(task1);
        manager.start();
        Thread.sleep(100);

        assertEquals(101, result[0]);

        manager.add(task2);
        Thread.sleep(100);

        assertEquals(200, result[0]);
    }

    @Test
    public void testRestartThread() throws InterruptedException {
        final int[] result = {1};

        Runnable task = new Runnable() {
            public void run() {
                result[0] += 1;
            }
        };

        manager.add(task, true);
        manager.start();

        Thread.sleep(500);
        assertTrue(result[0] > 2);
    }

    @Test
    public void testIsRunning() throws InterruptedException {
        manager.start();

        UUID task1 = manager.add(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {}
            }
        });

        UUID task2 = manager.add(new Runnable() {
            @Override
            public void run() {}
        });

        Thread.sleep(100);

        assertTrue(manager.isRunning(task1));
        assertFalse(manager.isRunning(task2));
    }

    @Test
    public void testIsRunningUsingName() throws InterruptedException {
        manager.start();

        manager.add("task1", new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {}
            }
        });

        manager.add("task2", new Runnable() {
            @Override
            public void run() {}
        });

        Thread.sleep(100);

        assertTrue(manager.isRunning("task1"));
        assertFalse(manager.isRunning("task2"));
        assertFalse(manager.isRunning("NOT_EXISTING_TASK"));
    }

    @Test
    public void testStop() throws InterruptedException {
        manager.start();

        UUID task1 = manager.add(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                }
            }
        });

        Thread.sleep(100);
        assertTrue(manager.isRunning(task1));
        manager.stop(task1);
        Thread.sleep(100);
        assertFalse(manager.isRunning(task1));

        final boolean[] interrupted = {false};

        UUID task2 = manager.add(new Task() {
            @Override
            public void doRun() throws InterruptedException {
                while(true) {
                    stopIfInterrupted();
                }
            }

            @Override
            protected void onInterrupt() {
                interrupted[0] = true;
            }
        });

        Thread.sleep(100);
        assertTrue(manager.isRunning(task2));
        manager.stop(task2);
        Thread.sleep(200);
        assertFalse(manager.isRunning(task2));
        assertTrue(interrupted[0]);
    }

    private void addRepeatedTasks(boolean keepAlive) {
        Thread task1 = new Thread();
        Thread task2 = new Thread();

        manager.add("Task1", task1, keepAlive);
        manager.add("Task2", task2, keepAlive);
        manager.add("Task1", task1, keepAlive);
    }
}
