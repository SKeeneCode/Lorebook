package org.ksoftware.lorebook.main

import org.ksoftware.lorebook.Styles
import tornadofx.*

/**
 * Entry class for the application
 */
class LorebookApp : App(ProjectWorkspace::class, Styles::class)

fun main() {
    launch<LorebookApp>()
}