package org.ksoftware.lorebook.pages

import javafx.geometry.Pos
import org.ksoftware.lorebook.main.ProjectViewModel
import org.ksoftware.lorebook.main.ProjectWorkspaceController
import org.ksoftware.lorebook.organiser.tagflow.TagFlowController
import org.ksoftware.lorebook.organiser.tagflow.TagFlowViewModel
import org.ksoftware.lorebook.richtext.ToolbarViewModal
import org.ksoftware.lorebook.styles.Styles
import tornadofx.*

class PageInspector : Fragment() {

    private val pageController: PageController by inject()
    private val pageViewModel: PageViewModel by inject()
    private val gridViewModal: PageGridViewModal by inject()
    private val gridController: PageGridController by inject()

    private val tagFlowViewModel: TagFlowViewModel by inject()
    private val tagFlowController: TagFlowController by inject()
    private val toolbarViewModal: ToolbarViewModal by inject()

    private val projectViewModel: ProjectViewModel by inject()
    private val projectController: ProjectWorkspaceController by inject()

    override val root = vbox {
        alignment = Pos.TOP_CENTER
        prefWidth = 200.0
        paddingHorizontal = 10
        hbox {
            alignment = Pos.CENTER
            button {
                addClass(Styles.hoverPopup, Styles.toolbarButton)
                background = null
                action {
                    projectController.dockNewPage()
                }
            }
            label("Page Settings") {
                addClass(Styles.titleLabel)
            }
            button {
                addClass(Styles.hoverPopup, Styles.toolbarButton)
                background = null
                action {
                    projectController.dockNewPage()
                }
            }
        }
        label("Page Title")
        textfield(pageViewModel.pageName)
        label("Page Grid")
    }

}