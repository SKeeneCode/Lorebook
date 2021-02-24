package org.ksoftware.lorebook.main

import javafx.stage.Stage
import ch.micheljung.fxwindow.FxStage
import ch.micheljung.fxwindow.WindowController
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.VBox
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

        val fxStage = FxStage.configure(stage).withSceneFactory {
            parent -> Scene(parent)
        }.apply()

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
            fxStage.setContent(view.root)
            view.callOnDock()
            aboutToBeShown = false
        }

        FX.initialized.value = true
        stage.show()
    }

}

fun main() {
    launch<LorebookApp>()
}