package org.ksoftware.lorebook.tagflow

import org.ksoftware.lorebook.pages.PageViewModel
import tornadofx.*

class TagFlow : View("My View") {

    private val tagFlowController: TagFlowController by inject()
    private val pageViewModel: PageViewModel by inject()

    override val root = vbox {
        prefWidth = 300.0
        flowpane {
            hgap = 5.0
            vgap = 5.0
            bindChildren(pageViewModel.tags.value, tagFlowController.tagToNodeConvertor)
        }
    }
}
