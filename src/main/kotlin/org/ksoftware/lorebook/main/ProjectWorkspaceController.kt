package org.ksoftware.lorebook.main

import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.javafx.JavaFx
import org.ksoftware.lorebook.actions.SaveProjectAction
import org.ksoftware.lorebook.nodes.TextController
import org.ksoftware.lorebook.pages.PageModel
import org.ksoftware.lorebook.pages.PageView
import org.ksoftware.lorebook.pages.PageViewModel
import org.ksoftware.lorebook.richtext.ToolbarViewModal
import tornadofx.*
import java.io.File
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

/**
 * Controller for the project workspace.
 */
class ProjectWorkspaceController : Controller() {


    private val textController: TextController by inject(FX.defaultScope)
    private val projectViewModel: ProjectViewModel by inject()
    private val toolbarViewModal: ToolbarViewModal by inject()
    private val saveProjectActor: SendChannel<SaveProjectAction> = createSaveActor()


    /**
     * Creates a new page and docks it in the provided workspace
     */
    fun dockNewPage() {
        val newPage = PageModel()
        projectViewModel.pages.value.add(newPage)
        dockPageView(newPage)
    }

    /**
     * Docks the PageView associated with the provided PageModel in the workspace.
     * Checks the cache of PageViews to see if the pages view is in there.
     * If not, creates a new page view in a new scope and docks it.
     */
    fun dockPageView(page: PageModel) {
        val pageToDock = projectViewModel.pageViewCache[page.idProperty.value] ?: find<PageView>(Scope(PageViewModel(page), projectViewModel, toolbarViewModal))
        projectViewModel.pageViewCache.putIfAbsent(page.idProperty.value, pageToDock)
        workspace.dock(pageToDock)
    }

    // --------------------------------------- //
    //                 SAVING                  //
    // --------------------------------------- //

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
                projectSaveFolder?.let { saveProjectInLocation(saveAction.project, it) }
            }
        }


    private suspend fun saveProjectInLocation(projectModel: ProjectModel, saveLocation: File) {
        projectModel.save(saveLocation)
    }

    private fun askUserForProjectSaveFolder(stage: Stage) : File? {
        val directoryChooser = DirectoryChooser()
        directoryChooser.initialDirectory = File("/")
        return directoryChooser.showDialog(stage)
    }

    // --------------------------------------- //
    //                 OVERLAY                  //
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
            textController.updateParagraphStyleInSelection(projectViewModel.currentRichText.value) { parStyle -> parStyle.updateWith(toolbarViewModal.createParagraphStyle()) }
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

