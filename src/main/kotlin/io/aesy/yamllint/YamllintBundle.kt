package io.aesy.yamllint

import com.intellij.AbstractBundle
import org.jetbrains.annotations.PropertyKey
import java.util.*

object YamllintBundle {
    private const val BUNDLE = "messages.YamllintBundle"

    private val bundle = ResourceBundle.getBundle(BUNDLE)

    fun message(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any): String {
        return AbstractBundle.message(bundle, key, *params)
    }
}
