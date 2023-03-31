package ru.netology.diploma.ui

import ParentFragmentPagerAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.activity.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.diploma.R
import ru.netology.diploma.viewmodel.AuthViewModel

@AndroidEntryPoint
class AppActivity : AppCompatActivity(R.layout.activity_app) {

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey("e35ce02e-18b3-4678-9859-286826ff3245")
        MapKitFactory.initialize(this)

        val viewPager: ViewPager2 = findViewById(R.id.view_pager)
        val tabLayout: TabLayout = findViewById(R.id.tab_layout)

        val pagerAdapter = ParentFragmentPagerAdapter(this)
        viewPager.adapter = pagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            val tabNames = listOf("Posts", "Events")
            tab.text = tabNames[position]
        }.attach()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_auth, menu)
        menu.setGroupVisible(R.id.authorized, viewModel.isAuthorized)
        menu.setGroupVisible(R.id.unauthorized, !viewModel.isAuthorized)
        return super.onCreateOptionsMenu(menu)
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean =
//        when (item.itemId) {
//            R.id.sign_in -> {
//                findNavController(R.id.nav_host_fragment)
//                    .navigate(R.id.action_global_authFragment)
//                true
//            }
//            R.id.sign_up -> {
//                findNavController(R.id.nav_host_fragment)
//                    .navigate(R.id.action_global_registrationFragment)
//                true
//            }
//            R.id.my_wall -> {
//                findNavController(R.id.nav_host_fragment)
//                    .navigate(R.id.action_global_myWallFragment)
//                true
//            }
//            R.id.my_jobs -> {
//                findNavController(R.id.nav_host_fragment)
//                    .navigate(R.id.action_global_myJobFeedFragment)
//                true
//            }
//            R.id.log_out -> {
//                findNavController(R.id.nav_host_fragment)
//                    .navigate(R.id.action_global_logOutFragment)
//                true
//            }
//            else -> false
//        }
}