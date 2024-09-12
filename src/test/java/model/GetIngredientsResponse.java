package model;

import model.objects.Ingredient;
import steps.SendRequest;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GetIngredientsResponse {
    private boolean success;
    private List<Ingredient> data;

    public Ingredient getRandomingredient(){
        List<Ingredient> allIngredients = this.getData();
        int randomIngredientId = ThreadLocalRandom.current().nextInt(0, allIngredients.size());
        return   allIngredients.get(randomIngredientId);
    }

    public String getRandomIngredientId(){
        return this.getRandomingredient().get_id();
    }

    public GetIngredientsResponse() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Ingredient> getData() {
        return data;
    }

    public void setData(List<Ingredient> data) {
        this.data = data;
    }
}
