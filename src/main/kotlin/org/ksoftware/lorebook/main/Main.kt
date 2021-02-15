package org.ksoftware.lorebook.main

import javafx.stage.Stage
import javafx.stage.StageStyle
import org.ksoftware.lorebook.newproject.NewProjectView
import org.ksoftware.lorebook.styles.Styles
import tornadofx.App
import tornadofx.launch

/**
 * Entry class for the application
 */
class LorebookApp : App(NewProjectView::class, Styles::class) {

    override fun start(stage: Stage) {
        stage.initStyle(StageStyle.UNDECORATED)
        super.start(stage)
    }

}

fun main() {
    launch<LorebookApp>()
}