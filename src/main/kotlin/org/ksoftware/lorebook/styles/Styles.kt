package org.ksoftware.lorebook.styles

import javafx.scene.paint.Color
import javafx.scene.paint.Paint
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
        val baseColor = c("#ececec")
        root {
            backgroundColor += baseColor.derive(0.3)
        }
        tabPane {

            tabHeaderArea {
                padding = box(0.px)
            }
            tabHeaderBackground {
                backgroundColor += baseColor
            }
            tab {
                padding = box(6.px, 12.px)
                fontSize = 16.px
                backgroundColor += baseColor
                backgroundInsets += box(0.px)
                and(hover) {
                    backgroundColor += baseColor.derive(-0.1)
                }
                and(selected) {
                    backgroundColor += baseColor.derive(-0.05)
                    backgroundInsets += box(0.px)
                    borderWidth = multi(box(0.px,0.px,4.px,0.px))
                    borderColor += box(baseColor.derive(-0.15))
                    padding = box(6.px,12.px,2.px,12.px)
                    focusIndicator {
                        borderWidth = multi(box(0.px))
                    }
                }
            }
        }

        toolbarButton {
            backgroundColor += baseColor.derive(0.3)
            padding = box(3.px, 6.px)
            and(hover) {
                backgroundColor += baseColor.derive(-0.1)
            }
            and(pressed) {
                backgroundColor += baseColor.derive(-0.15)
            }
            and(selected) {
                backgroundColor += baseColor.derive(-0.15)
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