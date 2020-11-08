package io.aesy.yamllint

import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

@Service
class YamllintProjectAnalyzer(
    project: Project
) {
    companion object {
        private val logger = getLogger()
        private const val DEFAULT_BIN_PATH = "yamllint"
        private const val DEFAULT_CONFIG_PATH = ""
    }

    private val executableFinder = project.service<YamllintExecutableFinder>()
    private val configurationFinder = project.service<YamllintConfigurationFinder>()

    private var settings: YamllintSettings? = null

    @Synchronized
    fun getSuggestedSettings(): YamllintSettings {
        if (settings != null) {
            return settings!!
        }

        logger.debug("Analyzing project...")

        logger.debug("Searching for yamllint executable...")
        val executable = executableFinder.find()
        val binPath = executable?.path ?: DEFAULT_BIN_PATH

        if (executable == null) {
            logger.info("No yamllint executable found, defaulting to '${DEFAULT_BIN_PATH}'")
        } else {
            logger.info("Found yaml executable at '$binPath'")
        }

        logger.debug("Searching for yamllint configuration...")
        val configuration = ReadAction.compute<VirtualFile, Throwable> { configurationFinder.find()?.virtualFile }
        val configPath = configuration?.path ?: DEFAULT_CONFIG_PATH

        if (configuration == null) {
            logger.info("No yamllint configuration found, defaulting to '${DEFAULT_CONFIG_PATH}'")
        } else {
            logger.info("Found yamllint configuration at '$configPath'")
        }

        val enabled = !configuration?.path.isNullOrBlank()

        settings = YamllintSettings(
            enabled = enabled,
            binPath = binPath,
            configPath = configPath)

        return settings!!
    }
}
