package courier;

import config.RestAssuredConfig;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static config.RestAssuredConfig.baseSpec;
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
        // Генерируем уникальные данные
        courierLogin = "user_" + UUID.randomUUID();
        courierPassword = "pass_" + UUID.randomUUID();
        // Создаем курьера
        Courier courier = new Courier(courierLogin, courierPassword, "TestName");
        courierClient.createCourier(courier)
                .statusCode(201);
    }

    @After
    public void tearDown() {
        if (courierId != null) {
            courierClient.deleteCourier(courierId)
                    .statusCode(anyOf(is(200), is(204)));
        }
    }

    @Test
    @DisplayName("Курьера можно создать")
    @Description("Проверка, что API позволяет создать нового курьера")
    public void courierCanBeCreated() {
        courierId = courierClient.loginCourier(new LoginRequest(courierLogin, courierPassword))
                .statusCode(200)
                .body("id", notNullValue())
                .extract().path("id");
    }

    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров")
    @Description("Проверка, что нельзя создать курьера с уже существующим логином")
    public void cannotCreateDuplicateCourier() {
        // второй курьер с тем же логином
        Courier duplicate = new Courier(courierLogin, courierPassword, "TestName");
        courierClient.createCourier(duplicate);
        courierClient.createCourier(duplicate)
                .statusCode(409)
                .body("message", containsString("Этот логин уже используется"));
        // логинимся оригинальным
        courierId = courierClient.loginCourier(new LoginRequest(courierLogin, courierPassword))
                .extract().path("id");
    }

    @Test
    @DisplayName("Ошибка при отсутствии обязательных полей")
    @Description("Проверка, что API возвращает ошибку, если не переданы обязательные поля")
    public void missingRequiredFieldsReturnsError() {
        courierClient.createCourier(new Courier(null, courierPassword, null))
                .statusCode(400)
                .body("message", containsString("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Запрос возвращает корректный статус ответа")
    @Description("Проверка, что при создании курьера возвращается 201 или 409 в зависимости от логина")
    public void responseReturnsCorrectStatusCode() {
        courierClient.createCourier(new Courier(courierLogin, courierPassword, "TestName"))
                .statusCode(anyOf(equalTo(201), equalTo(409)));
        courierId = courierClient.loginCourier(new LoginRequest(courierLogin, courierPassword))
                .extract().path("id");
    }

    @Test
    @DisplayName("Успешный запрос возвращает ok: true")
    @Description("Проверка, что успешное создание курьера возвращает поле ok со значением true")
    public void successResponseHasOkTrue() {
        String uniqueLogin = courierLogin + System.currentTimeMillis(); // или UUID.randomUUID().toString()
        Courier newCourier = new Courier(uniqueLogin, courierPassword, "TestName");

        courierClient.createCourier(newCourier)
                .statusCode(201)
                .body("ok", equalTo(true));

        courierId = courierClient.loginCourier(new LoginRequest(uniqueLogin, courierPassword))
                .extract().path("id");
    }

    @Test
    @DisplayName("Ошибка при отсутствии пароля")
    @Description("Проверка, что нельзя создать курьера без пароля")
    public void cannotCreateCourierWhenPasswordIsMissing() {
        courierClient.createCourier(new Courier(courierLogin, null, "TestName"))
                .statusCode(400)
                .body("message", containsString("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Ошибка при повторном создании логина")
    @Description("Проверка, что нельзя создать двух курьеров с одинаковым логином")
    public void cannotCreateCourierWithDuplicateLogin() {
        // уже покрыто в cannotCreateDuplicateCourier, можно объединить
    }
}




