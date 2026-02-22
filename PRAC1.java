import java.util.*;

// MAIN

public class PRAC1 {

    public static void main(String[] args) {

        Vehicle car1 = new Car("Toyota", "Camry", 2022, 4, "Automatic");
        Vehicle car2 = new Car("BMW", "M5", 2023, 4, "Manual");
        Vehicle moto1 = new Motorcycle("Yamaha", "R1", 2021, "Sport", true);

        Garage garage1 = new Garage("Garage A");
        Garage garage2 = new Garage("Garage B");

        garage1.addVehicle(car1);
        garage1.addVehicle(moto1);

        garage2.addVehicle(car2);

        Fleet fleet = new Fleet();
        fleet.addGarage(garage1);
        fleet.addGarage(garage2);

        car1.startEngine();
        moto1.startEngine();

        System.out.println("\nПоиск Toyota:");
        fleet.findVehicle("Toyota");

        garage1.removeVehicle(moto1);

        fleet.removeGarage(garage2);
    }
}

//  VEHICLE

class Vehicle {

    protected String brand;
    protected String model;
    protected int year;

    public Vehicle(String brand, String model, int year) {
        this.brand = brand;
        this.model = model;
        this.year = year;
    }

    public void startEngine() {
        System.out.println(brand + " " + model + " двигатель запущен.");
    }

    public void stopEngine() {
        System.out.println(brand + " " + model + " двигатель остановлен.");
    }

    public String getBrand() {
        return brand;
    }

    @Override
    public String toString() {
        return brand + " " + model + " (" + year + ")";
    }
}

// CAR

class Car extends Vehicle {

    private int doors;
    private String transmission;

    public Car(String brand, String model, int year, int doors, String transmission) {
        super(brand, model, year);
        this.doors = doors;
        this.transmission = transmission;
    }

    @Override
    public void startEngine() {
        System.out.println("Автомобиль " + brand + " запущен. Трансмиссия: " + transmission);
    }
}

//  MOTORCYCLE

class Motorcycle extends Vehicle {

    private String bodyType;
    private boolean hasSideBox;

    public Motorcycle(String brand, String model, int year, String bodyType, boolean hasSideBox) {
        super(brand, model, year);
        this.bodyType = bodyType;
        this.hasSideBox = hasSideBox;
    }

    @Override
    public void startEngine() {
        System.out.println("Мотоцикл " + brand + " запущен. Тип: " + bodyType);
    }
}

//  GARAGE

class Garage {

    private String name;
    private List<Vehicle> vehicles = new ArrayList<>();

    public Garage(String name) {
        this.name = name;
    }

    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
        System.out.println(vehicle + " добавлен в " + name);
    }

    public void removeVehicle(Vehicle vehicle) {
        vehicles.remove(vehicle);
        System.out.println(vehicle + " удален из " + name);
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }
}

//  FLEET

class Fleet {

    private List<Garage> garages = new ArrayList<>();

    public void addGarage(Garage garage) {
        garages.add(garage);
        System.out.println("Гараж добавлен в автопарк.");
    }

    public void removeGarage(Garage garage) {
        garages.remove(garage);
        System.out.println("Гараж удален из автопарка.");
    }

    public void findVehicle(String brand) {
        for (Garage garage : garages) {
            for (Vehicle v : garage.getVehicles()) {
                if (v.getBrand().equalsIgnoreCase(brand)) {
                    System.out.println("Найдено: " + v);
                }
            }
        }
    }
}