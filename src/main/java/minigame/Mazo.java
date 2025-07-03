package minigame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Mazo {

    private final List<Carta> cartas;
    private static final String[] VALORES = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
    private static final String[] PALOS = {"♠", "♥", "♦", "♣"};

    public Mazo() {
        cartas = new ArrayList<>();
        crearMazo();
        mezclar();
    }

    private void crearMazo() {
        for (String palo : PALOS) {
            for (String valor : VALORES) {
                cartas.add(new Carta(valor, palo));
            }
        }
    }

    public void mezclar() {
        Collections.shuffle(cartas);
    }

    public Carta sacarCarta() {
        if (cartas.isEmpty()) {
            crearMazo();
            mezclar();
        }
        return cartas.remove(0);
    }
}
