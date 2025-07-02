import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import java.io.File

object PipInstall {
    private val currentOS = DefaultNativePlatform.getCurrentOperatingSystem()

    enum class PackageName(val fallback: String) {
        NUMPY("numpy==1.26.4"),
        PILLOW("pillow<=11.2.1"),
        SHAPELY("shapely==2.0.4"),
        PANDAS("pandas==2.2.2"),
        SCIKITIMAGE("scikit-image==0.23.2"),
        PADDLEOCR("paddleocr==2.7.0.3"),
        MESONPYTHON("meson-python==0.16.0"),
        CYTHON("cython==3.0.11")
    }

    val wheelOsStandard: String = when {
        currentOS.isLinux -> "patchelf"
        currentOS.isMacOsX -> "delocate"
        currentOS.isWindows -> "delvewheel"
        else -> "patchelf"
    }

    private data class PackageConfig(val path: String, val fallback: String)

    private fun numpyConfig(rootDir: File): PackageConfig = when {
        currentOS.isLinux -> PackageConfig(
                File(
                        rootDir,
                        "python-resources/Linux/numpy-2.2.6-graalpy311-graalpy242_311_native-manylinux_2_27_aarch64.manylinux_2_28_aarch64.whl"
                    ).absolutePath, PackageName.NUMPY.fallback
                                          )

        currentOS.isMacOsX -> PackageConfig(
                File(
                        rootDir, "python-resources/MacOS/numpy-2.2.6-graalpy311-graalpy242_311_native-macosx_14_0_arm64.whl"
                    ).absolutePath, PackageName.NUMPY.fallback
                                           )

        else -> PackageConfig("", PackageName.NUMPY.fallback)
    }

    private fun pillowConfig(rootDir: File): PackageConfig = when {
        currentOS.isLinux -> PackageConfig(
                File(
                        rootDir,
                        "python-resources/Linux/pillow-11.1.0-graalpy311-graalpy242_311_native-manylinux_2_27_aarch64.manylinux_2_28_aarch64.whl"
                    ).absolutePath, PackageName.PILLOW.fallback
                                          )

        currentOS.isMacOsX -> PackageConfig(
                File(rootDir, "python-resources/MacOS/pillow-11.1.0-graalpy311-graalpy242_311_native-macosx_14_0_arm64.whl").absolutePath,
                PackageName.PILLOW.fallback
                                           )

        else -> PackageConfig("", PackageName.PILLOW.fallback)
    }

    private fun shapelyConfig(rootDir: File): PackageConfig = when {
        currentOS.isLinux -> PackageConfig(
                File(
                        rootDir,
                        "python-resources/Linux/shapely-2.1.1-graalpy311-graalpy242_311_native-manylinux_2_24_aarch64.manylinux_2_28_aarch64.whl"
                    ).absolutePath, PackageName.SHAPELY.fallback
                                          )

        currentOS.isMacOsX -> PackageConfig(
                File(rootDir, "python-resources/MacOS/shapely-2.1.1-graalpy311-graalpy242_311_native-macosx_14_0_arm64.whl").absolutePath,
                PackageName.SHAPELY.fallback
                                           )

        else -> PackageConfig("", PackageName.SHAPELY.fallback)
    }

    private fun cythonConfig(rootDir: File): PackageConfig = PackageConfig(
            File(rootDir, "python-resources/any/Cython-3.0.11-py2.py3-none-any.whl").path, PackageName.CYTHON.fallback
                                                                          )

    private fun mesonPythonConfig(rootDir: File): PackageConfig = PackageConfig(
            File(rootDir, "python-resources/any/meson_python-0.16.0-py3-none-any.whl").path, PackageName.MESONPYTHON.fallback
                                                                               )

    private fun paddleOcrConfig(rootDir: File): PackageConfig = PackageConfig(
            File(rootDir, "python-resources/any/paddleocr-3.1.0-py3-none-any.whl").path, PackageName.PADDLEOCR.fallback
                                                                             )

    private fun sciKitImageConfig(rootDir: File): PackageConfig = PackageConfig(
            File(rootDir, "python-resources/MacOS/scikit_image-0.25.0-cp312-cp312-macosx_15_0_arm64.whl").path, PackageName.SCIKITIMAGE.fallback
                                                                               )

    private fun pandasConfig(rootDir: File): PackageConfig = PackageConfig(
            File(rootDir, "python-resources/Linux/pandas-2.2.3-cp39-cp39-manylinux_2_34_aarch64.whl").path, PackageName.PANDAS.fallback
                                                                          )

    private fun createFileInstall(packageConfig: PackageConfig): String {
        val validPath = packageConfig.path.takeIf { it: String ->
            it.isNotBlank() && File(it).exists()
        }
            ?.let { "file://$it" }

        return validPath ?: packageConfig.fallback
    }

    fun getPackageBinary(rootDir: File, packageName: PackageName): String {
        return when (packageName) {
            PackageName.NUMPY -> createFileInstall(numpyConfig(rootDir))
            PackageName.PILLOW -> createFileInstall(pillowConfig(rootDir))
            PackageName.SHAPELY -> createFileInstall(shapelyConfig(rootDir))
            PackageName.PANDAS -> createFileInstall(pandasConfig(rootDir))
            PackageName.SCIKITIMAGE -> createFileInstall(sciKitImageConfig(rootDir))
            PackageName.PADDLEOCR -> createFileInstall(paddleOcrConfig(rootDir))
            PackageName.MESONPYTHON -> createFileInstall(mesonPythonConfig(rootDir))
            PackageName.CYTHON -> createFileInstall(cythonConfig(rootDir))
        }
    }
}