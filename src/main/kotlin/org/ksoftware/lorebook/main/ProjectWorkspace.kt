package org.ksoftware.lorebook.main

import javafx.scene.control.Label
import javafx.scene.layout.FlowPane
import org.ksoftware.lorebook.newproject.NewProjectView
import org.ksoftware.lorebook.test.AutocompletionTextField
import tornadofx.*

/**
 * Workspace implementation used for a projects main window.
 */
class ProjectWorkspace : Workspace("Lorebook", NavigationMode.Tabs) {

    private val projectWorkspaceController: ProjectWorkspaceController by inject()
    private val projectViewModel: ProjectViewModel by inject()
    var flow: FlowPane by singleAssign()

    override fun onDock() {
        super.onDock()
        currentStage?.width = 1200.0
        currentStage?.height = 800.0
    }

    init {
        with(header) {
            label(projectViewModel.taskMessage)
        }
    }

    init {
        with(leftDrawer) {
            item("all pages", expanded = true) {
                vbox {
                    prefWidth = 200.0
                    button("add a page") {
                        action {
                            projectWorkspaceController.dockNewPage(this@ProjectWorkspace)
                        }
                    }
                    listview(projectViewModel.pages) {
                        cellFormat {
                            text = this.item.toString()
                            this.onDoubleClick {
                                projectWorkspaceController.dockPageView(this.item, this@ProjectWorkspace)
                            }
                        }
                    }
                    button("print") {
                        action {
                            println(projectViewModel.pages.value.toString())
                        }
                    }
                    button("reset") {
                        enableWhen(projectViewModel.dirty)
                        action {
                            projectViewModel.rollback()
                            projectViewModel.rollBackPages()
                        }
                    }
                    button("save") {
                        enableWhen(projectViewModel.dirty)
                        action {
                            projectViewModel.commit()
                        }
                    }
                    button("save project") {
                        action {
                            currentStage?.let { projectWorkspaceController.saveProject(it) }
                        }
                    }
                    button("new project") {
                        action {
                            find(NewProjectView::class).openModal()
                        }
                    }
                }
            }
        }
    }

    init {
        with(bottomDrawer) {
            item("footers") {
                flow = flowpane()
            }
        }

        with(flow) {
            prefHeight = 200.0
            AutocompletionTextField().also {
                it.entries.addAll(arrayListOf("aaa", "bbb", "ccc", "albert"))
                this.onLeftClick{
                    it.requestFocus()
                }
                it.action {
                    this@with.children.add(Label(it.text))
                    it.clear()
                }
                add(it)
            }
        }
    }
}