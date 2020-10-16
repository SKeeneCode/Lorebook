package org.ksoftware.lorebook.pages

import javafx.scene.layout.Pane
import tornadofx.*

/**
 * View class for a page of a users lorebook. These LorebookPages are docked onto the MainWorkspace under
 * a new scope. They contain a pane to hold the users content nodes (text node etc) as well as controls for rich text
 * and page tags.
 */
class LorebookPage : View("MyPage") {

    private var nodeContainer: Pane by singleAssign()

    override val root = borderpane {
        // rich text controls
        top {

        }

        // pane container holding nodes
        center {
            nodeContainer = pane {

            }
        }

        // page tags
        bottom {

        }
    }
}
