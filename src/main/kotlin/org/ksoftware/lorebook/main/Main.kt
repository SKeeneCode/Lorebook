package org.ksoftware.lorebook.main

import javafx.stage.Stage
import ch.micheljung.fxwindow.FxStage
import ch.micheljung.fxwindow.WindowController
import javafx.scene.Scene
import org.ksoftware.lorebook.newproject.NewProjectView
import org.ksoftware.lorebook.styles.Styles
import tornadofx.*
import kotlin.reflect.KClass

/**
 * Entry class for the application
 */
class LorebookApp : App(NewProjectView::class, Styles::class) {

    /**
     * Start is customized to ensure compatibility with FxStage.
     */
    override fun start(stage: Stage) {
        val view = find(NewProjectView::class)
        view.muteDocking = true
        val myScene = Scene(view.root)
        val newStage = Stage()
        newStage.scene = myScene
        val fxStage = FxStage.configure(newStage).apply()
        fxStage.stage.apply {
            FX.registerApplication(scope, this@LorebookApp, this)
            aboutToBeShown = true
            view.properties["tornadofx.scene"] = scene
            FX.applyStylesheetsTo(scene)
            titleProperty().bind(view.titleProperty)
            hookGlobalShortcuts()
            view.onBeforeShow()
            onBeforeShow(view)
            view.muteDocking = false
            show()
            view.callOnDock()
            aboutToBeShown = false
        }
        FX.initialized.value = true
    }

}

fun main() {
    launch<LorebookApp>()
}