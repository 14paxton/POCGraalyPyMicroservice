import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import java.io.File
import java.io.FilenameFilter

private const val MACOS_WHL_DIR = "python-resources/MacOS/"

object PipInstall {
    private val currentOS = DefaultNativePlatform.getCurrentOperatingSystem()

    enum class PackageName(val fallback: String) {
        NUMPY("numpy==2.3.1"),
        PILLOW("pillow==11.3.0"),
        SHAPELY("shapely==2.1.1"),
        PADDLEOCR("paddleocr==3.1.0"),
        PADDLEPADDLE("paddlepaddle==3.1.0"),
        PADDLEX("paddlex==3.1.1"),
        SCIPY("scipy==1.16.1"),
        PANDAS("pandas==2.3.0"),
        SKITLEARN("scikit-learn==1.1.0"),
        TIKTOKEN("tiktoken==0.9.0"),
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
                        rootDir, "python-resources/MacOS/numpy-2.3.1-graalpy311-graalpy242_311_native-macosx_14_0_arm64.whl"
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
                File(rootDir, "python-resources/MacOS/pillow-11.3.0-graalpy311-graalpy242_311_native-macosx_11_0_arm64.whl").absolutePath,
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

    private fun paddlePaddleConfig(rootDir: File): PackageConfig = when {
        currentOS.isLinux -> PackageConfig(
                File(
                        rootDir, "python-resources/Linux/paddlepaddle-3.0.0-graalpy311-graalpy242_311_native-manylinux2014_aarch64.whl"
                    ).absolutePath, PackageName.PADDLEPADDLE.fallback
                                          )

        currentOS.isMacOsX -> PackageConfig(
                File(rootDir, "python-resources/MacOS/paddlepaddle-3.1.0-graalpy311-graalpy242_311_native-macosx_11_0_arm64.whl").absolutePath,
                PackageName.PADDLEPADDLE.fallback
                                           )

        else -> PackageConfig("", PackageName.PADDLEPADDLE.fallback)
    }

    private fun paddleOcrConfig(rootDir: File): PackageConfig = PackageConfig(
            File(rootDir, "python-resources/any/paddleocr-3.1.0-py3-none-any.whl").path,
            PackageName.PADDLEOCR.fallback
                                                                             )

    private fun paddleXConfig(rootDir: File): PackageConfig = PackageConfig(
            File(rootDir, "python-resources/any/paddlex-3.1.1-py3-none-any.whl").path,
            PackageName.PADDLEOCR.fallback
                                                                           )

    private fun sciPyConfig(rootDir: File): PackageConfig = PackageConfig(
            File(rootDir, "python-resources/MacOS/scipy-1.16.0-graalpy311-graalpy242_311_native-macosx_14_0_arm64.whl").path,
            PackageName.SCIPY.fallback
                                                                         )

    private fun pandasConfig(rootDir: File): PackageConfig = PackageConfig(
            File(rootDir, "python-resources/MacOS/pandas-2.3.0-graalpy311-graalpy242_311_native-macosx_11_0_arm64.whl").path,
            PackageName.PANDAS.fallback
                                                                          )

    private fun skitLearnConfig(rootDir: File): PackageConfig = PackageConfig(
            File(rootDir, "python-resources/MacOS/scikit_learn-1.7.0-graalpy311-graalpy242_311_native-macosx_12_0_arm64.whl").path,
            PackageName.SKITLEARN.fallback
                                                                             )

    private fun tikTokenConfig(rootDir: File): PackageConfig {
        val dir = File(rootDir, MACOS_WHL_DIR)
        val tikTokenFile = dir.listFiles(FilenameFilter { _, name -> name.startsWith("tiktoken") })
            ?.firstOrNull()
        val path = tikTokenFile?.absolutePath ?: ""
        return PackageConfig(path, PackageName.TIKTOKEN.fallback)
    }

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
            PackageName.PADDLEOCR -> createFileInstall(paddleOcrConfig(rootDir))
            PackageName.PADDLEPADDLE -> createFileInstall(paddlePaddleConfig(rootDir))
            PackageName.PADDLEX -> createFileInstall(paddleXConfig(rootDir))
            PackageName.SCIPY -> createFileInstall(sciPyConfig(rootDir))
            PackageName.PANDAS -> createFileInstall(pandasConfig(rootDir))
            PackageName.SKITLEARN -> createFileInstall(skitLearnConfig(rootDir))
            PackageName.TIKTOKEN -> createFileInstall(tikTokenConfig(rootDir))
        }
    }
}