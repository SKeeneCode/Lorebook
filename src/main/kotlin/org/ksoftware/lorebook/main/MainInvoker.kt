package org.ksoftware.lorebook.main

import tornadofx.launch

fun main() {
    org.burningwave.core.assembler.StaticComponentContainer.JVMInfo.version
    launch<LorebookApp>()
}