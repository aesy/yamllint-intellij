package io.aesy.yamllint.startup

import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.jetbrains.python.sdk.PythonSdkUtil
import java.nio.file.Paths

class YamllintPythonVirtualEnvExecutableProvider: YamllintExecutableProvider {
    private val executableName: String = if (SystemInfo.isWindows) {
        "yamllint.exe"
    } else {
        "yamllint"
    }

    override fun priority(): Int = 1000

    override fun find(project: Project): VirtualFile? {
        val projectRootManager = ProjectRootManager.getInstance(project)
        val sdk = projectRootManager.projectSdk ?: return null
        val home = sdk.homeDirectory ?: return null
        val path = PythonSdkUtil.getExecutablePath(home.path, executableName) ?: return null

        return VfsUtil.findFile(Paths.get(path), false)
    }
}
