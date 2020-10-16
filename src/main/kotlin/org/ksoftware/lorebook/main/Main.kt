package org.ksoftware.lorebook.main

import org.ksoftware.lorebook.Styles
import tornadofx.*

/**
 * Entry class for the application
 */
class LorebookApp : App(LorebookWorkspace::class, Styles::class)

fun main() {
    launch<LorebookApp>()
}