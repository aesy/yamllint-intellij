package io.aesy.yamllint.util

object Yamllint {
    val RULES: List<String> = listOf(
        "anchors",
        "braces",
        "brackets",
        "colons",
        "commas",
        "comments",
        "comments-indentation",
        "document-end",
        "document-start",
        "empty-lines",
        "empty-values",
        "float-values",
        "hyphens",
        "indentation",
        "key-duplicates",
        "key-ordering",
        "line-length",
        "new-line-at-end-of-file",
        "new-lines",
        "octal-values",
        "quoted-strings",
        "trailing-spaces",
        "truthy",
    )
}
