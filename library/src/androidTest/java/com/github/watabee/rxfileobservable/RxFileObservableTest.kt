package com.github.watabee.rxfileobservable

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.reactivex.functions.Predicate
import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileOutputStream

@RunWith(AndroidJUnit4::class)
class RxFileObservableTest {

    private lateinit var filesDir: File
    private lateinit var cacheDir: File

    @Before
    fun setup() {
        val appContext: Context = ApplicationProvider.getApplicationContext()
        filesDir = appContext.filesDir
        cacheDir = appContext.cacheDir
    }

    @Test
    fun testCreateTextFile() {
        val sampleTextFile = File(filesDir, "sample.txt")
        val filesDirObserver = RxFileObservable.events(sampleTextFile.parent).test()

        sampleTextFile.createNewFile()

        filesDirObserver.awaitCount(3)
        filesDirObserver.assertValueCount(3)

        val predicate = { type: FileEvent.Type -> checkFileEvent(type, sampleTextFile.name) }

        filesDirObserver.assertValueAt(0, predicate(FileEvent.Type.CREATE))
        filesDirObserver.assertValueAt(1, predicate(FileEvent.Type.OPEN))
        filesDirObserver.assertValueAt(2, predicate(FileEvent.Type.CLOSE_WRITE))
    }

    @Test
    fun testRenameTextFile() {
        val sampleTextFile = File(filesDir, "sample.txt")
        sampleTextFile.writeText("sample\n")

        val filesDirObserver = RxFileObservable.events(filesDir.path).test()
        val cacheDirObserver = RxFileObservable.events(cacheDir.path).test()

        sampleTextFile.renameTo(File(cacheDir, "cache.txt"))

        filesDirObserver.awaitCount(1)

        filesDirObserver.assertValueCount(1)
        filesDirObserver.assertValueAt(0, checkFileEvent(FileEvent.Type.MOVED_FROM, "sample.txt"))

        cacheDirObserver.awaitCount(1)

        cacheDirObserver.assertValueCount(1)
        cacheDirObserver.assertValueAt(0, checkFileEvent(FileEvent.Type.MOVED_TO, "cache.txt"))
    }

    @Test
    fun testDeleteTextFile() {
        val sampleTextFile = File(filesDir, "sample.txt")
        sampleTextFile.writeText("sample\n")

        val filesDirObserver = RxFileObservable.events(filesDir.path).test()

        sampleTextFile.delete()

        filesDirObserver.awaitCount(1)

        filesDirObserver.assertValueCount(1)
        filesDirObserver.assertValueAt(0, checkFileEvent(FileEvent.Type.DELETE, "sample.txt"))
    }

    @Test
    fun testMultipleObservable() {
        val sampleTextFile = File(filesDir, "sample.txt")
        val observer1 = RxFileObservable.events(filesDir.path).test()
        val predicate = { type: FileEvent.Type -> checkFileEvent(type, sampleTextFile.name) }

        sampleTextFile.createNewFile()

        observer1.awaitCount(3)
        observer1.assertValueCount(3)
        observer1.assertValueAt(0, predicate(FileEvent.Type.CREATE))
        observer1.assertValueAt(1, predicate(FileEvent.Type.OPEN))
        observer1.assertValueAt(2, predicate(FileEvent.Type.CLOSE_WRITE))

        val observer2 = RxFileObservable.events(filesDir.path).test()

        FileOutputStream(sampleTextFile, true).use {
            it.write("append\n".toByteArray())
        }

        observer1.awaitCount(6)
        observer1.assertValueCount(6)
        observer1.assertValueAt(3, predicate(FileEvent.Type.OPEN))
        observer1.assertValueAt(4, predicate(FileEvent.Type.MODIFY))
        observer1.assertValueAt(5, predicate(FileEvent.Type.CLOSE_WRITE))

        observer2.awaitCount(3)
        observer2.assertValueCount(3)
        observer2.assertValueAt(0, predicate(FileEvent.Type.OPEN))
        observer2.assertValueAt(1, predicate(FileEvent.Type.MODIFY))
        observer2.assertValueAt(2, predicate(FileEvent.Type.CLOSE_WRITE))

        observer1.onComplete()

        sampleTextFile.delete()

        observer1.assertValueCount(6)
        observer1.assertComplete()

        observer2.awaitCount(4)
        observer2.assertValueCount(4)
        observer2.assertValueAt(3, predicate(FileEvent.Type.DELETE))
    }

    @Test
    fun testFileExists() {
        val file1 = File(filesDir, "sample1.txt")
        val file2 = File(filesDir, "sample2.txt")

        val observer1 = RxFileObservable.exists(file1.parent, file1.name).test()
        val observer2 = RxFileObservable.exists(file2.parent, file2.name).test()

        fun TestObserver<Boolean>.exists(index: Int, value: Boolean) {
            awaitCount(index + 1)
            assertValueCount(index + 1)
            assertValueAt(index, value)
        }

        observer1.exists(0, false)
        observer2.exists(0, false)

        file1.createNewFile()
        observer1.exists(1, true)
        observer2.exists(0, false)

        file1.renameTo(file2)
        observer1.exists(2, false)
        observer2.exists(1, true)

        file1.createNewFile()
        observer1.exists(3, true)
        observer2.exists(1, true)

        file2.delete()
        observer1.exists(3, true)
        observer2.exists(2, false)

        file1.delete()
        observer1.exists(4, false)
        observer2.exists(2, false)
    }

    private fun checkFileEvent(type: FileEvent.Type, path: String): Predicate<FileEvent> =
        Predicate { fileEvent -> fileEvent.type == type && fileEvent.path == path }
}
