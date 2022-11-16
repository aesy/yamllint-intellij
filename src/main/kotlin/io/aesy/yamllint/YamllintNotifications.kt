package io.aesy.yamllint

import com.intellij.notification.*

object YamllintNotifications {
    private val group = NotificationGroupManager.getInstance().getNotificationGroup("Yamllint")

    fun error(content: String): Notification {
        return group.createNotification(content, NotificationType.ERROR)
    }
}
