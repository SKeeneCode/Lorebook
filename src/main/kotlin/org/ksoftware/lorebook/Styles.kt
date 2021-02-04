package org.ksoftware.lorebook

import javafx.scene.paint.Color
import tornadofx.*

class Styles : Stylesheet() {
    companion object {
        val tagTitleRegion by cssclass()
        val noBackgroundOrBorder by cssclass()
        val transparentContextMenu by cssclass()
        val glyphIconHover by cssclass()
    }

    init {
        glyphIconHover {
            button {
                and(hover) {
                    translateY = (-1).px
                }
            }
        }
        tagTitleRegion {
            borderWidth = multi(box(0.px))
            title {
                backgroundColor = multi(Color.TRANSPARENT)
            }
            content {
                backgroundColor = multi(Color.TRANSPARENT)
                borderWidth = multi(box(0.px))
            }
        }
        transparentContextMenu {
            contextMenu {
                backgroundColor = multi(Color.TRANSPARENT)
                and(focused) {
                    backgroundColor = multi(Color.TRANSPARENT)
                }
            }
            menuItem {
                backgroundColor = multi(Color.TRANSPARENT)
                padding = box(0.px)
                and(focused) {
                    backgroundColor = multi(Color.TRANSPARENT)
                }
            }
        }
    }
}

//.column-filter .context-menu {
//    -fx-background-color: white;
//}
//
//.column-filter .context-menu:focused {
//    -fx-background-color: white;
//}
//
//.column-filter .custom-menu-item {
//    -fx-background-color: white;
//    -fx-padding: 0;
//}
//
//.column-filter .custom-menu-item:focused {
//    -fx-background-color: white;
//}