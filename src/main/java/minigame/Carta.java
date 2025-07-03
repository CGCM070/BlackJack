package minigame;

public class Carta {

    private final String valor;
    private final String palo;


    public Carta(String valor, String palo) {
        this.valor = valor;
        this.palo = palo;
    }


    public int getValorNumerico() {
        return switch (valor) {
            case "A" -> 11;
            case "K", "Q", "J" -> 10;
            default -> Integer.parseInt(valor);
        };
    }



    public String getValor() {
        return valor;
    }


    public String getPalo() {
        return palo;
    }


    @Override
    public String toString() {
        return String.format("%s%s", valor, palo);
    }
}
