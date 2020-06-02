package io.aesy.yamllint

import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import java.io.File
import java.nio.charset.StandardCharsets
import java.time.Duration

object YamllintExecutableFinder {
    private val TIME_OUT = Duration.ofSeconds(1)
    private val logger = getLogger()

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
            CommandLineExecutor.execute(command, TIME_OUT)
        } catch (e: ExecutionException) {
            logger.warn("Failed to find yamllint executable: Failed to execute command", e)
            return null
        }

        if (output.exitCode != 0) {
            logger.warn("Failed to find yamllint executable: Expected exit code 0, but got ${output.exitCode}")
            logger.debug("Yamllint error output: ${output.stderr}")
            return null
        }

        logger.debug("Yamllint output: ${output.stdout}")

        val file = File(output.stdout)

        if (!file.exists()) {
            logger.warn("Failed to find yamllint executable: command succeeded, but file does not exist")
            return null
        }

        return VfsUtil.findFileByIoFile(file, true)
    }
}
