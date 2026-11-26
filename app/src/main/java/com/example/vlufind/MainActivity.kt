package com.example.vlufind

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.vlufind.data.MockData
import com.example.vlufind.ui.screens.HomeFragment
import com.example.vlufind.ui.screens.LoginFragment
import com.example.vlufind.ui.screens.MessageFragment
import com.example.vlufind.ui.screens.ProfileFragment
import com.example.vlufind.ui.screens.FavoriteFragment
import com.example.vlufind.ui.screens.PostItemFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        MockData.loadPosts(this)

        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(
            R.id.bottom_navigation
        )

        bottomNav.setOnItemSelectedListener { item ->

            when (item.itemId) {

                R.id.nav_home -> {
                    replaceFragment(HomeFragment())
                    true
                }

                R.id.nav_favorite -> {
                    replaceFragment(FavoriteFragment())
                    true
                }

                R.id.nav_create_post -> {
                    replaceFragment(PostItemFragment())
                    true
                }

                R.id.nav_messages -> {
                    replaceFragment(MessageFragment())
                    true
                }

                R.id.nav_profile -> {
                    replaceFragment(ProfileFragment())
                    true
                }

                else -> false
            }
        }

        if (savedInstanceState == null) {
            replaceFragment(LoginFragment())
        }
    }

    private fun replaceFragment(fragment: Fragment) {

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}