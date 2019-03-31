# RxFileObservable
[![Release](https://img.shields.io/badge/jcenter-0.2.0-blue.svg)](https://bintray.com/watabee/maven/rx-file-observable)

[RxJava](https://github.com/ReactiveX/RxJava) file bindings for Android

## Download

```
implementation "com.github.watabee:rx-file-observable:<latest-version>"
```

## Usage

### events

You can receive file events that are corresponds to [android.os.FileObserver](https://developer.android.com/reference/android/os/FileObserver) events.

Example

```
RxFileObservable.events(File(context.filesDir, "sample.txt").path)
  .observeOn(AndroidSchedulers.mainThread())
  .subscribe(fileEvent -> ...);
```


### exists

You can receive whether specified file exists.

Example

```
RxFileObservable.exists(context.filesDir.path, "sample.txt")
  .observeOn(AndroidSchedulers.mainThread())
  .subscribe(exists -> ...);
```

(Note: `AndroidSchedulers` is from [RxAndroid](https://github.com/ReactiveX/RxAndroid))

## License

```
Copyright 2019 watabee

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
