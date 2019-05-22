package com.github.watabee.rxfileobservable;

import android.os.FileObserver;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class FileObserverManager {

    private static FileObserverManager instance = null;

    private final Map<String, Holder> holders = new HashMap<>();

    private FileObserverManager() {
    }

    static FileObserverManager getInstance() {
        if (instance == null) {
            synchronized (FileObserverManager.class) {
                if (instance == null) {
                    instance = new FileObserverManager();
                }
            }
        }
        return instance;
    }

    synchronized void register(final String pathToWatch, final FileEventListener listener) {
        Holder holder = holders.get(pathToWatch);
        if (holder == null) {
            holder = new Holder();
            holders.put(pathToWatch, holder);
        }
        holder.addListener(pathToWatch, listener);
    }

    synchronized void unregister(final String pathToWatch, final FileEventListener listener) {
        final Holder holder = holders.get(pathToWatch);
        if (holder != null) {
            if (holder.removeListener(listener)) {
                holders.remove(pathToWatch);
            }
        }
    }

    private static class Holder {
        private final Set<FileEventListener> listeners = new HashSet<>();
        private FileObserver fileObserver = null;

        void addListener(final String pathToWatch, final FileEventListener listener) {
            synchronized (this) {
                listeners.add(listener);
            }

            if (fileObserver == null) {
                fileObserver = new FileObserver(pathToWatch, FileObserver.ALL_EVENTS) {
                    @Override
                    public void onEvent(final int event, final String path) {
                        final FileEvent fileEvent =
                            new FileEvent(FileEvent.Type.fromFileObserverEvent(event), path);

                        if (fileEvent.getType() == null) {
                            return;
                        }

                        synchronized (Holder.this) {
                            for (final FileEventListener l : listeners) {
                                l.onEvent(fileEvent);
                            }
                        }
                    }
                };

                fileObserver.startWatching();
            }
        }

        boolean removeListener(final FileEventListener listener) {
            synchronized (this) {
                listeners.remove(listener);
            }

            if (listeners.isEmpty() && fileObserver != null) {
                fileObserver.stopWatching();
                fileObserver = null;
                return true;
            }

            return false;
        }
    }
}
