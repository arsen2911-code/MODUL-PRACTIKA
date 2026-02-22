
import java.util.Scanner;

interface Document {
    void open();
}

class Report implements Document {
    @Override
    public void open() {
        System.out.println("Открытие документа: ОТЧЕТ (Report)");
    }
}

class Resume implements Document {
    @Override
    public void open() {
        System.out.println("Открытие документа: РЕЗЮМЕ (Resume)");
    }
}

class Letter implements Document {
    @Override
    public void open() {
        System.out.println("Открытие документа: ПИСЬМО (Letter)");
    }
}

class Invoice implements Document {
    @Override
    public void open() {
        System.out.println("Открытие документа: СЧЕТ (Invoice)");
    }
}

abstract class DocumentCreator {
    public abstract Document createDocument();

    public void openDocument() {
        Document doc = createDocument();
        doc.open();
    }
}

class ReportCreator extends DocumentCreator {
    @Override
    public Document createDocument() {
        return new Report();
    }
}

class ResumeCreator extends DocumentCreator {
    @Override
    public Document createDocument() {
        return new Resume();
    }
}

class LetterCreator extends DocumentCreator {
    @Override
    public Document createDocument() {
        return new Letter();
    }
}

class InvoiceCreator extends DocumentCreator {
    @Override
    public Document createDocument() {
        return new Invoice();
    }
}

public class PRAC4 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- Система управления документами ---");
            System.out.println("1. Отчет (Report)");
            System.out.println("2. Резюме (Resume)");
            System.out.println("3. Письмо (Letter)");
            System.out.println("4. Счет (Invoice)");
            System.out.println("0. Выход");
            System.out.print("Ваш выбор: ");

            String choice = scanner.nextLine();

            if (choice.equals("0")) break;

            DocumentCreator factory = null;

            switch (choice) {
                case "1":
                    factory = new ReportCreator();
                    break;
                case "2":
                    factory = new ResumeCreator();
                    break;
                case "3":
                    factory = new LetterCreator();
                    break;
                case "4":
                    factory = new InvoiceCreator();
                    break;
                default:
                    System.out.println("Неверный выбор.");
                    continue;
            }

            if (factory != null) {
                Document doc = factory.createDocument();
                doc.open();
            }
        }
        scanner.close();
    }
}
