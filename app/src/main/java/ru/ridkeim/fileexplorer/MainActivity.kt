package ru.ridkeim.fileexplorer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import ru.ridkeim.fileexplorer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var vBinding: ActivityMainBinding
    private val viewModel : MainViewModel by viewModels {
        MainViewModel.Factory()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(vBinding.root)
        setSupportActionBar(vBinding.toolbar)
        when (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        )){
            PackageManager.PERMISSION_GRANTED -> {
                startObserving()
            }
            else -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            PERMISSION_REQUEST_CODE
                    )
                }
            }
        }
    }

    private fun startObserving(){
        viewModel.currentDirectory.observe(this){
            val aPath = it.absolutePath
            val fragment = supportFragmentManager.findFragmentByTag(aPath)
            if(fragment != null) {
                supportFragmentManager.popBackStack(aPath,0)
            }else{
                supportFragmentManager.commit {
                    replace(R.id.filesContainer,
                            FilesFragment.build {
                                path = aPath
                            },
                            aPath
                    )
                    addToBackStack(aPath)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                                grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    startObserving()
                } else {
                    Log.d(TAG,"")
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                }
                return
            }
            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        viewModel.popToParent()
//        if(supportFragmentManager.backStackEntryCount == 0){
//            finish()
//        }else{
//
//        }
    }
    companion object {
        private const val PERMISSION_REQUEST_CODE: Int = 42
        private val TAG = MainActivity::class.qualifiedName
    }
}