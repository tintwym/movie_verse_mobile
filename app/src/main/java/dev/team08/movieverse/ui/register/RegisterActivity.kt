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
import dev.team08.movieverse.ui.login.LoginActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupTermsAndConditions()
        setupNextButton()

        binding.goToLoginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish() // This will close the current activity
        }
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
        val username = binding.nameInput.editText?.text.toString().trim()
        val email = binding.emailInput.editText?.text.toString().trim()
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
        val name = binding.nameInput.editText?.text.toString().trim()
        when {
            name.isEmpty() -> {
                binding.nameInput.error = "Name is required"
                isValid = false
            }
            !isValidName(name) -> {
                binding.nameInput.error = "Name must be at least 5 characters with no numbers or special characters"
                isValid = false
            }
            else -> {
                binding.nameInput.error = null
            }
        }

        // Validate email
        val email = binding.emailInput.editText?.text.toString().trim()
        when {
            email.isEmpty() -> {
                binding.emailInput.error = "Email is required"
                isValid = false
            }
            !isValidEmail(email) -> {
                binding.emailInput.error = "Please enter a valid email address"
                isValid = false
            }
            else -> {
                binding.emailInput.error = null
            }
        }

        // Validate password
        val password = binding.passwordInput.editText?.text.toString()
        val confirmPassword = binding.confirmPasswordInput.editText?.text.toString()

        when {
            password.isEmpty() -> {
                binding.passwordInput.error = "Password is required"
                isValid = false
            }
            !isValidPassword(password) -> {
                binding.passwordInput.error = "Password must be at least 6 characters and contain 1 uppercase letter, 1 lowercase letter, 1 number, and 1 special character"
                isValid = false
            }
            else -> {
                binding.passwordInput.error = null
            }
        }

        // Validate confirm password
        when {
            confirmPassword.isEmpty() -> {
                binding.confirmPasswordInput.error = "Confirm password is required"
                isValid = false
            }
            password != confirmPassword -> {
                binding.confirmPasswordInput.error = "Passwords don't match"
                isValid = false
            }
            else -> {
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
        return name.length >= 5 && name.matches(Regex("^[a-zA-Z ]*$"))
    }

    private fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        val passwordRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=~`\"?><])[A-Za-z\\d!@#$%^&*()_+\\-=~`\"?><]{6,}$"
        return password.matches(passwordRegex.toRegex())
    }
}
