package org.ksoftware.lorebook.main

import org.ksoftware.lorebook.MyView
import org.ksoftware.lorebook.Styles
import tornadofx.*

class MyApp : App(MyView::class, Styles::class)

fun main() {
    launch<MyApp>()
}