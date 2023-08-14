package com.satoshi.randommonkey.ui.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.satoshi.randommonkey.ui.utils.markdown.Markdown
import com.satoshi.randommonkey.ui.utils.markdown.textToPreviewText
import com.satoshi.randommonkey.utils.readAssets

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val content = remember { readAssets("index.md", context.assets) }
    Markdown(
        text = content,
        modifier = Modifier.fillMaxSize(),
        shouldLoadUrl = { webView, request ->
            if (request != null) {
                val requestUrl = request.url.toString()
                if (requestUrl.endsWith(".md")) {
                    val newFile =
                        requestUrl.removePrefix("file:///android_asset/html/")
                    webView?.evaluateJavascript(
                        textToPreviewText(readAssets(newFile, context.assets)), null
                    )
                    true
                } else {
                    false
                }
            } else {
                false
            }
        },
    )
}