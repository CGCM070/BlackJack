package minigame;

import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        Mesa mesa = new Mesa("Mesa Principal");
        Scanner scanner = new Scanner(System.in);
        String respuesta;

        do {
            mesa.iniciarJuego();
            System.out.println("\n¿Quieres jugar otra partida? (s/n)");
            respuesta = scanner.nextLine();
        } while (respuesta.equalsIgnoreCase("s"));

        System.out.println("¡Gracias por jugar!");
    }

}