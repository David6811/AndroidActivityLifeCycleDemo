package com.example.lifecycle.appintro

interface NotificationPermissionContract {
    
    interface View {
        fun showPermissionGrantedUI()
        fun showPermissionDeniedUI(isMultipleDenials: Boolean)
        fun showSystemPermissionDialog()
        fun showCustomPermissionDialog()
        fun openNotificationSettings()
        fun enableNavigation()
        fun disableNavigation()
    }
    
    interface Presenter {
        fun onViewCreated()
        fun onViewResumed()
        fun onPermissionButtonClicked()
        fun onSystemPermissionResult(isGranted: Boolean)
        fun onCustomDialogAllowClicked()
        fun onCustomDialogDenyClicked()
        fun onDestroy()
    }
}