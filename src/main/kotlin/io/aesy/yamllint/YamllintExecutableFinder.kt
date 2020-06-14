package io.aesy.yamllint

import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import java.io.File
import java.nio.charset.StandardCharsets
import java.time.Duration

@Service
class YamllintExecutableFinder(
    project: Project
) {
    companion object {
        private val logger = getLogger()
        private val timeout = Duration.ofSeconds(1)
    }

    private val executor = project.getService<CommandLineExecutor>()

    fun find(): VirtualFile? {
        val command = GeneralCommandLine()
        command.charset = StandardCharsets.UTF_8

        if (SystemInfo.isWindows) {
            command.exePath = "where"
        } else {
            command.exePath = "which"
        }

        command.addParameter("yamllint")

        val output = try {
            executor.execute(command, timeout)
        } catch (e: ExecutionException) {
            logger.warn("Failed to find yamllint executable: Failed to execute command", e)
            return null
        }

        val stdout = output.stdout.removeSuffix("\n")
        val stderr = output.stderr.removeSuffix("\n")

        if (output.exitCode != 0) {
            logger.warn("Failed to find yamllint executable: Expected exit code 0, but got ${output.exitCode}")
            logger.debug("Yamllint error output: $stderr")
            return null
        }

        logger.debug("Yamllint output: $stdout")

        val file = File(stdout)

        if (!file.exists()) {
            logger.warn("Failed to find yamllint executable: command succeeded, but file does not exist")
            return null
        }

        return VfsUtil.findFileByIoFile(file, true)
    }
}
