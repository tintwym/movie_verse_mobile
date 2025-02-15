package dev.team08.movieverse.ui.register

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import dev.team08.movieverse.R
import dev.team08.movieverse.databinding.ActivityRegisterBinding
import dev.team08.movieverse.ui.genre.GenreActivity
import dev.team08.movieverse.ui.login.LoginActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val usernamePattern = "^[a-zA-Z0-9_]{5,}$".toRegex()
    private val passwordPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[~!@#\$%^&*()_+\\[\\]{}\\|\\'\":;<>?,./]).{6,}$".toRegex()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupTermsAndConditions()
        setupNextButton()
        setupGoBackToLoginButton()
    }

    private fun setupTermsAndConditions() {
        val spannableString = SpannableString(binding.termsText.text)

        val termsClick = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Handle Terms and Conditions click
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.color = ContextCompat.getColor(this@RegisterActivity, R.color.blue)
                ds.isUnderlineText = false
            }
        }

        val privacyClick = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Handle Privacy Policy click
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.color = ContextCompat.getColor(this@RegisterActivity, R.color.blue)
                ds.isUnderlineText = false
            }
        }

        val terms = "Terms and Conditions"
        val privacy = "Privacy Policy"
        val text = binding.termsText.text.toString()

        spannableString.setSpan(
            termsClick,
            text.indexOf(terms),
            text.indexOf(terms) + terms.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannableString.setSpan(
            privacyClick,
            text.indexOf(privacy),
            text.indexOf(privacy) + privacy.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.termsText.text = spannableString
        binding.termsText.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun setupNextButton() {
        binding.nextButton.setOnClickListener {
            if (validateInputs()) {
                navigateToGenreSelection()
            }
        }
    }

    private fun setupGoBackToLoginButton() {
        binding.goBackToLoginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun navigateToGenreSelection() {
        val username = binding.nameInput.editText?.text.toString()
        val email = binding.emailInput.editText?.text.toString()
        val password = binding.passwordInput.editText?.text.toString()

        Intent(this, GenreActivity::class.java).apply {
            putExtra(GenreActivity.EXTRA_USERNAME, username)
            putExtra(GenreActivity.EXTRA_EMAIL, email)
            putExtra(GenreActivity.EXTRA_PASSWORD, password)
            startActivity(this)
        }
    }

    private fun validateInputs(): Boolean {
        val username = binding.nameInput.editText?.text.toString()
        val email = binding.emailInput.editText?.text.toString()
        val password = binding.passwordInput.editText?.text.toString()
        val confirmPassword = binding.confirmPasswordInput.editText?.text.toString()

        if (!username.matches(usernamePattern)) {
            binding.nameInput.error = "Username must be at least 5 characters, no spaces, no dashes, only underscore allowed"
            return false
        } else {
            binding.nameInput.error = null
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailInput.error = "Please enter a valid email address"
            return false
        } else {
            binding.emailInput.error = null
        }

        if (!password.matches(passwordPattern)) {
            binding.passwordInput.error = "Password must be at least 6 characters, include 1 uppercase, 1 lowercase, 1 number, and 1 special character"
            return false
        } else {
            binding.passwordInput.error = null
        }

        if (password != confirmPassword) {
            binding.confirmPasswordInput.error = "Passwords do not match"
            return false
        } else {
            binding.confirmPasswordInput.error = null
        }

        if (!binding.termsCheckbox.isChecked) {
            Snackbar.make(binding.root, "Please agree to the Terms and Conditions", Snackbar.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}
