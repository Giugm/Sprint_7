package config;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class RestAssuredConfig {

    public static final RequestSpecification baseSpec = new RequestSpecBuilder()
            .setBaseUri("http://qa-scooter.praktikum-services.ru")
            .setBasePath("/api/v1")
            .setContentType(ContentType.JSON)
            .addFilter(new AllureRestAssured())
            .build();
}

