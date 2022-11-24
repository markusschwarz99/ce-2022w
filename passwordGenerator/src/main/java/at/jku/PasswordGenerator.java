package at.jku;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Scanner;

public class PasswordGenerator {
    public static void main(String[] args) {
        for (int i = 0; i < 2; i++) {
            if (i == 0) {
                System.out.println("Enter Password for Worker: ");
            } else {
                System.out.println("Enter Password for Admin: ");
            }
            final Scanner in = new Scanner(System.in);
            final String password = in.nextLine();
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(16);
            String hashPassword = encoder.encode(password);
            System.out.println("Your password hash: " + hashPassword);
            System.out.println("---------------------------------------------");
        }
    }
}
