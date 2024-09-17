package model;

import model.objects.OrderNumber;

public class CreateOrderResponseShort {

    private String name;
    private OrderNumber order;
    private boolean success;

    public CreateOrderResponseShort() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OrderNumber getOrder() {
        return order;
    }

    public void setOrder(OrderNumber order) {
        this.order = order;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
