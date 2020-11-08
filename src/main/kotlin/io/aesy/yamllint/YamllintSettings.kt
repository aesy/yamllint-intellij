package io.aesy.yamllint

// Note: fields must either be nullable or have default values in order for
// IntelliJ to be able to serialize/deserialize the object...
data class YamllintSettings(
    var enabled: Boolean = false,
    var binPath: String = "",
    var configPath: String = ""
)
