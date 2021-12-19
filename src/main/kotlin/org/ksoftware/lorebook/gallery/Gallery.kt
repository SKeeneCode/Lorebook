package org.ksoftware.lorebook.gallery

import javafx.scene.layout.Priority
import org.ksoftware.lorebook.main.ProjectViewModel
import org.ksoftware.lorebook.main.ProjectWorkspaceController
import org.ksoftware.lorebook.styles.Styles
import tornadofx.*

class Gallery : View() {

    private val projectViewModel: ProjectViewModel by inject()
    private val projectController: ProjectWorkspaceController by inject()

    override val root = vbox {
        maxWidth = 800.0
        maxHeight = 800.0
        addClass(Styles.headerBluePadding, Styles.baseShadow)
        add(GalleryHeader::class)
        splitpane {
            paddingAll = 0.0
            this.setDividerPosition(0, 0.9)
            vgrow = Priority.ALWAYS
            vbox {
                add(GalleryDropArea::class)
                add(GalleryToolbar::class)
                add(GalleryTreeTableView::class)
            }
            add(GalleryImageEditArea::class)
        }
    }


}