package com.example.budgee.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budgee.R
import com.example.budgee.adapter.NewsAdapter
import com.example.budgee.json.NewsApi
import com.example.budgee.json.NewsResponse
import com.example.budgee.json.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var newsAdapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_news, container, false)
        recyclerView = view.findViewById(R.id.recycler_view_news)
        recyclerView.layoutManager = LinearLayoutManager(context)

        fetchNews()
        return view
    }

    private fun fetchNews() {
        val apiService = RetrofitInstance.retrofit.create(NewsApi::class.java)
        apiService.getTopHeadlines().enqueue(object : Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                if (response.isSuccessful) {
                    response.body()?.articles?.let {
                        newsAdapter = NewsAdapter(it)
                        recyclerView.adapter = newsAdapter
                    }
                }
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                // Tambahkan log atau toast untuk debugging
                t.printStackTrace()
                Toast.makeText(context, "Failed to fetch news", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
