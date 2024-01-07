package org.events.service;

import org.events.entities.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
public class EventSubscriptionService {
    private static final int HALF_HOUR_IN_SECONDS = 1800;
    private static final ConcurrentHashMap<Integer, ScheduledFuture<?>> eventIdToTaskMap =
            new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<Integer, String> userToEventIdSubMap =
            new ConcurrentHashMap<>();

    @Autowired
    private TaskScheduler taskScheduler;

    public void subscribeToEvent(int eventId, String username) {
        userToEventIdSubMap.put(eventId, username);
    }

    public void addRemainderEvent(Event event) {
        ScheduledFuture<?> schedule = taskScheduler.schedule(() -> {
            System.out.println("Your event with id : " + event.getId() + " is about to happened in 30 minutes!");
        }, event.getStartTime().minusSeconds(HALF_HOUR_IN_SECONDS));

        eventIdToTaskMap.put(event.getId(), schedule);
    }

    public void cancelRemainderEvent(Event event) {
        if (eventIdToTaskMap.containsKey(event.getId())) {
            ScheduledFuture<?> schedule = eventIdToTaskMap.get(event.getId());
            schedule.cancel(false);
            eventIdToTaskMap.remove(event.getId());
        }

        if (userToEventIdSubMap.containsKey(event.getId())) {
            System.out.println("[ for user : " + userToEventIdSubMap.get(event.getId()) + " ] event : " + event.getTitle() + " canceled");
        }
    }

    public void updateRemainderEvent(Event event) {
        cancelRemainderEvent(event);
        addRemainderEvent(event);

        if (userToEventIdSubMap.containsKey(event.getId())) {
            System.out.println("[ for user : " + userToEventIdSubMap.get(event.getId()) + " ] event : " + event.getTitle() + " updated");
        }
    }
}
