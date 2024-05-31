package testutil

import testutil.Resources.uriFromResource
import java.net.URI
import java.nio.file.FileSystem
import java.nio.file.FileSystems

fun Any.withFileSystem(test: (FileSystem) -> Unit) {
  newFileSystem().use(test)
}

private fun Any.newFileSystem(): FileSystem {
  val fsZip = uriFromResource("register-fs.zip")
  val uri = URI.create("jar:$fsZip")
  return FileSystems.newFileSystem(uri, mutableMapOf<String, Any>())
}