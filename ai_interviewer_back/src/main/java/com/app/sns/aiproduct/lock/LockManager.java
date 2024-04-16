package com.app.sns.aiproduct.lock;

import org.springframework.stereotype.Component;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class LockManager {

    private final ConcurrentHashMap<String, WeakReference<ReentrantLock>> lockMap = new ConcurrentHashMap<>();

    /**
     * 指定された ID に対応するロックを取得します。
     *
     * @param id 資源の ID
     * @return 資源の ID に対応するロック
     */
    public ReentrantLock getLock(Class<?> clazz, Long id) {
        String mapId = clazz.getSimpleName() + "_" + id;
        // 既存のロックを取得しようとする
        WeakReference<ReentrantLock> weakLockRef = lockMap.get(mapId);
        ReentrantLock lock = weakLockRef != null ? weakLockRef.get() : null;

        // ロックが存在しないか、ガベージコレクションで回収された場合は、新しいロックを作成する
        if (lock == null) {
            lock = new ReentrantLock();
            WeakReference<ReentrantLock> newWeakLockRef = new WeakReference<>(lock);
            // putIfAbsent を使用して、スレッドセーフに新しいロックをマップに追加する
            WeakReference<ReentrantLock> existingWeakLockRef = lockMap.putIfAbsent(mapId, newWeakLockRef);
            // 他のスレッドがすでに新しいロックを追加していた場合は、そのスレッドのロックを使用する
            if (existingWeakLockRef != null) {
                ReentrantLock existingLock = existingWeakLockRef.get();
                if (existingLock != null) {
                    lock = existingLock;
                } else {
                    lockMap.put(mapId, newWeakLockRef);
                }
            }
        }
        return lock;
    }
}

