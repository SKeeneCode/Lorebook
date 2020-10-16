package org.ksoftware.lorebook.main

import tornadofx.*

class MainWorkspace : Workspace("Lorebook", NavigationMode.Tabs) {
    init {
        primaryStage.width = 1200.0
        primaryStage.height = 800.0
    }
}