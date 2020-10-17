package org.ksoftware.lorebook.main

import tornadofx.*

/**
 * Workspace implementation used for a projects main window.
 */
class ProjectWorkspace : Workspace("Lorebook", NavigationMode.Tabs) {

    private val projectWorkspaceController: ProjectWorkspaceController by inject(FX.defaultScope)
    private val projectViewModel: ProjectViewModel by inject(FX.defaultScope)

    init {
        primaryStage.width = 1200.0
        primaryStage.height = 800.0
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
                            projectWorkspaceController.dockNewPage(workspace)
                        }
                    }
                    listview(projectViewModel.pages) {
                        cellFormat {
                            text = this.item.toString()
                            this.onDoubleClick {
                                //projectViewModel.pages.value.remove(this.item)
                                projectWorkspaceController.dockPageView(this.item, workspace)
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
                            projectWorkspaceController.saveProject()
                        }
                    }
                }
            }
        }
    }

    init {
        with(bottomDrawer) {
            item("footers") {
                pane {
                    prefHeight = 200.0
                }
            }
        }
    }
}