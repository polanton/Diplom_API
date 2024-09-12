package model.objects;

import java.util.ArrayList;
import java.util.List;

public class OrderFull {
    private List<Ingredient> ingredients;
    private Owner owner;
    private String _id;
    private String status;
    private String name;
    private String createdAt;
    private String updatedAt;
    private int number;
    private int price;

    public OrderFull() {}


    public int getIngredientsPriceSum(){
        int priceSum = 0;
        for(Ingredient ingredient : ingredients){
            priceSum+=  ingredient.getPrice();
        }
        return priceSum;
    }

    public List<String> getIngredientsIdList(){
        List<String> result = new ArrayList<>();
        for (Ingredient ingredient:ingredients){
          result.add(ingredient.get_id());
        }
        return result;
    }

    public boolean hasAllIngredients(List<String> expectedIngredients){
        return
                (this.getIngredientsIdList().isEmpty() && expectedIngredients.isEmpty()) ||
                        (this.getIngredientsIdList().containsAll(expectedIngredients) && expectedIngredients.containsAll(this.getIngredientsIdList()));

    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }



    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}

