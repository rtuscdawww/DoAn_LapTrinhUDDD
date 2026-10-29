package com.example.vlufind

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.vlufind.ui.screens.LoginFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Trỏ tới file layout XML của Activity chính
        setContentView(R.layout.activity_main)

        // Gọi HomeFragment hiển thị lên màn hình đầu tiên
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LoginFragment())
                .commit()
        }
    }
}