package org.ksoftware.lorebook.attributes

import com.squareup.moshi.Moshi
import org.ksoftware.lorebook.adapters.StringPropertyAdapter

/**
 * Base class for models that require JSON serialization/deserialization using Moshi.
 */
abstract class JsonModel {
    val moshi = Moshi.Builder().add(StringPropertyAdapter()).build()
}