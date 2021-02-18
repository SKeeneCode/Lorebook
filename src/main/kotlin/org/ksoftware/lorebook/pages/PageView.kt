package org.ksoftware.lorebook.pages

import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.geometry.Orientation
import javafx.scene.Cursor
import javafx.scene.control.ScrollPane
import javafx.scene.control.SplitPane
import javafx.scene.layout.Priority
import org.ksoftware.lorebook.controls.AutoCompleteTagField
import org.ksoftware.lorebook.main.ProjectViewModel
import org.ksoftware.lorebook.main.ProjectWorkspaceController
import org.ksoftware.lorebook.organiser.Organiser
import org.ksoftware.lorebook.organiser.tagflow.TagFlowController
import org.ksoftware.lorebook.organiser.tagflow.TagFlowViewModel
import org.ksoftware.lorebook.richtext.ToolbarViewModal
import org.ksoftware.lorebook.styles.Styles
import org.ksoftware.lorebook.tags.TagFunction
import tornadofx.*
import org.ksoftware.lorebook.actions.DrawGridAction


/**
 * View class for a page of a users project. These Pages are docked a ProjectWorkspace under
 * a new scope. They contain a pane to hold the users content nodes (text node etc) as well as controls for rich text
 * and page tags.
 */
class PageView : View("MyPage") {

    private val pageController: PageController by inject()
    private val pageViewModel: PageViewModel by inject()
    private val gridViewModal: PageGridViewModal by inject()
    private val gridController: PageGridController by inject()

    private val tagFlowViewModel: TagFlowViewModel by inject()
    private val tagFlowController: TagFlowController by inject()
    private val toolbarViewModal: ToolbarViewModal by inject()

    private val projectViewModel: ProjectViewModel by inject()
    private val projectController: ProjectWorkspaceController by inject()


    init {
        println(toolbarViewModal)
        tagFlowViewModel.deleteFunction = TagFunction { pageViewModel.tags.value.remove(it) }

    }

    override fun onDock() {
        projectViewModel.currentRichText.value = null
        gridController.drawGrid(DrawGridAction())

        val pageTab = workspace.tabContainer.tabs.find { tab -> tab.content == root }
        pageTab?.let { tab -> pageController.makeTabEditable(tab) }
    }

    override val root = splitpane(Orientation.VERTICAL) {
        add<PageContentView>()
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
