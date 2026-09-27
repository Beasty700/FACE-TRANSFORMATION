package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.ClothingMode
import com.example.model.PerformanceMode
import com.example.model.TransformationSettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Identity Transformer", appName)
  }

  @Test
  fun `verify transformation settings defaults`() {
    val settings = TransformationSettings()
    assertEquals(PerformanceMode.HIGH_QUALITY, settings.performanceMode)
    assertEquals(ClothingMode.REFERENCE_CLOTHING, settings.clothingMode)
    assertNotNull(settings.clothingPrompt)
  }
}
