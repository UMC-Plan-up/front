package com.planup.planup.signup.data

import android.content.Context

object SignUpDraftStore {
    private const val PREF = "signup_draft"
    private const val K_EMAIL = "email"
    private const val K_PW = "pw"

    private fun prefs(ctx: Context) = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)

    fun saveEmail(ctx: Context, email: String?) {
        prefs(ctx).edit().putString(K_EMAIL, email).apply()
    }

    fun savePw(ctx: Context, pw: String?) {
        prefs(ctx).edit().putString(K_PW, pw).apply()
    }

    fun loadEmail(ctx: Context): String? = prefs(ctx).getString(K_EMAIL, null)
    fun loadPw(ctx: Context): String? = prefs(ctx).getString(K_PW, null)
    fun clear(ctx: Context) { prefs(ctx).edit().clear().apply() }
}
