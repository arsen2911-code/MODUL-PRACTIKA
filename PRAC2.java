import java.util.ArrayList;
import java.util.List;

class User {
    private String name;
    private String email;
    private String role;

    public User(String name, String email, String role) {
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public String getEmail() { return email; }

    public void setName(String name) { this.name = name; }
    public void setRole(String role) { this.role = role; }

    @Override
    public String toString() {
        return name + " (" + email + ") - " + role;
    }
}

class UserManager {
    private List<User> users = new ArrayList<>();

    public void addUser(User user) {
        users.add(user);
        System.out.println("Пользователь добавлен: " + user.getEmail());
    }

    public void removeUser(String email) {
        boolean removed = users.removeIf(u -> u.getEmail().equals(email));
        if (removed) {
            System.out.println("Пользователь удален: " + email);
        } else {
            System.out.println("Пользователь не найден для удаления.");
        }
    }

    public void updateUser(String email, String newName, String newRole) {
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                user.setName(newName);
                user.setRole(newRole);
                System.out.println("Данные обновлены для: " + email);
                return;
            }
        }
        System.out.println("Пользователь не найден для обновления.");
    }

    public void printAllUsers() {
        System.out.println("--- Список пользователей ---");
        for (User user : users) {
            System.out.println(user);
        }
        System.out.println("----------------------------");
    }
}

public class PRAC2 {
    public static void main(String[] args) {
        UserManager manager = new UserManager();

        manager.addUser(new User("Алибек", "aibek@example.com", "Admin"));
        manager.addUser(new User("Иван", "ivan@example.com", "User"));
        manager.printAllUsers();

        manager.updateUser("ivan@example.com", "Иван Петров", "Moderator");
        manager.printAllUsers();

        manager.removeUser("aibek@example.com");
        manager.printAllUsers();
    }
}