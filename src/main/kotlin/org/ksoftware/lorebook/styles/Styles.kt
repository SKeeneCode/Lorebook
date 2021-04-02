package org.ksoftware.lorebook.styles

import javafx.scene.control.ScrollPane
import javafx.scene.effect.BlurType
import javafx.scene.effect.DropShadow
import javafx.scene.effect.Effect
import javafx.scene.effect.InnerShadow
import javafx.scene.paint.*
import javafx.scene.text.FontPosture
import javafx.scene.text.FontWeight
import tornadofx.*

class Styles : Stylesheet() {
    companion object {
        val tagTitleRegion by cssclass()
        val noBackgroundOrBorder by cssclass()
        val transparentContextMenu by cssclass()
        val titleLabel by cssclass()
        val glyphIconHover by cssclass()
        val hoverPopup by cssclass()
        val organizerButton by cssclass()
        val toolbarButton by cssclass()

        val headerDarkBlue = c("516CA2")
        val baseDarker = c("839ED7")
        val baseSlight = c("A8BDEE")
        val baseBlueGray = c("C2D5FF")
        val lightPeriwinkle = c("E3EAF9")
        val parchment = c("FFFCF5")
        val sepia = c("74450B")
        val darkGreen = c("1A7A18")
        val jet = c("2B2B2B")

        val baseBlueBG by cssclass()
        val headerBlueBG by cssclass()
        val headerBluePadding by cssclass()
        val parchmentBG by cssclass()

        val baseShadow by cssclass()
        val headerFont by cssclass()

        val calaendarCell by cssclass()
        val blueTitledPane by cssclass()

        val parchmentShadow by cssclass()

        val headerDarkBlueBottom by cssclass()

        val blueScrollbar by cssclass()
        val trackBackground by cssclass()

        val baseSlightBG by cssclass()

    }

    init {
        baseSlightBG {
            backgroundColor += baseSlight
        }
        baseBlueBG {
            backgroundColor += baseBlueGray
        }
        headerBlueBG {
            val stops = mutableListOf(Stop(0.0, baseDarker), Stop(1.0, baseBlueGray))
            val lngnt = LinearGradient(1.0, 0.0, 0.0, 1.0, true, CycleMethod.NO_CYCLE, stops)
            backgroundColor += lngnt
            borderWidth += box(0.px,0.px,1.px,0.px)
            borderColor += box(headerDarkBlue)
        }
        headerDarkBlueBottom {
            borderWidth += box(0.px,0.px,1.px,0.px)
            borderColor += box(headerDarkBlue)
        }
        parchmentBG {
            backgroundColor += parchment
        }
        headerBluePadding {
            borderWidth = multi(box(1.px))
            borderColor += box(headerDarkBlue)
        }
        baseShadow {
            effect = DropShadow(BlurType.GAUSSIAN, Color.rgb(0, 0, 0, 0.4), 10.0, 0.0, 4.0, 4.0)
        }
        headerFont {
            fontSize = 1.2.em
            fontWeight = FontWeight.BOLD
        }
        calaendarCell {
                backgroundColor += baseBlueGray
        }
        blueScrollbar {
            viewport {
                backgroundColor += baseBlueGray
            }
                vertical {
                    trackBackground {
                        backgroundColor += Color.TRANSPARENT
                        borderColor += box(Color.TRANSPARENT)
                        backgroundRadius += box(0.px)
                        borderRadius += box(0.px)
                    }
                    track {
                        backgroundColor += Color.TRANSPARENT
                        borderColor += box(Color.TRANSPARENT)
                        backgroundRadius += box(0.px)
                        borderRadius += box(0.px)
                    }

                    incrementButton {
                        backgroundColor += Color.TRANSPARENT
                        backgroundRadius += box(0.px)
                        padding = box(0.px)
                        insets(0)
                        visibility = FXVisibility.HIDDEN
                        incrementArrow {
                            shape = ""
                            padding = box(0.5.em, 0.em)
                        }
                    }
                    decrementButton {
                        backgroundColor += Color.TRANSPARENT
                        backgroundRadius += box(0.px)
                        padding = box(0.px)
                        insets(0)
                        visibility = FXVisibility.HIDDEN
                        decrementArrow {
                            shape = ""
                            padding = box(0.5.em, 0.em)
                        }
                    }
                    thumb {
                        backgroundColor += headerDarkBlue
                        backgroundRadius += box(2.em)
                        and(hover) {
                            backgroundColor += headerDarkBlue.saturate()
                            backgroundRadius += box(2.em)
                        }
                    }
                    prefWidth = 8.px
                    backgroundColor += baseBlueGray
                }
        }
        blueTitledPane {
            borderWidth += box(0.px, 0.px, 1.px, 0.px)
            borderColor += box(lightPeriwinkle)
                content {
                    borderWidth += box(0.px)
                }
            title {
                fontWeight = FontWeight.BOLD
                backgroundColor += baseBlueGray
                backgroundRadius += box(0.px)
                borderWidth += box(0.px)
                borderRadius += box(0.px)
                arrowButton {
                    arrow {
                        backgroundColor += parchment
                    }
                    backgroundColor += Color.TRANSPARENT
                }
            }
            and(expanded) {
                content {
                    backgroundColor += baseSlight
                }
                title {
                    backgroundColor += baseSlight
                }
            }
        }
        parchmentShadow {
            effect = InnerShadow(BlurType.GAUSSIAN, Color.rgb(0,0,0,0.1), 10.0, 0.0, 4.0, 4.0)
        }
    }

    //    "dropshadow(${toCss(value.blurType)}, ${value.color.css}, " +
//    "${value.radius}, ${value.spread}, ${value.offsetX}, ${value.offsetY})"
    init {
        val baseColor = c("#ececec")
        root {
            backgroundColor += baseColor.derive(0.3)
        }
        titleLabel {
            fontSize = 16.px
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
                    borderWidth = multi(box(0.px, 0.px, 4.px, 0.px))
                    borderColor += box(baseColor.derive(-0.15))
                    padding = box(6.px, 12.px, 2.px, 12.px)
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