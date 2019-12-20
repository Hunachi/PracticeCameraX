package io.github.hunachi.practicecamerax

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat


class PermissionsFragment : Fragment() {

    companion object {
        fun newInstance() = PermissionsFragment()

        private const val REQUEST_CODE = 11
        private val PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

        fun hasPermissions(context: Context) = PERMISSIONS.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private lateinit var viewModel: PermissionsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(!hasPermissions(requireContext())){
            requestPermissions(PERMISSIONS, REQUEST_CODE)
        } else {
            moveToCamera()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.permissions_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(PermissionsViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == REQUEST_CODE){
            if(grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED){
                moveToCamera()
            } else {
                context?.toast("Permission request is denied")
                activity?.finish()
            }
        }
    }

    private fun moveToCamera() {
        (activity as? MainActivity)?.moveToCamera()
    }
}
