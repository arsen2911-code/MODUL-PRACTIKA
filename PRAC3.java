import java.util.ArrayList;
import java.util.List;

interface IPayment {
    void processPayment(double amount);
}

interface IDelivery {
    void deliverOrder(Order order);
}

interface INotification {
    void sendNotification(String message);
}

interface IDiscountStrategy {
    double applyDiscount(double totalAmount);
}

class CreditCardPayment implements IPayment {
    public void processPayment(double amount) {
        System.out.println("Payment via Credit Card: " + amount);
    }
}

class PayPalPayment implements IPayment {
    public void processPayment(double amount) {
        System.out.println("Payment via PayPal: " + amount);
    }
}

class CourierDelivery implements IDelivery {
    public void deliverOrder(Order order) {
        System.out.println("Courier delivery initiated.");
    }
}

class PostDelivery implements IDelivery {
    public void deliverOrder(Order order) {
        System.out.println("Post delivery initiated.");
    }
}

class EmailNotification implements INotification {
    public void sendNotification(String message) {
        System.out.println("Email sent: " + message);
    }
}

class SmsNotification implements INotification {
    public void sendNotification(String message) {
        System.out.println("SMS sent: " + message);
    }
}

class NoDiscount implements IDiscountStrategy {
    public double applyDiscount(double totalAmount) {
        return totalAmount;
    }
}

class VipDiscount implements IDiscountStrategy {
    public double applyDiscount(double totalAmount) {
        return totalAmount * 0.90;
    }
}

class Order {
    private List<String> items = new ArrayList<>();
    private double totalAmount = 0;

    private IPayment paymentMethod;
    private IDelivery deliveryMethod;
    private INotification notificationService;
    private IDiscountStrategy discountStrategy;

    public Order(IPayment payment, IDelivery delivery, INotification notification, IDiscountStrategy discount) {
        this.paymentMethod = payment;
        this.deliveryMethod = delivery;
        this.notificationService = notification;
        this.discountStrategy = discount;
    }

    public void addItem(String item, double price) {
        items.add(item);
        totalAmount += price;
        System.out.println("Item added: " + item);
    }

    public void processOrder() {
        double finalPrice = discountStrategy.applyDiscount(totalAmount);
        System.out.println("Total amount: " + finalPrice);

        paymentMethod.processPayment(finalPrice);
        deliveryMethod.deliverOrder(this);
        notificationService.sendNotification("Order processed successfully.");
    }
}

public class PRAC3 {
    public static void main(String[] args) {
        IPayment myPayment = new CreditCardPayment();
        IDelivery myDelivery = new CourierDelivery();
        INotification myNotification = new EmailNotification();
        IDiscountStrategy myDiscount = new VipDiscount();

        Order order = new Order(myPayment, myDelivery, myNotification, myDiscount);

        order.addItem("Laptop", 1000.0);
        order.addItem("Mouse", 50.0);

        order.processOrder();
    }
}