package org.ksoftware.lorebook.io

import com.squareup.moshi.Moshi
import org.ksoftware.lorebook.adapters.ColorAdapter
import org.ksoftware.lorebook.adapters.PageModelJsonAdapter
import org.ksoftware.lorebook.adapters.StringPropertyAdapter
import org.ksoftware.lorebook.adapters.TagModelJsonAdapter
import org.ksoftware.lorebook.navigator.BookmarkJsonAdapter
import tornadofx.Controller

class IOController : Controller() {

    private val pageModelJsonAdapter: PageModelJsonAdapter by inject()
    private val bookmarkJsonAdapter: BookmarkJsonAdapter by inject()

    val moshi: Moshi = Moshi.Builder()
        .add(StringPropertyAdapter())
        .add(TagModelJsonAdapter())
        .add(ColorAdapter())
        .add(pageModelJsonAdapter)
        .add(bookmarkJsonAdapter)
        .build()
}