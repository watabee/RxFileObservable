package com.github.watabee.rxfileobservable;

import android.os.FileObserver;

public final class FileEvent {

    private final Type type;
    private final String path;

    FileEvent(final Type type, final String path) {
        this.type = type;
        this.path = path;
    }

    public Type getType() {
        return type;
    }

    public String getPath() {
        return path;
    }

    public enum Type {
        /**
         * Event type: Data was read from a file
         */
        ACCESS,

        /**
         * Event type: Data was written to a file
         */
        MODIFY,

        /**
         * Event type: Metadata (permissions, owner, timestamp) was changed explicitly
         */
        ATTRIB,

        /**
         * Event type: Someone had a file or directory open for writing, and closed it
         */
        CLOSE_WRITE,

        /**
         * Event type: Someone had a file or directory open read-only, and closed it
         */
        CLOSE_NOWRITE,

        /**
         * Event type: A file or directory was opened
         */
        OPEN,

        /**
         * Event type: A file or subdirectory was moved from the monitored directory
         */
        MOVED_FROM,

        /**
         * Event type: A file or subdirectory was moved to the monitored directory
         */
        MOVED_TO,

        /**
         * Event type: A new file or subdirectory was created under the monitored directory
         */
        CREATE,

        /**
         * Event type: A file was deleted from the monitored directory
         */
        DELETE,

        /**
         * Event type: The monitored file or directory was deleted; monitoring effectively stops
         */
        DELETE_SELF,

        /**
         * Event type: The monitored file or directory was moved; monitoring continues
         */
        MOVE_SELF;

        static Type fromFileObserverEvent(final int eventType) {
            switch (eventType) {
                case FileObserver.ACCESS:
                    return ACCESS;
                case FileObserver.MODIFY:
                    return MODIFY;
                case FileObserver.ATTRIB:
                    return ATTRIB;
                case FileObserver.CLOSE_WRITE:
                    return CLOSE_WRITE;
                case FileObserver.CLOSE_NOWRITE:
                    return CLOSE_NOWRITE;
                case FileObserver.OPEN:
                    return OPEN;
                case FileObserver.MOVED_FROM:
                    return MOVED_FROM;
                case FileObserver.MOVED_TO:
                    return MOVED_TO;
                case FileObserver.CREATE:
                    return CREATE;
                case FileObserver.DELETE:
                    return DELETE;
                case FileObserver.DELETE_SELF:
                    return DELETE_SELF;
                case FileObserver.MOVE_SELF:
                    return MOVE_SELF;
                default:
                    return null;
            }
        }
    }
}
