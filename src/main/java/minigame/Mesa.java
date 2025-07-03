package minigame;

import java.util.List;
import java.util.Scanner;

public class Mesa {
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RESET = "\u001B[0m";


    private final Mazo mazo;
    private final Jugador jugador;
    private final Jugador dealer;

    public Mesa(String nombre) {
        this.mazo = new Mazo();
        this.jugador = new Jugador("Player", 1000);
        this.dealer = new Jugador("Dealer", Double.POSITIVE_INFINITY);
    }

    public void iniciarJuego() {
        Scanner sc = new Scanner(System.in);
        clearScreen();
        mostrarTitulo();

        jugador.limpiarMano();
        dealer.limpiarMano();

        // Repartir cartas iniciales
        for (int i = 0; i < 2; i++) {
            jugador.recibirCarta(mazo.sacarCarta());
            dealer.recibirCarta(mazo.sacarCarta());
        }

        mostrarMesa(true);
        jugarTurnoJugador(sc);
        jugarTurnoDealer();
        determinarGanador();
    }

    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private void mostrarTitulo() {
        System.out.println(ANSI_GREEN + """
				╔══════════════════════════════════════════╗
				║             BLACKJACK RETRO              ║
				║             César Castillo               ║
				╚══════════════════════════════════════════╝
				""" + ANSI_RESET);
    }


    private void mostrarMesa(boolean ocultarSegundaCarta) {
        System.out.println("\nDealer: ");
        List<Carta> manoDealer = dealer.getMano();

        if (ocultarSegundaCarta) {
            mostrarCartasEnLinea(List.of(manoDealer.get(0)));

            // carta oculta
            System.out.println("""
					┌─────────┐ ┌─────────┐
					│ ## XX ##│ │## XX ## │
					│         │ │         │
					│    ?    │ │    ?    │
					│         │ │         │
					│## XX ## │ │## XX ## │
					└─────────┘ └─────────┘
					""");
        } else {
            mostrarCartasEnLinea(manoDealer);
        }

        System.out.println("\nJugador: (Puntos: " + jugador.getPuntos() + ")");
        mostrarCartasEnLinea(jugador.getMano());
    }


    private void mostrarCartasEnLinea(List<Carta> cartas) {
        String[][] representaciones = new String[cartas.size()][6];

        for (int i = 0; i < cartas.size(); i++) {
            Carta carta = cartas.get(i);
            String color = carta.getPalo().equals("♥") || carta.getPalo().equals("♦") ? ANSI_RED : "";
            String reset = ANSI_RESET;

            String valor = carta.getValor();
            String palo = carta.getPalo();

            representaciones[i][0] = color + "┌─────────┐" + reset;
            representaciones[i][1] = color + "│ %-2s      │".formatted(valor) + reset;
            representaciones[i][2] = color + "│         │" + reset;
            representaciones[i][3] = color + "│    %s    │".formatted(palo) + reset;
            representaciones[i][4] = color + "│         │" + reset;
            representaciones[i][5] = color + "│      %-2s │".formatted(valor) + reset;

        }

        for (int linea = 0; linea < 6; linea++) {
            for (String[] carta : representaciones) {
                System.out.print(carta[linea] + " ");
            }
            System.out.println();
        }

        for (int i = 0; i < cartas.size(); i++) {
            System.out.print("└─────────┘ ");
        }
        System.out.println();
    }

    private void jugarTurnoJugador(Scanner sc) {
        while (jugador.getPuntos() < 21) {
            System.out.println("\n¿Desea otra carta? (s/n)");
            if (!sc.nextLine().equalsIgnoreCase("s")) break;

            jugador.recibirCarta(mazo.sacarCarta());
            clearScreen();
            mostrarTitulo();
            mostrarMesa(true);

            if (jugador.getPuntos() > 21) {
                System.out.println(ANSI_RED + "¡Te has pasado! " + jugador.getPuntos() + ANSI_RESET);
                return;
            }
        }
    }

    private void jugarTurnoDealer() {
        clearScreen();
        mostrarTitulo();
        mostrarMesa(false);

        while (dealer.getPuntos() < 17) {
            dealer.recibirCarta(mazo.sacarCarta());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            clearScreen();
            mostrarTitulo();
            mostrarMesa(false);
        }
    }

    private void determinarGanador() {
        int puntosJugador = jugador.getPuntos();
        int puntosDealer = dealer.getPuntos();

        System.out.println("\nPuntos finales:");
        System.out.println("Jugador: " + puntosJugador);
        System.out.println("Dealer: " + puntosDealer);

        if (puntosJugador > 21) {
            System.out.println(ANSI_RED + "¡Dealer gana!" + ANSI_RESET);
        } else if (puntosDealer > 21) {
            System.out.println(ANSI_GREEN + "¡Jugador gana!" + ANSI_RESET);
        } else if (puntosJugador > puntosDealer) {
            System.out.println(ANSI_GREEN + "¡Jugador gana!" + ANSI_RESET);
        } else if (puntosDealer > puntosJugador) {
            System.out.println(ANSI_RED + "¡Dealer gana!" + ANSI_RESET);
        } else {
            System.out.println("¡Empate!");
        }
    }
}
