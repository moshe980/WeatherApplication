package com.giniapps.weatherapplication.permission

import androidx.fragment.app.Fragment

interface PermissionManager {
    fun setFragment(fragment: Fragment)

    fun rationale(description: String): PermissionManagerImp

    fun request(vararg permission: Permission): PermissionManagerImp

    fun checkPermission(callback: (Boolean) -> Unit)
    fun checkDetailedPermission(callback: (Map<Permission, Boolean>) -> Unit)
}