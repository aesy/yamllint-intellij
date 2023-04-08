package io.aesy.yamllint.runner

import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.*
import com.intellij.openapi.util.Key
import io.aesy.yamllint.util.getLogger
import java.time.Duration

object CommandLineExecutor {
    private val logger = getLogger()

    @Throws(ExecutionException::class)
    fun GeneralCommandLine.execute(timeout: Duration? = null): ProcessOutput {
        val process = createProcess()
        val handler = OSProcessHandler(process, commandLineString, Charsets.UTF_8)
        val output = ProcessOutput()

        logger.debug("Executing command: $commandLineString")

        handler.addProcessListener(object: ProcessAdapter() {
            override fun onTextAvailable(event: ProcessEvent, outputType: Key<*>) {
                when (outputType) {
                    ProcessOutputType.STDERR -> output.appendStderr(event.text)
                    ProcessOutputType.STDOUT -> output.appendStdout(event.text)
                }
            }
        })

        handler.startNotify()

        val ended = when (timeout) {
            null -> handler.waitFor()
            else -> handler.waitFor(timeout.toMillis())
        }

        if (ended) {
            logger.debug("Command successfully executed with exit code ${output.exitCode}")
            output.exitCode = process.exitValue()
        } else {
            logger.debug("Command timed out after ${timeout?.toMillis()} ms")
            handler.destroyProcess()
            output.setTimeout()
        }

        return output
    }
}
