package dev.team08.movieverse.ui.welcome

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dev.team08.movieverse.databinding.ActivityWelcomeBinding
import dev.team08.movieverse.ui.dashboard.DashboardActivity
import dev.team08.movieverse.ui.login.LoginActivity
import dev.team08.movieverse.utils.AuthManager

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check for existing auth token before setting up UI
        if (AuthManager.hasValidToken(this)) {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
            return
        }

        // If no token, proceed with welcome screen setup
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hide the action bar for full screen experience
        supportActionBar?.hide()

        // Set up click listener for the get started button
        binding.getStartedButton.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java).apply {
                putExtra("EXTRA_FROM_WELCOME", true)
            }
            startActivity(intent)
            finish()
        }


        binding.loginRegisterButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
