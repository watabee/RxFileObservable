package com.github.watabee.rxfileobservable.example

import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.set
import androidx.core.text.toSpannable
import com.github.watabee.rxfileobservable.RxFileObservable
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.withLatestFrom
import java.io.File

class MainActivity : AppCompatActivity() {

    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setup(
            textFile = File(filesDir, "sample.txt"),
            textResId = R.id.files_dir_text,
            buttonResId = R.id.files_dir_button
        )

        setup(
            textFile = File(cacheDir, "sample.txt"),
            textResId = R.id.cache_dir_text,
            buttonResId = R.id.cache_dir_button
        )

        setup(
            textFile = File(getExternalFilesDir(null), "sample.txt"),
            textResId = R.id.external_files_dir_text,
            buttonResId = R.id.external_files_dir_button
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

    private fun setup(
        textFile: File,
        @IdRes textResId: Int,
        @IdRes buttonResId: Int
    ) {
        val existsFile = RxFileObservable.exists(textFile.path)
            .observeOn(AndroidSchedulers.mainThread())
            .share()

        val textView: TextView = findViewById(textResId)
        val button: Button = findViewById(buttonResId)

        button.clicks()
            .withLatestFrom(existsFile)
            .subscribeBy { (_, exists) ->
                if (exists) {
                    if (textFile.delete()) {
                        Toast.makeText(this, "File is deleted\n$textFile", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    textFile.writeText("sample")
                    Toast.makeText(this, "File is created\n$textFile", Toast.LENGTH_SHORT).show()
                }
            }
            .addTo(disposable)

        existsFile
            .map { exists -> if (exists) "Delete text file" else "Create text file" }
            .subscribeBy(onNext = button::setText)
            .addTo(disposable)

        existsFile
            .map { exists ->
                "${textFile.path} ${if (exists) "exists" else "doesn't exist"}."
                    .toSpannable()
                    .apply {
                        this[textFile.path.length, length] =
                            ForegroundColorSpan(
                                ResourcesCompat.getColor(resources, R.color.red, null)
                            )
                    }
            }
            .subscribeBy(onNext = textView::setText)
            .addTo(disposable)
    }
}
