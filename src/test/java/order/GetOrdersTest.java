package order;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.Before;
import org.junit.Test;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class GetOrdersTest {

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru";
        RestAssured.basePath = "/api/v1";
        RestAssured.filters(new AllureRestAssured());
    }

    @Test
    @DisplayName("Получение списка заказов")
    @Description("Проверяет, что API возвращает список заказов со статусом 200 и непустым полем 'orders'")
    @Step("Отправка GET-запроса на /api/v1/orders и проверка, что список заказов возвращается")
    public void getOrdersReturnsOrderList() {

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/orders")
                .then()
                .statusCode(200)
                .body("orders", is(notNullValue()))
                .body("orders.size()", greaterThan(0)); // список не пустой
    }
}
