package ru.yana.NotificationFilter;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class NotificationFilterTest {

    @Test
    public void testFilter_DuplicatePrevention() {
        NotificationFilter filter = getNotificationFilter();

        List<Notification> notifications = List.of(
                new Notification(
                        1L,
                        NotificationType.EMAIL,
                        100L,
                        "Test message",
                        LocalDateTime.now()
                ), // Дубликат по ID
                new Notification(
                        2L,
                        NotificationType.EMAIL,
                        100L,
                        "Test message",
                        LocalDateTime.now()
                )  // Дубликат по содержанию
        );

        List<Notification> result = filter.filter(notifications, 999L);

        assertTrue(result.isEmpty()); // Оба должны быть отфильтрованы
    }

    private static NotificationFilter getNotificationFilter() {
        UserPreferencesService prefService = userId ->
                new UserPreferences(Set.of(NotificationType.EMAIL), Set.of());

        NotificationHistoryService historyService = userId ->
                List.of(new Notification(
                        1L,
                        NotificationType.EMAIL,
                        userId,
                        "Test message",
                        LocalDateTime.now().minusHours(1)
                ));

        NotificationFilter filter = new NotificationFilter(prefService, historyService);
        return filter;
    }

    @Test
    public void testFilter_BlockedSender() {
        UserPreferencesService prefService = userId ->
                new UserPreferences(Set.of(NotificationType.EMAIL), Set.of(999L));

        NotificationHistoryService historyService = userId -> List.of();

        NotificationFilter filter = new NotificationFilter(prefService, historyService);

        List<Notification> notifications = List.of(
                new Notification(1L, NotificationType.EMAIL, 100L, "Test", LocalDateTime.now())
        );

        List<Notification> result = filter.filter(notifications, 999L);

        assertTrue(result.isEmpty());
    }

}