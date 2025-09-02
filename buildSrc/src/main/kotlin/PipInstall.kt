import org.gradle.nativeplatform.OperatingSystemFamily.*
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.collections.map
import kotlin.io.path.exists
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name

private val OS_FAMILY_MAP: Map<String, String> = mapOf(
    MACOS to "python-resources/MacOS/",
    LINUX to "python-resources/Linux/",
    WINDOWS to "python-resources/Windows/"
                                                      )

object PipInstall {
    private val currentOS = DefaultNativePlatform.getCurrentOperatingSystem()
    private val OS_SPECIFIC_PACKAGE_DIR = OS_FAMILY_MAP.getValue(currentOS.toFamilyName())
    private const val ANY_OS_PACKAGE_DIR = "python-resources/Any/"

    enum class PackageName(val fallback: String) {
        NUMPY("numpy>=2.3.1"),
        PILLOW("pillow>=11.3.0"),
        SHAPELY("shapely>=2.1.1"),
        PADDLEOCR("paddleocr>=3.1.0"),
        PADDLEPADDLE("paddlepaddle>=3.1.0"),
        PADDLEX("paddlex>=3.1.1"),
        SCIPY("scipy>=1.16.1"),
        PANDAS("pandas>=2.3.0"),
        SCIKIT_LEARN("scikit-learn>=1.1.0"),
        TIKTOKEN("tiktoken>=0.9.0"),
        OPENCV("opencv-contrib-python==4.10.0.84");

        fun getPackage(rootDir: File): String {
            return createFileInstall(PackageConfig(getFile(rootDir.toPath(), OS_SPECIFIC_PACKAGE_DIR, name.lowercase()), fallback))
        }
    }

    val wheelOsStandard: String = when {
        currentOS.isLinux -> "patchelf"
        currentOS.isMacOsX -> "delocate"
        currentOS.isWindows -> "delvewheel"
        else -> "patchelf"
    }

    private data class PackageConfig(val path: String, val fallback: String)

    private fun findMatchingFile(directory: Path, prefix: String): Path? {
        return if (directory.exists()) {
            directory.listDirectoryEntries()
                .firstOrNull { it.name.startsWith(prefix) }
        }
        else null
    }

    private fun getFile(rootDir: Path, pathFromRoot: String, pkgNameContains: String): String {
        val dir = rootDir.resolve(pathFromRoot)
        val anyOsDir = rootDir.resolve(ANY_OS_PACKAGE_DIR)

        val pkgFile = findMatchingFile(dir, pkgNameContains)
            ?: findMatchingFile(anyOsDir, pkgNameContains)

        return pkgFile?.toString()
            ?: ""
    }

    private fun createFileInstall(packageConfig: PackageConfig): String {
        return packageConfig.path.takeIf {
            it.isNotBlank() && Paths.get(it)
                .exists()
        }
            ?.let { "file://$it" }
            ?: packageConfig.fallback
    }

    fun resolvePackages(rootDir: File, packages: List<PackageName>): Set<String> {
        return packages.map { it.getPackage(rootDir) }
            .toSet()
    }

}
