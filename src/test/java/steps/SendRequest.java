package steps;

import configs.Configs;
import utils.Tokens;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import model.*;

import static io.restassured.RestAssured.given;

public class SendRequest {

    @Step("отправка запроса на создание пользователя")
    public static Response sendCreateUser(CreateUserRequest requestJson) {
        return given()
                .header("Content-type", "application/json")
                .body(requestJson)
                .when()
                .post(Configs.CREATE_USER_URL);
    }

    @Step("отправка запроса на логин")
    public static Response sendLogin(LoginRequest requestJson) {
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", Tokens.getAccessToken())
                .body(requestJson)
                .when()
                .post(Configs.LOGIN_URL);
    }

    @Step("отправка запроса на изменение пользователя")
    public static Response sendUpdateUser (UpdateUserRequest requestJson ){
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", Tokens.getAccessToken())
                .body(requestJson)
                .when()
                .patch(Configs.UPDATE_USER_URL);
    }

    @Step("отправка запроса на изменение пользователя")
    public static Response sendUpdateUserWithoutAuthorisation (UpdateUserRequest requestJson ){
        return given()
                .header("Content-type", "application/json")
                .body(requestJson)
                .when()
                .patch(Configs.UPDATE_USER_URL);
    }

    @Step("отправка запроса на получение списка ингредиентов")
    public static Response sendGetIngredientList () {
        return given()
                .header("Content-type", "application/json")
                .get(Configs.GET_INGREDIENTS_LIST);
    }

    @Step("отправка запроса на создание заказа")
    public static Response sendCreateOrder (CreateOrderRequest requestJson ){
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", Tokens.getAccessToken())
                .body(requestJson)
                .when()
                .post(Configs.CREATE_ORDER_URL);
    }

    @Step("отправка запроса на создание заказа без авторизации")
    public static Response sendCreateOrderWithoutAuthorisation (CreateOrderRequest requestJson ){
        return given()
                .header("Content-type", "application/json")
                .body(requestJson)
                .when()
                .post(Configs.CREATE_ORDER_URL);
    }

    @Step("отправка запроса на получение заказов пользователя")
    public static Response sendGetUsersOrderList (){
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", Tokens.getAccessToken())
                .when()
                .get(Configs.GET_USERS_ORDER_LIST);
    }

    @Step("отправка запроса на получение заказов пользователя")
    public static Response sendGetUsersOrderListWithoutAuthorisation (){
        return given()
                .header("Content-type", "application/json")
                .when()
                .get(Configs.GET_USERS_ORDER_LIST);
    }

    @Step("отправка запроса на удаление пользователя")
    public static Response sendDeleteUser() {
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", Tokens.getAccessToken())
                .when()
                .delete(Configs.DELETE_USER_URL);
    }

}
