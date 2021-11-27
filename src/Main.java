import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (sc.hasNextLine())
            try {
                System.out.println(
                        Calculator.calculate(sc.nextLine())
                );
            } catch (RuntimeException ex) {
                System.out.println(ex.getMessage());
            }
    }
}
