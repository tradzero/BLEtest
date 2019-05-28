package com.example.bletest

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.NotificationCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.snail.easyble.callback.ScanListener
import com.snail.easyble.core.Ble
import com.snail.easyble.core.Device
import com.snail.easyble.core.ScanConfig


class MainActivity : AppCompatActivity(), View.OnClickListener  {

    private lateinit var textMessage: TextView
    private lateinit var hitButton : Button
    private lateinit var startButton : Button
    private lateinit var endButton : Button

    private lateinit var notificationManager : NotificationManager
    private lateinit var mRunnable: Runnable;
    private lateinit var mHandler: Handler
    private var runnableFlag: Boolean = false;



    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                textMessage.setText(R.string.title_home)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                textMessage.setText(R.string.title_dashboard)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                textMessage.setText(R.string.title_notifications)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.button -> {
                Ble.instance.startScan()
            }
            R.id.startBtn -> {
                if (! runnableFlag) {
                    mRunnable = Runnable {
                        updateNotice((0..100).random().toString());
                        mHandler.postDelayed(mRunnable, 1000);
                    }
                    mHandler.postDelayed(mRunnable, 1000);
                }
                runnableFlag = true;
            }
            R.id.endBtn -> {
                mHandler.removeCallbacks(mRunnable);
                runnableFlag = false;
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews();
        initBLE();
        startNotice();
    }

    private fun initViews()
    {
        val navView: BottomNavigationView = findViewById(R.id.nav_view)


        textMessage = findViewById(R.id.message)
        hitButton = findViewById(R.id.button)
        startButton = findViewById(R.id.startBtn)
        endButton = findViewById(R.id.endBtn)

        hitButton.setOnClickListener(this)
        startButton.setOnClickListener(this)
        endButton.setOnClickListener(this)

        mHandler = Handler();
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
    }

    private fun initBLE()
    {
        Ble.instance.addScanListener(scanListener)
        val scanConfig = ScanConfig().setAcceptSysConnectedDevice(false)
            .setHideNonBleDevice(true)
            .setUseBluetoothLeScanner(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            scanConfig.setScanSettings(ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build())
        }
        Ble.instance.bleConfig.setScanConfig(scanConfig)
    }

    private fun startNotice()
    {
        val channelId = "channel test";
        val channelName = "BLE notification channel";
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW);

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(channel);
        updateNotice("123");
    }

    private fun updateNotice(rate: String?)
    {
        Log.d("test", "in");
        val notificationText = "当前心率： " + rate;
        val notification =
            NotificationCompat.Builder(this, "channel test").setSmallIcon(R.drawable.notification_template_icon_bg)
                .setContentTitle("Title")
                .setContentText(notificationText)
                .build();
        notification.flags = notification.flags or Notification.FLAG_NO_CLEAR
        notificationManager.notify(1, notification);
    }

    override fun onDestroy() {
        super.onDestroy()
        notificationManager.cancelAll();
    }

    private val scanListener = object : ScanListener {
        override fun onScanStart() {
            Log.d("test", "start");
//            if (!refreshLayout.isRefreshing) {
//                refreshLayout.isRefreshing = true
//            }
        }

        override fun onScanStop() {
            Log.d("test", "end");
//            refreshLayout.isRefreshing = false
        }

        override fun onScanResult(device: Device) {
            Log.d("test", device.name);
//            refreshLayout.isRefreshing = false
//            layoutEmpty.visibility = View.INVISIBLE
//            listAdapter?.add(device)
        }

        override fun onScanError(errorCode: Int, errorMsg: String) {

        }
    }

}
