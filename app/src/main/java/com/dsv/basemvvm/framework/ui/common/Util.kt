package com.dsv.basemvvm.framework.ui.common

import android.content.Context
import android.util.DisplayMetrics
import android.util.TypedValue
import java.io.IOException
import java.nio.charset.Charset

object Util {

    fun spToPx(context: Context, sp: Int): Int {
        val r = context.resources
        return Math.round(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                sp.toFloat(),
                r.displayMetrics
            )
        )
    }

    fun dpToPx(context: Context, dp: Int): Int {
        val r = context.resources
        return Math.round(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp.toFloat(),
                r.displayMetrics
            )
        )
    }

    fun pxToDp(context: Context, px: Int): Int {
        val resources = context.resources
        val metrics = resources.displayMetrics
        val dp = px / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
        return Math.round(dp)
    }

    fun loadJSONFromAsset(context: Context, fileName: String?): String? {
        var json: String? = null
        json = try {
            val inputStream = context.assets.open(fileName!!)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, Charset.defaultCharset())
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }
}