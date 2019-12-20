package io.github.hunachi.practicecamerax

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) moveToPermission()
    }


    fun moveToCamera() {
        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, CameraFragment.newInstance())
            .commit()
    }

    fun moveToPermission() {
        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, PermissionsFragment.newInstance())
            .commit()
    }
}
