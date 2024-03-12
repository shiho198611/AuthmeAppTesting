package com.testing.githubusersdk.extension

fun String?.parseNextUrl(): String? {
    if (this == null) return null
    val pattern = Regex("""<([^>]*)>; rel="next"""")
    val matchResult = pattern.find(this)
    return matchResult?.groupValues?.get(1)
}