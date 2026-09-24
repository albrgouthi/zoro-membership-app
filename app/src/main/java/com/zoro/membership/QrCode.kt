package com.zoro.membership

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

fun generateQrBitmap(content: String, sizePx: Int = 512): Bitmap {
    val writer = QRCodeWriter()
    val matrix = writer.encode(content, BarcodeFormat.QR_CODE, sizePx, sizePx)
    val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.RGB_565)
    for (x in 0 until sizePx) {
        for (y in 0 until sizePx) {
            bitmap.setPixel(x, y, if (matrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
        }
    }
    return bitmap
}

/**
 * The token changes every 30 seconds. A screenshot of it stops matching
 * within half a minute, which is what tells a merchant's scanner this is
 * a live card and not a saved image. Real fraud-proofing still needs a
 * server-side check when we build the merchant scanner — this generates
 * the rotating value the scanner will eventually verify against.
 */
fun rotatingToken(userId: String): String {
    val window = System.currentTimeMillis() / 30_000L
    return "ZORO:$userId:$window"
}
