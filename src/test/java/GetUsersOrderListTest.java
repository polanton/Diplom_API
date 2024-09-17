import configs.Configs;
import io.qameta.allure.Step;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.CreateOrderRequest;
import model.CreateUserRequest;
import model.GetIngredientsResponse;
import model.GetUsersOrderListResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import steps.Generate;
import steps.SendRequest;
import utils.Tokens;

import java.util.concurrent.ThreadLocalRandom;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class GetUsersOrderListTest {

    @Step("Проверка ответа на запрос списка заказов без авторизации")
    public void  checkGetOrdersWithoutAuthorisationResponse(Response getUsersOrderListResponse){
        getUsersOrderListResponse.then()
                .assertThat().statusCode(SC_UNAUTHORIZED)
                .and().body("success",equalTo(false))
                .and().body("message",equalTo("You should be authorised"));
    }

    @Step("Проверка ответа на запрос списка заказов c авторизацией. До создания заказов")
    public void  checkGetOrdersWithAuthorisationResponseEmpty(Response getUsersOrderListResponse){
        GetUsersOrderListResponse respObj = getUsersOrderListResponse.as(GetUsersOrderListResponse.class);
        getUsersOrderListResponse.then()
                .assertThat().statusCode(SC_OK)
                .and().body("success",equalTo(true))
                .and().body("total",notNullValue(int.class))
                .and().body("totalToday",notNullValue(int.class));
        Assert.assertTrue(respObj.getOrders().isEmpty());

    }

    @Step("Проверка ответа на запрос списка заказов c авторизацией. После создания заказов")
    public void  checkGetOrdersWithAuthorisationResponse(Response getUsersOrderListResponse, int expectedOrdersCount){
        GetUsersOrderListResponse respObj = getUsersOrderListResponse.as(GetUsersOrderListResponse.class);
        getUsersOrderListResponse.then()
                .assertThat().statusCode(SC_OK)
                .and().body("success",equalTo(true))
                .and().body("total",notNullValue(int.class))
                .and().body("totalToday",notNullValue(int.class));

        Assert.assertEquals(expectedOrdersCount, respObj.getOrders().size());

    }

    CreateUserTests createUserTests = new CreateUserTests();
    CreateUserRequest createUserRequest;
    GetIngredientsResponse getIngredientsResponse;

    @Before
    public void setUp(){
        RestAssured.baseURI = Configs.BASE_URL;
        createUserRequest = new CreateUserRequest(Generate.userMail(),Generate.userName(),Generate.password());
        Response response = SendRequest.sendCreateUser(createUserRequest);
        createUserTests.checkResponseOnSuccessfulUserCreation(response,createUserRequest);
        createUserTests.userIsCreated = false;
        getIngredientsResponse = SendRequest.sendGetIngredientList().as(GetIngredientsResponse.class);
    }

    @Test
    @DisplayName("Запрос списка заказов без авторизации")
    public void onRequestWithoutAuthorisationReturnsError(){
        checkGetOrdersWithoutAuthorisationResponse(SendRequest.sendGetUsersOrderListWithoutAuthorisation());
    }

    @Test
    @DisplayName("Запрос списка заказов без создания заказов")
    public void ifNoOrdersCreatedReturnsEmptyList(){
        checkGetOrdersWithAuthorisationResponseEmpty(SendRequest.sendGetUsersOrderList());
    }

    @Test
    @DisplayName("Запрос списка заказов при наличии заказов")
    public void ifOrdersWereCreatedReturnsListOfCreatedOrders(){
        int randomOrdersCount = ThreadLocalRandom.current().nextInt(1, 6);
        for (int i = 1; i<= randomOrdersCount; i++){
            CreateOrderRequest createOrderRequest = new CreateOrderRequest(Generate.ingredientListForRequest(getIngredientsResponse));
            SendRequest.sendCreateOrder(createOrderRequest);
        }
        checkGetOrdersWithAuthorisationResponse(SendRequest.sendGetUsersOrderList(),randomOrdersCount);
    }



    @After
    public void tearDown(){
        if (createUserTests.userIsCreated) {
            SendRequest.sendDeleteUser();
            createUserTests.userIsCreated = false;
            Tokens.setAccessToken("");
            Tokens.setRefreshToken("");
        }
    }
}
