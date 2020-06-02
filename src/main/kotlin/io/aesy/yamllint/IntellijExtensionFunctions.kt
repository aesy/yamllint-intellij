package io.aesy.yamllint

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project

inline fun <reified T : Any> Project.getService(): T {
    return getService(T::class.java)
}

@Suppress("unused")
inline fun <reified T> T.getLogger(): Logger {
    if (T::class.isCompanion) {
        return Logger.getInstance(T::class.java.enclosingClass)
    }

    return Logger.getInstance(T::class.java)
}
