package util

import com.google.common.truth.Truth.assertThat
import com.setup.ide.util.toGradlePath
import com.setup.ide.util.toNormalizedGradleName
import org.junit.jupiter.api.Test
import java.nio.file.Files
import kotlin.io.path.absolutePathString

class IOTest {

  @Test
  fun `can convert to valid normalized gradle names`() {
    val withoutAnyColons = "app"
    val withoutAnyColonsValid = withoutAnyColons.toNormalizedGradleName()
    assertThat(withoutAnyColonsValid).isEqualTo(":app")

    val withSlashesOnly = "app/common/place"
    val withSlashesOnlyValid = withSlashesOnly.toNormalizedGradleName()
    assertThat(withSlashesOnlyValid).isEqualTo(":app:common:place")

    val withLeadingColon = ":app:common"
    val withLeadingColonValid = withLeadingColon.toNormalizedGradleName()
    assertThat(withLeadingColonValid).isEqualTo(":app:common")

    val withEndingColon = "app:common:"
    val withEndingColonValid = withEndingColon.toNormalizedGradleName()
    assertThat(withEndingColonValid).isEqualTo(":app:common")

    val withMixedColonAndSlash = "app/common:place"
    val withMixedColonAndSlashValid = withMixedColonAndSlash.toNormalizedGradleName()
    assertThat(withMixedColonAndSlashValid).isEqualTo(":app:common:place")
  }

  @Test
  fun `can convert normalized gradle name to path`() {
    val rootRepo = Files.createTempDirectory("tmp")

    val validNormalizedGradleName = ":app:common"

    val gradlePath = validNormalizedGradleName.toGradlePath(rootRepo)

    assertThat(gradlePath.absolutePathString()).isEqualTo(
      rootRepo.absolutePathString() + "/app/common"
    )
  }
}