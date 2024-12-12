package com.example.budgee.model
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.budgee.json.TransactionResponse
import com.example.budgee.json.User
import java.util.*

class HomeViewModel : ViewModel() {
    private val _currentBalance = MutableLiveData<Double>()
    val currentBalance: LiveData<Double> = _currentBalance

    private val _incomeTransactions = MutableLiveData<List<TransactionResponse>>()
    val incomeTransactions: LiveData<List<TransactionResponse>> = _incomeTransactions

    private val _outcomeTransactions = MutableLiveData<List<TransactionResponse>>()
    val outcomeTransactions: LiveData<List<TransactionResponse>> = _outcomeTransactions

    private val _userData = MutableLiveData<User>()
    val userData: LiveData<User> = _userData

    init {
        _incomeTransactions.value = listOf()
        _outcomeTransactions.value = listOf()
        _currentBalance.value = 0.0
    }

    fun addNewTransaction(transaction: TransactionResponse) {
        when (transaction.type?.lowercase(Locale.getDefault()) ?: "") {
            "income" -> {
                val currentList = _incomeTransactions.value?.toMutableList() ?: mutableListOf()
                currentList.add(transaction)
                _incomeTransactions.value = currentList
                android.util.Log.d("HomeViewModel", "Added income transaction: ${transaction.amount}")
            }
            "expense" -> {
                val currentList = _outcomeTransactions.value?.toMutableList() ?: mutableListOf()
                currentList.add(transaction)
                _outcomeTransactions.value = currentList
                android.util.Log.d("HomeViewModel", "Added expense transaction: ${transaction.amount}")
            }
            else -> {
                android.util.Log.e("HomeViewModel", "Unknown transaction type: ${transaction.type}")
                return
            }
        }
        updateBalance()
    }

    fun setIncomeTransactions(transactions: List<TransactionResponse>) {
        _incomeTransactions.value = transactions
        android.util.Log.d("HomeViewModel", "Set income transactions: ${transactions.size} items")
        updateBalance()
    }

    fun setOutcomeTransactions(transactions: List<TransactionResponse>) {
        _outcomeTransactions.value = transactions
        android.util.Log.d("HomeViewModel", "Set outcome transactions: ${transactions.size} items")
        updateBalance()
    }

    fun setUserData(user: User) {
        _userData.value = user
    }

    private fun updateBalance() {
        val totalIncome = _incomeTransactions.value?.filter { it.type == "income" }?.sumOf { it.amount } ?: 0.0
        val totalOutcome = _outcomeTransactions.value?.filter { it.type == "expense" }?.sumOf { it.amount } ?: 0.0
        _currentBalance.value = totalIncome - totalOutcome
        
        android.util.Log.d("HomeViewModel", """
            Balance Updated:
            Total Income: $totalIncome
            Total Outcome: $totalOutcome
            New Balance: ${_currentBalance.value}
        """.trimIndent())
    }

    fun getCurrentState() {
        updateBalance()
    }

    fun initializeFromSharedPreferences(context: Context) {
        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val userName = sharedPreferences.getString("user_name", null)
        val userEmail = sharedPreferences.getString("user_email", null)
        
        if (!userName.isNullOrEmpty()) {
            _userData.value = User(
                id = "", // ID tidak perlu disimpan di SharedPreferences
                name = userName,
                email = userEmail ?: ""
            )
        }
    }
}
