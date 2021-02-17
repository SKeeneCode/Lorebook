package org.ksoftware.lorebook.pages

import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.geometry.Orientation
import javafx.geometry.Point2D
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.Node
import javafx.scene.control.ScrollPane
import javafx.scene.control.SplitPane
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority
import org.ksoftware.lorebook.controls.AutoCompleteTagField
import org.ksoftware.lorebook.controls.CanvasPane
import org.ksoftware.lorebook.main.ProjectViewModel
import org.ksoftware.lorebook.main.ProjectWorkspaceController
import org.ksoftware.lorebook.nodes.TextNode
import org.ksoftware.lorebook.organiser.Organiser
import org.ksoftware.lorebook.organiser.tagflow.TagFlowController
import org.ksoftware.lorebook.organiser.tagflow.TagFlowViewModel
import org.ksoftware.lorebook.richtext.ToolbarViewModal
import org.ksoftware.lorebook.styles.Styles
import org.ksoftware.lorebook.tags.TagFunction
import tornadofx.*
import javafx.scene.control.ScrollBar
import javafx.scene.control.Skin
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import org.ksoftware.lorebook.actions.DrawGridAction


/**
 * View class for a page of a users project. These Pages are docked a ProjectWorkspace under
 * a new scope. They contain a pane to hold the users content nodes (text node etc) as well as controls for rich text
 * and page tags.
 */
class PageView : View("MyPage") {

    private val pageController: PageController by inject(FX.defaultScope)
    private val pageViewModel: PageViewModel by inject()
    private val gridViewModal: PageGridViewModal by inject()
    private val gridController: PageGridController by inject()

    private val tagFlowViewModel: TagFlowViewModel by inject()
    private val tagFlowController: TagFlowController by inject()
    private val toolbarViewModal: ToolbarViewModal by inject()

    private val projectViewModel: ProjectViewModel by inject()
    private val projectController: ProjectWorkspaceController by inject()

    var contentContainer: Pane by singleAssign()

    init {
        tagFlowViewModel.deleteFunction = TagFunction { pageViewModel.tags.value.remove(it) }
    }

    override fun onDock() {
        projectViewModel.currentRichText.value = null
        gridController.drawGrid(DrawGridAction())
    }

    override val root = splitpane(Orientation.VERTICAL) {
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


        scrollpane {
            isFitToWidth = true
            isFitToHeight = true
            isPannable = true
            paddingAll = 0
            stackpane {
                pane {
                    style {
                        backgroundColor += Color.GRAY
                    }
                }

                contentContainer = pane {
                    add(gridViewModal.pageCanvas.apply {
                        width = 1000.0
                        height = 800.0
                    })
                    style {
                        backgroundColor += Color.WHITE
                    }
                    minWidth = Region.USE_PREF_SIZE
                    minHeight = Region.USE_PREF_SIZE
                    prefWidth = 1000.0
                    prefHeight = 800.0
                    maxWidth = Region.USE_PREF_SIZE
                    maxHeight = Region.USE_PREF_SIZE
                    for (i in 0..2) {
                        val node = find<TextNode>(Scope(projectViewModel, toolbarViewModal))
                        node.root.setPrefSize(300.0, 300.0)
                        node.root.style = CSS_STYLE
                        node.root.layoutX = 50.0 + 200.0 * i
                        node.root.layoutY = 50.0
                        add(node)
                    }
                }


            }
        }

        hbox {
            SplitPane.setResizableWithParent(this, false)
            prefHeight = 32.0
            maxHeight = 76.0
            val textfield = find(AutoCompleteTagField::class)
            textfield.root.prefHeightProperty().bind(this@hbox.heightProperty())

            add(textfield)

            scrollpane {
                hgrow = Priority.ALWAYS
                hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
                flowpane {
                    prefWrapLengthProperty().bind(this@scrollpane.widthProperty())
                    hgap = 5.0
                    vgap = 5.0
                    hgrow = Priority.ALWAYS
                    paddingAll = 2
                    bindChildren(pageViewModel.tags.value, tagFlowController.tagToNodeConvertor)
                }
            }

            button {
                addClass(Styles.hoverPopup)
                addClass(Styles.organizerButton)
                prefHeightProperty().bind(this@hbox.heightProperty())
                paddingHorizontal = 8
                background = null
                graphic = MaterialIconView(MaterialIcon.CHROME_READER_MODE).apply {
                    glyphSize = 28
                }
                onHover {
                    cursor = Cursor.HAND
                }
                action {
                    val organiser = find(Organiser::class)
                    projectController.openOverlayWith(organiser)
                }
            }
        }
        setDividerPositions((1.0))
    }

}
