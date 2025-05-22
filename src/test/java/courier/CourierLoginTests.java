package courier;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import static org.hamcrest.Matchers.*;

public class CourierLoginTests {

    @Before
    @Step("Настройка базового URI и пути для RestAssured")
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru";
        RestAssured.basePath = "/api/v1";
        RestAssured.filters(new AllureRestAssured());
    }

    // Класс для запроса авторизации
    static class LoginRequest {
        private String login;
        private String password;

        public LoginRequest(String login, String password) {
            this.login = login;
            this.password = password;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        public void setPassword(String password) {
            this.password = password;
        }
        public String getLogin() {
            return login;
        }
        public String getPassword() {
            return password;
        }
    }

    @Test
    @DisplayName("Курьер может авторизоваться")
    @Description("Проверка, что курьер может успешно авторизоваться и получить id")
    public void courierCanLogin() {
        LoginRequest loginRequest = new LoginRequest("ni1njaaa", "42314");
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/courier/login")
                .then()
                .statusCode(200)
                .body("id", notNullValue());
    }

    @Test
    @DisplayName("Для авторизации нужно передать все обязательные поля")
    @Description("Проверка, что при отсутствии обязательных полей сервер возвращает ошибку")
    public void loginRequiresAllFields() {
        LoginRequest loginRequest = new LoginRequest(null, "42314");

        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/courier/login")
                .then()
                .statusCode(anyOf(is(400), is(404)))
                .body("message", not(emptyOrNullString()));
    }

    @Test
    @DisplayName("Ошибка при неправильном логине или пароле")
    @Description("Проверка, что система возвращает ошибку при неверных данных авторизации")
    public void wrongLoginOrPasswordReturnsError() {
        LoginRequest loginRequest = new LoginRequest("ni1njaaaa", "42314");

        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/courier/login")
                .then()
                .statusCode(404)
                .body("message", containsString("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Ошибка при отсутствии обязательного поля")
    @Description("Проверка, что при отсутствии одного из полей запрос возвращает ошибку")
    public void missingFieldReturnsError() {
        LoginRequest loginRequest = new LoginRequest(null, "somePass");

        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/courier/login")
                .then()
                .statusCode(anyOf(is(400), is(404)))
                .body("message", not(emptyOrNullString()));
    }

    @Test
    @DisplayName("Ошибка при авторизации несуществующего пользователя")
    @Description("Проверка, что попытка авторизоваться под несуществующим пользователем возвращает ошибку")
    public void loginNonexistentUserReturnsError() {
        LoginRequest loginRequest = new LoginRequest("ni1njaaaa", "42314");

        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/courier/login")
                .then()
                .statusCode(404)
                .body("message", containsString("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Успешный запрос возвращает id")
    @Description("Проверка, что успешный запрос авторизации возвращает поле id")
    public void courierCanGetId() {
        LoginRequest loginRequest = new LoginRequest("ni1njaaa", "42314");
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/courier/login")
                .then()
                .statusCode(200)
                .body("id", notNullValue());
    }
}
