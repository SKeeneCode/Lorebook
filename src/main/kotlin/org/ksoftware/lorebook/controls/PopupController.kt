package org.ksoftware.lorebook.controls

import javafx.animation.Interpolator
import javafx.geometry.Point2D
import javafx.scene.layout.Region
import javafx.stage.Popup
import javafx.stage.PopupWindow
import javafx.util.Duration
import tornadofx.Controller
import tornadofx.animate

class PopupController : Controller() {

    fun showPopup(popup: Popup, ownerNode: Region) {
        val popupContent = popup.content.first()
        popupContent.opacity = 0.0
        popupContent.layoutY = 0.0
        popup.anchorLocation = PopupWindow.AnchorLocation.WINDOW_TOP_LEFT
        val anchorPoint: Point2D = ownerNode.localToScreen(
            ownerNode.width / 2,
            ownerNode.height
        )

        popup.show(
            ownerNode,
            anchorPoint.x,
            anchorPoint.y - ownerNode.height / 2
        )

        popup.anchorX -= popup.width / 2

        popupContent.layoutYProperty().animate(endValue = ownerNode.height / 2, duration = Duration(300.0), Interpolator.EASE_OUT)
        popupContent.opacityProperty().animate(endValue = 1.0, duration = Duration(300.0), Interpolator.EASE_OUT)
    }
}