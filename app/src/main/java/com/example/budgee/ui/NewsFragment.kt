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
import com.example.budgee.json.Articles
import com.example.budgee.json.NewsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import okhttp3.Interceptor

class NewsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var noArticlesTextView: TextView
    private var articles = mutableListOf<Articles>()

    private val BASE_URL = "https://backend-budgetin.et.r.appspot.com/"

    // Buat interface untuk API
    private interface NewsApiService {
        @GET("news")
        fun getNews(): Call<NewsResponse>
    }

    private fun setupRetrofit(): NewsApiService {
        val token = getToken()
        if (token == null) {
            Toast.makeText(context, "Token tidak ditemukan", Toast.LENGTH_SHORT).show()
            throw IllegalStateException("Token tidak ditemukan")
        }

        // Debug log
        Log.d("NewsFragment", "Setting up Retrofit with token: ${token.take(20)}...")

        // Buat interceptor untuk autentikasi
        val authInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val requestBuilder = originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
            
            val request = requestBuilder.build()
            Log.d("NewsFragment", "Request URL: ${request.url}")
            Log.d("NewsFragment", "Request Headers: ${request.headers}")
            
            chain.proceed(request)
        }

        // Setup OkHttpClient dengan interceptor
        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

        // Buat Retrofit instance
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsApiService::class.java)
    }

    private fun fetchNews() {
        try {
            val newsApi = setupRetrofit()
            
            newsApi.getNews().enqueue(object : Callback<NewsResponse> {
                override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                    if (!isAdded) return

                    Log.d("NewsFragment", "Response Code: ${response.code()}")
                    Log.d("NewsFragment", "Response Headers: ${response.headers()}")

                    activity?.runOnUiThread {
                        when (response.code()) {
                            200 -> handleSuccessResponse(response.body())
                            401, 403 -> handleAuthError()
                            500 -> handleServerError(response.errorBody()?.string())
                            else -> handleOtherError(response)
                        }
                    }
                }

                override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                    if (!isAdded) return
                    handleNetworkError(t)
                }
            })
        } catch (e: Exception) {
            Log.e("NewsFragment", "Error setting up network call: ${e.message}")
            handleSetupError(e)
        }
    }

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

    private fun handleAuthError() {
        activity?.runOnUiThread {
            Toast.makeText(context, "Sesi telah berakhir. Silakan login kembali.", Toast.LENGTH_SHORT).show()
            context?.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                ?.edit()?.remove("auth_token")?.apply()
            (activity as? MainActivity)?.replaceFragmentInActivity(LoginFragment())
        }
    }

    private fun handleServerError(errorBody: String?) {
        Log.e("NewsFragment", "Server Error: $errorBody")
        activity?.runOnUiThread {
            Toast.makeText(context, "Terjadi kesalahan server", Toast.LENGTH_SHORT).show()
            recyclerView.visibility = View.GONE
            noArticlesTextView.visibility = View.VISIBLE
            noArticlesTextView.text = "Gagal memuat berita: Server Error"
        }
    }

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

    private fun handleNetworkError(t: Throwable) {
        Log.e("NewsFragment", "Network error: ${t.message}")
        activity?.runOnUiThread {
            Toast.makeText(context, "Gagal terhubung ke server", Toast.LENGTH_SHORT).show()
            recyclerView.visibility = View.GONE
            noArticlesTextView.visibility = View.VISIBLE
            noArticlesTextView.text = "Gagal memuat berita: Tidak ada koneksi"
        }
    }

    private fun handleSetupError(e: Exception) {
        activity?.runOnUiThread {
            Toast.makeText(context, "Gagal mempersiapkan koneksi: ${e.message}", Toast.LENGTH_SHORT).show()
            recyclerView.visibility = View.GONE
            noArticlesTextView.visibility = View.VISIBLE
            noArticlesTextView.text = "Gagal memuat berita: Error setup"
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

    private fun getToken(): String? {
        val sharedPreferences = requireActivity().getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("auth_token", null)
        Log.d("NewsFragment", "Raw token from SharedPreferences: $token")
        return token
    }

    override fun onResume() {
        super.onResume()
        fetchNews() // Refresh data saat fragment di-resume
    }
}
