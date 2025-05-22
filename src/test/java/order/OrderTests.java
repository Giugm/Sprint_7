package order;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;

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

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru";
        RestAssured.basePath = "/api/v1";
        RestAssured.filters(new AllureRestAssured());
    }

    //@After Тут у меня возникли проблемы с API. Не работает ручка PUT /api/v1/orders/cancel. Точнее даже сам заказ не отображается в списке, если искать через GET /api/v1/orders (новые не добавляются). Не знаю, что с этим делать. Если знаете, можете, пожалуйста написать, может это баг и нужен баг-репорт?
    //public void tearDown() {
    //    if (track != null) {
    //         given()
    //                 .contentType(ContentType.JSON)
    //                 .body("{\"track\": \"" + track + "\"}")  // track как строка
    //                 .when()
    //                .put("/orders/cancel")
    //                .then()
    //                .statusCode(200);
    //    }
    //}

    @Test
    @DisplayName("Создание заказа с параметризованными цветами")
    @Description("Проверяет, что можно создать заказ с разными вариантами цветов и что тело ответа содержит поле track")
    @Step("Отправка запроса на создание заказа")
    public void testOrderCreationWithDifferentColors() {
        Order order = new Order(
                "Naruto", "Uchiha", "Konoha, 142 apt.", "4",
                "+7 800 355 35 35", 5, "2020-06-06",
                "Saske, come back to Konoha", color
        );

        track = given()
                .contentType(ContentType.JSON)
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
