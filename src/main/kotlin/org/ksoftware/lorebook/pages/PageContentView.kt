package org.ksoftware.lorebook.pages

import javafx.event.EventHandler
import javafx.geometry.Point2D
import javafx.geometry.Pos
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.TabPane
import javafx.scene.control.TextArea
import javafx.scene.input.ScrollEvent
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import org.ksoftware.lorebook.main.ProjectViewModel
import org.ksoftware.lorebook.main.ProjectWorkspaceController
import org.ksoftware.lorebook.nodes.TextNode
import org.ksoftware.lorebook.organiser.tagflow.TagFlowController
import org.ksoftware.lorebook.organiser.tagflow.TagFlowViewModel
import org.ksoftware.lorebook.richtext.ToolbarViewModal
import tornadofx.*
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.LinearGradient
import javafx.scene.paint.Stop
import org.fxmisc.wellbehaved.event.InputMap
import org.fxmisc.wellbehaved.event.Nodes
import org.ksoftware.lorebook.styles.Styles


class PageContentView : View() {

    private val pageController: PageController by inject(FX.defaultScope)
    private val pageViewModel: PageViewModel by inject()
    private val gridViewModal: PageGridViewModal by inject()
    private val gridController: PageGridController by inject()

    private val toolbarViewModal: ToolbarViewModal by inject()

    private val projectViewModel: ProjectViewModel by inject()
    private val projectController: ProjectWorkspaceController by inject()

    private var scaleValue = 1.0
    private val zoomIntensity = 0.04

    val target = pane {
        addEventHandler(MouseEvent.ANY) {
            if (it.button == MouseButton.PRIMARY) it.consume()
        }
        style {
            backgroundColor = multi(

                Styles.parchment,
                LinearGradient(0.0, 0.0, 40.0, 0.0, false,
                CycleMethod.REPEAT,
                Stop(0.0, Color.rgb(0,0,0,0.3)),
                Stop(0.01, Color.rgb(0,0,0,0.3)),
                Stop(0.02, Color.TRANSPARENT),
                Stop(1.0, Color.TRANSPARENT)),
                LinearGradient(0.0, 0.0, 0.0, 40.0, false,
                    CycleMethod.REPEAT,
                    Stop(0.0, Color.rgb(0,0,0,0.3)),
                    Stop(0.01, Color.rgb(0,0,0,0.3)),
                    Stop(0.02, Color.TRANSPARENT),
                    Stop(1.0, Color.TRANSPARENT))
            )
        }
//        add(gridViewModal.pageCanvas.apply {
//            width = 3000.0
//            height = 2000.0
//        })

        for (i in 0..2) {
            val node = find<TextNode>(Scope(projectViewModel, toolbarViewModal))
            node.root.setPrefSize(300.0, 300.0)
            // define the style via css
            node.root.style = CSS_STYLE
            // position the node
            node.root.layoutX = 200.0 + 500.0*i
            node.root.layoutY = 200.0 + 500.0*i
            // add the node to the root pane
            add(node)
        }
    }
    val zoomNode = Group(target)
    val CSS_STYLE = """  
                -fx-glass-color: rgba(85, 132, 160, 0.9);
                -fx-alignment: center;
                -fx-background-color: -fx-glass-color;
                -fx-border-color: derive(-fx-glass-color, -60%);
                -fx-border-width: 2;
                -fx-background-insets: 1;
                -fx-border-radius: 3;
                -fx-padding: 20;
                """

    override val root = scrollpane {
        isPannable = true
        isFitToHeight = true //center
        isFitToWidth = true //center
        content = outerNode(zoomNode)
        updateScale()
    }

    private fun updateScale() {
        target.scaleX = scaleValue
        target.scaleY = scaleValue
    }

    private fun onScroll(wheelDelta: Double, mousePoint: Point2D) {
        with (root) {
            var zoomFactor = Math.exp(wheelDelta * zoomIntensity)
            if (zoomFactor < 1 && scaleValue == 0.25) return
            if (zoomFactor > 1 && scaleValue == 3.0) return
            val innerBounds = zoomNode.layoutBounds
            val viewportBounds = viewportBounds

            // calculate pixel offsets from [0, 1] range
            val valX = hvalue * (innerBounds.width - viewportBounds.width)
            val valY = vvalue * (innerBounds.height - viewportBounds.height)
            scaleValue = scaleValue * zoomFactor
            scaleValue = scaleValue.coerceAtLeast(0.25)
            scaleValue = scaleValue.coerceAtMost(3.0)
            updateScale()
            layout() // refresh ScrollPane scroll positions & target bounds

            // convert target coordinates to zoomTarget coordinates
            val posInZoomTarget = target.parentToLocal(zoomNode.parentToLocal(mousePoint))

            // calculate adjustment of scroll position (pixels)
            val adjustment = target.localToParentTransform.deltaTransform(posInZoomTarget.multiply(zoomFactor - 1))

            // convert back to [0, 1] range
            // (too large/small values are automatically corrected by ScrollPane)
            val updatedInnerBounds = zoomNode.boundsInLocal
            hvalue = (valX + adjustment.x) / (updatedInnerBounds.width - viewportBounds.width)
            vvalue = (valY + adjustment.y) / (updatedInnerBounds.height - viewportBounds.height)
        }
    }

    private fun centeredNode(node: Node): Node {
        val vBox = VBox(node)
        vBox.style {
            backgroundColor += Color.GRAY
        }
        vBox.alignment = Pos.CENTER
        return vBox
    }

    private fun outerNode(node: Node): Node {
        val outerNode = centeredNode(node)
        outerNode.addEventFilter(ScrollEvent.SCROLL) {
            if (it.isControlDown) {
                onScroll(it.textDeltaY, Point2D(it.x, it.y))
                it.consume()
            }
        }
        return outerNode
    }
}