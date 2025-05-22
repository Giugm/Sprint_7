package courier;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.Before;
import org.junit.Test;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;

import static org.hamcrest.Matchers.*;

public class CourierTests {

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru";
        RestAssured.basePath = "/api/v1";
        RestAssured.filters(new AllureRestAssured());
    }

    @Step("Создание тестового курьера")
    private Courier createTestCourier() {
        return new Courier("nianja1a111", "4234", "saske");
    }

    @Step("Авторизация курьера: логин={login}")
    private int loginCourier(String login, String password) {
        return RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(new CourierLogin(login, password))
                .when()
                .post("/courier/login")
                .then()
                .statusCode(200)
                .extract()
                .path("id");
    }

    @Step("Удаление курьера с id={id}")
    private void deleteCourier(int courierId) {
        RestAssured
                .given()
                .when()
                .delete("/courier/" + courierId)
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("Курьера можно создать")
    @Description("Проверка, что API позволяет создать нового курьера")
    public void courierCanBeCreated() {
        Courier courier = new Courier("krop", "pass123", "kakashi");

        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(courier)
                .when()
                .post("/courier")
                .then()
                .statusCode(201)
                .body("ok", equalTo(true));

        int courierId = loginCourier(courier.getLogin(), courier.getPassword());
        deleteCourier(courierId);
    }

    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров")
    @Description("Проверка, что нельзя создать курьера с уже существующим логином")
    public void cannotCreateDuplicateCourier() {
        Courier courier = new Courier("krops1122", "pass123", "kakashi");

        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(courier)
                .when()
                .post("/courier");

        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(courier)
                .when()
                .post("/courier")
                .then()
                .statusCode(409)
                .body("message", containsString("Этот логин уже используется."));

        int courierId = loginCourier(courier.getLogin(), courier.getPassword());
        deleteCourier(courierId);
    }

    @Test
    @DisplayName("Ошибка при отсутствии обязательных полей")
    @Description("Проверка, что API возвращает ошибку, если не переданы обязательные поля")
    public void missingRequiredFieldsReturnsError() {
        Courier courier = new Courier(null, "1234", null);

        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(courier)
                .when()
                .post("/courier")
                .then()
                .statusCode(400)
                .body("message", containsString("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Запрос возвращает корректный статус ответа")
    @Description("Проверка, что при создании курьера возвращается 201 или 409 в зависимости от логина")
    public void responseReturnsCorrectStatusCode() {
        Courier courier = createTestCourier();

        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(courier)
                .when()
                .post("/courier")
                .then()
                .statusCode(anyOf(equalTo(201), equalTo(409)));

        int courierId = loginCourier(courier.getLogin(), courier.getPassword());
        deleteCourier(courierId);
    }

    @Test
    @DisplayName("Успешный запрос возвращает ok: true")
    @Description("Проверка, что успешное создание курьера возвращает поле ok со значением true")
    public void successResponseHasOkTrue() {
        Courier courier = new Courier("krop", "pass123", "kakashi");

        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(courier)
                .when()
                .post("/courier")
                .then()
                .statusCode(201)
                .body("ok", equalTo(true));

        int courierId = loginCourier(courier.getLogin(), courier.getPassword());
        deleteCourier(courierId);
    }

    @Test
    @DisplayName("Ошибка при отсутствии пароля")
    @Description("Проверка, что нельзя создать курьера без пароля")
    public void cannotCreateCourierWhenPasswordIsMissing() {
        Courier courierWithoutPassword = new Courier("ninjaa1", null, "saske");

        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(courierWithoutPassword)
                .when()
                .post("/courier")
                .then()
                .statusCode(400)
                .body("message", containsString("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Ошибка при повторном создании логина")
    @Description("Проверка, что нельзя создать двух курьеров с одинаковым логином")
    public void cannotCreateCourierWithDuplicateLogin() {
        Courier firstCourier = new Courier("Groks", "password123", "Sasuka");

        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(firstCourier)
                .when()
                .post("/courier")
                .then()
                .statusCode(201)
                .body("ok", equalTo(true));

        Courier secondCourier = new Courier("Groks", "123password", "Sasuka");

        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(secondCourier)
                .when()
                .post("/courier")
                .then()
                .statusCode(409)
                .body("message", containsString("Этот логин уже используется"));

        int courierId = loginCourier(firstCourier.getLogin(), firstCourier.getPassword());
        deleteCourier(courierId);
    }
}


