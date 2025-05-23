package courier;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.Matchers.*;

public class CourierLoginTests {

    private CourierClient courierClient;
    private String courierLogin;
    private String courierPassword;
    private Integer courierId; // id курьера для удаления

    @Before
    public void setUp() {
        courierClient = new CourierClient();

        // Создаем уникальные логин и пароль для каждого теста
        courierLogin = "user_" + UUID.randomUUID();
        courierPassword = "pass_" + UUID.randomUUID();

        // Создаем курьера с уникальными данными
        Courier courier = new Courier(courierLogin, courierPassword, "AnyName");
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
    @DisplayName("Курьер может авторизоваться")
    @Description("Проверка, что курьер может успешно авторизоваться и получить id")
    public void courierCanLogin() {
        LoginRequest loginRequest = new LoginRequest(courierLogin, courierPassword);

        courierId = courierClient.loginCourier(loginRequest)
                .statusCode(200)
                .body("id", notNullValue())
                .extract()
                .path("id");
    }

    @Test
    @DisplayName("Для авторизации нужно передать все обязательные поля")
    @Description("Проверка, что при отсутствии обязательных полей сервер возвращает ошибку")
    public void loginRequiresAllFields() {
        LoginRequest loginRequest = new LoginRequest(null, courierPassword);

        courierClient.loginCourier(loginRequest)
                .statusCode(anyOf(is(400), is(404)))
                .body("message", not(emptyOrNullString()));
    }

    @Test
    @DisplayName("Ошибка при неправильном логине или пароле")
    @Description("Проверка, что система возвращает ошибку при неверных данных авторизации")
    public void wrongLoginOrPasswordReturnsError() {
        LoginRequest loginRequest = new LoginRequest(courierLogin + "wrong", courierPassword);

        courierClient.loginCourier(loginRequest)
                .statusCode(404)
                .body("message", containsString("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Ошибка при отсутствии обязательного поля — пароль null")
    @Description("Проверка, что при отсутствии пароля запрос возвращает ошибку")
    public void missingPasswordReturnsError() {
        LoginRequest loginRequest = new LoginRequest(courierLogin, "");

        courierClient.loginCourier(loginRequest)
                .statusCode(anyOf(is(400), is(404)))
                .body("message", not(emptyOrNullString()));
    }

    @Test
    @DisplayName("Ошибка при авторизации несуществующего пользователя")
    @Description("Проверка, что попытка авторизоваться под несуществующим пользователем возвращает ошибку")
    public void loginNonexistentUserReturnsError() {
        LoginRequest loginRequest = new LoginRequest("nonexistentUser", "anyPass");

        courierClient.loginCourier(loginRequest)
                .statusCode(404)
                .body("message", containsString("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Успешный запрос возвращает id")
    @Description("Проверка, что успешный запрос авторизации возвращает поле id")
    public void courierCanGetId() {
        LoginRequest loginRequest = new LoginRequest(courierLogin, courierPassword);

        courierId = courierClient.loginCourier(loginRequest)
                .statusCode(200)
                .body("id", notNullValue())
                .extract()
                .path("id");
    }
}



