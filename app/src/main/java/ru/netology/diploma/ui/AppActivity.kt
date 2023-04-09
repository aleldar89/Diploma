package ru.netology.diploma.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.diploma.R
import ru.netology.diploma.viewmodel.AuthViewModel

@AndroidEntryPoint
class AppActivity : AppCompatActivity(R.layout.activity_app) {

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey("1e4c7c64-4488-4233-a8f1-16488295ec90")
        MapKitFactory.initialize(this)

        viewModel.data.observe(this) {
            invalidateOptionsMenu()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_auth, menu)
        menu.setGroupVisible(R.id.authorized, viewModel.isAuthorized)
        menu.setGroupVisible(R.id.unauthorized, !viewModel.isAuthorized)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.sign_in -> {
                findNavController(R.id.nav_host_fragment)
                    .navigate(R.id.action_global_authFragment)
                true
            }
            R.id.sign_up -> {
                findNavController(R.id.nav_host_fragment)
                    .navigate(R.id.action_global_registrationFragment)
                true
            }
            R.id.my_wall -> {
                findNavController(R.id.nav_host_fragment)
                    .navigate(R.id.action_global_myWallFragment)
                true
            }
            R.id.my_jobs -> {
                findNavController(R.id.nav_host_fragment)
                    .navigate(R.id.action_global_myJobFeedFragment)
                true
            }
            R.id.log_out -> {
                findNavController(R.id.nav_host_fragment)
                    .navigate(R.id.action_global_logOutFragment)
                true
            }
            else -> false
        }
}