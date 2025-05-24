package courier;

import config.RestAssuredConfig;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static io.restassured.RestAssured.given;

public class CourierTests {

    private CourierClient courierClient;
    private String courierLogin;
    private String courierPassword;
    private Integer courierId;

    @Before
    public void setUp() {
        courierClient = new CourierClient();
        courierLogin = generateLogin();
        courierPassword = generatePassword();
        createCourier(courierLogin, courierPassword, "TestName");
    }

    @After
    public void tearDown() {
        if (courierId != null) {
            deleteCourier(courierId);
        }
    }

    @Test
    @DisplayName("Курьера можно создать")
    @Description("Проверка, что API позволяет создать нового курьера")
    public void courierCanBeCreated() {
        String login = "testUserCreate";
        String password = "testPassCreate";

        createCourier(login, password, "TestNameCreate");
        Integer createdCourierId = loginCourierAndGetId(login, password);
        deleteCourier(createdCourierId);
    }

    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров")
    @Description("Проверка, что нельзя создать курьера с уже существующим логином")
    public void cannotCreateDuplicateCourier() {
        CourierModel duplicate = new CourierModel(courierLogin, courierPassword, "TestName");
        createDuplicateCourierExpectingConflict(duplicate);
        courierId = loginCourierAndGetId(courierLogin, courierPassword);
    }

    @Test
    @DisplayName("Ошибка при отсутствии обязательных полей")
    @Description("Проверка, что API возвращает ошибку, если не переданы обязательные поля")
    public void missingRequiredFieldsReturnsError() {
        attemptToCreateCourierWithMissingFields(courierPassword);
    }

    @Test
    @DisplayName("Запрос возвращает корректный статус ответа")
    @Description("Проверка, что при создании курьера возвращается 201 или 409 в зависимости от логина")
    public void responseReturnsCorrectStatusCode() {
        courierClient.createCourier(new CourierModel(courierLogin, courierPassword, "TestName"))
                .statusCode(anyOf(equalTo(201), equalTo(409)));
        courierId = loginCourierAndGetId(courierLogin, courierPassword);
    }

    @Test
    @DisplayName("Ошибка при отсутствии пароля")
    @Description("Проверка, что нельзя создать курьера без пароля")
    public void cannotCreateCourierWhenPasswordIsMissing() {
        courierClient.createCourier(new CourierModel(courierLogin, null, "TestName"))
                .statusCode(400)
                .body("message", containsString("Недостаточно данных для создания учетной записи"));
    }

    private String generateLogin() {
        return "user_" + UUID.randomUUID();
    }

    private String generatePassword() {
        return "pass_" + UUID.randomUUID();
    }

    private void createCourier(String login, String password, String name) {
        CourierModel courier = new CourierModel(login, password, name);
        courierClient.createCourier(courier)
                .statusCode(201);
    }

    private void deleteCourier(Integer id) {
        courierClient.deleteCourier(id)
                .statusCode(anyOf(is(200), is(204)));
    }

    private Integer loginCourierAndGetId(String login, String password) {
        return courierClient.loginCourier(new LoginRequest(login, password))
                .statusCode(200)
                .body("id", notNullValue())
                .extract().path("id");
    }

    private void createDuplicateCourierExpectingConflict(CourierModel courier) {
        courierClient.createCourier(courier);
        courierClient.createCourier(courier)
                .statusCode(409)
                .body("message", containsString("Этот логин уже используется"));
    }

    private void attemptToCreateCourierWithMissingFields(String password) {
        courierClient.createCourier(new CourierModel(null, password, null))
                .statusCode(400)
                .body("message", containsString("Недостаточно данных для создания учетной записи"));
    }
}





