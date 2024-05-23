package com.interspeed.company_overlay_app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.annotation.NonNull
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.android.FlutterView
import io.flutter.embedding.android.FlutterSurfaceView
import android.graphics.PixelFormat

class MainActivity : FlutterActivity() {
    private val CHANNEL = "com.example.companyoverlay/overlay"
    private var windowManager: WindowManager? = null
    private var flutterView: FlutterView? = null

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            if (call.method == "requestOverlayPermission") {
                requestOverlayPermission()
                result.success(null)
            } else {
                result.notImplemented()
            }
        }
    }

    private fun requestOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivityForResult(intent, 1234)
        } else {
            showOverlay()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1234) {
            if (Settings.canDrawOverlays(this)) {
                showOverlay()
            } else {
                // Permission not granted, handle the case
            }
        }
    }

    private fun showOverlay() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        val flutterEngine = FlutterEngine(this)
        flutterEngine.dartExecutor.executeDartEntrypoint(
            DartExecutor.DartEntrypoint.createDefault()
        )
        FlutterEngineCache.getInstance().put("my_engine_id", flutterEngine)

        val flutterSurfaceView = FlutterSurfaceView(this)
        flutterView = FlutterView(this, flutterSurfaceView)
        flutterView!!.attachToFlutterEngine(flutterEngine)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.CENTER
        windowManager!!.addView(flutterView, params)

        Handler().postDelayed({
            if (flutterView != null) windowManager!!.removeView(flutterView)
            finish()
        }, 3000) // 3 seconds delay
    }
}
