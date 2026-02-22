import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

//  MAIN

public class PRAC5 {

    public static void main(String[] args) throws Exception {

        System.out.println("===== SINGLETON LOGGER =====");

        Logger logger = Logger.getInstance();
        logger.setLogLevel(LogLevel.INFO);

        Runnable task = () -> {
            Logger log = Logger.getInstance();
            log.log("Информация от " + Thread.currentThread().getName(), LogLevel.INFO);
            log.log("Предупреждение от " + Thread.currentThread().getName(), LogLevel.WARNING);
            log.log("Ошибка от " + Thread.currentThread().getName(), LogLevel.ERROR);
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        Thread t3 = new Thread(task);

        t1.start(); t2.start(); t3.start();
        t1.join(); t2.join(); t3.join();

        System.out.println("\n===== LOG READER (ERROR ONLY) =====");
        LogReader.readLogs("app.log", LogLevel.ERROR);

        System.out.println("\n===== BUILDER =====");

        ReportStyle style = new ReportStyle("white", "black", 14);

        IReportBuilder textBuilder = new TextReportBuilder();
        ReportDirector director = new ReportDirector();
        director.constructReport(textBuilder, style);
        System.out.println(textBuilder.getReport().export());

        System.out.println("\n===== PROTOTYPE =====");

        GameCharacter template = new GameCharacter(
                100, 50, 30, 20,
                new Weapon("Sword", 40),
                new Armor("Steel Armor", 25)
        );

        template.addSkill(new Skill("Fireball", 60));
        template.addSkill(new Skill("Dash", 20));

        GameCharacter clone = template.clone();
        clone.setHealth(200);

        System.out.println("TEMPLATE:\n" + template);
        System.out.println("CLONE:\n" + clone);
    }
}

//  SINGLETON LOGGER

enum LogLevel { INFO, WARNING, ERROR }

class Logger {

    private static volatile Logger instance;
    private LogLevel currentLevel = LogLevel.INFO;
    private String logFile = "app.log";
    private final Object lock = new Object();
    private long maxSize = 5000; // bytes

    private Logger() {
        loadConfig("logger_config.txt");
    }

    public static Logger getInstance() {
        if (instance == null) {
            synchronized (Logger.class) {
                if (instance == null) {
                    instance = new Logger();
                }
            }
        }
        return instance;
    }

    public void setLogLevel(LogLevel level) {
        currentLevel = level;
    }

    public void log(String message, LogLevel level) {
        if (level.ordinal() < currentLevel.ordinal())
            return;

        synchronized (lock) {
            try {
                rotateIfNeeded();

                String time = LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                String record = time + " [" + level + "] " + message;

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
                    writer.write(record);
                    writer.newLine();
                }

                System.out.println(record);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void rotateIfNeeded() throws IOException {
        File file = new File(logFile);
        if (file.exists() && file.length() > maxSize) {
            String newName = "log_" + System.currentTimeMillis() + ".txt";
            Files.move(file.toPath(), Paths.get(newName));
        }
    }

    private void loadConfig(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) return;

            BufferedReader reader = new BufferedReader(new FileReader(file));
            logFile = reader.readLine();
            currentLevel = LogLevel.valueOf(reader.readLine());
            reader.close();
        } catch (Exception e) {
            System.out.println("Ошибка загрузки конфигурации.");
        }
    }
}

//  LOG READER

class LogReader {

    public static void readLogs(String path, LogLevel level) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("[" + level + "]")) {
                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка чтения логов.");
        }
    }
}

//  BUILDER

interface IReportBuilder {
    void setHeader(String header);
    void setContent(String content);
    void setFooter(String footer);
    void addSection(String name, String content);
    void setStyle(ReportStyle style);
    Report getReport();
}

class ReportStyle {
    String bgColor;
    String fontColor;
    int fontSize;

    public ReportStyle(String bgColor, String fontColor, int fontSize) {
        this.bgColor = bgColor;
        this.fontColor = fontColor;
        this.fontSize = fontSize;
    }
}

class Report {
    String header;
    String content;
    String footer;
    List<String> sections = new ArrayList<>();
    ReportStyle style;

    public String export() {
        return "HEADER: " + header +
                "\nSTYLE: bg=" + style.bgColor + ", font=" + style.fontColor +
                ", size=" + style.fontSize +
                "\nCONTENT: " + content +
                "\nSECTIONS: " + sections +
                "\nFOOTER: " + footer + "\n";
    }
}

class TextReportBuilder implements IReportBuilder {

    private Report report = new Report();

    public void setHeader(String header) { report.header = header; }
    public void setContent(String content) { report.content = content; }
    public void setFooter(String footer) { report.footer = footer; }
    public void addSection(String name, String content) {
        report.sections.add(name + ": " + content);
    }
    public void setStyle(ReportStyle style) { report.style = style; }
    public Report getReport() { return report; }
}

class ReportDirector {

    public void constructReport(IReportBuilder builder, ReportStyle style) {
        builder.setStyle(style);
        builder.setHeader("2026 Annual Report");
        builder.setContent("Sales increased by 30%");
        builder.addSection("Finance", "Revenue growth");
        builder.addSection("HR", "New hires");
        builder.setFooter("End of report");
    }
}

//  PROTOTYPE 

class GameCharacter implements Cloneable {

    private int health, strength, agility, intelligence;
    private Weapon weapon;
    private Armor armor;
    private List<Skill> skills = new ArrayList<>();

    public GameCharacter(int h, int s, int a, int i, Weapon w, Armor ar) {
        health = h; strength = s; agility = a; intelligence = i;
        weapon = w; armor = ar;
    }

    public void addSkill(Skill skill) { skills.add(skill); }
    public void setHealth(int health) { this.health = health; }

    public GameCharacter clone() {
        GameCharacter cloned = new GameCharacter(
                health, strength, agility, intelligence,
                weapon.clone(),
                armor.clone()
        );
        for (Skill s : skills)
            cloned.addSkill(s.clone());
        return cloned;
    }

    public String toString() {
        return "HP: " + health +
                "\nWeapon: " + weapon +
                "\nArmor: " + armor +
                "\nSkills: " + skills + "\n";
    }
}

class Weapon implements Cloneable {
    String name;
    int damage;

    public Weapon(String name, int damage) {
        this.name = name; this.damage = damage;
    }

    public Weapon clone() { return new Weapon(name, damage); }

    public String toString() { return name + " (DMG: " + damage + ")"; }
}

class Armor implements Cloneable {
    String name;
    int defense;

    public Armor(String name, int defense) {
        this.name = name; this.defense = defense;
    }

    public Armor clone() { return new Armor(name, defense); }

    public String toString() { return name + " (DEF: " + defense + ")"; }
}

class Skill implements Cloneable {
    String name;
    int power;

    public Skill(String name, int power) {
        this.name = name; this.power = power;
    }

    public Skill clone() { return new Skill(name, power); }

    public String toString() { return name + "(" + power + ")"; }
}