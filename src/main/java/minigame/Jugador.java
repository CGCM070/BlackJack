package minigame;

import java.util.ArrayList;
import java.util.List;

public class Jugador {
    private final String nombre;
    private final List<Carta> mano;
    private double dinero;
    private int victorias;

    public Jugador(String nombre, double dineroInicial) {
        this.nombre = nombre;
        this.mano = new ArrayList<>();

    }

    public void recibirCarta(Carta carta) {
        mano.add(carta);
    }

    public int getPuntos() {
        int suma = 0;
        int ases = 0;
        for (Carta carta : mano) {
            if (carta.getValorNumerico() == 11) ases++;
            suma += carta.getValorNumerico();
        }
        while (suma > 21 && ases > 0) {
            suma -= 10;
            ases--;
        }
        return suma;
    }

    public void limpiarMano() {
        mano.clear();
    }

    public List<Carta> getMano() {
        return new ArrayList<>(mano);
    }
}
