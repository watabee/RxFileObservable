package com.github.watabee.rxfileobservable;

import io.reactivex.Observable;

public final class RxFileObservable {

    public static Observable<Boolean> exists(final String pathToWatch) {
        return Observable.create(new RxFileExistenceObservableOnSubscribe(pathToWatch));
    }
}
