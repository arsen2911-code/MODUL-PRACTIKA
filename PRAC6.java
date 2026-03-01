import java.util.*;
import java.util.concurrent.*;


interface ICostCalculationStrategy {
    double calculateCost(TravelRequest request);
}

class TravelRequest {
    double distance;
    int passengers;
    boolean isChildDiscount;
    boolean isSeniorDiscount;
    boolean hasLuggage;
    String serviceClass; // economy / business
    double regionalCoefficient;

    public TravelRequest(double distance, int passengers,
                         boolean isChildDiscount,
                         boolean isSeniorDiscount,
                         boolean hasLuggage,
                         String serviceClass,
                         double regionalCoefficient) {

        if (distance <= 0 || passengers <= 0)
            throw new IllegalArgumentException("Некорректные данные!");

        this.distance = distance;
        this.passengers = passengers;
        this.isChildDiscount = isChildDiscount;
        this.isSeniorDiscount = isSeniorDiscount;
        this.hasLuggage = hasLuggage;
        this.serviceClass = serviceClass;
        this.regionalCoefficient = regionalCoefficient;
    }
}

class PlaneStrategy implements ICostCalculationStrategy {

    @Override
    public double calculateCost(TravelRequest r) {
        double base = r.distance * 0.5;

        if (r.serviceClass.equalsIgnoreCase("business"))
            base *= 1.8;

        if (r.hasLuggage)
            base += 30;

        base *= r.regionalCoefficient;

        base *= r.passengers;

        if (r.isChildDiscount)
            base *= 0.7;

        if (r.isSeniorDiscount)
            base *= 0.8;

        return base;
    }
}

class TrainStrategy implements ICostCalculationStrategy {

    @Override
    public double calculateCost(TravelRequest r) {
        double base = r.distance * 0.3;

        if (r.serviceClass.equalsIgnoreCase("business"))
            base *= 1.4;

        if (r.hasLuggage)
            base += 10;

        base *= r.passengers;

        return base;
    }
}


class BusStrategy implements ICostCalculationStrategy {

    @Override
    public double calculateCost(TravelRequest r) {
        double base = r.distance * 0.2;

        if (r.hasLuggage)
            base += 5;

        base *= r.passengers;

        return base;
    }
}


class TravelBookingContext {
    private ICostCalculationStrategy strategy;

    public void setStrategy(ICostCalculationStrategy strategy) {
        this.strategy = strategy;
    }

    public double calculate(TravelRequest request) {
        if (strategy == null)
            throw new IllegalStateException("Стратегия не выбрана!");

        return strategy.calculateCost(request);
    }
}


interface IObserver {
    void update(String stock, double price);
}

interface ISubject {
    void subscribe(String stock, IObserver observer);
    void unsubscribe(String stock, IObserver observer);
    void notifyObservers(String stock, double price);
}

class StockExchange implements ISubject {

    private Map<String, Double> stocks = new ConcurrentHashMap<>();
    private Map<String, List<IObserver>> subscribers = new ConcurrentHashMap<>();

    @Override
    public void subscribe(String stock, IObserver observer) {
        subscribers.computeIfAbsent(stock, k -> new CopyOnWriteArrayList<>()).add(observer);
        log("Подписка на " + stock);
    }

    @Override
    public void unsubscribe(String stock, IObserver observer) {
        if (subscribers.containsKey(stock))
            subscribers.get(stock).remove(observer);

        log("Отписка от " + stock);
    }

    @Override
    public void notifyObservers(String stock, double price) {
        if (!subscribers.containsKey(stock)) return;

        for (IObserver obs : subscribers.get(stock)) {
            CompletableFuture.runAsync(() -> obs.update(stock, price));
        }
    }

    public void setPrice(String stock, double price) {
        stocks.put(stock, price);
        log("Цена обновлена: " + stock + " = " + price);
        notifyObservers(stock, price);
    }

    private void log(String message) {
        System.out.println("[LOG] " + message);
    }

    public void generateReport() {
        System.out.println("\n=== ОТЧЕТ ПО ПОДПИСЧИКАМ ===");
        for (String stock : subscribers.keySet()) {
            System.out.println(stock + " -> " + subscribers.get(stock).size() + " подписчиков");
        }
    }
}

class Trader implements IObserver {
    @Override
    public void update(String stock, double price) {
        System.out.println("Трейдер получил обновление: " + stock + " = " + price);
    }
}

class TradingRobot implements IObserver {
    private double threshold;

    public TradingRobot(double threshold) {
        this.threshold = threshold;
    }

    @Override
    public void update(String stock, double price) {
        if (price > threshold)
            System.out.println("Робот: ПРОДАЖА " + stock);
        else
            System.out.println("Робот: ПОКУПКА " + stock);
    }
}

public class PRAC6 {

    public static void main(String[] args) {


        TravelBookingContext context = new TravelBookingContext();

        TravelRequest request = new TravelRequest(
                1000, 2, true, false,
                true, "business", 1.2
        );

        context.setStrategy(new PlaneStrategy());
        System.out.println("Самолет: " + context.calculate(request));

        context.setStrategy(new TrainStrategy());
        System.out.println("Поезд: " + context.calculate(request));

        context.setStrategy(new BusStrategy());
        System.out.println("Автобус: " + context.calculate(request));
        

        StockExchange exchange = new StockExchange();

        Trader trader = new Trader();
        TradingRobot robot = new TradingRobot(100);

        exchange.subscribe("AAPL", trader);
        exchange.subscribe("AAPL", robot);
        exchange.subscribe("GOOG", robot);

        exchange.setPrice("AAPL", 120);
        exchange.setPrice("GOOG", 80);

        exchange.generateReport();
    }
}