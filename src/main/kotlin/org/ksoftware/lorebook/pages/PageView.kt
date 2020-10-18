package org.ksoftware.lorebook.pages

import javafx.scene.layout.Pane
import org.ksoftware.lorebook.main.ProjectViewModel
import tornadofx.*

/**
 * View class for a page of a users project. These Pages are docked a ProjectWorkspace under
 * a new scope. They contain a pane to hold the users content nodes (text node etc) as well as controls for rich text
 * and page tags.
 */
class PageView : View("MyPage") {

    private val pageController: PageController by inject(FX.defaultScope)
    private val pageViewModel: PageViewModel by inject()

    private val projectViewModel: ProjectViewModel by inject()
    private var nodeContainer: Pane by singleAssign()

    init {
        println(projectViewModel)
        println(workspace)
    }

    override val root = borderpane {
        // rich text controls
        top {

        }

        // pane container holding nodes
        center {
            nodeContainer = pane {
                button("moshi") {
                    action {
                        pageViewModel.item.getJson()
                    }
                }
            }
        }

        // page tags
        bottom {

        }
    }
}
