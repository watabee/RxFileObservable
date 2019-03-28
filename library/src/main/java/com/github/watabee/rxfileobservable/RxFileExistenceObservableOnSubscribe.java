package com.github.watabee.rxfileobservable;

import android.os.FileObserver;

import java.io.File;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Cancellable;

class RxFileExistenceObservableOnSubscribe implements ObservableOnSubscribe<Boolean> {

    private static final int EVENT_MASK =
        FileObserver.CREATE | FileObserver.DELETE | FileObserver.MOVED_FROM | FileObserver.MOVED_TO;

    private final String pathToWatch;

    RxFileExistenceObservableOnSubscribe(final String pathToWatch) {
        this.pathToWatch = pathToWatch;
    }

    @Override
    public void subscribe(final ObservableEmitter<Boolean> emitter) throws Exception {

        final File file = new File(pathToWatch);
        final File parent = new File(pathToWatch).getParentFile();

        final FileObserver observer = new FileObserver(parent.getPath(), EVENT_MASK) {
            @Override
            public void onEvent(final int event, final String path) {
                if (file.getName().equals(path)) {
                    emitter.onNext(file.exists());
                }
            }
        };

        emitter.onNext(file.exists());
        emitter.setCancellable(new Cancellable() {
            @Override
            public void cancel() throws Exception {
                observer.stopWatching();
            }
        });

        observer.startWatching();
    }
}
