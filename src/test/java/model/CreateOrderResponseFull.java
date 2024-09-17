package model;

import model.objects.Ingredient;
import model.objects.Order;
import model.objects.OrderFull;
import model.objects.OrderNumber;

import java.util.List;

public class CreateOrderResponseFull {

    private boolean success;
    private String name;
    private OrderFull order;


    public CreateOrderResponseFull() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OrderFull getOrder() {
        return order;
    }

    public void setOrder(OrderFull order) {
        this.order = order;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
