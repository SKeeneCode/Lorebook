package org.ksoftware.lorebook.main

import tornadofx.*

/**
 * Workspace implementation used for the applications main window.
 */
class MainWorkspace : Workspace("Lorebook", NavigationMode.Tabs) {
    init {
        primaryStage.width = 1200.0
        primaryStage.height = 800.0
    }
}