package com.jereksel.libresubstratum.views

import android.os.Build
import android.support.v7.app.AppCompatActivity
import com.jereksel.libresubstratum.BuildConfig
import com.jereksel.libresubstratum.MockedApp
import com.jereksel.libresubstratum.ResettableLazy
import com.jereksel.libresubstratum.activities.detailed.DetailedContract
import com.nhaarman.mockito_kotlin.verify
import kotlinx.android.synthetic.main.activity_detailed.*
import org.junit.*
import org.junit.Assert.*
import org.assertj.android.api.Assertions.assertThat
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config
import android.content.Intent
import android.provider.Settings
import android.widget.ArrayAdapter
import com.jereksel.libresubstratum.activities.detailed.DetailedView
import com.jereksel.libresubstratum.data.Type3ExtensionToString
import com.jereksel.libresubstratumlib.ThemePack
import com.jereksel.libresubstratumlib.Type3Data
import com.jereksel.libresubstratumlib.Type3Extension
import org.robolectric.shadows.ShadowApplication


@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class,
        application = MockedApp::class,
        sdk = intArrayOf(Build.VERSION_CODES.LOLLIPOP))
class DetailedViewTest {

    lateinit var activityController : ActivityController<DetailedView>
    lateinit var activity : DetailedContract.View
    lateinit var presenter: DetailedContract.Presenter

    var activityCasted by ResettableLazy { activity as AppCompatActivity? }
    var recyclerView by ResettableLazy { activityCasted!!.recyclerView }
    var spinner by ResettableLazy { activityCasted!!.spinner }

    val appId = "id1"

    @Before
    fun setup() {
        val app = RuntimeEnvironment.application as MockedApp
        presenter = app.mockedDetailedPresenter
        val intent = Intent(ShadowApplication.getInstance().applicationContext, DetailedView::class.java)
        val APP_ID_EXTRA = "com.jereksel.libresubstratum.activities.detailed.appIdStarterKey"
        intent.putExtra(APP_ID_EXTRA, appId)
        activityController = Robolectric.buildActivity(DetailedView::class.java, intent).create()
        activity = activityController.get()
    }

    @After
    fun cleanup() {
        activityController.destroy()
        activityCasted = null
        recyclerView = null
        spinner = null
    }

    @Test
    fun `readTheme() is invoked after starting`() {
        verify(presenter).readTheme(appId)
    }

    @Test
    fun `type3 spinner has list of type3 extensions`() {
        val colors = listOf("black", "white", "green")
        val type3 = Type3Data(colors.map { Type3Extension(it, false) })
        activity.addThemes(ThemePack(listOf(), type3))
        assertThat(spinner).isVisible
        assertEquals(type3.extensions.map { Type3ExtensionToString(it) }, (spinner.adapter as ArrayAdapter<*>).getAllItems())
    }

    fun `type3 spinner is no visible whenno type3 extensions are available`() {
        activity.addThemes(ThemePack(listOf()))
        assertThat(spinner).isNotVisible
    }

    private fun <T> ArrayAdapter<T>.getAllItems() = (0..count-1).map { this.getItem(it) }
}

