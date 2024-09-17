import configs.Configs;
import utils.Tokens;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.CreateUserRequest;
import model.LoginRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import steps.Generate;
import steps.SendRequest;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class LoginTests {



    @Step("Проверка ответа на запрос на авторизацию")
    public void checkResponseOnSuccessfulLogin(Response loginResponse, CreateUserRequest createUserRequest){
        loginResponse.then()
                .assertThat()
                .statusCode(SC_OK)
                .and().body("success",equalTo(true))
                .and().body("user.email",equalTo(createUserRequest.getEmail()))
                .and().body("user.name",equalTo(createUserRequest.getName()))
                .and().body("accessToken",notNullValue())
                .and().body("refreshToken",notNullValue());
    }

    @Step("Проверка ответа при неуспешной авторизации")
    public void checkResponseOnFailedAuthorisation(Response loginResponse){
        loginResponse.then()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and().body("success",equalTo(false))
                .and().body("message",equalTo("email or password are incorrect"));
    }
    CreateUserTests createUserTests = new CreateUserTests();
    CreateUserRequest createUserRequest = new CreateUserRequest(Generate.userMail(),Generate.userName(),Generate.password());

    @Before
    public void setUp(){
        RestAssured.baseURI = Configs.BASE_URL;
        Response response = SendRequest.sendCreateUser(createUserRequest);
        createUserTests.checkResponseOnSuccessfulUserCreation(response,createUserRequest);
    }

    @Test
    @DisplayName("Успешная авторизация")
    public void successfulAuthorisationTest(){
        LoginRequest loginRequest = new LoginRequest(createUserRequest.getEmail(), createUserRequest.getPassword());
        Response response = SendRequest.sendLogin(loginRequest);
        checkResponseOnSuccessfulLogin(response, createUserRequest);
    }

    @Test
    @DisplayName("Неуспешная авторизация с некорректными данными")
    public void onWrongCreditalsReturnsCorrectResponse(){
        LoginRequest loginRequest = new LoginRequest(createUserRequest.getEmail(), createUserRequest.getPassword()+"xxx");
        Response response = SendRequest.sendLogin(loginRequest);
        checkResponseOnFailedAuthorisation(response);
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
