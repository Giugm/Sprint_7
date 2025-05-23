package courier;

import config.RestAssuredConfig;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class CourierClient {

    private static final String LOGIN_ENDPOINT = "/courier/login";
    private static final String CREATE_ENDPOINT = "/courier";
    private static final String DELETE_ENDPOINT = "/courier/{id}";

    @Step("Создание курьера")
    public ValidatableResponse createCourier(CourierModel courier) {
        return given()
                .spec(RestAssuredConfig.baseSpec)
                .body(courier)    // теперь передаём CourierModel для сериализации
                .when()
                .post(CREATE_ENDPOINT)
                .then();
    }

    @Step("Логин курьера")
    public ValidatableResponse loginCourier(LoginRequest loginRequest) {
        return given()
                .spec(RestAssuredConfig.baseSpec)
                .body(loginRequest)
                .when()
                .post(LOGIN_ENDPOINT)
                .then();
    }

    @Step("Удаление курьера по id")
    public ValidatableResponse deleteCourier(int courierId) {
        return given()
                .spec(RestAssuredConfig.baseSpec)
                .pathParam("id", courierId)
                .when()
                .delete(DELETE_ENDPOINT)
                .then();
    }
}


