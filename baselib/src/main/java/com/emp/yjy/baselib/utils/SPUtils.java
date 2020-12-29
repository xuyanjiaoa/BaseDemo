package com.emp.yjy.baselib.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.util.Map;

/**
 * SharedPreferences封装
 *
 * @author LRH
 * Created on 2020/12/29
 */
public class SPUtils {
    /**
     * 保存在手机里面的文件名
     */
    private static final String DEFAULT_SP_FILE_NAME = "DEFAULT_SP_FILE_NAME";

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param context
     * @param key
     * @param object
     */
    public static void put(Context context, String key, Object object) {
        put(context, DEFAULT_SP_FILE_NAME, key, object);
    }


    /**
     * 保存数据到sp
     *
     * @param context
     * @param fileName
     * @param key
     * @param object
     */
    public static void put(Context context, String fileName, String key, Object object) {

        fileName = getFileName(fileName);

        SharedPreferences sp = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 获取sp文件名称
     *
     * @param fileName
     * @return
     */
    @NonNull
    private static String getFileName(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            fileName = DEFAULT_SP_FILE_NAME;
        }
        return fileName;
    }


    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param context
     * @param key
     * @param defaultObject
     * @return
     */
    public static Object get(Context context, String key, Object defaultObject) {
        return get(context, DEFAULT_SP_FILE_NAME, key, defaultObject);
    }

    /**
     * 获取数据
     *
     * @param context
     * @param fileName
     * @param key
     * @param defaultObject
     * @return
     */
    public static Object get(Context context, String fileName, String key, Object defaultObject) {
        fileName = getFileName(fileName);
        SharedPreferences sp = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);

        if (defaultObject instanceof String) {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sp.getLong(key, (Long) defaultObject);
        }

        return null;
    }

    /**
     * 移除某个key值已经对应的值
     *
     * @param context
     * @param key
     */
    public static void remove(Context context, String key) {
        remove(context, DEFAULT_SP_FILE_NAME, key);
    }

    /**
     * 移除某个键值对应的值
     *
     * @param context
     * @param fileName
     * @param key
     */
    public static void remove(Context context, String fileName, String key) {
        fileName = getFileName(fileName);
        SharedPreferences sp = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 清除数据
     *
     * @param context
     */
    public static void clear(Context context) {
        clear(context, DEFAULT_SP_FILE_NAME);
    }


    /**
     * 清除数据
     *
     * @param context
     * @param fileName
     */
    public static void clear(Context context, String fileName) {
        fileName = getFileName(fileName);
        SharedPreferences sp = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 查询某个key是否已经存在
     *
     * @param context
     * @param key
     * @return
     */
    public static boolean contains(Context context, String key) {
        return contains(context, DEFAULT_SP_FILE_NAME, key);
    }


    /**
     * 查询某个key是否已经存在
     *
     * @param context
     * @param fileName
     * @param key
     * @return
     */
    public static boolean contains(Context context, String fileName, String key) {
        fileName = getFileName(fileName);
        SharedPreferences sp = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        return sp.contains(key);
    }

    /**
     * 返回所有的键值对
     *
     * @param context
     * @return
     */
    public static Map<String, ?> getAll(Context context) {
        return getAll(context, DEFAULT_SP_FILE_NAME);
    }


    /**
     * 返回所有的键值对
     *
     * @param context
     * @param fileName
     * @return
     */
    public static Map<String, ?> getAll(Context context, String fileName) {
        fileName = getFileName(fileName);
        SharedPreferences sp = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        return sp.getAll();
    }

    /**
     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
     *
     * @author zhy
     */
    private static class SharedPreferencesCompat {
//        private static final Method sApplyMethod = findApplyMethod();

//        /**
//         * 反射查找apply的方法
//         *
//         * @return
//         */
//        @SuppressWarnings({ "unchecked", "rawtypes" })
//        private static Method findApplyMethod()
//        {
//            try
//            {
//                Class clz = SharedPreferences.Editor.class;
//                return clz.getMethod("apply");
//            } catch (NoSuchMethodException e)
//            {
//            }
//
//            return null;
//        }

        /**
         * 如果找到则使用apply执行，否则使用commit
         *
         * @param editor
         */
        public static void apply(SharedPreferences.Editor editor) {
            //异步写入
//            try
//            {
//                if (sApplyMethod != null)
//                {
//                    sApplyMethod.invoke(editor);
//                    return;
//                }
//            } catch (IllegalArgumentException e)
//            {
//            } catch (IllegalAccessException e)
//            {
//            } catch (InvocationTargetException e)
//            {
//            }
            //同步写入
            editor.commit();
        }
    }


}