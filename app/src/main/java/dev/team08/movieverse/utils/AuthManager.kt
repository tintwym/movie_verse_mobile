package dev.team08.movieverse.utils

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object AuthManager {
    private const val PREF_NAME = "secure_prefs"
    private const val KEY_AUTH_TOKEN = "auth_token"

    fun getAuthToken(context: Context): String? {
        return try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            val sharedPreferences = EncryptedSharedPreferences.create(
                context,
                PREF_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

            sharedPreferences.getString(KEY_AUTH_TOKEN, null)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun saveAuthToken(context: Context, token: String) {
        try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            val sharedPreferences = EncryptedSharedPreferences.create(
                context,
                PREF_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

            sharedPreferences.edit().putString(KEY_AUTH_TOKEN, token).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun clearAuthToken(context: Context) {
        try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            val sharedPreferences = EncryptedSharedPreferences.create(
                context,
                PREF_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

            sharedPreferences.edit().remove(KEY_AUTH_TOKEN).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun hasValidToken(context: Context): Boolean {
        return !getAuthToken(context).isNullOrEmpty()
    }
}
