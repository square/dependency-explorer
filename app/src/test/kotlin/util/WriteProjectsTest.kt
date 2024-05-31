package util

import com.google.common.truth.Truth.assertThat
import com.setup.ide.util.WriteProjects
import com.setup.ide.util.WriteProjectsToOutput
import org.junit.jupiter.api.Test
import java.nio.file.Files
import kotlin.io.path.readText

class WriteProjectsTest {

  @Test
  fun `can write imports to file`() {
    val subject: WriteProjects = WriteProjectsToOutput()
    val output = Files.createTempFile("temp_override", ".gradle")
    val targets = listOf("terminal-api/analytics/public")

    val dependencies = setOf(
      ":common:cdp-helper:public",
      ":protos:eventstream-v1:public",
      ":protos:connect-v2-terminal:public",
      ":common:analytics-common:public",
      ":common:consent-status:public",
      ":protos:common:public",
      ":protos:eventstream-v2:public",
      ":protos:bill:public",
      ":protos:client-common:public",
      ":protos:connect-v2:public",
      ":protos:connect-v2-payment:public",
      ":common:dagger:public",
      ":common:utilities-jvm:public",
      ":protos:bill-common:public",
      ":protos:catalog-sync-id:public",
      ":protos:coupons:public",
      ":protos:items:public",
      ":protos:order:public",
      ":protos:payment:public",
      ":protos:rolodex:public",
      ":protos:tipping:public",
      ":common:wire-utilities:public",
      ":protos:connect-v2-merchant-catalog:public",
      ":protos:loyalty-common:public",
    )

    val out = subject.write(
      to = output,
      targets = targets,
      projects = dependencies
    )

    val actual = out.readText()

    assertThat(actual).isEqualTo(
      """
        // THIS FILE IS GENERATED AUTOMATICALLY - DO NOT MODIFY
        // targets = terminal-api/analytics/public
        include ':common:cdp-helper:public'
        include ':protos:eventstream-v1:public'
        include ':protos:connect-v2-terminal:public'
        include ':common:analytics-common:public'
        include ':common:consent-status:public'
        include ':protos:common:public'
        include ':protos:eventstream-v2:public'
        include ':protos:bill:public'
        include ':protos:client-common:public'
        include ':protos:connect-v2:public'
        include ':protos:connect-v2-payment:public'
        include ':common:dagger:public'
        include ':common:utilities-jvm:public'
        include ':protos:bill-common:public'
        include ':protos:catalog-sync-id:public'
        include ':protos:coupons:public'
        include ':protos:items:public'
        include ':protos:order:public'
        include ':protos:payment:public'
        include ':protos:rolodex:public'
        include ':protos:tipping:public'
        include ':common:wire-utilities:public'
        include ':protos:connect-v2-merchant-catalog:public'
        include ':protos:loyalty-common:public'

      """.trimIndent()
    )
  }
}