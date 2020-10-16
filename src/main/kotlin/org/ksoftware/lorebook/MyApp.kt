package org.ksoftware.lorebook

import tornadofx.*

class MyApp : App(MyView::class, Styles::class)

fun main() {
    launch<MyApp>()
}