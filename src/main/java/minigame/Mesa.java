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
                ╔══════════════════════════════════════════════════════════════════════════════════════════════════╗
                ║                                      BLACKJACK RETRO                                             ║
                ║                                      César Castillo                                              ║
                ╚══════════════════════════════════════════════════════════════════════════════════════════════════╝
                                """ + ANSI_RESET);
    }

    private void mostrarMesa(boolean ocultarSegundaCarta) {
        // Marco superior - Mesa muy ancha (100 caracteres)
        System.out.println(ANSI_GREEN + """
                ╔══════════════════════════════════════════════════════════════════════════════════════════════════╗
                ║                                      MESA DE BLACKJACK                                           ║
                ╠══════════════════════════════════════════════════════════════════════════════════════════════════╣"""
                + ANSI_RESET);

        // Área del dealer
        System.out.print(ANSI_GREEN + "║  " + ANSI_RESET);
        System.out.print("DEALER: ");
        if (!ocultarSegundaCarta) {
            System.out.print("(Puntos: " + dealer.getPuntos() + ")");
        }
        // Calcular espacios restantes para alinear el borde derecho
        String dealerText = "DEALER: " + (ocultarSegundaCarta ? "" : "(Puntos: " + dealer.getPuntos() + ")");
        int espaciosDealer = 98 - 2 - dealerText.length(); // 98 es el ancho interno, 2 son los espacios iniciales
        System.out.print(" ".repeat(espaciosDealer));
        System.out.println(ANSI_GREEN + "║" + ANSI_RESET);

        System.out.println(ANSI_GREEN
                + "║                                                                                                  ║"
                + ANSI_RESET);

        // Mostrar cartas del dealer
        if (ocultarSegundaCarta) {
            mostrarCartasEnLineaConMarco(List.of(dealer.getMano().get(0)), true);
        } else {
            mostrarCartasEnLineaConMarco(dealer.getMano(), false);
        }

        // Separador central
        System.out.println(ANSI_GREEN + """
                ║                                                                                                  ║
                ╠══════════════════════════════════════════════════════════════════════════════════════════════════╣
                ║                                         CENTRO                                                   ║
                ╠══════════════════════════════════════════════════════════════════════════════════════════════════╣
                ║                                                                                                  ║"""
                + ANSI_RESET);

        // Área del jugador
        System.out.print(ANSI_GREEN + "║  " + ANSI_RESET);
        String jugadorText = "JUGADOR: (Puntos: " + jugador.getPuntos() + ")";
        System.out.print(jugadorText);
        // Calcular espacios restantes para alinear el borde derecho
        int espaciosJugador = 98 - 2 - jugadorText.length(); // 98 es el ancho interno, 2 son los espacios iniciales
        System.out.print(" ".repeat(espaciosJugador));
        System.out.println(ANSI_GREEN + "║" + ANSI_RESET);

        System.out.println(ANSI_GREEN
                + "║                                                                                                  ║"
                + ANSI_RESET);

        // Mostrar cartas del jugador
        mostrarCartasEnLineaConMarco(jugador.getMano(), false);

        // Marco inferior
        System.out.println(ANSI_GREEN + """
                ║                                                                                                  ║
                ╠══════════════════════════════════════════════════════════════════════════════════════════════════╣
                ║                                       Player: César                                              ║
                ╚══════════════════════════════════════════════════════════════════════════════════════════════════╝"""
                + ANSI_RESET);
    }

    private void mostrarCartasEnLineaConMarco(List<Carta> cartas, boolean ocultarSegunda) {
        if (cartas.isEmpty())
            return;

        String[][] representaciones = new String[cartas.size()][7];

        for (int i = 0; i < cartas.size(); i++) {
            if (i == 1 && ocultarSegunda) {
                representaciones[i][0] = "┌─────────┐";
                representaciones[i][1] = "│ ## XX ##│";
                representaciones[i][2] = "│         │";
                representaciones[i][3] = "│    ?    │";
                representaciones[i][4] = "│         │";
                representaciones[i][5] = "│## XX ## │";
                representaciones[i][6] = "└─────────┘";
            } else {
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
                representaciones[i][6] = color + "└─────────┘" + reset;
            }
        }

        // Calcular espacios para centrar las cartas en la mesa muy ancha
        int anchoCartas = cartas.size() * 11 + (cartas.size() - 1) * 2; // 11 por carta + 2 espacios entre cartas
        int anchoTotalMesa = 98; // Ancho total interno exacto de la mesa (100 - 2 para los bordes ║)
        int espacioRestante = anchoTotalMesa - anchoCartas;
        int espacioIzquierdo = espacioRestante / 2;
        int espacioDerecho = espacioRestante - espacioIzquierdo;

        // Imprimir las cartas línea por línea
        for (int linea = 0; linea < 7; linea++) {
            System.out.print(ANSI_GREEN + "║" + ANSI_RESET);
            System.out.print(" ".repeat(espacioIzquierdo));

            for (int i = 0; i < representaciones.length; i++) {
                System.out.print(representaciones[i][linea]);
                if (i < representaciones.length - 1) {
                    System.out.print("  ");
                }
            }

            // Rellenar con espacios hasta completar el ancho exacto
            System.out.print(" ".repeat(espacioDerecho));
            System.out.println(ANSI_GREEN + "║" + ANSI_RESET);
        }
    }

    private void jugarTurnoJugador(Scanner sc) {
        while (jugador.getPuntos() < 21) {
            System.out.println("\n¿Desea otra carta? (s/n)");
            if (!sc.nextLine().equalsIgnoreCase("s"))
                break;

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
