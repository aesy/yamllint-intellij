package io.aesy.yamllint.util

import com.intellij.notification.*

object YamllintNotifications {
    private val group = NotificationGroupManager.getInstance().getNotificationGroup("Yamllint")

    fun error(content: String): Notification {
        return group.createNotification(content, NotificationType.ERROR)
    }
}
