package io.aesy.yamllint

import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import java.nio.charset.StandardCharsets
import java.text.ParseException
import java.time.Duration

@Service
class YamllintRunner(
    private val project: Project
) {
    companion object {
        private val TIME_OUT = Duration.ofSeconds(30)
    }

    private val logger = getLogger()

    fun run(
        workDirectory: String? = project.basePath,
        configPath: String? = null,
        files: Array<String> = arrayOf(".")
    ): List<YamllintProblem> {
        val command = GeneralCommandLine()
        command.charset = StandardCharsets.UTF_8
        command.exePath = "yamllint"

        command.setWorkDirectory(workDirectory)
        command.addParameters("--format", "parsable")

        if (!configPath.isNullOrBlank()) {
            command.addParameters("--config", configPath)
        }

        for (file in files) {
            command.addParameter(file)
        }

        val output = try {
            CommandLineExecutor.execute(command, TIME_OUT)
        } catch (e: ExecutionException) {
            logger.error("Failed to execute yamllint: Failed to execute command", e)
            return emptyList()
        }

        val successExitCodes = 0..2

        if (output.exitCode in successExitCodes) {
            logger.debug("Yamllint output: ${output.stdout}")

            try {
                return YamllintOutputParser.parse(output.stdout)
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
