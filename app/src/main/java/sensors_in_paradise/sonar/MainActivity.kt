package sensors_in_paradise.sonar

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ViewAnimator
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.xsens.dot.android.sdk.events.XsensDotData
import sensors_in_paradise.sonar.file_uploader.FileUploader
import sensors_in_paradise.sonar.file_uploader.FileUploaderDialog
import sensors_in_paradise.sonar.page1.ConnectionInterface
import sensors_in_paradise.sonar.page1.Page1Handler
import sensors_in_paradise.sonar.page2.Page2Handler
import sensors_in_paradise.sonar.page3.Page3Handler
import sensors_in_paradise.sonar.page1.XSENSArrayList

class MainActivity : AppCompatActivity(), TabLayout.OnTabSelectedListener {

    private lateinit var switcher: ViewAnimator
    private lateinit var tabLayout: TabLayout

    private val pageHandlers = ArrayList<PageInterface>()

    private val scannedDevices = XSENSArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        switcher = findViewById(R.id.switcher_activity_main)
        tabLayout = findViewById(R.id.tab_layout_activity_main)

        initClickListeners()

        val page2 = Page2Handler(scannedDevices)
        pageHandlers.add(Page1Handler(scannedDevices, page2))
        pageHandlers.add(page2)
        pageHandlers.add(Page3Handler())
        for (handler in pageHandlers) {
            handler.activityCreated(this)
        }
        supportActionBar?.setBackgroundDrawable(ColorDrawable(getColor(R.color.colorPrimary)))


    }

    override fun onResume() {
        super.onResume()
        for (handler in pageHandlers) {
            handler.activityResumed()
        }
    }
    private fun initClickListeners() {
        tabLayout.addOnTabSelectedListener(this)
    }
    override fun onTabSelected(tab: TabLayout.Tab?) {
        if (tab != null) {
            switcher.displayedChild = tab.position
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        // TODO("Not yet implemented")
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
        // TODO("Not yet implemented")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.activity_main_menu, menu)
        return true
    }

    fun onFileUploadMenuItemClicked(menuItem: MenuItem){
        FileUploaderDialog(this).show()
    }
}
