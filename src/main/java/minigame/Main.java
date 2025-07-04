package minigame;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Solicitar nombre del jugador
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                        BLACKJACK RETRO                       ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();

        String nombreJugador;
        do {
            System.out.print("Ingresa tu nombre (máximo 10 caracteres): ");
            nombreJugador = scanner.nextLine().trim();

            if (nombreJugador.isEmpty()) {
                nombreJugador = "Jugador";
                break;
            }

            if (nombreJugador.length() > 10) {
                System.out.println(" El nombre es muy largo. Máximo 10 caracteres. Intenta nuevamente.");
                continue;
            }

            break;
        } while (true);

        // Crear jugador con dinero inicial de $10
        Jugador jugador = new Jugador(nombreJugador, 10.0);
        Mesa mesa = new Mesa("Mesa Principal");

        System.out.println("\n¡Bienvenido " + nombreJugador + "! Tienes $" + String.format("%.2f", jugador.getDinero())
                + " para apostar.");
        System.out.println("Presiona Enter para continuar...");
        scanner.nextLine();

        String respuesta;
        do {
            // Verificar si el jugador tiene dinero para apostar
            if (jugador.getDinero() <= 0) {
                System.out.println("\n Te has quedado sin dinero. ¡El juego ha terminado!");
                System.out.println("Victorias totales: " + jugador.getVictorias());
                break;
            }

            mesa.iniciarJuego(jugador);

            System.out.println("\n¿Quieres jugar otra partida? (s/n)");
            respuesta = scanner.nextLine();
        } while (respuesta.equalsIgnoreCase("s"));

        System.out.println("\n ¡Gracias por jugar, " + nombreJugador + "!");
        System.out.println("Victorias finales: " + jugador.getVictorias());
        System.out.println("Dinero final: $" + String.format("%.2f", jugador.getDinero()));
    }
}