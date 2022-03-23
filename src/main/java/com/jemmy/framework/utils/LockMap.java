package com.jemmy.framework.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public abstract class LockMap<K> {


    public static <K> LockMap<K> forLargeKeySet() {

        return new GCedLockMap<>();
    }

    public static <K> LockMap<K> forSmallKeySet() {

        return new NotGCedLockMap<>();
    }

    public abstract void lock(K key) throws InterruptedException;

    public abstract void unlock(K key);

    public abstract boolean isLockedByCurrentThread(K key);

    LockMap() {
    }

    private static class GCedLockMap<K> extends LockMap<K> {

        private final ConcurrentHashMap<K, ReentrantLock> sharedLocks;

        private final ThreadLocal<Map<K, ReentrantLock>> localLocks;

        private GCedLockMap() {
            sharedLocks = new ConcurrentHashMap<>();
            localLocks = ThreadLocal.withInitial(HashMap::new);
        }

        @Override
        public void lock(K key) throws InterruptedException {

            if (key == null)
                throw new NullPointerException();

            Map<K, ReentrantLock> localMap = localLocks.get();
            ReentrantLock localLock = localMap.get(key);
            if (localLock == null) {

                localLock = new ReentrantLock();

                // potential failure point: safe to fail, won't effect any internal state.
                localLock.lockInterruptibly();
                localMap.put(key, localLock);

                ReentrantLock sharedLock = sharedLocks.put(key, localLock);
                if (sharedLock != null) {

                    // while waiting for the sharedLock, the current thread simply can not
                    // be interrupted, or we're screwed.

                    sharedLock.lock();
                    sharedLock.unlock();

                    // sharedLock (the old one, created by another thread) will then be GCed,
                    // once its reference in its creating thread's ThreadLocal map is released.
                }
            } else {
                localLock.lockInterruptibly();
            }
        }

        @Override
        public void unlock(K key) {

            if (key == null)
                throw new NullPointerException();

            Map<K, ReentrantLock> localMap = localLocks.get();
            ReentrantLock localLock = localMap.get(key);
            if (localLock == null) {

                throw new IllegalArgumentException(
                        "Cannot release lock, because current thread does not hold "
                                + "lock for the key: " + key);
            }

            int count = localLock.getHoldCount();
            if (count == 1) {
                sharedLocks.remove(key, localLock);
                localMap.remove(key);
            }
            localLock.unlock();
        }

        @Override
        public boolean isLockedByCurrentThread(K key) {

            if (key == null)
                throw new NullPointerException();

            return localLocks.get().containsKey(key);
        }
    }

    private static class NotGCedLockMap<K> extends LockMap<K> {

        private final Map<K, ReentrantLock> locks;

        private NotGCedLockMap() {

            locks = new ConcurrentHashMap<>();
        }

        @Override
        public void lock(K key) throws InterruptedException {

            if (key == null)
                throw new NullPointerException();

            ReentrantLock lock = locks.get(key);

            if (lock == null) synchronized (locks) {

                lock = locks.get(key);
                if (lock == null) {

                    lock = new ReentrantLock();
                    locks.put(key, lock);
                }
            }
            lock.lockInterruptibly();
        }

        @Override
        public void unlock(K key) {

            if (key == null)
                throw new NullPointerException();

            ReentrantLock lock = locks.get(key);
            if (lock == null) {

                throw new IllegalArgumentException(
                        "Cannot release lock, because current thread does not hold "
                                + "lock for the key: " + key);
            }
            lock.unlock();
        }

        @Override
        public boolean isLockedByCurrentThread(K key) {

            if (key == null)
                throw new NullPointerException();

            ReentrantLock lock = locks.get(key);
            return lock != null && lock.isHeldByCurrentThread();
        }
    }
}
