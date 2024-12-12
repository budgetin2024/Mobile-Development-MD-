package com.example.budgee.model
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.budgee.json.TransactionResponse

class HomeViewModel : ViewModel() {
    private val _currentBalance = MutableLiveData<Double>()
    val currentBalance: LiveData<Double> = _currentBalance

    private val _incomeTransactions = MutableLiveData<MutableList<TransactionResponse>>()
    val incomeTransactions: LiveData<MutableList<TransactionResponse>> = _incomeTransactions

    private val _outcomeTransactions = MutableLiveData<MutableList<TransactionResponse>>()
    val outcomeTransactions: LiveData<MutableList<TransactionResponse>> = _outcomeTransactions

    init {
        _incomeTransactions.value = mutableListOf()
        _outcomeTransactions.value = mutableListOf()
        _currentBalance.value = 0.0
    }

    fun updateBalance() {
        val incomeTotal = _incomeTransactions.value?.sumOf { it.amount } ?: 0.0
        val outcomeTotal = _outcomeTransactions.value?.sumOf { it.amount } ?: 0.0
        
        android.util.Log.d("HomeViewModel", """
            Income Transactions Detail:
            ${_incomeTransactions.value?.joinToString("\n") { 
                "- Amount: ${it.amount}, Type: ${it.type}, Category: ${it.category}"
            }}
            
            Outcome Transactions Detail:
            ${_outcomeTransactions.value?.joinToString("\n") { 
                "- Amount: ${it.amount}, Type: ${it.type}, Category: ${it.category}"
            }}
            
            Calculating balance:
            Income Total: $incomeTotal
            Outcome Total: $outcomeTotal
            New Balance: ${incomeTotal - outcomeTotal}
        """.trimIndent())
        
        _currentBalance.value = incomeTotal - outcomeTotal
    }

    fun setIncomeTransactions(transactions: List<TransactionResponse>) {
        val incomeOnly = transactions.filter { it.type.lowercase() == "income" }
        android.util.Log.d("HomeViewModel", "Setting income transactions: ${incomeOnly.size}")
        _incomeTransactions.value = incomeOnly.toMutableList()
        updateBalance()
    }

    fun setOutcomeTransactions(transactions: List<TransactionResponse>) {
        val outcomeOnly = transactions.filter { 
            it.type.lowercase() in listOf("outcome", "expense") 
        }
        android.util.Log.d("HomeViewModel", "Setting outcome transactions: ${outcomeOnly.size}")
        _outcomeTransactions.value = outcomeOnly.toMutableList()
        updateBalance()
    }

    fun addNewTransaction(transaction: TransactionResponse) {
        android.util.Log.d("HomeViewModel", """
            Adding new transaction:
            Type: ${transaction.type}
            Amount: ${transaction.amount}
            Category: ${transaction.category}
        """.trimIndent())

        when (transaction.type.lowercase()) {
            "income" -> {
                val currentList = _incomeTransactions.value ?: mutableListOf()
                currentList.add(transaction)
                _incomeTransactions.value = currentList
                android.util.Log.d("HomeViewModel", "Income transactions now: ${currentList.size}")
            }
            "expense", "outcome" -> {
                val currentList = _outcomeTransactions.value ?: mutableListOf()
                currentList.add(transaction)
                _outcomeTransactions.value = currentList
                android.util.Log.d("HomeViewModel", "Outcome transactions now: ${currentList.size}")
            }
            else -> {
                android.util.Log.e("HomeViewModel", "Unknown transaction type: ${transaction.type}")
                return
            }
        }
        updateBalance()
    }

    private fun verifyTransactionType(transaction: TransactionResponse): Boolean {
        return when (transaction.type.lowercase()) {
            "income" -> true
            "outcome", "expense" -> true
            else -> false
        }
    }

    fun getTransactionLists(): String {
        return """
            Income Transactions (${_incomeTransactions.value?.size ?: 0}):
            ${_incomeTransactions.value?.joinToString("\n") { "- ${it.amount} (${it.type})" }}
            
            Outcome Transactions (${_outcomeTransactions.value?.size ?: 0}):
            ${_outcomeTransactions.value?.joinToString("\n") { "- ${it.amount} (${it.type})" }}
            
            Current Balance: ${_currentBalance.value}
        """.trimIndent()
    }

    fun getCurrentState() {
        android.util.Log.d("HomeViewModel", getTransactionLists())
    }
}
