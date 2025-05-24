package courier;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.Step;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.Matchers.*;

public class CourierLoginTests {

    private CourierClient courierClient;
    private String courierLogin;
    private String courierPassword;
    private Integer courierId;

    @Before
    @Step("Подготовка тестовых данных и создание курьера")
    public void setUp() {
        courierClient = new CourierClient();
        courierLogin = "user_" + UUID.randomUUID();
        courierPassword = "pass_" + UUID.randomUUID();

        createCourier(new CourierModel(courierLogin, courierPassword, "AnyName"));
    }

    @After
    @Step("Удаление курьера после теста, если он был создан")
    public void tearDown() {
        if (courierId != null) {
            deleteCourier(courierId);
        }
    }

    @Test
    @Step("Тест: Курьер может авторизоваться")
    @DisplayName("Курьер может авторизоваться")
    @Description("Проверка, что курьер может успешно авторизоваться и получить id")
    public void courierCanLogin() {
        LoginRequest loginRequest = new LoginRequest(courierLogin, courierPassword);
        courierId = loginCourierAndExtractId(loginRequest);
    }

    @Test
    @Step("Тест: Ошибка при отсутствии логина (пустая строка)")
    @DisplayName("Ошибка при отсутствии логина (пустая строка)")
    @Description("Проверка, что при пустом логине возвращается ошибка 400 и корректное сообщение")
    public void loginWithEmptyLoginReturnsError() {
        LoginRequest loginRequest = new LoginRequest("", courierPassword);
        attemptLoginExpecting400WithMessage(loginRequest, "Недостаточно данных для входа");
    }

    @Test
    @Step("Тест: Ошибка при отсутствии пароля (пустая строка)")
    @DisplayName("Ошибка при отсутствии пароля (пустая строка)")
    @Description("Проверка, что при пустом пароле возвращается ошибка 400 и корректное сообщение")
    public void loginWithEmptyPasswordReturnsError() {
        LoginRequest loginRequest = new LoginRequest(courierLogin, "");
        attemptLoginExpecting400WithMessage(loginRequest, "Недостаточно данных для входа");
    }

    @Test
    @Step("Тест: Ошибка при неправильном логине или пароле")
    @DisplayName("Ошибка при неправильном логине или пароле")
    @Description("Проверка, что система возвращает ошибку при неверных данных авторизации")
    public void wrongLoginOrPasswordReturnsError() {
        LoginRequest loginRequest = new LoginRequest(courierLogin + "wrong", courierPassword);
        attemptLoginWithWrongCredentials(loginRequest);
    }

    @Test
    @Step("Тест: Ошибка при авторизации несуществующего пользователя")
    @DisplayName("Ошибка при авторизации несуществующего пользователя")
    @Description("Проверка, что попытка авторизоваться под несуществующим пользователем возвращает ошибку")
    public void loginNonexistentUserReturnsError() {
        LoginRequest loginRequest = new LoginRequest("nonexistentUser", "anyPass");
        attemptLoginWithWrongCredentials(loginRequest);
    }

    @Test
    @Step("Тест: Успешный запрос возвращает id")
    @DisplayName("Успешный запрос возвращает id")
    @Description("Проверка, что успешный запрос авторизации возвращает поле id")
    public void courierCanGetId() {
        LoginRequest loginRequest = new LoginRequest(courierLogin, courierPassword);
        courierId = loginCourierAndExtractId(loginRequest);
    }

    @Step("Создание курьера")
    private void createCourier(CourierModel courier) {
        courierClient.createCourier(courier)
                .statusCode(201);
    }

    @Step("Удаление курьера с id = {courierId}")
    private void deleteCourier(int courierId) {
        courierClient.deleteCourier(courierId)
                .statusCode(anyOf(is(200), is(204)));
    }

    @Step("Авторизация курьера и извлечение id")
    private int loginCourierAndExtractId(LoginRequest loginRequest) {
        return courierClient.loginCourier(loginRequest)
                .statusCode(200)
                .body("id", notNullValue())
                .extract()
                .path("id");
    }

    @Step("Попытка авторизации с пустыми обязательными полями, ожидаем ошибку 400 с сообщением: {expectedMessage}")
    private void attemptLoginExpecting400WithMessage(LoginRequest loginRequest, String expectedMessage) {
        courierClient.loginCourier(loginRequest)
                .statusCode(400)
                .body("message", containsString(expectedMessage));
    }

    @Step("Попытка авторизации с неправильными данными, ожидаем ошибку 404")
    private void attemptLoginWithWrongCredentials(LoginRequest loginRequest) {
        courierClient.loginCourier(loginRequest)
                .statusCode(404)
                .body("message", containsString("Учетная запись не найдена"));
    }
}





