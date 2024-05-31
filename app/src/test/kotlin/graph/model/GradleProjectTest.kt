package graph.model

import com.google.common.truth.Truth.assertThat
import com.setup.ide.graph.model.GradleProject
import org.junit.jupiter.api.Test
import java.nio.file.Files

class GradleProjectTest {

  @Test
  fun `verify gradle project are the same if params are the same`() {
    val project = ":app"
    val path = Files.createTempDirectory("tmp").toAbsolutePath()

    val p1 = GradleProject(path, project)
    val p2 = GradleProject(path, project)

    val result = setOf(p1, p2)

    // Since the name and path are the same, the size should be 1
    assertThat(result.size).isEqualTo(1)
  }

  @Test
  fun `verify gradle project are not the same if paths are different`() {
    val project = ":app"
    val path1 = Files.createTempDirectory("tmp").toAbsolutePath()
    val path2 = Files.createTempDirectory("tmp2").toAbsolutePath()

    val p1 = GradleProject(path1, project)
    val p2 = GradleProject(path2, project)

    val result = setOf(p1, p2)

    // Since the name and path are the not same, the size should be 2
    assertThat(result.size).isEqualTo(2)
  }
}