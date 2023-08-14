package com.satoshi.randommonkey.ui.utils.markdown

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Base64
import android.util.Log
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.util.regex.Pattern

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun Markdown(
    modifier: Modifier = Modifier, text: String, shouldLoadUrl: (
        view: WebView?, request: WebResourceRequest?
    ) -> Boolean
) {
    val previewText = textToPreviewText(text)
    AndroidView(factory = { context ->
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    view?.evaluateJavascript(previewText, null)
                }

                override fun shouldOverrideUrlLoading(
                    view: WebView?, request: WebResourceRequest?
                ): Boolean {
                    return shouldLoadUrl(view, request)
                }
            }
            loadUrl("file:///android_asset/html/preview.html")
            settings.javaScriptEnabled = true
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
    }, modifier = modifier)
}

//region Helper Methods
private fun escapeForText(mdText: String): String {
    var escText = mdText.replace("\n", "\\\\n")
    escText = escText.replace("'", "\\\'")
    escText = escText.replace("\r", "")
    return escText
}

private fun imgToBase64(mdText: String): String {
    val ptn = Pattern.compile("!\\[(.*)\\]\\((.*)\\)")
    val matcher = ptn.matcher(mdText)
    if (!matcher.find()) {
        return mdText
    }
    val imgPath = matcher.group(2)
    imgPath?.let {
        if (isUrlPrefix(imgPath) || !isPathExCheck(imgPath)) {
            return mdText
        }
        val baseType = imgEx2BaseType(imgPath)
        if ("" == baseType) {
            return mdText
        }
        val file = File(imgPath)

        val bytes = ByteArray(file.length().toInt())
        try {
            val buf = BufferedInputStream(FileInputStream(file))
            buf.read(bytes, 0, bytes.size)
            buf.close()
        } catch (e: FileNotFoundException) {
            e.message?.let { Log.d(TAG, it) }
        } catch (e: IOException) {
            e.message?.let { Log.d(TAG, it) }
        }
        val base64Img = baseType + Base64.encodeToString(bytes, Base64.NO_WRAP)
        return mdText.replace(imgPath, base64Img)
    }
    return ""
}

fun textToPreviewText(text: String): String = "preview('${escapeForText(imgToBase64(text))}')"

private fun isUrlPrefix(text: String): Boolean {
    return text.startsWith("http://") || text.startsWith("https://")
}

private fun isPathExCheck(text: String): Boolean {
    return (text.endsWith(".png") || text.endsWith(".jpg") || text.endsWith(".jpeg") || text.endsWith(
        ".gif"
    ))
}

private fun imgEx2BaseType(text: String): String {
    return if (text.endsWith(".png")) {
        "data:image/png;base64,"
    } else if (text.endsWith(".jpg") || text.endsWith(".jpeg")) {
        "data:image/jpg;base64,"
    } else if (text.endsWith(".gif")) {
        "data:image/gif;base64,"
    } else {
        ""
    }
}

private const val TAG = "Markdown"