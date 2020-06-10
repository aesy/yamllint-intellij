package io.aesy.yamllint

import com.intellij.notification.Notification
import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType

object YamllintNotifications {
    private val group = NotificationGroup("Yamllint", NotificationDisplayType.BALLOON, true)

    fun error(content: String): Notification {
        return group.createNotification(content, NotificationType.ERROR)
    }
}
