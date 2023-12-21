package com.example.lesson20

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.transition.Visibility
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var _broadCastReceiver: BroadcastReceiver
    private val noConnectTextView: TextView by lazy { findViewById(R.id.NoConnectTextView) }

    inner  class LoadImages(private val images: List<String>): BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            if (p1?.action == ConnectivityManager.CONNECTIVITY_ACTION && p0 != null) {
                if (isNetworkActive()) {
                    noConnectTextView.visibility = View.GONE
                    showImages(p0)
                } else {
                    noConnectTextView.visibility = View.VISIBLE
                    hideImages(p0)
                }
            }
        }

        private fun isNetworkActive(): Boolean {
                val connMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

                val activeInfo: NetworkInfo? = connMgr.activeNetworkInfo
                val isActive =  (activeInfo?.isConnected == true)
                return isActive
        }

        private fun showImages(p0: Context?) {
            p0?.let {
                images.forEachIndexed { index, url ->
                    val img = findViewById<ImageView>(
                        resources.getIdentifier(
                            "image${index + 1}",
                            "id",
                            packageName
                        )
                    )
                    img.visibility = View.VISIBLE
                    Glide.with(p0).load(url).into(img)
                }
            }
        }

        private fun hideImages(p0: Context?) {
            p0?.let {
                clearGlideBuffer()
                images.forEachIndexed { index, url ->
                    val img = findViewById<ImageView>(
                        resources.getIdentifier(
                            "image${index + 1}",
                            "id",
                            packageName
                        )
                    )
                    img.visibility = View.INVISIBLE
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        clearGlideBuffer()
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        _broadCastReceiver = LoadImages(listOf(
            "https://cdn-icons-png.flaticon.com/512/197/197572.png",
            "https://cdn-icons-png.flaticon.com/512/197/197571.png",
            "https://cdn-icons-png.flaticon.com/512/197/197484.png",
            "https://cdn-icons-png.flaticon.com/512/197/197579.png"
        ))
        registerReceiver(_broadCastReceiver, filter)
    }

    override fun onDestroy() {
        unregisterReceiver(_broadCastReceiver)
        super.onDestroy()
    }

    private fun clearGlideBuffer() {
        GlobalScope.launch(Dispatchers.IO) {
            Glide.get(applicationContext).clearDiskCache()
        }
    }
}