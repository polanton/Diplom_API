import configs.Configs;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.CreateOrderRequest;
import model.CreateOrderResponseFull;
import model.CreateUserRequest;
import model.GetIngredientsResponse;
import org.junit.*;
import steps.Generate;
import steps.SendRequest;
import utils.Tokens;

import java.util.ArrayList;
import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class CreateOrderTests {

    @Step("Проверка ответа при запросе на создание заказа без авторизации")
    public void checkCreateOrderSuccessfulShortResponse(Response createOrderResponse){
        createOrderResponse.then()
                .assertThat()
                .statusCode(SC_OK)
                .and().body("success", equalTo(true))
                .and().body("name",notNullValue(String.class))
                .and().body("order.number",notNullValue(int.class));
    }

    @Step("Проверка ответа при запросе на создание заказа с авторизацией")
    public void checkCreateOrderSuccessfulFullResponse(Response createOrderResponse, CreateOrderRequest createOrderRequest, CreateUserRequest createUserRequest){
        CreateOrderResponseFull respObj = createOrderResponse.as(CreateOrderResponseFull.class);
        createOrderResponse.then()
                .assertThat()
                .statusCode(SC_OK)
                .and().body("success", equalTo(true))
                .and().body("name",notNullValue(String.class))
                .and().body("order.owner.name", equalTo(createUserRequest.getName()))
                .and().body("order.owner.email",equalTo(createUserRequest.getEmail())).and().body("order.number",notNullValue(int.class))
                .and().body("order.number",notNullValue(int.class))
                .and().body("order.price",equalTo(respObj.getOrder().getIngredientsPriceSum()));
        //проверка что в полном ответе есть все ингредиенты
        Assert.assertTrue(respObj.getOrder().hasAllIngredients(createOrderRequest.getIngredients()));
    }

    @Step("Проверка ответа на запрос с пустым списком ингридиентов")
    public void  checkCreateOrderFaultResponseOnEmptyIngrList(Response createOrderResponse){
        createOrderResponse.then()
                .assertThat().statusCode(SC_BAD_REQUEST)
                .and().body("success",equalTo(false))
                .and().body("message",equalTo("Ingredient ids must be provided"));

    }

    @Step("Проверка ответа на запрос с несущесвующим id ингридиента")
    public void  checkCreateOrderFaultResponse(Response createOrderResponse){
        createOrderResponse.then()
                .assertThat().statusCode(SC_INTERNAL_SERVER_ERROR);

    }


    CreateUserTests createUserTests = new CreateUserTests();
    CreateUserRequest createUserRequest = new CreateUserRequest(Generate.userMail(),Generate.userName(),Generate.password());
    GetIngredientsResponse getIngredientsResponse;

    @Before
    public void setUp(){
        RestAssured.baseURI = Configs.BASE_URL;
        Response response = SendRequest.sendCreateUser(createUserRequest);
        createUserTests.checkResponseOnSuccessfulUserCreation(response,createUserRequest);
        createUserTests.userIsCreated = false;
        getIngredientsResponse = SendRequest.sendGetIngredientList().as(GetIngredientsResponse.class);

    }
    @Test
    @DisplayName("Успешное создание заказа с авторизацией")
    public void onValidRequestWithAuthorisationReturnsCorrectResponse(){
        CreateOrderRequest createOrderRequest = new CreateOrderRequest(Generate.ingredientListForRequest(getIngredientsResponse));
        checkCreateOrderSuccessfulFullResponse(SendRequest.sendCreateOrder(createOrderRequest),createOrderRequest,createUserRequest);
    }

    @Test
    @DisplayName("Успешное создание заказа без авторизации")
    public void onValidRequestWithOutAuthorisationReturnsCorrectResponse(){
        CreateOrderRequest createOrderRequest = new CreateOrderRequest(Generate.ingredientListForRequest(getIngredientsResponse));
        checkCreateOrderSuccessfulShortResponse(SendRequest.sendCreateOrderWithoutAuthorisation(createOrderRequest));
    }

    @Test
    @DisplayName("Попытка создания заказа без ингридиентов  с авторизацией")
    public void onEmptyRequestWithAuthorisationReturnsCorrectResponse(){
        CreateOrderRequest createOrderRequest = new CreateOrderRequest(new ArrayList<>());
        checkCreateOrderFaultResponseOnEmptyIngrList(SendRequest.sendCreateOrder(createOrderRequest));
    }

    @Test
    @DisplayName("Попытка создания заказа без ингридиентов без авторизации")
    public void onEmptyRequestWithOutAuthorisationReturnsCorrectResponse(){
        CreateOrderRequest createOrderRequest = new CreateOrderRequest(new ArrayList<>());
        checkCreateOrderFaultResponseOnEmptyIngrList(SendRequest.sendCreateOrderWithoutAuthorisation(createOrderRequest));
    }

    @Test
    @DisplayName("Попытка создания заказа c некорректным  ингридиентом  с авторизацией")
    public void onInvalidRequestWithAuthorisationReturnsCorrectResponse(){
        List<String> invalidIngredientList = Generate.ingredientListForRequest(getIngredientsResponse);
        invalidIngredientList.add("invalidIngrId");
        CreateOrderRequest createOrderRequest = new CreateOrderRequest(invalidIngredientList);
        checkCreateOrderFaultResponse(SendRequest.sendCreateOrder(createOrderRequest));
    }

    @Test
    @DisplayName("Попытка создания заказа c некорректным  ингридиентом без авторизации")
    public void onInvalidRequestWithOutAuthorisationReturnsCorrectResponse(){
        List<String> invalidIngredientList = new ArrayList<>();
        invalidIngredientList.add("onlyInvalidIngrId");
        CreateOrderRequest createOrderRequest = new CreateOrderRequest(invalidIngredientList);
        checkCreateOrderFaultResponse(SendRequest.sendCreateOrderWithoutAuthorisation(createOrderRequest));
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
