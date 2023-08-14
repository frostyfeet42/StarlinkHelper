package com.satoshi.randommonkey.utils

import android.content.res.AssetManager

fun readAssets(inFile: String, assets: AssetManager): String {
    val stream = assets.open(inFile)
    val size = stream.available()
    val buffer = ByteArray(size)
    stream.read(buffer)
    stream.close()
    return String(buffer)
}