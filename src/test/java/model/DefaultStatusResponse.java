package model;

public class DefaultStatusResponse {

  /* Универсальный ответ с 2мя полями
  {"success": false,
"message": "User with such email already exists"}
   */

    private boolean success;
    private String message;

    public DefaultStatusResponse() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
