package org.ksoftware.lorebook

import javafx.scene.paint.Color
import tornadofx.*

class Styles : Stylesheet() {
    companion object {
        val tagTitleRegion by cssclass()
        val noBackgroundOrBorder by cssclass()
        val transparentContextMenu by cssclass()
        val glyphIconHover by cssclass()
        val hoverPopup by cssclass()
        val organizerButton by cssclass()
        val toolbarButton by cssclass()
    }

    init {
        toolbarButton {
            backgroundColor += Color.color(0.95,0.95,0.95)
            padding = box(3.px, 6.px)
            and(hover) {
                backgroundColor += Color.color(0.88,0.85,0.85)
            }
            and(pressed) {
                backgroundColor += Color.color(0.8,0.8,0.8)
            }
            and(selected) {
                backgroundColor += Color.color(0.8,0.8,0.8)
            }

        }
        glyphIconHover {
            button {
                and(hover) {
                    translateY = (-1).px
                }
            }
        }
        organizerButton {
            backgroundColor += Color.LIGHTGRAY
            opacity = 0.8
            and(hover) {
                opacity = 1.0
            }
        }

        hoverPopup {
                and(hover) {
                    translateY = (-1).px
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