package org.ksoftware.lorebook.pages

import javafx.scene.image.PixelFormat
import javafx.scene.image.WritablePixelFormat
import javafx.scene.paint.Color
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.delay
import kotlinx.coroutines.javafx.JavaFx
import org.ksoftware.lorebook.actions.DrawGridAction
import org.ksoftware.lorebook.actions.SaveProjectAction
import tornadofx.Controller
import java.nio.IntBuffer
import kotlin.coroutines.CoroutineContext

class PageGridController : Controller(), CoroutineScope {


    override val coroutineContext: CoroutineContext = Dispatchers.JavaFx
    private val gridViewModal: PageGridViewModal by inject()
    private val drawGridActor: SendChannel<DrawGridAction> = createDrawGridActor()
    private val canvas = gridViewModal.pageCanvas

    fun drawGrid(action: DrawGridAction) {
        drawGridActor.offer(action)
    }

    private fun createDrawGridActor() = this.actor<DrawGridAction>(capacity = 1) {
        for (action in channel) {
            val pixelFormat: WritablePixelFormat<IntBuffer> = PixelFormat.getIntArgbPreInstance()
            val gc = canvas.graphicsContext2D
            val w = canvas.width.toInt()
            val h = canvas.height.toInt()
            val buffer = IntArray(w * h)
            val gridColor = colorToInt(Color.rgb(220, 220, 220))
            val gridColor2 = colorToInt(Color.rgb(180, 180, 180))

            for (i in 0 until w) {
                for (j in 0 until h) {
                    if ((j % 128 == 0) || (i % 128 == 0)) {
                        buffer[i + w * j] = gridColor2
                        continue
                    }
                    if ((j % 32 == 0) || (i % 32 == 0)) {
                        buffer[i + w * j] = gridColor
                    }
                }
            }

            val pixelWriter = gc.pixelWriter
            pixelWriter.setPixels(0, 0, w, h, pixelFormat, buffer, 0, w)
        }
    }

    private fun colorToInt(c: Color): Int {
        return 255 shl 24 or
                ((c.red * 255).toInt() shl 16) or
                ((c.green * 255).toInt() shl 8) or
                (c.blue * 255).toInt()
    }

}