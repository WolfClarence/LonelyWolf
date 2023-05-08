package com.example.lonelywolf.base

import android.app.Activity

/**
 * 游戏：LonelyWolf
 * 作者：宋宇轩，左天伦，周柏均
 * 版本：1.2.0
 * 版权所有，侵权必究
 */
class ActivityCollector {
    /**
     * 一个专门对所有的活动（ Activity）进行管理的
     */
    companion object {

        var activities = mutableListOf<Activity>()

        /**
         * 添加Activity
         * @param activity 添加的Activity对象
         */
        fun addActivity(activity: Activity) {
            activities.add(activity)
        }

        /**
         * 删除Activity
         * @param activity 删除的Activity对象
         */
        fun removeActivity(activity: Activity) {
            activities.remove(activity)
        }

        /**
         * 关闭指定的Activity
         * @param activityName 需要关闭的Activity类名
         */
        fun finishOneActivity(activityName: String) {
            //在activities集合中找到类名与指定类名相同的Activity就关闭
            for (activity in activities) {
                val name = activity.javaClass.name//activity的类名
                if (name == activityName) {
                    if (activity.isFinishing) {
                        activities.remove(activity)
                    } else {
                        activity.finish()
                    }
                }
            }
        }

        /**
         * 只保留某个Activity，关闭其他所有Activity
         * @param activityName 要保留的Activity类名
         */
        fun finishOtherActivity(activityName: String) {

            for (activity in activities) {
                val name = activity.javaClass.name //activity的类名
                if (name != activityName) {
                    if (activity.isFinishing) {
                        activities.remove(activity)
                    } else {
                        activity.finish()
                    }
                }
            }
        }

        /**
         * 关闭所有Activity
         */
        fun finishAll() {
            for (activity in activities) {
                if (!activity.isFinishing) {
                    activity.finish()
                }
            }
            activities.clear()
        }
    }
}