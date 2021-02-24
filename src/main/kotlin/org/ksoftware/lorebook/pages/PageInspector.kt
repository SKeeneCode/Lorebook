package org.ksoftware.lorebook.pages

import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.beans.property.BooleanProperty
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import org.ksoftware.lorebook.main.ProjectViewModel
import org.ksoftware.lorebook.main.ProjectWorkspaceController
import org.ksoftware.lorebook.organiser.tagflow.TagFlowController
import org.ksoftware.lorebook.organiser.tagflow.TagFlowViewModel
import org.ksoftware.lorebook.richtext.ToolbarViewModal
import org.ksoftware.lorebook.styles.Styles
import tornadofx.*

class PageInspector : Fragment() {

    private val pageController: PageController by inject()
    private val pageViewModel: PageViewModel by inject()
    private val gridViewModal: PageGridViewModal by inject()
    private val gridController: PageGridController by inject()

    private val tagFlowViewModel: TagFlowViewModel by inject()
    private val tagFlowController: TagFlowController by inject()
    private val toolbarViewModal: ToolbarViewModal by inject()

    private val projectViewModel: ProjectViewModel by inject()
    private val projectController: ProjectWorkspaceController by inject()

    override val root = vbox {
        alignment = Pos.TOP_CENTER
        prefWidth = 200.0
        paddingHorizontal = 10
        hbox {
            alignment = Pos.CENTER
            button {
                addClass(Styles.hoverPopup, Styles.toolbarButton)
                background = null
                graphic = MaterialIconView(MaterialIcon.CONTENT_COPY).apply {
                    glyphSize = 26
                    fill = Color.DARKGREEN
                }
                action {
                    projectController.dockNewPage(projectViewModel.projectWorkspace)
                }
            }
            label("Page Settings") {
                addClass(Styles.titleLabel)
            }
            button {
                addClass(Styles.hoverPopup, Styles.toolbarButton)
                background = null
                graphic = MaterialIconView(MaterialIcon.DELETE).apply {
                    glyphSize = 26
                    fill = Color.DARKRED
                }
                action {
                    projectController.dockNewPage(projectViewModel.projectWorkspace)
                }
            }



        }

        label("Page Title")
        textfield(pageViewModel.pageName)


        label("Page Grid")
        hbox {
            alignment = Pos.CENTER
            button {
                addClass(Styles.hoverPopup, Styles.toolbarButton)
                background = null
                graphic = getVisibleGraphic(pageViewModel.gridMinorLinesVisible)
                pageViewModel.gridMinorLinesVisible.onChange { graphic = getVisibleGraphic(pageViewModel.gridMinorLinesVisible) }
                action {
                    pageViewModel.gridMinorLinesVisible.value = !pageViewModel.gridMinorLinesVisible.value
                }
            }
            textfield()
            button {
                addClass(Styles.hoverPopup, Styles.toolbarButton)
                background = null
                graphic = getVisibleGraphic(pageViewModel.gridMajorLinesVisible)
                pageViewModel.gridMajorLinesVisible.onChange { graphic = getVisibleGraphic(pageViewModel.gridMajorLinesVisible) }
                action {
                    pageViewModel.gridMajorLinesVisible.value = !pageViewModel.gridMajorLinesVisible.value
                }
            }
            textfield()
        }

    }

    fun getVisibleGraphic(booleanProperty: BooleanProperty) : Node {
        return if (booleanProperty.value) {
            MaterialIconView(MaterialIcon.VISIBILITY).apply {
                glyphSize = 26
                fill = Color.DARKGREEN
            }
        } else {
            MaterialIconView(MaterialIcon.VISIBILITY_OFF).apply {
                glyphSize = 26
                fill = Color.DARKGREEN
            }
        }
    }

}