package org.events;

import org.events.entities.Event;

import java.time.Instant;

public class TestUtils {
    public static Event createRandomEntity(String username, int id) {
        return Event.builder()
                .id(id)
                .creator(username)
                .title("Test test")
                .location("Testland")
                .creationTime(Instant.now())
                .startTime(Instant.now())
                .endTime(Instant.now())
                .build();
    }

    public static Event createRandomEntity(int id) {
        return createRandomEntity("niv",id);
    }
}
