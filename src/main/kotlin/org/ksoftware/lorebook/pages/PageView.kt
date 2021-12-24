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
import org.ksoftware.lorebook.organiser.TagOrganiser
import org.ksoftware.lorebook.richtext.ToolbarViewModal
import org.ksoftware.lorebook.styles.Styles
import tornadofx.*
import org.ksoftware.lorebook.actions.DrawGridAction
import org.ksoftware.lorebook.controls.AutoCompleteTagFieldViewModal
import org.ksoftware.lorebook.organiser.TagOrganiserController
import org.ksoftware.lorebook.organiser.TagOrganiserViewModel
import org.ksoftware.lorebook.settings.ProjectSettingsViewModel
import org.ksoftware.lorebook.tagflow.TagFlowViewModel
import org.ksoftware.lorebook.utilities.findTabFromUIComponent


/**
 * View class for a page of a users project. These Pages are docked a ProjectWorkspace under
 * a new scope. They contain a pane to hold the users content nodes (text node etc) as well as controls for rich text
 * and page tags.
 */
class PageView : View() {

    private val pageController: PageController by inject()
    val pageViewModel: PageViewModel by inject()
    private val gridViewModal: PageGridViewModal by inject()
    private val gridController: PageGridController by inject()

    private val tagOrganiserViewModel: TagOrganiserViewModel by inject()
    private val tagFlowViewModel: TagFlowViewModel by inject()
    private val tagOrganiserController: TagOrganiserController by inject()
    private val toolbarViewModal: ToolbarViewModal by inject()

    private val projectViewModel: ProjectViewModel by inject()
    private val projectSettingsViewModel: ProjectSettingsViewModel by inject()
    private val projectController: ProjectWorkspaceController by inject()

    private var pageInspector: PageInspector by singleAssign()
    private val tagOrganiser: TagOrganiser by inject()

    private val tagFieldViewModel: AutoCompleteTagFieldViewModal by inject()

    private var madeTabEditable = false

    init {
        pageInspector = find(PageInspector::class)
        tagFieldViewModel.comparisonTagSet.value = pageViewModel.tags.value
        title = pageViewModel.id.value.take(8)
    }

    override fun onDock() {
        println("DOCKING PAGE")
        projectViewModel.currentRichText.value = null

        gridController.drawGrid(DrawGridAction())

        with(workspace) {
            with (rightDrawer) {
                item("Page Settings", expanded = projectSettingsViewModel.allowRightDrawerOpen.value) {
                    add(pageInspector)
                }
            }
        }

        if(!madeTabEditable) {
            val pageTab = workspace.findTabFromUIComponent(this)
            pageTab?.let { pageController.makeTabEditable(it) }
            madeTabEditable = true
        }
    }

    override fun onUndock() {
        println("Undocking ${this.pageViewModel.id}")
    }

    override val root = splitpane(Orientation.VERTICAL) {

        // Bind root nodes ID to model id for easy access when saving
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
                    bindChildren(pageViewModel.tags.value, tagFlowViewModel.tagToNodeConverter)
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
                    println("page is $tagFlowViewModel")
                    tagFlowViewModel.tagSet.value = null
                    tagFlowViewModel.tagSet.value = pageViewModel.tags.value
                    tagOrganiserViewModel.updateHeader(MaterialIcon.DESCRIPTION.name,
                        "Tag Organiser",
                    "Here you can add and remove tags for the page ${pageViewModel.pageName.value}.")
                    projectController.openOverlayWith(tagOrganiser)
                }
            }
        }
        setDividerPositions((1.0))
    }

}
