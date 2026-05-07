package com.fenixgames.core.serialization

import kotlinx.serialization.json.Json

object JsonConfig {
    val default: Json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        prettyPrint = false
    }
}

