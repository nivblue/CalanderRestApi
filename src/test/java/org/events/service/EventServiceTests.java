package org.events.service;

import org.events.entities.Event;
import org.events.repository.EventRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Arrays;
import java.util.List;

import static org.events.TestUtils.createRandomEntity;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventServiceTests {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    @MockBean
    private Authentication authentication;

    @MockBean
    private SecurityContextHolder securityContext;


    private static List<Event> expectedEvents;

    @BeforeAll
    static void init() {
        expectedEvents = Arrays.asList(createRandomEntity("test_user", 1),
                createRandomEntity("test_user", 2),
                createRandomEntity("test_user", 3));


        Authentication authentication = mock(Authentication.class);

        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test_user");
        SecurityContextHolder.setContext(securityContext);

    }

    @Test
    @WithMockUser(username="test_user",roles={"USER","ADMIN"})
    @DisplayName("Test that getAllEvents end point return list of all events")
    void testGetAllEventsReturnsAllEvents() {
        when(eventRepository.findAll((Sort) any())).thenReturn(expectedEvents);

        List<Event> actualEvents = eventService.getAllEvents().getBody();

        Assertions.assertArrayEquals(expectedEvents.toArray(), actualEvents.toArray());
    }
}
