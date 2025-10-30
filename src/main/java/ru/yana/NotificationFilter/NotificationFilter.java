package ru.yana.NotificationFilter;

import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
public class NotificationFilter {

    private final UserPreferencesService userPreferencesService;
    private final NotificationHistoryService notificationHistoryService;

    public List<Notification> filter(List<Notification> notifications, Long senderId) {
        if (senderId == null) throw new IllegalArgumentException("senderId cannot be null");
        if (notifications.isEmpty()) return Collections.emptyList();

        return notifications
                .stream()
                .filter(notification -> {
                    var recipientId = notification.recipientId();
                    var preferences = userPreferencesService.getUserPreferences(recipientId);
                    if (preferences == null ||
                        preferences.blockedSenders().contains(senderId) ||
                        !preferences.allowedChannels()
                                .contains(notification.notificationType())) return false;
                    return true;
                })
                .filter(notification -> {
                    var recipientId = notification.recipientId();

                    List<Notification> recentNotifications = notificationHistoryService
                            .getNotificationsSentLast24Hours(recipientId);

                    return recentNotifications
                            .stream()
                            .noneMatch(recent ->
                                    recent.id().equals(notification.id()) ||
                                    (recent.notificationType().equals(notification.notificationType()) &&
                                    recent.recipientId().equals(recipientId) &&
                                    recent.message().equals(notification.message())));
                })
                .toList();
    }

}

record Notification(
        Long id,
        NotificationType notificationType,
        Long recipientId,
        String message,
        LocalDateTime createdAt
) {
}

enum NotificationType {
    EMAIL, SMS, PUSH
}

record UserPreferences(
        Set<NotificationType> allowedChannels,
        Set<Long> blockedSenders
) {
}

interface UserPreferencesService {
    UserPreferences getUserPreferences(Long userId);
}

interface NotificationHistoryService {
    List<Notification> getNotificationsSentLast24Hours(Long userId);
}