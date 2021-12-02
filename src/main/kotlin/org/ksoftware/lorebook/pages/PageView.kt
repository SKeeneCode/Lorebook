package org.ksoftware.lorebook.pages

import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.geometry.Orientation
import javafx.scene.Cursor
import javafx.scene.control.ScrollPane
import javafx.scene.control.SplitPane
import javafx.scene.control.TabPane
import javafx.scene.input.MouseEvent
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
import org.ksoftware.lorebook.settings.ProjectSettingsViewModel


/**
 * View class for a page of a users project. These Pages are docked a ProjectWorkspace under
 * a new scope. They contain a pane to hold the users content nodes (text node etc) as well as controls for rich text
 * and page tags.
 */
class PageView : View() {

    private val pageController: PageController by inject()
    private val pageViewModel: PageViewModel by inject()
    private val gridViewModal: PageGridViewModal by inject()
    private val gridController: PageGridController by inject()

    private val tagFlowViewModel: TagFlowViewModel by inject()
    private val tagFlowController: TagFlowController by inject()
    private val toolbarViewModal: ToolbarViewModal by inject()

    private val projectViewModel: ProjectViewModel by inject()
    private val projectSettingsViewModel: ProjectSettingsViewModel by inject()
    private val projectController: ProjectWorkspaceController by inject()

    private var pageInspector: PageInspector by singleAssign()

    private var done = false

    init {
        tagFlowViewModel.deleteFunction = TagFunction { pageViewModel.tags.value.remove(it) }
        pageInspector = find(PageInspector::class)
        title = pageViewModel.id.value.take(8)
    }

    override fun onDock() {
        projectViewModel.currentRichText.value = null

        gridController.drawGrid(DrawGridAction())

        with(workspace) {
            with (rightDrawer) {
                item("Page Settings", expanded = projectSettingsViewModel.allowRightDrawerOpen.value) {
                    add(pageInspector)
                }
            }
        }

        if(!done) {
            val pageTab = workspace.tabPanes.flatMap { it.tabs }.find { it.content == this.root }
            pageTab?.let { pageController.makeTabEditable(it) }
            done = true
        }
    }

    override val root = splitpane(Orientation.VERTICAL) {

        // Bind root nodes ID to model id for each access when saving
        idProperty().bind(pageViewModel.id)

        addEventFilter(MouseEvent.MOUSE_PRESSED) {
            (this.parent.parent as TabPane).requestFocus()
        }

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
