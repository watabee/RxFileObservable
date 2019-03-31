package com.github.watabee.rxfileobservable;

import android.text.TextUtils;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

public final class RxFileObservable {

    /**
     * Returns an Observable that emits whether specified file exists.
     * <p>
     * If the file is created, deleted, moved, then you can receive emitted boolean value.
     *
     * @param parentDirPath an parent directory path that you want to watch
     * @param filePath      a file path relative to the parent directory path
     * @return an Observable that emits whether specified file exists
     */
    public static Observable<Boolean> exists(final String parentDirPath, final String filePath) {
        if (TextUtils.isEmpty(parentDirPath)) {
            throw new IllegalArgumentException("'parentDirPath' must not be empty.");
        }
        if (TextUtils.isEmpty(filePath)) {
            throw new IllegalArgumentException("'filePath' must not be empty.");
        }

        final File file = new File(parentDirPath, filePath);

        return Observable.create(new FileEventsObservableOnSubscribe(parentDirPath))
            .filter(new Predicate<FileEvent>() {
                @Override
                public boolean test(final FileEvent fileEvent) throws Exception {
                    switch (fileEvent.getType()) {
                        case CREATE:
                        case DELETE:
                        case MOVED_TO:
                        case MOVED_FROM:
                            return filePath.equals(fileEvent.getPath());
                        default:
                            return false;
                    }
                }
            })
            .map(new Function<FileEvent, Boolean>() {
                @Override
                public Boolean apply(final FileEvent fileEvent) throws Exception {
                    return file.exists();
                }
            })
            .startWith(file.exists());
    }

    /**
     * Returns an Observable that emits file events that are corresponds to {@link android.os.FileObserver} events.
     *
     * @param pathToWatch a path that you want to watch
     * @return an Observable that emits file events
     * @see FileEvent
     * @see android.os.FileObserver
     */
    public static Observable<FileEvent> events(final String pathToWatch) {
        if (TextUtils.isEmpty(pathToWatch)) {
            throw new IllegalArgumentException("'pathToWatch' must not be empty.");
        }
        return Observable.create(new FileEventsObservableOnSubscribe(pathToWatch));
    }
}
