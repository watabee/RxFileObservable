package com.github.watabee.rxfileobservable;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Cancellable;

class FileEventsObservableOnSubscribe implements ObservableOnSubscribe<FileEvent> {

    private final String pathToWatch;

    FileEventsObservableOnSubscribe(final String pathToWatch) {
        this.pathToWatch = pathToWatch;
    }

    @Override
    public void subscribe(final ObservableEmitter<FileEvent> emitter) throws Exception {

        final FileEventListener listener = new FileEventListener() {
            @Override
            public void onEvent(final FileEvent fileEvent) {
                emitter.onNext(fileEvent);
            }
        };

        emitter.setCancellable(new Cancellable() {
            @Override
            public void cancel() throws Exception {
                FileObserverManager.getInstance().unregister(pathToWatch, listener);
            }
        });

        FileObserverManager.getInstance().register(pathToWatch, listener);
    }
}
