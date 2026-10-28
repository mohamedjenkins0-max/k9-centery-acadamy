package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.LocationHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `verify k9 century academy branding and motto`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        val motto = context.getString(R.string.academy_motto)
        assertEquals("K9 Century", appName)
        assertEquals("Inspect What You Expect.", motto)
    }

    @Test
    fun `verify location distance calculation`() {
        // Distance between same coordinates should be zero
        val distZero = LocationHelper.calculateDistanceMiles(34.0522, -118.2437, 34.0522, -118.2437)
        assertEquals(0.0, distZero, 0.01)

        // Distance between LA and Hollywood (~6 miles)
        val dist = LocationHelper.calculateDistanceMiles(34.0522, -118.2437, 34.1012, -118.3253)
        assertTrue("Distance should be greater than 4 miles", dist > 4.0)
        assertTrue("Distance should be less than 10 miles", dist < 10.0)
    }
}
