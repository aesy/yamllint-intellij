package io.aesy.yamllint

import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.text.ParseException
import java.time.Duration

@Service
class YamllintRunner(
    project: Project
) {
    companion object {
        private val logger = getLogger()
        private val timeout = Duration.ofSeconds(30)
    }

    private val executor = project.getService<CommandLineExecutor>()
    private val parser = project.getService<YamllintOutputParser>()
    private val settings = project.getService<YamllintSettingsProvider>()

    @Throws(YamllintException::class)
    fun run(workDirectory: String = "", files: Array<String> = arrayOf(".")): List<YamllintProblem> {
        return createCommand()
            .withParameters(*files)
            .withWorkDirectory(workDirectory)
            .execute()
    }

    @Throws(YamllintException::class)
    fun run(content: String): List<YamllintProblem> {
        val tempFile = try {
            File.createTempFile("yamllint-intellij-input-", ".yaml").apply {
                writeText(content)
            }
        } catch (e: IOException) {
            throw YamllintException("Failed to create temporary file", e)
        }

        return try {
            createCommand()
                .withParameters("-")
                .withInput(tempFile)
                .execute()
        } finally {
            if (!tempFile.delete()) {
                logger.warn("Failed to delete temporary file ${tempFile.canonicalPath}")
            }
        }
    }

    private fun createCommand(): GeneralCommandLine {
        val command = GeneralCommandLine()
            .withCharset(StandardCharsets.UTF_8)
            .withExePath(settings.state.binPath)
            .withParameters("--format", "parsable")

        if (!settings.state.configPath.isBlank()) {
            command.addParameters("--config-file", settings.state.configPath)
        }

        return command
    }

    @Throws(YamllintException::class)
    private fun GeneralCommandLine.execute(): List<YamllintProblem> {
        val output = try {
            executor.execute(this, timeout)
        } catch (e: ExecutionException) {
            throw YamllintException("Failed to execute command", e)
        }

        val successExitCodes = (0..2).toList()

        if (output.exitCode !in successExitCodes) {
            logger.debug("Yamllint error output: ${output.stderr}")
            throw YamllintException("Expected exit code to be one of $successExitCodes but was ${output.exitCode}\n${output.stderr}")
        }

        if (!output.stderr.isBlank()) {
            logger.debug("Yamllint error output: ${output.stderr}")
            throw YamllintException("An unexpected error occurred:\n${output.stderr}")
        }

        logger.debug("Yamllint output: ${output.stdout}")

        try {
            return parser.parse(output.stdout)
        } catch (e: ParseException) {
            throw YamllintException("Could not parse output", e)
        }
    }
}
