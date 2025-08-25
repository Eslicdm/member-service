package com.eslirodrigues.service_app_java.controller;

import com.eslirodrigues.service_app_java.dto.CreateMemberRequest;
import com.eslirodrigues.service_app_java.entity.ServiceType;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class MemberControllerTest {

    @LocalServerPort
    private Integer port;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    }

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
    }

    @Test
    void testCreateMember_whenValidRequest_shouldReturnCreated() {
        Long managerId = 1L;
        var request = new CreateMemberRequest(
                "John Doe",
                "john.doe@test.com",
                LocalDate.of(1990, 1, 15),
                "http://example.com/photo.jpg",
                ServiceType.FULL_PRICE
        );

        given().contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1/managers/{managerId}/members", managerId)
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", equalTo("John Doe"))
                .body("email", equalTo("john.doe@test.com"))
                .body("managerId", equalTo(managerId.intValue()));
    }

    @Test
    void testGetAllMembers_whenMembersExist_shouldReturnMemberList() {
        Long managerId = 2L;
        var request = new CreateMemberRequest(
                "Jane Smith",
                "jane.smith@test.com",
                null,
                null,
                ServiceType.FREE
        );
        given().contentType(ContentType.JSON)
                .body(request)
                .post("/api/v1/managers/{managerId}/members", managerId)
                .then()
                .statusCode(201);

        given().when()
                .get("/api/v1/managers/{managerId}/members", managerId)
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].name", equalTo("Jane Smith"))
                .body("[0].managerId", equalTo(managerId.intValue()));
    }

    @Test
    void testCreateMember_whenInvalidServiceType_shouldReturnBadRequest() {
        Long managerId = 1L;
        String invalidRequestJson = """
                {
                    "name": "Invalid User",
                    "email": "invalid.user@test.com",
                    "serviceType": "invalid-type"
                }
                """;

        given().contentType(ContentType.JSON)
                .body(invalidRequestJson)
                .when()
                .post("/api/v1/managers/{managerId}/members", managerId)
                .then()
                .statusCode(400);
    }
}