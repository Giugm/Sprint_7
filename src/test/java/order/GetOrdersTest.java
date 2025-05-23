package order;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;

import static org.hamcrest.Matchers.*;

public class GetOrdersTest {

    private final OrderClient orderClient = new OrderClient();

    @Test
    @DisplayName("Получение списка заказов")
    @Description("Проверяет, что API возвращает список заказов со статусом 200 и непустым полем 'orders'")
    public void getOrdersReturnsOrderList() {
        orderClient.getOrders()
                .then()
                .statusCode(200)
                .body("orders", is(notNullValue()))
                .body("orders.size()", greaterThan(0));
    }
}

