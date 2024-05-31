package testutil

import java.io.File
import java.net.URI
import java.net.URL
import java.nio.file.Path
import java.nio.file.Paths

object Resources {
  @JvmStatic
  fun Any.fileFromResource(resourcePath: String): File = pathFromResource(resourcePath).toFile()

  @JvmStatic
  fun Any.pathFromResource(resourcePath: String): Path = Paths.get(uriFromResource(resourcePath))

  @JvmStatic
  fun Any.uriFromResource(resourcePath: String): URI = urlFromResource(resourcePath).toURI()

  @JvmStatic
  fun Any.urlFromResource(resourcePath: String): URL =
    javaClass.classLoader.getResource(resourcePath) ?: error("No resource at '$resourcePath'")
}