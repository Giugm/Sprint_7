package order;

import config.RestAssuredConfig;
import io.restassured.response.Response;

import static config.RestAssuredConfig.baseSpec;
import static io.restassured.RestAssured.given;

public class OrderClient {

    public Response createOrder(Order order) {
        return given()
                .spec(baseSpec)
                .contentType("application/json")
                .body(order)
                .when()
                .post("/orders");
    }

    public Response getOrders() {
        return given()
                .spec(baseSpec)
                .contentType("application/json")
                .when()
                .get("/orders");
    }

    public Response cancelOrder(int track) {
        return given()
                .spec(baseSpec)
                .contentType("application/json")
                .body("{\"track\": " + track + "}")
                .when()
                .put("/orders/cancel");
    }
}
