package com.example.memeapp

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.memeapp.databinding.ActivityMainBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(),SwipeRefreshLayout.OnRefreshListener,MemeAdapter.OnItemClickListener {
    private lateinit var mAdapterData:MemeAdapter
    private lateinit var recyclerView:RecyclerView
    private lateinit var mSwipetoRefreshLayout: SwipeRefreshLayout
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(findViewById(R.id.my_toolbar))
        recyclerView=binding.recyclerView
        val linearLayoutManager=LinearLayoutManager(this)
        linearLayoutManager.orientation=LinearLayoutManager.VERTICAL
        recyclerView.layoutManager=linearLayoutManager
        mSwipetoRefreshLayout=binding.swipeRefreshLayout
        mAdapterData= MemeAdapter(this)
        recyclerView.adapter=mAdapterData
        getData()
        mSwipetoRefreshLayout.setOnRefreshListener {
            getData()
            mSwipetoRefreshLayout.isRefreshing=false
            Toast.makeText(this,getString(R.string.please_wait),Toast.LENGTH_LONG).show()
        }
        val connectivityManager:ConnectivityManager= getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo=connectivityManager.activeNetwork
        if(networkInfo==null){
            val constraintLayout=binding.constraintLayout
            Snackbar.make(constraintLayout,"This app requires internet to show the memes",Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.enable_internet)){
                    val intent = Intent(Intent.ACTION_MAIN)
                    intent.setClassName("com.android.phone", "com.android.phone.NetworkSetting")
                    startActivity(intent)
                }
                .show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when(item.itemId){
            R.id.share_button->{
                val newIntent=Intent().apply {
                    action=Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT,getString(R.string.share_app_message))
                    type="text/plain"
                }
                val titleChooser=resources.getString(R.string.chooser)
                val chooser=Intent.createChooser(newIntent,titleChooser)
                try {
                    startActivity(chooser)
                }catch (e:Exception){
                    Toast.makeText(this,R.string.warning,Toast.LENGTH_LONG).show()
                }
                true
            }
            R.id.about_button->{
                val newIntent=Intent(this,AboutActivity::class.java)
                startActivity(newIntent)
                true
            }
            else->super.onOptionsItemSelected(item)
        }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater:MenuInflater=menuInflater
        inflater.inflate(R.menu.menu_item,menu)
        return true
    }

    private fun getData(){
        val data=ArrayList<MemeData>()
        val url="https://meme-api.herokuapp.com/gimme/50"
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener { response ->
                try {
                    val memes=response.getJSONArray("memes")
                    for (i in 0 until memes.length()){
                        val jsonObejct=memes.getJSONObject(i)
                        val postLink=jsonObejct.getString("postLink")
                        val memeUrl=jsonObejct.getString("url")
                        val author=jsonObejct.getString("author")
                        val subReddit=jsonObejct.getString("subreddit")
                        data.add(MemeData(memeUrl,author,subReddit,postLink))
                    }
                    mAdapterData.updateData(data)
                }catch (e:Exception){
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                error.printStackTrace()
            }
        )
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }

    override fun onRefresh() {
        getData()
        mSwipetoRefreshLayout.isRefreshing=false
    }

    override fun onItemClick(item: MemeData) {
        val builder:CustomTabsIntent.Builder=CustomTabsIntent.Builder()
        val customTabsIntent=builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(item.getmPostLink()))
    }

    override fun shareMeme(str: String) {
        val newIntent=Intent().apply {
            action=Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT,getString(R.string.meme_sharing_message).plus(str))
            type="text/plain"
        }
        val titleChooser=getString(R.string.chooser)
        val chooser=Intent.createChooser(newIntent,titleChooser)
        try {
            startActivity(chooser)
        }catch (e:Exception){
            Toast.makeText(this,R.string.warning,Toast.LENGTH_LONG).show()
        }
    }
}

