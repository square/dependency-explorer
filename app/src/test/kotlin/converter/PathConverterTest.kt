package converter

import com.google.common.truth.Truth.assertThat
import com.setup.ide.converter.PathConverter
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.nio.file.Paths

class PathConverterTest {

  @Test
  fun `verify cannot convert a null path`() {
    val converter = PathConverter()

    val exception = assertThrows<NullPointerException> {
      converter.convert(null)
    }

    assertThat(exception.message).isEqualTo("You must set a valid path to a repository!")
  }

  @Test
  fun `verify can convert non null path`() {
    val converter = PathConverter()
    val expected = Paths.get("tmp")

    val actual = converter.convert("tmp")

    assertThat(actual).isEqualTo(expected)
  }
}