package org.ksoftware.lorebook.nodes

import javafx.scene.Cursor
import javafx.scene.layout.Region
import javafx.scene.transform.Scale
import tornadofx.View
import tornadofx.stackpane
import kotlin.math.abs

/**
 * The transformable node can be resized a well as drag and dropped by the user.
 */
abstract class TransformableNode : View() {

    private var mouseX = 0.0
    private var mouseY = 0.0

    private var nodeOriginX = 0.0
    private var nodeOriginY = 0.0
    private var nodeX = 0.0
    private var nodeY = 0.0
    private var resize = Resize.NONE
    private var RESIZE_TOP = false
    private var RESIZE_LEFT = false
    private var RESIZE_BOTTOM = false
    private var RESIZE_RIGHT = false

    override val root = stackpane {
        val scaleTransform = Scale(1.0, 1.0, 0.0, 0.0)
        transforms.add(scaleTransform)

        setOnMousePressed { event ->
            mouseX = event.sceneX
            mouseY = event.sceneY

            val parentScaleX = parent.localToSceneTransformProperty().value.mxx
            val parentScaleY = parent.localToSceneTransformProperty().value.myy

            nodeX = layoutX * parentScaleX
            nodeY = layoutY * parentScaleY
            nodeOriginX = layoutX * parentScaleX
            nodeOriginY = layoutY * parentScaleY
        }

        setOnMouseReleased {
            val origin = Pair(nodeOriginX, nodeOriginY)
            val end = Pair(nodeX, nodeY)
        }

        setOnMouseDragged { event ->
            val parentScaleX = parent.localToSceneTransformProperty().value.mxx
            val parentScaleY = parent.localToSceneTransformProperty().value.myy
            val scaleX = localToSceneTransformProperty().value.mxx
            val scaleY = localToSceneTransformProperty().value.myy
            val offsetX = event.sceneX - mouseX
            val offsetY = event.sceneY - mouseY

            if (resize == Resize.NONE) {

                nodeX += offsetX
                nodeY += offsetY


                var scaledX = nodeX / parentScaleX
                var scaledY = nodeY / parentScaleY

                if (scaledX < 0) {
                    scaledX = 0.0
                }
                if (scaledX > (parent as Region).width - width) {
                    scaledX = (parent as Region).width - width
                }

                if (scaledY < 0) {
                    scaledY = 0.0
                }
                if (scaledY > (parent as Region).height - height) {
                    scaledY = (parent as Region).height - height
                }

                layoutX = scaledX
                layoutY = scaledY

            } else {
                if (RESIZE_TOP) {
                    nodeY += offsetY
                    var scaledY = nodeY / parentScaleY
                    if (scaledY < 0) {
                        scaledY = 0.0
                        nodeY -= offsetY
                    } else {
                        prefHeight -= offsetY
                        if (prefHeight < 100) {
                            prefHeight = 100.0
                        }
                    }
                    layoutY = scaledY
                }
                if (RESIZE_LEFT) {
                    nodeX += offsetX
                    var scaledX = nodeX / parentScaleX
                    if (scaledX < 0) {
                        scaledX = 0.0
                        nodeX -= offsetX
                    } else {
                        prefWidth -= offsetX
                    }
                    layoutX = scaledX
                }
                if (RESIZE_RIGHT) {
                    if (nodeX + prefWidth + offsetX < (parent as Region).width) {
                        prefWidth += offsetX
                    } else {
                        prefWidth = (parent as Region).width - nodeX
                    }
                }
                if (RESIZE_BOTTOM) {
                    if (nodeY + prefHeight + offsetY < (parent as Region).height) {
                        prefHeight += offsetY
                    } else {
                        prefHeight = (parent as Region).height - nodeY
                    }
                }
            }

            mouseX = event.sceneX
            mouseY = event.sceneY

            event.consume()
        }

        setOnMouseMoved { t ->
            val scaleX = localToSceneTransformProperty().value.mxx
            val scaleY = localToSceneTransformProperty().value.myy
            val border = 10.0
            val diffMinX: Double = abs(boundsInLocal.minX - t.x)
            val diffMinY: Double = abs(boundsInLocal.minY - t.y)
            val diffMaxX: Double = abs(boundsInLocal.maxX - t.x)
            val diffMaxY: Double = abs(boundsInLocal.maxY - t.y)
            val left = diffMinX * scaleX < border
            val top = diffMinY * scaleY < border
            val right = diffMaxX * scaleX < border
            val bottom = diffMaxY * scaleY < border
            RESIZE_TOP = false
            RESIZE_LEFT = false
            RESIZE_BOTTOM = false
            RESIZE_RIGHT = false
            if (left && !top && !bottom) {
                cursor = Cursor.W_RESIZE
                resize = Resize.LEFT
                RESIZE_LEFT = true
            } else if (left && top && !bottom) {
                cursor = Cursor.NW_RESIZE
                resize = Resize.TOP_LEFT
                RESIZE_LEFT = true
                RESIZE_TOP = true
            } else if (left && !top && bottom) {
                cursor = Cursor.SW_RESIZE
                resize = Resize.BOTTOM_LEFT
                RESIZE_LEFT = true
                RESIZE_BOTTOM = true
            } else if (right && !top && !bottom) {
                cursor = Cursor.E_RESIZE
                resize = Resize.RIGHT
                RESIZE_RIGHT = true
            } else if (right && top && !bottom) {
                cursor = Cursor.NE_RESIZE
                resize = Resize.TOP_RIGHT
                RESIZE_RIGHT = true
                RESIZE_TOP = true
            } else if (right && !top && bottom) {
                cursor = Cursor.SE_RESIZE
                resize = Resize.BOTTOM_RIGHT
                RESIZE_RIGHT = true
                RESIZE_BOTTOM = true
            } else if (top && !left && !right) {
                cursor = Cursor.N_RESIZE
                resize = Resize.TOP
                RESIZE_TOP = true
            } else if (bottom && !left && !right) {
                cursor = Cursor.S_RESIZE
                resize = Resize.BOTTOM
                RESIZE_BOTTOM = true
            } else {
                cursor = Cursor.DEFAULT
                resize = Resize.NONE
            }
        }

    }

    private enum class Resize {
        NONE,
        TOP,
        LEFT,
        BOTTOM,
        RIGHT,
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT
    }

}
