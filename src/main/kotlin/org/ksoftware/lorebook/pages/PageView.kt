package org.ksoftware.lorebook.pages

import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.geometry.Orientation
import javafx.scene.Cursor
import javafx.scene.control.ScrollPane
import javafx.scene.control.SplitPane
import javafx.scene.input.TransferMode
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority
import org.ksoftware.lorebook.Styles
import org.ksoftware.lorebook.controls.AutoCompleteTextField
import org.ksoftware.lorebook.events.TagTreeRebuildRequest
import org.ksoftware.lorebook.main.ProjectViewModel
import org.ksoftware.lorebook.organiser.Organiser
import org.ksoftware.lorebook.organiser.tagflow.TagFlow
import org.ksoftware.lorebook.organiser.tagflow.TagFlowController
import org.ksoftware.lorebook.organiser.tagflow.TagFlowViewModel
import org.ksoftware.lorebook.tags.TagFunction
import org.ksoftware.lorebook.utilities.getContrastColor
import tornadofx.*

/**
 * View class for a page of a users project. These Pages are docked a ProjectWorkspace under
 * a new scope. They contain a pane to hold the users content nodes (text node etc) as well as controls for rich text
 * and page tags.
 */
class PageView : View("MyPage") {

    private val pageController: PageController by inject(FX.defaultScope)
    private val pageViewModel: PageViewModel by inject()

    private val tagFlowViewModel: TagFlowViewModel by inject()
    private val tagFlowController: TagFlowController by inject()

    private val projectViewModel: ProjectViewModel by inject()
    private var nodeContainer: Pane by singleAssign()

    init {
        tagFlowViewModel.deleteFunction = TagFunction { pageViewModel.tags.value.remove(it) }
    }

    override val root = borderpane {

        // rich text controls
        top {
        }

        // pane container holding nodes
        center {
            splitpane(Orientation.VERTICAL) {

                nodeContainer = pane {
                    vbox {
                        button("moshi") {
                            action {
                                pageViewModel.item.getJson()
                            }
                        }
                    }
                }

                hbox {
                    SplitPane.setResizableWithParent(this, false)
                    prefHeight = 32.0
                    maxHeight = 76.0
                    val textfield = find(AutoCompleteTextField::class)
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
                            openInternalWindow(find(Organiser::class), owner = workspace.root, modal = true, movable = false)
                        }
                    }
                }

                setDividerPositions((1.0))
            }

        }
    }
}
