package com.emp.yjy.baselib.utils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.permissionx.guolindev.PermissionX;

import java.util.List;

/**
 * 动态权限工具类
 *
 * @author Created by LRH
 * @date 2020/11/23 13:59
 */
public class PermissionUtils {
    /**
     * 初始化权限检查
     *
     * @param activity             activity
     * @param perms                权限列表
     * @param onPermissionCallback 权限回掉接口
     */
    public static void checkPermissions(FragmentActivity activity, String[] perms, OnPermissionCallback onPermissionCallback) {
        PermissionX.init(activity)
                .permissions(perms)
                .onExplainRequestReason((scope, deniedList, beforeRequest) -> {
                    scope.showRequestReasonDialog(deniedList, "s需要您同意以下权限才能正常使用", "同意", "拒绝");
                })
                .onForwardToSettings((scope, deniedList) -> {
                    scope.showForwardToSettingsDialog(deniedList, "您需要去设置中手动开启以下权限", "开启", "拒绝");
                })
                .request((allGranted, grantedList, deniedList) -> {
                    if (allGranted) {
                        onPermissionCallback.onSuccess();
                    } else {
                        onPermissionCallback.onError(deniedList);
                    }
                });
    }

    /**
     * 初始化权限检查
     *
     * @param fragment             fragment
     * @param perms                权限列表
     * @param onPermissionCallback 权限回掉接口
     */
    public static void checkPermissions(Fragment fragment, String[] perms, OnPermissionCallback onPermissionCallback) {
        PermissionX.init(fragment)
                .permissions(perms)
                .onExplainRequestReason((scope, deniedList, beforeRequest) -> {
                    scope.showRequestReasonDialog(deniedList, "s需要您同意以下权限才能正常使用", "同意", "拒绝");
                })
                .onForwardToSettings((scope, deniedList) -> {
                    scope.showForwardToSettingsDialog(deniedList, "您需要去设置中手动开启以下权限", "开启", "拒绝");
                })
                .request((allGranted, grantedList, deniedList) -> {
                    if (allGranted) {
                        onPermissionCallback.onSuccess();
                    } else {
                        onPermissionCallback.onError(deniedList);
                    }
                });
    }

    public interface OnPermissionCallback {
        /**
         * 授权成功
         */
        void onSuccess();

        /**
         * 授权失败
         *
         * @param deniedList 未授权权限列表
         */
        void onError(List<String> deniedList);
    }
}
