package com.example.budgee.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budgee.R
import com.example.budgee.adapter.NewsAdapter
import com.example.budgee.json.Articles
import com.example.budgee.json.NewsResponse
import com.example.budgee.json.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var noArticlesTextView: TextView
    private var articles = mutableListOf<Articles>()

    // Ambil token autentikasi dari SharedPreferences atau cara lain
    private val authToken = "Bearer YOUR_ACCESS_TOKEN" // Ganti dengan token yang valid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_news, container, false)

        recyclerView = view.findViewById(R.id.recycler_view_news)
        recyclerView.layoutManager = LinearLayoutManager(context)

        noArticlesTextView = view.findViewById(R.id.text_no_articles)

        newsAdapter = NewsAdapter(articles)
        recyclerView.adapter = newsAdapter

        // Ambil data berita
        fetchNews()

        return view
    }

    private fun fetchNews() {
        RetrofitInstance.api.getNews(authHeader = authToken).enqueue(object : Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                if (response.isSuccessful) {
                    val newsResponse = response.body()
                    Log.d("NewsFragment", "Response: ${newsResponse?.articles}")

                    if (newsResponse != null && newsResponse.articles.isNotEmpty()) {
                        articles.clear()
                        articles.addAll(newsResponse.articles)
                        newsAdapter.notifyDataSetChanged()
                        noArticlesTextView.visibility = View.GONE // Sembunyikan pesan "No Articles"
                    } else {
                        noArticlesTextView.visibility = View.VISIBLE // Tampilkan pesan jika tidak ada artikel
                        Log.d("NewsFragment", "No articles available")
                    }
                } else {
                    // Log respons error lebih detail
                    Log.e("NewsFragment", "Error: ${response.code()} - ${response.message()}")
                    Log.e("NewsFragment", "Error Body: ${response.errorBody()?.string()}")

                    // Tampilkan pesan error
                    Toast.makeText(requireContext(), "Error: ${response.code()} - ${response.message()}", Toast.LENGTH_SHORT).show()
                    noArticlesTextView.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                Log.e("NewsFragment", "Error: ${t.message}")
                Toast.makeText(requireContext(), "Failed to load news", Toast.LENGTH_SHORT).show()
                noArticlesTextView.visibility = View.VISIBLE
            }
        })
    }

}
