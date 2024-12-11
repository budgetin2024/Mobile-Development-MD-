package com.example.budgee.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budgee.R
import com.example.budgee.adapter.NewsAdapter
import com.example.budgee.json.Article
import com.example.budgee.json.NewsApi
import com.example.budgee.json.NewsResponse
import com.example.budgee.json.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var noArticlesTextView: TextView
    private var articles = mutableListOf<Article>() // List of articles yang benar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_news, container, false)

        // Inisialisasi RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view_news)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Inisialisasi TextView untuk pesan tidak ada artikel
        noArticlesTextView = view.findViewById(R.id.text_no_articles)

        // Inisialisasi Adapter dengan list kosong
        newsAdapter = NewsAdapter(articles)
        recyclerView.adapter = newsAdapter

        // Ambil data berita
        fetchNews()
        return view
    }

    private fun fetchNews() {
        val apiService = RetrofitInstance.newsRetrofit.create(NewsApi::class.java)
        apiService.getTopHeadlines().enqueue(object : Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                if (response.isSuccessful) {
                    val newsResponse = response.body()
                    if (newsResponse != null) {
                        Log.d(
                            "NewsFragment",
                            "Status: ${newsResponse.status}, Total Results: ${newsResponse.totalResults}"
                        )
                        Log.d("NewsFragment", "Articles size: ${newsResponse.articles?.size}")

                        newsResponse.articles?.let {
                            articles.clear() // Clear the existing list
                            articles.addAll(it) // Add new data to the list
                            newsAdapter.notifyDataSetChanged() // Notify adapter for data change

                            // Cek apakah artikel kosong
                            if (articles.isEmpty()) {
                                // Tampilkan pesan tidak ada artikel
                                recyclerView.visibility = View.GONE
                                noArticlesTextView.visibility = View.VISIBLE
                            } else {
                                // Tampilkan RecyclerView jika ada artikel
                                recyclerView.visibility = View.VISIBLE
                                noArticlesTextView.visibility = View.GONE
                            }
                        }
                    }
                } else {
                    Log.e("NewsFragment", "Failed to fetch news: ${response.code()}")
                    Toast.makeText(
                        context,
                        "Failed to fetch news: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                t.printStackTrace()
                Log.e("NewsFragment", "Error fetching news: ${t.localizedMessage}")
                Toast.makeText(
                    context,
                    "Failed to fetch news: ${t.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}
