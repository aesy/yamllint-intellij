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

    fun run(
        configPath: String? = null,
        workDirectory: String = "",
        files: Array<String> = arrayOf(".")
    ): List<YamllintProblem> {
        return createCommand(configPath)
            .withParameters(*files)
            .withWorkDirectory(workDirectory)
            .execute()
    }

    fun run(
        configPath: String? = null,
        content: String
    ): List<YamllintProblem> {
        val tempFile = try {
            File.createTempFile("yamllint-intellij-input-", ".yaml").apply {
                writeText(content)
            }
        } catch (e: IOException) {
            logger.error("Failed to execute yamllint: Failed to create temporary file", e)
            return emptyList()
        }

        val result = createCommand(configPath)
            .withParameters("-")
            .withInput(tempFile)
            .execute()

        if (!tempFile.delete()) {
            logger.warn("Failed to delete temporary file ${tempFile.canonicalPath}")
        }

        return result
    }

    private fun createCommand(configPath: String?): GeneralCommandLine {
        val command = GeneralCommandLine()
            .withCharset(StandardCharsets.UTF_8)
            .withExePath("yamllint")
            .withParameters("--format", "parsable")

        if (!configPath.isNullOrBlank()) {
            command.addParameters("--config", configPath)
        }

        return command
    }

    private fun GeneralCommandLine.execute(): List<YamllintProblem> {
        val output = try {
            executor.execute(this, timeout)
        } catch (e: ExecutionException) {
            logger.error("Failed to execute yamllint: Failed to execute command", e)
            return emptyList()
        }

        val successExitCodes = (0..2).toList()

        if (output.exitCode in successExitCodes) {
            logger.debug("Yamllint output: ${output.stdout}")

            try {
                return parser.parse(output.stdout)
            } catch (e: ParseException) {
                logger.error("Failed to execute yamllint: Could not parse output", e)
            }
        } else {
            logger.error("Failed to execute yamllint: Expected exit code to be one of $successExitCodes but was ${output.exitCode}")
            logger.debug("Yamllint error output: ${output.stderr}")
        }

        return emptyList()
    }
}
