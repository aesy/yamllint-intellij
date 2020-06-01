package io.aesy.yamllint

import com.intellij.openapi.diagnostic.Logger

@Suppress("unused")
inline fun <reified T> T.getLogger(): Logger {
    if (T::class.isCompanion) {
        return Logger.getInstance(T::class.java.enclosingClass)
    }

    return Logger.getInstance(T::class.java)
}
