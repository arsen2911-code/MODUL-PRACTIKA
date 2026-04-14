import java.util.*;

class RoomBookingSystem {
    public void bookRoom(String guest, int room) {
        System.out.println("Room booked for " + guest + " in room " + room);
    }

    public void cancelRoom(String guest) {
        System.out.println("Room booking cancelled for " + guest);
    }

    public boolean checkAvailability(int room) {
        return true;
    }
}

class RestaurantSystem {
    public void bookTable(String guest) {
        System.out.println("Table booked for " + guest);
    }

    public void orderFood(String guest) {
        System.out.println("Food ordered for " + guest);
    }
}

class EventManagementSystem {
    public void bookHall(String event) {
        System.out.println("Hall booked for " + event);
    }

    public void orderEquipment(String event) {
        System.out.println("Equipment ordered for " + event);
    }
}

class CleaningService {
    public void scheduleCleaning(String room) {
        System.out.println("Cleaning scheduled for room " + room);
    }

    public void performCleaning(String room) {
        System.out.println("Cleaning done for room " + room);
    }
}

class TaxiService {
    public void callTaxi(String guest) {
        System.out.println("Taxi called for " + guest);
    }
}

class HotelFacade {
    private RoomBookingSystem roomSystem = new RoomBookingSystem();
    private RestaurantSystem restaurantSystem = new RestaurantSystem();
    private EventManagementSystem eventSystem = new EventManagementSystem();
    private CleaningService cleaningService = new CleaningService();
    private TaxiService taxiService = new TaxiService();

    public void bookFullPackage(String guest, int room) {
        if (roomSystem.checkAvailability(room)) {
            roomSystem.bookRoom(guest, room);
            restaurantSystem.orderFood(guest);
            cleaningService.scheduleCleaning(String.valueOf(room));
        }
    }

    public void organizeEvent(String event, List<String> guests) {
        eventSystem.bookHall(event);
        eventSystem.orderEquipment(event);
        for (String g : guests) {
            roomSystem.bookRoom(g, new Random().nextInt(100));
        }
    }

    public void bookRestaurantWithTaxi(String guest) {
        restaurantSystem.bookTable(guest);
        taxiService.callTaxi(guest);
    }

    public void cancelBooking(String guest) {
        roomSystem.cancelRoom(guest);
    }

    public void requestCleaning(String room) {
        cleaningService.performCleaning(room);
    }
}


abstract class OrganizationComponent {
    public void add(OrganizationComponent c) {}
    public void remove(OrganizationComponent c) {}
    public List<OrganizationComponent> getChildren() { return new ArrayList<>(); }

    public abstract double getBudget();
    public abstract int getEmployeeCount();
    public abstract void show(String indent);
    public OrganizationComponent find(String name) { return null; }
    public List<Employee> getAllEmployees() { return new ArrayList<>(); }
}

class Employee extends OrganizationComponent {
    protected String name;
    protected String position;
    protected double salary;

    public Employee(String name, String position, double salary) {
        this.name = name;
        this.position = position;
        this.salary = salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public double getBudget() {
        return salary;
    }

    public int getEmployeeCount() {
        return 1;
    }

    public void show(String indent) {
        System.out.println(indent + name + " (" + position + ") $" + salary);
    }

    public OrganizationComponent find(String name) {
        if (this.name.equals(name)) return this;
        return null;
    }

    public List<Employee> getAllEmployees() {
        return Arrays.asList(this);
    }
}

class Contractor extends Employee {
    public Contractor(String name, String position, double salary) {
        super(name, position, salary);
    }

    public double getBudget() {
        return 0;
    }
}

class Department extends OrganizationComponent {
    private String name;
    private List<OrganizationComponent> children = new ArrayList<>();

    public Department(String name) {
        this.name = name;
    }

    public void add(OrganizationComponent c) {
        children.add(c);
    }

    public void remove(OrganizationComponent c) {
        children.remove(c);
    }

    public List<OrganizationComponent> getChildren() {
        return children;
    }

    public double getBudget() {
        double sum = 0;
        for (OrganizationComponent c : children) {
            sum += c.getBudget();
        }
        return sum;
    }

    public int getEmployeeCount() {
        int count = 0;
        for (OrganizationComponent c : children) {
            count += c.getEmployeeCount();
        }
        return count;
    }

    public void show(String indent) {
        System.out.println(indent + "Department: " + name);
        for (OrganizationComponent c : children) {
            c.show(indent + "  ");
        }
    }

    public OrganizationComponent find(String name) {
        for (OrganizationComponent c : children) {
            OrganizationComponent res = c.find(name);
            if (res != null) return res;
        }
        return null;
    }

    public List<Employee> getAllEmployees() {
        List<Employee> list = new ArrayList<>();
        for (OrganizationComponent c : children) {
            list.addAll(c.getAllEmployees());
        }
        return list;
    }
}


public class PRAC9 {
    public static void main(String[] args) {

        HotelFacade facade = new HotelFacade();

        facade.bookFullPackage("Arsen", 101);
        facade.organizeEvent("Conference", Arrays.asList("A", "B", "C"));
        facade.bookRestaurantWithTaxi("Arsen");
        facade.cancelBooking("Arsen");
        facade.requestCleaning("101");

        Department root = new Department("Head Office");

        Department it = new Department("IT");
        Department hr = new Department("HR");

        Employee e1 = new Employee("John", "Dev", 3000);
        Employee e2 = new Employee("Anna", "HR", 2000);
        Contractor c1 = new Contractor("Mike", "Temp", 1500);

        it.add(e1);
        hr.add(e2);
        hr.add(c1);

        root.add(it);
        root.add(hr);

        root.show("");

        System.out.println("Budget: " + root.getBudget());
        System.out.println("Employees: " + root.getEmployeeCount());

        OrganizationComponent found = root.find("John");
        if (found != null) {
            found.show("Found: ");
        }

        List<Employee> all = root.getAllEmployees();
        System.out.println("All employees:");
        for (Employee e : all) {
            System.out.println(e.name);
        }

        e1.setSalary(4000);
        System.out.println("Updated Budget: " + root.getBudget());
    }
}
