package org.events.controller;

import org.events.entities.Event;
import org.events.service.EventService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.events.TestUtils.createRandomEntity;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventControllerTest {
    @Mock
    private EventService eventService;

    @InjectMocks
    private EventController eventController;
    private static List<Event> expectedEvents;

    @BeforeAll
    static void init() {
        expectedEvents = Arrays.asList(createRandomEntity(1),
                createRandomEntity(2),
                createRandomEntity(3));
    }

    @Test
    @DisplayName("Test that getAllEvents end point return list of all events")
    void testGetAllEventsReturnsAllEvents() {
        when(eventService.getAllEvents()).thenReturn(ResponseEntity.ok(expectedEvents));

        ResponseEntity<List<Event>> actualResponseEntity = eventController.getAllEvents();

        Assertions.assertArrayEquals(expectedEvents.toArray(), actualResponseEntity.getBody().toArray());
        Assertions.assertEquals(HttpStatus.OK, actualResponseEntity.getStatusCode());
    }

//    @ParameterizedTest(name = "Test that getAllEvents sort by {0} return list of all events")
//    @ValueSource(strings = {"creationTime", "startTime", "endTime", "location", "title"})
//    void testGetAllEventsReturnsAllEvents(String field) {
//        List<Event> actualEvents = eventController.getAllEvents(field).getBody();
//
//        Assertions.assertArrayEquals(expectedEvents.toArray(), actualEvents.toArray());
//    }

    @Test
    @DisplayName("Test that getSpecificEvent with existing id return the desired event")
    void testGetSpecificEventsForExistingIdReturnsAllEvents() {
        when(eventService.getSpecificEventById(1)).thenReturn(ResponseEntity.ok(expectedEvents.get(0)));

        int desiredId = 1;
        ResponseEntity<Event> actualResponseEntity = eventController.getSpecificEvent(desiredId);

        Assertions.assertTrue(Objects.nonNull(actualResponseEntity.getBody()));
        Assertions.assertEquals(expectedEvents.get(0), actualResponseEntity.getBody());
        Assertions.assertEquals(HttpStatus.OK, actualResponseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test that getSpecificEvent with existing id returns not-found status")
    void testGetSpecificEventsForNonExistingIdReturnsNotFound() {
        when(eventService.getSpecificEventById(anyInt())).thenAnswer(invocation -> {
            int argument = invocation.getArgument(0);
            return expectedEvents.stream()
                    .filter((Event e) -> e.getId() == argument)
                    .findAny()
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        });


        int desiredId = 4;
        ResponseEntity<Event> actualResponseEntity = eventController.getSpecificEvent(desiredId);

        Assertions.assertTrue(Objects.isNull(actualResponseEntity.getBody()));
        Assertions.assertEquals(HttpStatus.NOT_FOUND, actualResponseEntity.getStatusCode());
    }
}
