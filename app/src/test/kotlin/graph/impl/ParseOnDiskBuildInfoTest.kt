package graph.impl

import com.setup.ide.graph.ParseBuildInfo
import com.setup.ide.graph.impl.ParseOnDiskBuildInfo
import org.junit.jupiter.api.Test
import testutil.withFileSystem

class ParseOnDiskBuildInfoTest {
  //private val subject: ParseBuildInfo = ParseOnDiskBuildInfo()

  @Test
  fun `verify can parse implementation project build info`() = withFileSystem { fs ->

  }

  @Test
  fun `verify can parse test project build info`() = withFileSystem { fs ->

  }

  @Test
  fun `verify can ignore commented out project lines`() = withFileSystem { fs ->

  }
}