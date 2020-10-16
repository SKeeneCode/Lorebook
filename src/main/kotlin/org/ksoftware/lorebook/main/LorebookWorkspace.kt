package org.ksoftware.lorebook.main

import tornadofx.*

/**
 * Workspace implementation used for a lorebooks main window.
 */
class LorebookWorkspace : Workspace("Lorebook", NavigationMode.Tabs) {

    private val lorebookWorkspaceController: LorebookWorkspaceController by inject(FX.defaultScope)
    private val lorebookViewModel: LorebookViewModel by inject()

    init {
        primaryStage.width = 1200.0
        primaryStage.height = 800.0
    }
}