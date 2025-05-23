package order;
import io.restassured.http.ContentType;
import config.RestAssuredConfig;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.hamcrest.Matchers.equalTo;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static config.RestAssuredConfig.baseSpec;
import static io.restassured.RestAssured.given;
import static io.restassured.mapper.ObjectMapperType.GSON;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class OrderTests {

    private final List<String> color;
    private Integer track; // хранит номер созданного заказа

    public OrderTests(List<String> color) {
        this.color = color;
    }

    @Parameterized.Parameters(name = "Цвет(а): {0}")
    public static Collection<Object[]> testData() {
        return Arrays.asList(new Object[][]{
                {List.of("BLACK")},
                {List.of("GREY")},
                {List.of("BLACK", "GREY")},
                {List.of()}  // без цветов
        });
    }

    @After
    public void tearDown() {
        if (track != null) {
            System.out.println("Canceling order with track: " + track);
            given()
                    .spec(baseSpec)
                    .contentType(ContentType.JSON)
                    .body("{\"track\": " + track + "}")
                    .when()
                    .put("/orders/cancel")
                    .then()
                    .statusCode(200)
                    .body("ok", equalTo(true));
        }
    }

    @Test
    @DisplayName("Создание заказа с параметризованными цветами")
    @Description("Проверяет, что можно создать заказ с разными вариантами цветов и что тело ответа содержит поле track")
    public void testOrderCreationWithDifferentColors() {
        Order order = new Order(
                "Naruto", "Uchiha", "Konoha, 142 apt.", "4",
                "+7 800 355 35 35", 5, "2020-06-06",
                "Saske, come back to Konoha", color
        );

        track = given()
                .spec(baseSpec)
                .contentType("application/json")
                .body(order, GSON)
                .when()
                .post("/orders")
                .then()
                .statusCode(201)
                .body("track", notNullValue())
                .extract()
                .path("track");
    }
}
