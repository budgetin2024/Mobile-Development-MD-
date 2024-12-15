package com.example.budgee.ui

import android.content.Context
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
import com.example.budgee.MainActivity
import com.example.budgee.R
import com.example.budgee.adapter.NewsAdapter
import com.example.budgee.json.Article
import com.example.budgee.json.NewsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class NewsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var noArticlesTextView: TextView
    private var articles = mutableListOf<Article>()

    private interface NewsApiService {
        @GET("top-headlines")
        fun getNews(
            @Query("country") country: String,
            @Query("category") category: String,
            @Query("apiKey") apiKey: String
        ): Call<NewsResponse>
    }

    private fun setupRetrofit(): NewsApiService {
        val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

        return Retrofit.Builder()
            .baseUrl("https://newsapi.org/v2/") // URL NewsAPI
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsApiService::class.java)
    }

    private fun fetchNews() {
        val newsApi = setupRetrofit()
        newsApi.getNews("us", "business", "d785abb609e845ffa5828f81a90faf18")
            .enqueue(object : Callback<NewsResponse> {
                override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                    if (!isAdded) return
                    if (response.isSuccessful) {
                        val newsResponse = response.body()
                        newsResponse?.articles?.let {
                            articles.clear()
                            articles.addAll(it)
                            newsAdapter.notifyDataSetChanged()
                        }
                    } else {
                        handleOtherError(response)
                    }
                }

                override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                    handleNetworkError(t)
                }
            })
    }

    // Menangani error jaringan
    private fun handleNetworkError(t: Throwable) {
        Log.e("NewsFragment", "Network error: ${t.message}")
        activity?.runOnUiThread {
            Toast.makeText(context, "Gagal terhubung ke server", Toast.LENGTH_SHORT).show()
            recyclerView.visibility = View.GONE
            noArticlesTextView.visibility = View.VISIBLE
            noArticlesTextView.text = "Gagal memuat berita: Tidak ada koneksi"
        }
    }

    // Menangani error yang terkait dengan respons API lainnya
    private fun handleOtherError(response: Response<NewsResponse>) {
        val errorBody = response.errorBody()?.string()
        Log.e("NewsFragment", "Error: ${response.code()} - $errorBody")
        activity?.runOnUiThread {
            Toast.makeText(context, "Gagal memuat berita: ${response.code()}", Toast.LENGTH_SHORT).show()
            recyclerView.visibility = View.GONE
            noArticlesTextView.visibility = View.VISIBLE
            noArticlesTextView.text = "Gagal memuat berita"
        }
    }

    // Menangani UI ketika data berita berhasil diterima
    private fun handleSuccessResponse(newsResponse: NewsResponse?) {
        activity?.runOnUiThread {
            if (newsResponse?.articles?.isNotEmpty() == true) {
                articles.clear()
                articles.addAll(newsResponse.articles)
                newsAdapter.notifyDataSetChanged()
                recyclerView.visibility = View.VISIBLE
                noArticlesTextView.visibility = View.GONE
            } else {
                recyclerView.visibility = View.GONE
                noArticlesTextView.visibility = View.VISIBLE
                noArticlesTextView.text = "Tidak ada berita tersedia"
            }
        }
    }

    // Menangani autentikasi error
    private fun handleAuthError() {
        activity?.runOnUiThread {
            Toast.makeText(context, "Sesi telah berakhir. Silakan login kembali.", Toast.LENGTH_SHORT).show()
            context?.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                ?.edit()?.remove("auth_token")?.apply()
            (activity as? MainActivity)?.replaceFragmentInActivity(LoginFragment())
        }
    }

    // Menangani error server
    private fun handleServerError(errorBody: String?) {
        Log.e("NewsFragment", "Server Error: $errorBody")
        activity?.runOnUiThread {
            Toast.makeText(context, "Terjadi kesalahan server", Toast.LENGTH_SHORT).show()
            recyclerView.visibility = View.GONE
            noArticlesTextView.visibility = View.VISIBLE
            noArticlesTextView.text = "Gagal memuat berita: Server Error"
        }
    }

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

    override fun onResume() {
        super.onResume()
        fetchNews() // Refresh data saat fragment di-resume
    }
}
