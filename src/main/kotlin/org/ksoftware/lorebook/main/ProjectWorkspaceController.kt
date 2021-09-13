package org.ksoftware.lorebook.main

import com.squareup.moshi.JsonClass
import dock.DetachableTabPane
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.SplitPane
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import javafx.stage.Stage
import javafx.util.Duration
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import org.ksoftware.lorebook.actions.SaveProjectAction
import org.ksoftware.lorebook.io.Savable
import org.ksoftware.lorebook.io.SaveIO
import org.ksoftware.lorebook.nodes.TextController
import org.ksoftware.lorebook.pages.PageModel
import org.ksoftware.lorebook.pages.PageView
import org.ksoftware.lorebook.pages.PageViewModel
import org.ksoftware.lorebook.richtext.ToolbarViewModal
import tornadofx.*
import java.io.*
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

/**
 * Controller for the project workspace.
 */
class ProjectWorkspaceController : Controller(), Savable {


    private val textController: TextController by inject(FX.defaultScope)
    private val projectViewModel: ProjectViewModel by inject()
    private val toolbarViewModal: ToolbarViewModal by inject()
    private val saveProjectActor: SendChannel<SaveProjectAction> = createSaveActor()


    /**
     * Creates a new page and docks it in the provided workspace
     */
    fun dockNewPage(placement: Pos = Pos.CENTER) {
        val newPage = PageModel()
        projectViewModel.pages.value.add(newPage)
        dockPageView(newPage, placement)
    }

    /**
     * Docks the PageView associated with the provided PageModel in the workspace.
     * Checks the cache of PageViews to see if the pages view is in there.
     * If not, creates a new page view in a new scope and docks it.
     */
    fun dockPageView(page: PageModel, placement: Pos = Pos.CENTER): UIComponent {
        val pageToDock = projectViewModel.pageViewCache[page.idProperty.value] ?: find<PageView>(
            Scope(
                PageViewModel(page),
                projectViewModel,
                toolbarViewModal
            )
        )
        projectViewModel.pageViewCache.putIfAbsent(page.idProperty.value, pageToDock)
        workspace.dock(pageToDock, placement = placement)
        return pageToDock
    }

    // --------------------------------------- //
    //                 SAVING                  //
    // --------------------------------------- //

    override suspend fun save(projectFolder: File) {
        saveOpenPageStructure(projectFolder)
        //projectModel.save(projectFolder)
    }

    fun saveProject(stage: Stage) {
        saveProjectActor.trySend(SaveProjectAction(projectViewModel.item, stage))
    }

    /**
     * Creates an actor that processes one save action at a time. Will block any further save actions until the
     * current one has finished processing.
     */
    private fun createSaveActor() = projectViewModel.saveCoroutineScope.actor<SaveProjectAction> {
        for (saveAction in channel) {
            val projectSaveFolder = askUserForProjectSaveFolder(saveAction.stage)
            projectSaveFolder?.let {
                save(it)
                saveAction.project.save(it)
            }
        }
    }

    private fun saveOpenPageStructure(saveLocation: File) {
        val topSplitPane = findTopmostSplitPane(workspace.detachableTabPane)
        if (topSplitPane != null) {
            val tree = traverseSplitPaneTree(topSplitPane)
            val mainFile = File(saveLocation.path + "/openPages.txt")
            val br = BufferedWriter(FileWriter(mainFile))
            val json = SaveIO.moshi.adapter(TreeNode::class.java).indent("    ").toJson(tree)
            br.write(json)
            br.close()
        }
    }

    private fun traverseSplitPaneTree(sp: SplitPane): TreeNode {
        val spt = TreeNode(type = NodeType.SplitPane)
        sp.items.forEach { node ->
            when (node) {
                is SplitPane -> {
                    spt.children += traverseSplitPaneTree(node)
                }
                is DetachableTabPane -> {
                    val treeNode = TreeNode(
                        type = NodeType.TabPane,
                        pages = node.tabs.map { it.content.id }.toMutableList()
                    )
                    spt.children += treeNode
                }
            }
        }
        spt.dividerPositions += sp.dividerPositions.toMutableList()
        spt.split = sp.orientation
        return spt
    }

    /**
     * Traverses up the scenegraph from the supplied parent and returns the top most found SplitPane
     */
    private fun findTopmostSplitPane(node: Parent): SplitPane? {
        var sp: SplitPane? = null
        var parent: Parent? = node.parent
        while (parent != null) {
            if (parent is SplitPane) {
                sp = parent
            }
            parent = parent.parent
        }
        return sp
    }

    /**
     * Used to build a data tree structure to represent the tree of SplitPanes and DetachableTabPanes that
     * make up the pages open in the workspace.
     */
    @JsonClass(generateAdapter = true)
    data class TreeNode(
        val type: NodeType,
        val dividerPositions: MutableList<Double> = mutableListOf(),
        val pages: MutableList<String> = mutableListOf(),
        val children: MutableList<TreeNode> = mutableListOf(),
        var split: Orientation = Orientation.HORIZONTAL
    )

    enum class NodeType {
        SplitPane,
        TabPane
    }


    private fun askUserForProjectSaveFolder(stage: Stage): File? {
        val directoryChooser = DirectoryChooser()
        directoryChooser.initialDirectory = File("/")
        return directoryChooser.showDialog(stage)
    }


    // --------------------------------------- //
    //                 LOADING                 //
    // --------------------------------------- //

    fun loadOpenPages() {
        val fileChooser = FileChooser()
        fileChooser.initialDirectory = File("/")
        val file = fileChooser.showOpenDialog(workspace.primaryStage)
        val json = Files.readString(Path.of(file.path));
        val tree = SaveIO.moshi.adapter(TreeNode::class.java).indent("    ").fromJson(json)
        tree?.let { traverseTree(it, workspace.detachableTabPane) }
    }

    private fun traverseTree(tree: TreeNode, parentTabPane: DetachableTabPane) {
        var tabPane = parentTabPane
        tree.children.forEach { x ->
            if (x.type == NodeType.TabPane) {
                workspace.focusedTabPane.value = tabPane
                traverseDock(x.pages.removeFirst(), tree.split)
                tabPane = workspace.focusedTabPane.value
                x.pages.forEach {
                    dockPageView(
                        PageModel(idProperty = SimpleStringProperty(it)),
                        placement = Pos.CENTER
                    )
                }
            } else if (x.type == NodeType.SplitPane) {
                val temp = tabPane
                val pageID = ("Temp" + UUID.randomUUID().toString().take(8))
                val cmp = traverseDock(pageID, tree.split)
                tabPane = workspace.focusedTabPane.value
                traverseTree(x, tabPane)
                val tab = workspace.findTabFromUIComponent(cmp)
                tab?.close()
                tabPane = temp
            }
        }
        tabPane.parentSplitPane.setDividerPositions(*tree.dividerPositions.toDoubleArray())
    }

    private fun traverseDock(id: String, orientation: Orientation): UIComponent {
        val cmp = if (workspace.dockedComponent == null) {
            dockPageView(PageModel(idProperty = SimpleStringProperty(id)), placement = Pos.CENTER)
        } else if (orientation == Orientation.VERTICAL) {
            dockPageView(PageModel(idProperty = SimpleStringProperty(id)), placement = Pos.TOP_CENTER)
        } else {
            dockPageView(PageModel(idProperty = SimpleStringProperty(id)), placement = Pos.CENTER_RIGHT)
        }
        return cmp
    }


    // --------------------------------------- //
    //                 OVERLAY                 //
    // --------------------------------------- //

    /**
     * Causes the overlay to appear with this UIComponent centered.
     */
    fun openOverlayWith(uiComponent: UIComponent) {
        projectViewModel.overlayNode.value = uiComponent
    }

    /**
     * Removes the UIComponent from the overlay and closes the overlay.
     */
    fun closeOverlay() {
        projectViewModel.overlayNode.value = null
    }


    // --------------------------------------- //
    //                RICHTEXT                 //
    // --------------------------------------- //

    fun connectToolbarViewModalToRichTextAreas() {
        toolbarViewModal.updateParagraphTrigger.onChange {
            textController.updateParagraphStyleInSelection(projectViewModel.currentRichText.value) { parStyle ->
                parStyle.updateWith(
                    toolbarViewModal.createParagraphStyle()
                )
            }
            projectViewModel.currentRichText.value.requestFocus()
        }
        toolbarViewModal.increaseIndentTrigger.onChange {
            textController.updateParagraphStyleInSelection(projectViewModel.currentRichText.value) { parStyle -> parStyle.increaseIndent() }
            projectViewModel.currentRichText.value.requestFocus()
        }
        toolbarViewModal.decreaseIndentTrigger.onChange {
            textController.updateParagraphStyleInSelection(projectViewModel.currentRichText.value) { parStyle -> parStyle.decreaseIndent() }
            projectViewModel.currentRichText.value.requestFocus()
        }
        toolbarViewModal.updateTextTrigger.onChange {
            val style = toolbarViewModal.createTextStyle()
            textController.updateStyleInSelection(projectViewModel.currentRichText.value, style)
            projectViewModel.currentRichText.value.textInsertionStyle = style
            projectViewModel.currentRichText.value.requestFocus()
        }
    }

}

