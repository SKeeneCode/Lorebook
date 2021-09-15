package org.ksoftware.lorebook.io

interface Loadable {
    suspend fun <T> load(id: String) : T
}