package com.parvanpajooh.baseapp.ui.main

import android.content.Intent
import android.os.Bundle
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
import dev.kourosh.baseapp.Helpers
import dev.kourosh.baseapp.hideKeyboard
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


abstract class BaseMainActivity(
    @LayoutRes private val layoutId: Int,
    @IdRes private val toolbarId: Int,
    @IdRes private val drawerLayoutId: Int,
    @IdRes private val navHostId: Int,
    neededPermissions: List<PermissionRequest>
) : BaseActivity(layoutId, neededPermissions) {
    private lateinit var navController: NavController
    private var destId = 0
    private lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbar = findViewById(toolbarId)
        drawerLayout = findViewById(drawerLayoutId)
        initNavController()
    }


    private fun initNavController() {
        setSupportActionBar(toolbar)
        navController = findNavController(navHostId)
        NavigationUI.setupWithNavController(
            toolbar,
            navController,
            drawerLayout
        )
        navController.addOnDestinationChangedListener { _, destination, _ ->

            this.destId = destination.id
            hideKeyboard(this)

            /*toolbar.setNavigationOnClickListener {

            }*/

        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        supportFragmentManager.findFragmentById(navHostId)!!.onActivityResult(
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


    private fun closeDrawerLayout() {
        drawerLayout.closeDrawer(GravityCompat.START, true)
    }

    override fun onBackPressed() {
        when (supportFragmentManager.backStackEntryCount) {
            1 -> super.onBackPressed()
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