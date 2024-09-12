import configs.Configs;
import utils.Tokens;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.CreateUserRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import steps.Generate;
import steps.SendRequest;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class CreateUserTests {

    boolean userIsCreated = false;

    @Step("Проверка успешности создания пользователя при валидном запросе")
    public void checkResponseOnSuccessfulUserCreation(Response response, CreateUserRequest createUserRequest){

        response.then()
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success",equalTo(true))
                .and()
                .body("user.email",equalTo(createUserRequest.getEmail()))
                .and()
                .body("user.name",equalTo(createUserRequest.getName()))
                .and()
                .body("accessToken",notNullValue())
                .and()
                .body("refreshToken",notNullValue());

            //если прошло успешное создание пользователся - прикапываем признак успешного создания и токены
            userIsCreated = true;
            Tokens.setAccessToken(response.then().extract().body().path("accessToken").toString());
            Tokens.setRefreshToken(response.then().extract().body().path("refreshToken").toString());

    }

    @Step("Проверка ответа при повторном создании пользователя")
    public void checkResponseOnRepeatedUserCreation(Response response){
        response.then()
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("success",equalTo(false))
                .and()
                .body("message",equalTo("User already exists"));

        if (response.getStatusCode() == SC_CREATED){ userIsCreated = true;       }
    }

    @Step("Проверка ответа при отсутствии в запросе на создание пользователя одного из полей")
    public void checkResponseOnInvalidRequest(Response response){
        response.then()
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("success",equalTo(false))
                .and()
                .body("message",equalTo("Email, password and name are required fields"));

        if (response.getStatusCode() == SC_CREATED){ userIsCreated = true;       }
    }

    @Before
    public void setUp(){
        RestAssured.baseURI = Configs.BASE_URL;
        userIsCreated = false;
    }

    @Test
    @DisplayName("Успешное создание пользователя")
    public void onValidRequestReturnsCorrectResponse(){
        CreateUserRequest createUserRequest = new CreateUserRequest(Generate.userMail(),Generate.userName(),Generate.password());
        checkResponseOnSuccessfulUserCreation(SendRequest.sendCreateUser(createUserRequest), createUserRequest);


    }

    @Test
    @DisplayName("Попытка повторного создания пользователя")
    public void onRepeatedRequestReturnsCorrectErrorResponse(){
        CreateUserRequest createUserRequest = new CreateUserRequest(Generate.userMail(),Generate.userName(),Generate.password());
        checkResponseOnSuccessfulUserCreation(SendRequest.sendCreateUser(createUserRequest), createUserRequest);
        checkResponseOnRepeatedUserCreation(SendRequest.sendCreateUser(createUserRequest));
    }

    @Test
    @DisplayName("Попытка создания пользователя без обязательного элемента")
    public void onInvalidRequestReturnsCorrectErrorResponse(){
        CreateUserRequest createUserRequest = new CreateUserRequest(Generate.userMail(),Generate.password());
        checkResponseOnInvalidRequest(SendRequest.sendCreateUser(createUserRequest));

    }

    @After
    public void tearDown(){
        if (userIsCreated) {
            SendRequest.sendDeleteUser();
            userIsCreated = false;
            Tokens.setAccessToken("");
            Tokens.setRefreshToken("");
        }
    }

}
/*{
        "success": true,
        "user": {
        "email": "test-polanton@yandex.ru",
        "name": "Username"
        },
        "accessToken": "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjY2ZGY2NjkwOWVkMjgwMDAxYjRiMmY3YiIsImlhdCI6MTcyNTkxNjgxNiwiZXhwIjoxNzI1OTE4MDE2fQ.fxNnSrJM4VrhmNSeDQRs-nXVcyn4yPkFJJHUvzCxlPw",
        "refreshToken": "22cc57592767f9db6e31856885ca0aaedb4f90eefd8de032a16d3a45360064e41c437ad092580cd7"
        }

 */