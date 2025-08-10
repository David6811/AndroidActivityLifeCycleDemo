package com.example.lifecycle.appintro

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.lifecycle.R

class NotificationPermissionFragment : Fragment() {
    
    companion object {
        private const val TAG = "PermissionFragment"
    }
    
    private lateinit var permissionButton: Button
    private lateinit var permissionLauncher: androidx.activity.result.ActivityResultLauncher<String>
    private lateinit var presenter: NotificationPermissionPresenter
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_notification_permission, container, false)
        setupViews(view)
        setupPresenter()
        setupPermissionLauncher()
        return view
    }
    
    override fun onResume() {
        super.onResume()
        presenter.onViewResumed()
    }
    
    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }
    
    private fun setupViews(view: View) {
        permissionButton = view.findViewById(R.id.btn_allow_notifications)
        permissionButton.setOnClickListener { presenter.onPermissionButtonClicked() }
    }
    
    private fun setupPresenter() {
        presenter = NotificationPermissionPresenter(requireContext(), this)
    }
    
    private fun setupPermissionLauncher() {
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            Log.d(TAG, "Permission result: $isGranted")
            presenter.onSystemPermissionResult(isGranted)
        }
    }
    
    fun showPermissionGrantedUI() {
        permissionButton.apply {
            text = getString(R.string.notifications_enabled)
            isEnabled = false
            alpha = 0.6f
        }
    }
    
    fun showPermissionDeniedUI(isMultipleDenials: Boolean) {
        permissionButton.apply {
            text = if (isMultipleDenials) {
                getString(R.string.allow_notifications_required)
            } else {
                getString(R.string.tap_to_allow_notifications)
            }
            isEnabled = true
            alpha = 1.0f
        }
    }
    
    fun showSystemPermissionDialog() {
        Log.d(TAG, "Launching system permission request")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }
    
    
    
    fun enableNavigation() {
        (activity as? IntroActivity)?.enableNavigation()
    }
    
    fun disableNavigation() {
        (activity as? IntroActivity)?.disableNavigation()
    }

    fun getPresenter() = presenter
}