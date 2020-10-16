package org.ksoftware.lorebook.main

import org.ksoftware.lorebook.Styles
import tornadofx.*

class MyApp : App(MainWorkspace::class, Styles::class)

fun main() {
    launch<MyApp>()
}