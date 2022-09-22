package com.devyk.aveditor.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView

/**
 * <pre>
 * author  : devyk on 2020-08-05 14:01
 * blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 * github  : https://github.com/yangkun19921001
 * mailbox : yang1001yk@gmail.com
 * desc    : This is BitmapUtils
</pre> *
 */
object BitmapUtils {
    fun messageToBitmap(
        context: Context,
        message: String
    ): Bitmap {
        val tv = TextView(context)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        tv.layoutParams = layoutParams
        tv.text = message
        tv.textSize = 20f
        tv.gravity = Gravity.CENTER_HORIZONTAL
        tv.isDrawingCacheEnabled = true
        tv.setTextColor(Color.WHITE)
        tv.measure(
            View.MeasureSpec.makeMeasureSpec(
                0,
                View.MeasureSpec.UNSPECIFIED
            ),
            View.MeasureSpec.makeMeasureSpec(
                0,
                View.MeasureSpec.UNSPECIFIED
            )
        )
        tv.layout(0, 0, tv.measuredWidth, tv.measuredHeight)
        tv.setBackgroundColor(Color.TRANSPARENT)
        tv.buildDrawingCache()
        //        return zoomBitmap(oldBitmap, oldBitmap.getWidth() * 2, oldBitmap.getHeight() * 2);
        return tv.drawingCache
    }

    fun messageToBitmapWithBg(
        context: Context,
        message: String,
        drawable: Drawable? = null,
        typeface: Typeface? = null
    ): Bitmap {
        val tv = TextView(context)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        tv.layoutParams = layoutParams
        tv.background = drawable
        tv.text = message
        tv.textSize = 19f
        tv.setTypeface(typeface, Typeface.BOLD_ITALIC)
        tv.gravity = Gravity.CENTER
        tv.isDrawingCacheEnabled = true
        tv.setTextColor(Color.WHITE)
        tv.measure(
            View.MeasureSpec.makeMeasureSpec(
                0,
                View.MeasureSpec.UNSPECIFIED
            ),
            View.MeasureSpec.makeMeasureSpec(
                0,
                View.MeasureSpec.UNSPECIFIED
            )
        )
        tv.layout(0, 0, tv.measuredWidth, tv.measuredHeight)
        tv.buildDrawingCache()
        //        return zoomBitmap(oldBitmap, oldBitmap.getWidth() * 2, oldBitmap.getHeight() * 2);
        return tv.drawingCache
    }

    fun zoomBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
        // 获得图片的宽高
        val width = bm.width.toFloat()
        val height = bm.height.toFloat()
        // 计算缩放比例
        val scaleWidth = newWidth / width
        val scaleHeight = newHeight / height
        // 取得想要缩放的matrix参数
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)
        // 得到新的图片
        return Bitmap.createBitmap(bm, 0, 0, width.toInt(), height.toInt(), matrix, true)
    }
}