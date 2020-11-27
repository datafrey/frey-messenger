package com.datafrey.freymessenger.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.datafrey.freymessenger.startActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (FirebaseAuth.getInstance().currentUser != null) {
            startActivity<MainActivity>()
        } else {
            startActivity<SignInActivity>()
        }

        finish()
    }
}