package org.ksoftware.lorebook.main

import org.ksoftware.lorebook.Styles
import tornadofx.*

class LorebookApp : App(MainWorkspace::class, Styles::class)

fun main() {
    launch<LorebookApp>()
}