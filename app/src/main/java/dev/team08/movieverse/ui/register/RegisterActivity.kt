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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import dev.team08.movieverse.R
import dev.team08.movieverse.databinding.ActivityRegisterBinding
import dev.team08.movieverse.ui.genre.GenreActivity
import com.google.android.material.snackbar.Snackbar

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupTermsAndConditions()
        setupNextButton()
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
        var isValid = true

        // Validate name
        val name = binding.nameInput.editText?.text.toString()
        if (!isValidName(name)) {
            binding.nameInput.error = "Name must be at least 2 characters with no numbers or special characters"
            isValid = false
        } else {
            binding.nameInput.error = null
        }

        // Validate email
        val email = binding.emailInput.editText?.text.toString()
        if (!isValidEmail(email)) {
            binding.emailInput.error = "Please enter a valid email address"
            isValid = false
        } else {
            binding.emailInput.error = null
        }

        // Validate password
        val password = binding.passwordInput.editText?.text.toString()
        val confirmPassword = binding.confirmPasswordInput.editText?.text.toString()

        when {
            password.length < 6 -> {
                binding.passwordInput.error = "Password must be at least 6 characters"
                binding.confirmPasswordInput.error = null
                isValid = false
            }
            password != confirmPassword -> {
                binding.confirmPasswordInput.error = "Passwords don't match"
                binding.passwordInput.error = null
                isValid = false
            }
            else -> {
                binding.passwordInput.error = null
                binding.confirmPasswordInput.error = null
            }
        }

        // Validate terms checkbox
        if (!binding.termsCheckbox.isChecked) {
            Snackbar.make(binding.root, "Please agree to the Terms and Conditions", Snackbar.LENGTH_SHORT).show()
            isValid = false
        }

        return isValid
    }

    private fun isValidName(name: String): Boolean {
        return name.length >= 2 && name.matches(Regex("^[a-zA-Z ]*$"))
    }

    private fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
