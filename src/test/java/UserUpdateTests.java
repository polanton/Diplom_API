import configs.Configs;
import utils.Tokens;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.CreateUserRequest;
import model.UpdateUserRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import steps.Generate;
import steps.SendRequest;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;

public class UserUpdateTests {

    @Step
    public void checkSuccessfulUpdateResponse(Response response, UpdateUserRequest request){
            response.then()
                    .assertThat()
                    .statusCode(SC_OK)
                    .and().body("success",equalTo(true))
                    .and().body("user.email",equalTo(request.getEmail()))
                    .and().body("user.name",equalTo(request.getName()));
    }

    @Step
    public void checkFaultResponseOnRequestWithoutAuthorisation(Response response){
        response.then()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and().body("success",equalTo(false))
                .and().body("message",equalTo("You should be authorised"));
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
    @DisplayName("Успешное обновление поля email")
    public void updateMailUpdatesMail(){
        UpdateUserRequest updateRequest = new UpdateUserRequest(Generate.userMail(), createUserRequest.getName());
        checkSuccessfulUpdateResponse(SendRequest.sendUpdateUser(updateRequest), updateRequest);
    }

    @Test
    @DisplayName("Успешное обновление поля name")
    public void updateNameUpdatesName(){
        UpdateUserRequest updateRequest = new UpdateUserRequest(createUserRequest.getEmail(), Generate.userName());
        checkSuccessfulUpdateResponse(SendRequest.sendUpdateUser(updateRequest), updateRequest);
    }

    @Test
    @DisplayName("Успешное обновление name и email")
    public void updateAllUpdatesAllFields(){
        UpdateUserRequest updateRequest = new UpdateUserRequest(Generate.userMail(), Generate.userName());
        checkSuccessfulUpdateResponse(SendRequest.sendUpdateUser(updateRequest), updateRequest);
    }

    @Test
    @DisplayName("Попытка обновления пользователя без Authorization")
    public void updateWithoutAuthorisationReturnsCorrectMessage(){
        UpdateUserRequest updateRequest = new UpdateUserRequest(Generate.userMail(), Generate.userName());
        checkFaultResponseOnRequestWithoutAuthorisation(SendRequest.sendUpdateUserWithoutAuthorisation(updateRequest));
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
