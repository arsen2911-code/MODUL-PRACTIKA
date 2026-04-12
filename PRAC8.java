import java.util.*;


// 1. Интерфейс
interface IReport {
    String generate();
}

// 2. Базовые отчеты
class SalesReport implements IReport {
    public String generate() {
        return "Sales Report: [Order1: 100$, Order2: 200$, Order3: 150$]";
    }
}

class UserReport implements IReport {
    public String generate() {
        return "User Report: [User1, User2, User3]";
    }
}

// 3. Абстрактный декоратор
abstract class ReportDecorator implements IReport {
    protected IReport report;

    public ReportDecorator(IReport report) {
        this.report = report;
    }

    public String generate() {
        return report.generate();
    }
}

// 4. Декораторы

// Фильтр по датам
class DateFilterDecorator extends ReportDecorator {
    private String fromDate;
    private String toDate;

    public DateFilterDecorator(IReport report, String fromDate, String toDate) {
        super(report);
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public String generate() {
        return report.generate() + " | Filtered by date: " + fromDate + " - " + toDate;
    }
}

// Сортировка
class SortingDecorator extends ReportDecorator {
    private String criteria;

    public SortingDecorator(IReport report, String criteria) {
        super(report);
        this.criteria = criteria;
    }

    public String generate() {
        return report.generate() + " | Sorted by: " + criteria;
    }
}

// CSV экспорт
class CsvExportDecorator extends ReportDecorator {
    public CsvExportDecorator(IReport report) {
        super(report);
    }

    public String generate() {
        return report.generate() + " | Exported to CSV";
    }
}

// PDF экспорт
class PdfExportDecorator extends ReportDecorator {
    public PdfExportDecorator(IReport report) {
        super(report);
    }

    public String generate() {
        return report.generate() + " | Exported to PDF";
    }
}

// Доп: фильтр по сумме
class AmountFilterDecorator extends ReportDecorator {
    private double minAmount;

    public AmountFilterDecorator(IReport report, double minAmount) {
        super(report);
        this.minAmount = minAmount;
    }

    public String generate() {
        return report.generate() + " | Filtered by amount > " + minAmount;
    }
}

class ReportClient {
    public static IReport buildReport(String type, List<String> options) {
        IReport report;

        if (type.equals("sales")) {
            report = new SalesReport();
        } else {
            report = new UserReport();
        }

        for (String opt : options) {
            switch (opt) {
                case "date":
                    report = new DateFilterDecorator(report, "2025-01-01", "2025-12-31");
                    break;
                case "sort":
                    report = new SortingDecorator(report, "amount");
                    break;
                case "csv":
                    report = new CsvExportDecorator(report);
                    break;
                case "pdf":
                    report = new PdfExportDecorator(report);
                    break;
                case "amount":
                    report = new AmountFilterDecorator(report, 150);
                    break;
            }
        }

        return report;
    }
}


// 1. Интерфейс внутренней службы
interface IInternalDeliveryService {
    void deliverOrder(String orderId);
    String getDeliveryStatus(String orderId);
    double calculateCost(String orderId);
}

// 2. Внутренняя служба
class InternalDeliveryService implements IInternalDeliveryService {

    public void deliverOrder(String orderId) {
        System.out.println("Internal delivery: Order " + orderId + " delivered.");
    }

    public String getDeliveryStatus(String orderId) {
        return "Internal: Delivered";
    }

    public double calculateCost(String orderId) {
        return 10.0;
    }
}


class ExternalLogisticsServiceA {
    public void shipItem(int itemId) {
        System.out.println("ServiceA: Shipping item " + itemId);
    }

    public String trackShipment(int shipmentId) {
        return "ServiceA: In transit";
    }

    public double getPrice(int itemId) {
        return 20.0;
    }
}

class ExternalLogisticsServiceB {
    public void sendPackage(String info) {
        System.out.println("ServiceB: Sending package " + info);
    }

    public String checkPackageStatus(String code) {
        return "ServiceB: Delivered";
    }

    public double computeCost(String code) {
        return 25.0;
    }
}

// ДОП СЕРВИС
class ExternalLogisticsServiceC {
    public void dispatch(String id) {
        System.out.println("ServiceC: Dispatch " + id);
    }

    public String status(String id) {
        return "ServiceC: Pending";
    }

    public double cost(String id) {
        return 30.0;
    }
}


class LogisticsAdapterA implements IInternalDeliveryService {
    private ExternalLogisticsServiceA service = new ExternalLogisticsServiceA();

    public void deliverOrder(String orderId) {
        try {
            service.shipItem(Integer.parseInt(orderId));
        } catch (Exception e) {
            System.out.println("Error in AdapterA: " + e.getMessage());
        }
    }

    public String getDeliveryStatus(String orderId) {
        return service.trackShipment(Integer.parseInt(orderId));
    }

    public double calculateCost(String orderId) {
        return service.getPrice(Integer.parseInt(orderId));
    }
}

class LogisticsAdapterB implements IInternalDeliveryService {
    private ExternalLogisticsServiceB service = new ExternalLogisticsServiceB();

    public void deliverOrder(String orderId) {
        service.sendPackage(orderId);
    }

    public String getDeliveryStatus(String orderId) {
        return service.checkPackageStatus(orderId);
    }

    public double calculateCost(String orderId) {
        return service.computeCost(orderId);
    }
}

class LogisticsAdapterC implements IInternalDeliveryService {
    private ExternalLogisticsServiceC service = new ExternalLogisticsServiceC();

    public void deliverOrder(String orderId) {
        service.dispatch(orderId);
    }

    public String getDeliveryStatus(String orderId) {
        return service.status(orderId);
    }

    public double calculateCost(String orderId) {
        return service.cost(orderId);
    }
}


class DeliveryServiceFactory {
    public static IInternalDeliveryService getService(String type) {
        switch (type) {
            case "internal":
                return new InternalDeliveryService();
            case "A":
                return new LogisticsAdapterA();
            case "B":
                return new LogisticsAdapterB();
            case "C":
                return new LogisticsAdapterC();
            default:
                throw new IllegalArgumentException("Unknown service type");
        }
    }
}


public class PRAC8 {
    public static void main(String[] args) {

        // ===== DECORATOR TEST =====
        System.out.println("=== REPORT SYSTEM ===");

        List<String> options = Arrays.asList("date", "sort", "csv", "amount");
        IReport report = ReportClient.buildReport("sales", options);

        System.out.println(report.generate());

        // ===== ADAPTER TEST =====
        System.out.println("\n=== DELIVERY SYSTEM ===");

        IInternalDeliveryService service = DeliveryServiceFactory.getService("A");

        service.deliverOrder("123");
        System.out.println(service.getDeliveryStatus("123"));
        System.out.println("Cost: " + service.calculateCost("123"));
    }
}