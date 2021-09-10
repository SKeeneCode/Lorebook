package org.ksoftware.lorebook.main

import javafx.stage.Stage
import ch.micheljung.fxwindow.FxStage
import ch.micheljung.fxwindow.WindowController
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.shape.Shape
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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
        val view = find(primaryView)
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
            fxStage.setContent(view.root as Region)
            view.callOnDock()
            aboutToBeShown = false
        }

        FX.initialized.value = true
        stage.show()
    }

}
