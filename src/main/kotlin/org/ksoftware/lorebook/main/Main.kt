package org.ksoftware.lorebook.main

import org.ksoftware.lorebook.Styles
import org.ksoftware.lorebook.newproject.NewProjectView
import tornadofx.*

/**
 * Entry class for the application
 */
class LorebookApp : App(NewProjectView::class, Styles::class)

fun main() {
    launch<LorebookApp>()
}