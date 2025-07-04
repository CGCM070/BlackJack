package minigame;

import java.util.List;
import java.util.Scanner;
import java.awt.Toolkit;
import javax.sound.sampled.*;

public class Mesa {
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RESET = "\u001B[0m";

    private final Mazo mazo;
    private Jugador jugador;
    private final Jugador dealer;
    private double apuestaActual;

    // Clase interna para generar sonidos de 8 bits
    private static class SonidosRetro {
        // Generar un beep de 8 bits
        public static void beepSimple() {
            Toolkit.getDefaultToolkit().beep();
        }

        // Generar tono de victoria (ascendente)
        public static void sonidoVictoria() {
            try {
                reproducirTono(440, 150); // A4
                Thread.sleep(50);
                reproducirTono(523, 150); // C5
                Thread.sleep(50);
                reproducirTono(659, 300); // E5
            } catch (Exception e) {
                beepSimple();
            }
        }

        // Generar tono de derrota (descendente)
        public static void sonidoDerrota() {
            try {
                reproducirTono(523, 150); // C5
                Thread.sleep(50);
                reproducirTono(440, 150); // A4
                Thread.sleep(50);
                reproducirTono(330, 300); // E4
            } catch (Exception e) {
                beepSimple();
            }
        }

        // Sonido al repartir carta
        public static void sonidoCarta() {
            try {
                reproducirTono(800, 100);
            } catch (Exception e) {
                beepSimple();
            }
        }

        // Sonido de empate
        public static void sonidoEmpate() {
            try {
                reproducirTono(440, 200);
                Thread.sleep(100);
                reproducirTono(440, 200);
            } catch (Exception e) {
                beepSimple();
            }
        }

        // Método para reproducir un tono específico
        private static void reproducirTono(double frecuencia, int duracion) throws Exception {
            AudioFormat format = new AudioFormat(22050, 8, 1, true, false);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();

            byte[] buffer = new byte[22050 * duracion / 1000];
            for (int i = 0; i < buffer.length; i++) {
                double tiempo = i / 22050.0;
                buffer[i] = (byte) (Math.sin(2 * Math.PI * frecuencia * tiempo) * 127);
            }

            line.write(buffer, 0, buffer.length);
            line.drain();
            line.close();
        }
    }

    public Mesa(String nombre) {
        this.mazo = new Mazo();
        this.dealer = new Jugador("Dealer", Double.POSITIVE_INFINITY);
        this.apuestaActual = 1.0; // Apuesta mínima por defecto
    }

    public void iniciarJuego(Jugador jugadorActual) {
        this.jugador = jugadorActual;
        Scanner sc = new Scanner(System.in);
        clearScreen();
        mostrarTitulo();

        // Configurar apuesta
        configurarApuesta(sc);

        // Verificar que el jugador pueda apostar
        if (!jugador.puedeApostar(apuestaActual)) {
            System.out.println(" No tienes suficiente dinero para apostar $" + String.format("%.2f", apuestaActual));
            return;
        }

        // Realizar la apuesta
        jugador.perderDinero(apuestaActual);

        clearScreen();
        mostrarTitulo();

        jugador.limpiarMano();
        dealer.limpiarMano();

        // Repartir cartas iniciales con animación
        mostrarMesa(true); // Mostrar mesa inicial vacía

        // Repartir primera carta al jugador
        jugador.recibirCarta(mazo.sacarCarta());
        SonidosRetro.sonidoCarta();
        clearScreen();
        mostrarTitulo();
        mostrarMesa(true);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }

        // Repartir primera carta al dealer (visible)
        dealer.recibirCarta(mazo.sacarCarta());
        SonidosRetro.sonidoCarta();
        clearScreen();
        mostrarTitulo();
        mostrarMesa(true);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }

        // Repartir segunda carta al jugador
        jugador.recibirCarta(mazo.sacarCarta());
        SonidosRetro.sonidoCarta();
        clearScreen();
        mostrarTitulo();
        mostrarMesa(true);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }

        // Repartir segunda carta al dealer (oculta)
        dealer.recibirCarta(mazo.sacarCarta());
        SonidosRetro.sonidoCarta();
        clearScreen();
        mostrarTitulo();
        mostrarMesa(true); // La segunda carta del dealer aparece oculta
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }
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
        if (dealer.getMano().isEmpty()) {
            // Si el dealer no tiene cartas, mostrar área vacía
            for (int linea = 0; linea < 7; linea++) {
                System.out.println(ANSI_GREEN
                        + "║                                                                                                  ║"
                        + ANSI_RESET);
            }
        } else if (ocultarSegundaCarta && dealer.getMano().size() > 1) {
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
        String jugadorText = jugador.getNombre().toUpperCase() + ": (Puntos: " + jugador.getPuntos() + ")";
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

        // Marco inferior con información del jugador
        String infoJugador = String.format("Player: %s | Dinero: $%.2f | Victorias: %d | Apuesta: $%.2f",
                jugador.getNombre(), jugador.getDinero(), jugador.getVictorias(), apuestaActual);
        int espaciosInfo = 98 - infoJugador.length();
        int espacioIzqInfo = espaciosInfo / 2;
        int espacioDerInfo = espaciosInfo - espacioIzqInfo;

        System.out.println(ANSI_GREEN + """
                ║                                                                                                  ║
                ╠══════════════════════════════════════════════════════════════════════════════════════════════════╣
                ║""" + " ".repeat(espacioIzqInfo) + infoJugador + " ".repeat(espacioDerInfo) + """
                ║
                ╚══════════════════════════════════════════════════════════════════════════════════════════════════╝"""
                + ANSI_RESET);
    }

    private void mostrarCartasEnLineaConMarco(List<Carta> cartas, boolean ocultarSegunda) {
        if (cartas.isEmpty()) {
            // Mostrar líneas vacías si no hay cartas
            for (int linea = 0; linea < 7; linea++) {
                System.out.println(ANSI_GREEN
                        + "║                                                                                                  ║"
                        + ANSI_RESET);
            }
            return;
        }

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
            SonidosRetro.sonidoCarta(); // Sonido al recibir carta
            clearScreen();
            mostrarTitulo();
            mostrarMesa(true);

            if (jugador.getPuntos() > 21) {
                SonidosRetro.sonidoDerrota(); // Sonido de derrota por pasarse
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
            SonidosRetro.sonidoCarta(); // Sonido al dealer recibir carta
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
        System.out.println(jugador.getNombre() + ": " + puntosJugador);
        System.out.println("Dealer: " + puntosDealer);
        System.out.println();

        boolean jugadorGana = false;
        boolean esEmpate = false;

        if (puntosJugador > 21) {
            SonidosRetro.sonidoDerrota();
            System.out.println(ANSI_RED + "¡Te has pasado! Dealer gana." + ANSI_RESET);
            System.out.println("Pierdes $" + String.format("%.2f", apuestaActual));
        } else if (puntosDealer > 21) {
            SonidosRetro.sonidoVictoria();
            System.out.println(ANSI_GREEN + "¡Dealer se pasa! " + jugador.getNombre() + " gana!" + ANSI_RESET);
            jugadorGana = true;
        } else if (puntosJugador > puntosDealer) {
            SonidosRetro.sonidoVictoria();
            System.out.println(ANSI_GREEN + "¡" + jugador.getNombre() + " gana!" + ANSI_RESET);
            jugadorGana = true;
        } else if (puntosDealer > puntosJugador) {
            SonidosRetro.sonidoDerrota();
            System.out.println(ANSI_RED + "¡Dealer gana!" + ANSI_RESET);
            System.out.println("💸 Pierdes $" + String.format("%.2f", apuestaActual));
        } else {
            SonidosRetro.sonidoEmpate();
            System.out.println("¡Empate!");
            esEmpate = true;
        }

        // Manejar premios y estadísticas
        if (jugadorGana) {
            double premio = apuestaActual * 2; // Ganancia 1:1 + devolución de la apuesta
            jugador.ganarDinero(premio);
            jugador.incrementarVictorias();
            System.out.println("Ganas $" + String.format("%.2f", premio));
            System.out.println(" ¡Victoria #" + jugador.getVictorias() + "!");
        } else if (esEmpate) {
            // En empate, devolver la apuesta
            jugador.ganarDinero(apuestaActual);
            System.out.println(" Se devuelve tu apuesta de $" + String.format("%.2f", apuestaActual));
        }

        System.out.println();
        System.out.println(" Dinero actual: $" + String.format("%.2f", jugador.getDinero()));
        System.out.println(" Victorias totales: " + jugador.getVictorias());
    }

    private void configurarApuesta(Scanner sc) {
        System.out.println(" Dinero disponible: $" + String.format("%.2f", jugador.getDinero()));
        System.out.println(" Apuesta actual: $" + String.format("%.2f", apuestaActual));
        System.out.println();
        System.out.println("¿Deseas cambiar tu apuesta? (s/n) o Enter para continuar:");

        String respuesta = sc.nextLine().trim();
        if (respuesta.equalsIgnoreCase("s")) {
            while (true) {
                System.out.print("Ingresa tu nueva apuesta (min: $0.50, max: $"
                        + String.format("%.2f", jugador.getDinero()) + "): $");
                try {
                    String input = sc.nextLine().trim();
                    double nuevaApuesta = Double.parseDouble(input);

                    if (nuevaApuesta < 0.50) {
                        System.out.println(" La apuesta mínima es $0.50");
                        continue;
                    }

                    if (nuevaApuesta > jugador.getDinero()) {
                        System.out.println(" No tienes suficiente dinero para esa apuesta");
                        continue;
                    }

                    apuestaActual = nuevaApuesta;
                    System.out.println(" Apuesta establecida en $" + String.format("%.2f", apuestaActual));
                    break;
                } catch (NumberFormatException e) {
                    System.out.println(" Por favor ingresa un número válido");
                }
            }
        }

        System.out.println("\nPresiona Enter para comenzar la partida...");
        sc.nextLine();
    }
}
