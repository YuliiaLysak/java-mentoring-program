package edu.lysak.events.controller;

import edu.lysak.events.domain.EventRequest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.time.LocalDateTime;
import java.time.Month;

import static io.restassured.RestAssured.delete;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.with;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EventServiceControllerTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @AfterEach
    void tearDown() {
        RestAssured.reset();
    }

    @Nested
    @DisplayName("#addEvent(EventRequest)")
    class AddEventMethodTest {

        @Test
        @DisplayName("should add new event and return id of the created event and status 200 (ok)")
        void addEvent_shouldAddEvent() {
            EventRequest requestBody = new EventRequest();
            requestBody.setTitle("Friends");
            requestBody.setPlace("Kharkiv, Ukraine");
            requestBody.setSpeaker("HBO");
            requestBody.setEventType("tv series");
            requestBody.setDateTime(LocalDateTime.now());

            String id = with().body(requestBody)
                    .header("Content-type", "application/json")
                    .when()
                    .post("/event/new")
                    .then()
                    .statusCode(200)
                    .extract().body().asString();

            assertNotNull(id);
        }

        @ParameterizedTest
        @ArgumentsSource(EventServiceControllerTestData.EventRequestMandatoryField.class)
        @DisplayName("should not add event and return status 400 (bad request) if request body is invalid")
        void addEvent_shouldNotAddEvent_ifRequestBodyInvalid(String title, String place, String speaker, String eventType) {
            EventRequest invalidRequestBody = new EventRequest();
            invalidRequestBody.setTitle(title);
            invalidRequestBody.setPlace(place);
            invalidRequestBody.setSpeaker(speaker);
            invalidRequestBody.setEventType(eventType);

            with().body(invalidRequestBody)
                    .header("Content-type", "application/json")
                    .when()
                    .post("/event/new")
                    .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("should not add event and return status 409 (conflict) if event already exists")
        void addEvent_shouldNotAddEvent_ifEventAlreadyExists() {
            EventRequest existedRequestBody = new EventRequest();
            existedRequestBody.setTitle("test title");
            existedRequestBody.setPlace("test place");
            existedRequestBody.setSpeaker("test speaker");
            existedRequestBody.setEventType("test event type");
            existedRequestBody.setDateTime(LocalDateTime.of(2022, Month.JUNE, 1, 19, 0));

            with().body(existedRequestBody)
                    .header("Content-type", "application/json")
                    .when()
                    .post("/event/new")
                    .then()
                    .statusCode(409);
        }
    }

    @Nested
    @DisplayName("#updateEvent(Long, EventRequest)")
    class UpdateEventMethodTest {

        @Test
        @DisplayName("should update existing event and return status 200 (ok)")
        void updateEvent_shouldUpdateEvent() {
            EventRequest requestBody = new EventRequest();
            requestBody.setTitle("Friends updated");
            requestBody.setPlace("Kharkiv, Ukraine");
            requestBody.setSpeaker("HBO");
            requestBody.setEventType("tv series");
            requestBody.setDateTime(LocalDateTime.now());

            with().body(requestBody)
                    .header("Content-type", "application/json")
                    .when()
                    .put("/event/{id}", 1001)
                    .then()
                    .statusCode(200);
        }

        @ParameterizedTest
        @ArgumentsSource(EventServiceControllerTestData.EventRequestMandatoryField.class)
        @DisplayName("should not update event and return status 400 (bad request) if request body is invalid")
        void updateEvent_shouldNotUpdateEvent_ifRequestBodyInvalid(String title, String place, String speaker, String eventType) {
            EventRequest invalidRequestBody = new EventRequest();
            invalidRequestBody.setTitle(title);
            invalidRequestBody.setPlace(place);
            invalidRequestBody.setSpeaker(speaker);
            invalidRequestBody.setEventType(eventType);

            with().body(invalidRequestBody)
                    .header("Content-type", "application/json")
                    .when()
                    .put("/event/{id}", 1001)
                    .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("should not update event and return status 404 (not found) if event does not exist")
        void updateEvent_shouldNotUpdateEvent_ifEventNotExist() {
            EventRequest requestBody = new EventRequest();
            requestBody.setTitle("Friends updated");
            requestBody.setPlace("Kharkiv, Ukraine");
            requestBody.setSpeaker("HBO");
            requestBody.setEventType("tv series");
            requestBody.setDateTime(LocalDateTime.now());

            with().body(requestBody)
                    .header("Content-type", "application/json")
                    .when()
                    .put("/event/{id}", 1002)
                    .then()
                    .statusCode(404);
        }
    }

    @Nested
    @DisplayName("#findEventById(Long)")
    class FindEventByIdMethodTest {

        @Test
        @DisplayName("should return found event and status 200 (ok)")
        void findEventById_shouldReturnFoundEvent() {
            get("/event/{id}", 1000)
                    .then()
                    .statusCode(200)
                    .body("title", equalTo("test title"))
                    .body("place", equalTo("test place"))
                    .body("speaker", equalTo("test speaker"))
                    .body("eventType", equalTo("test event type"))
                    .body("dateTime", equalTo("2022-06-01T19:00:00"));
        }

        @Test
        @DisplayName("should return status 404 (not found) if event does not exist")
        void findEventById_shouldNotReturnEvent() {
            get("/event/{id}", 100)
                    .then()
                    .statusCode(404);
        }
    }

    @Nested
    @DisplayName("#deleteEventById(Long)")
    class DeleteEventByIdMethodTest {

        @Test
        @DisplayName("should delete event and return status 200 (ok)")
        void deleteEventById_shouldDeleteEvent() {
            delete("/event/{id}", 1002)
                    .then()
                    .statusCode(200);
        }

        @Test
        @DisplayName("should not delete any event and return status 404 (not found) if event does not exist")
        void deleteEventById_shouldNotDeleteEvent() {
            delete("/event/{id}", 1111)
                    .then()
                    .statusCode(404);
        }
    }

    @Nested
    @DisplayName("#getAllEvents()")
    class GetAllEventsMethodTest {

        @Test
        @DisplayName("should return list of events and status 200 (ok)")
        void getAllEvents_shouldReturnListOfEvents() {
            get("/event")
                    .then()
                    .statusCode(200)
                    .assertThat().body("size()", greaterThan(0));
        }
    }

    @Nested
    @DisplayName("#getAllEvents(String)")
    class GetAllEventsByTitleMethodTest {

        @Test
        @DisplayName("should return list of events by title and status 200 (ok)")
        void getAllEventsByTitle_shouldReturnListOfEvents() {
            given()
                    .get("/event/title?title=test title")
                    .then()
                    .statusCode(200)
                    .assertThat().body("size()", greaterThan(0));
        }

        @Test
        @DisplayName("should return empty list of events and status 404 (not found) if events bu title don't exist")
        void getAllEventsByTitle_shouldReturnEmptyListOfEvents() {
            given()
                    .get("/event/title?title=notExistingTitle")
                    .then()
                    .statusCode(404);
        }
    }
}
