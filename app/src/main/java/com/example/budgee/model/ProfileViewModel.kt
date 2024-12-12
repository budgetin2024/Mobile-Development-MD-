package com.example.budgee.model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.budgee.json.User

class ProfileViewModel : ViewModel() {
    private val _userData = MutableLiveData<User>()
    val userData: LiveData<User> = _userData

    fun setUserData(user: User) {
        _userData.value = user
    }

    fun initializeFromSharedPreferences(context: Context) {
        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val userName = sharedPreferences.getString("user_name", null)
        val userEmail = sharedPreferences.getString("user_email", null)

        if (!userName.isNullOrEmpty()) {
            _userData.value = User(
                id = "",
                name = userName,
                email = userEmail ?: ""
            )
        }
    }
}