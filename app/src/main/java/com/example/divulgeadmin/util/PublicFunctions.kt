package com.example.divulgeadmin.util

import android.graphics.Bitmap
import java.util.*

class PublicFunctions {
    companion object {
        fun getCurrentDateTime(): String {
            val calendar = Calendar.getInstance()
            val date = Date()
            calendar.time = date
            return "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-${calendar.get(
                Calendar.DAY_OF_MONTH)} ${calendar.get(Calendar.HOUR_OF_DAY)}:${
                calendar.get(
                    Calendar.MINUTE
                )
            }:${calendar.get(Calendar.SECOND)}"
        }

        fun resizeBitmap(source: Bitmap, maxLength: Int = 200): Bitmap {
            return try {
                if (source.height >= source.width) {
                    // if image already smaller than the required height.
                    if (source.height <= maxLength)
                        return source

                    val aspectRatio = source.width.toDouble() / source.height.toDouble()
                    val targetWidth = (maxLength * aspectRatio).toInt()
                    Bitmap.createScaledBitmap(source, targetWidth, maxLength, false)
                } else {
                    // if image already smaller than the required height.
                    if (source.width <= maxLength)
                        return source

                    val aspectRatio = source.height.toDouble() / source.width.toDouble()
                    val targetHeight = (maxLength * aspectRatio).toInt()
                    Bitmap.createScaledBitmap(source, maxLength, targetHeight, false)
                }
            } catch (e: Exception) {
                source
            }
        }
    }
}