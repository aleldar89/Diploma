package ru.netology.diploma.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.diploma.R
import ru.netology.diploma.adapter.PostViewHolder.Companion.textArg
import ru.netology.diploma.viewmodel.AuthViewModel

@AndroidEntryPoint
class AppActivity : AppCompatActivity(R.layout.activity_app) {

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent?.let {
            if (it.action != Intent.ACTION_SEND) {
                return@let
            }

            val text = it.getStringExtra(Intent.EXTRA_TEXT)
            if (text?.isNotBlank() != true) {
                return@let
            }

            intent.removeExtra(Intent.EXTRA_TEXT)
            findNavController(R.id.nav_host_fragment)
                .navigate(
                    R.id.action_postsFeedFragment_to_newPostFragment,
                    Bundle().apply {
                        textArg = text
                    }
                )

            viewModel.data.observe(this) {
                invalidateOptionsMenu()
            }

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
                    .navigate(R.id.action_postsFeedFragment_to_authFragment)
                true
            }
            R.id.sign_up -> {
                findNavController(R.id.nav_host_fragment)
                    .navigate(R.id.action_postsFeedFragment_to_registrationFragment)
                true
            }
            R.id.log_out -> {
                findNavController(R.id.nav_host_fragment)
                    .navigate(R.id.action_postsFeedFragment_to_myDialogFragment)
                true
            }
            else -> false
        }

}