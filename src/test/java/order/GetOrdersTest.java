package order;

import config.RestAssuredConfig;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.http.ContentType;
import org.junit.Test;

import static config.RestAssuredConfig.baseSpec;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class GetOrdersTest {

    @Test
    @DisplayName("Получение списка заказов")
    @Description("Проверяет, что API возвращает список заказов со статусом 200 и непустым полем 'orders'")

    public void getOrdersReturnsOrderList() {
        given()
                .spec(baseSpec)
                .contentType(ContentType.JSON)
                .when()
                .get("/orders")
                .then()
                .statusCode(200)
                .body("orders", is(notNullValue()))
                .body("orders.size()", greaterThan(0));
    }
}
