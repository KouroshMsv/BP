package com.parvanpajooh.baseapp.ui.main

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore.Images
import android.view.PixelCopy
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.parvanpajooh.baseapp.infrastructure.BaseActivity
import com.parvanpajooh.baseapp.models.eventbus.TitleEvent
import com.parvanpajooh.baseapp.ui.CheckTimeDialog
import com.parvanpajooh.baseapp.utils.PermissionRequest
import com.parvanpajooh.basedomain.utils.sharedpreferences.BasePrefKey
import com.parvanpajooh.basedomain.utils.sharedpreferences.PrefHelper
import com.parvanpajooh.basedomain.utils.username
import dev.kourosh.baseapp.*
import kotlinx.coroutines.delay
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import saman.zamani.persiandate.PersianDate
import saman.zamani.persiandate.PersianDateFormat
import java.io.File
import java.io.FileOutputStream
import java.util.*


abstract class BaseMainActivity(
    @LayoutRes private val layoutId: Int,
    @IdRes private val toolbarId: Int,
    @IdRes private val drawerLayoutId: Int,
    @IdRes private val navHostId: Int,
    @IdRes private val rootId: Int,
    @IdRes private val txtScreenShotDetailsId: Int,
    requiredPermissions: List<PermissionRequest>
) : BaseActivity(layoutId, requiredPermissions) {
    lateinit var navController: NavController
    var destId = 0
    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var txtScreenShotDetails: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbar = findViewById(toolbarId)
        drawerLayout = findViewById(drawerLayoutId)
        txtScreenShotDetails = findViewById(txtScreenShotDetailsId)
        txtScreenShotDetails.gone()
        initNavController()
        PrefHelper.put(BasePrefKey.FIRST_RUNNING.name, false)
    }


    fun takeScreenshot() {
        txtScreenShotDetails.visible()
        txtScreenShotDetails.text = buildString {
            append("نسخه: ")
            append(packageManager.getPackageInfo(applicationContext.packageName, 0).versionName)
            append(" -- ")
            append("نام کاربری: ")
            append(username)
            append("\n")
            append(PersianDateFormat("l d/m/Y H:i").format(PersianDate(System.currentTimeMillis())))
            append("\n")
            append("${Build.MANUFACTURER.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ENGLISH) else it.toString() }} ${Build.MODEL}")
        }
        launchIO {
            delay(150)
            onMain {
                try {
                    val bitmap=getBitmapFromView(findViewById(rootId))
                    bitmap?.apply {
                        shareImage(this)
                    }
                } catch (e: Throwable) {
                    e.printStackTrace()
                    Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_LONG).show()
                } finally {
                    txtScreenShotDetails.gone()
                }
            }
        }
    }

    private fun shareImage(bitmap: Bitmap) {
        try {
            val file = File(this.externalCacheDir, "screenshot.png")
            val fOut = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut)
            fOut.flush()
            fOut.close()
            file.setReadable(true, false)
            val intent = Intent(Intent.ACTION_SEND)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file))
            intent.type = "image/png"
            startActivity(Intent.createChooser(intent, "Share image via"))
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun getBitmapFromView(view: View): Bitmap? {
        val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        val bgDrawable = view.background
        if (bgDrawable != null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas)
        } else {
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return returnedBitmap
    }

    private fun initNavController() {
        setSupportActionBar(toolbar)
        navController = findNavController(navHostId)
        toolbar.setOnLongClickListener {
            takeScreenshot()
            true
        }
        NavigationUI.setupWithNavController(
            toolbar,
            navController,
            drawerLayout
        )
        navController.addOnDestinationChangedListener { _, destination, _ ->
            this.destId = destination.id
            hideKeyboard(this)
        }
    }

    val navHostFragment by lazy { supportFragmentManager.findFragmentById(navHostId)!! }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        navHostFragment.onActivityResult(
            requestCode,
            resultCode,
            data
        )
        super.onActivityResult(requestCode, resultCode, data)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: TitleEvent) {
        toolbar.title = event.title
    }


    fun closeDrawerLayout() {
        drawerLayout.closeDrawer(GravityCompat.START, true)
    }

    override fun onBackPressed() {
        when (navHostFragment.childFragmentManager.backStackEntryCount) {
            0 -> super.onBackPressed()
            else -> {
                navController.navigateUp()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (!Helpers.isTimeAutomatic(this)) {
            if (!CheckTimeDialog.visible)
                CheckTimeDialog.newInstance().show(supportFragmentManager)
        } else {
            try {
                if (CheckTimeDialog.instance != null) {
                    CheckTimeDialog.newInstance().dismiss()
                }
            } catch (e: Exception) {

            }
        }
    }
}