import java.util.*;

public class Nodo {
    String valor;
    String tipoDato; // Necesario para la semántica
    List<Nodo> hijos;

    public Nodo(String valor) {
        this.valor = valor;
        this.tipoDato = "void";
        this.hijos = new ArrayList<>();
    }

    public void agregarHijo(Nodo hijo) { hijos.add(hijo); }
    public String getValor() { return valor; }
    public List<Nodo> getHijos() { return hijos; }
    
    public void setTipoDato(String tipoDato) { this.tipoDato = tipoDato; }
    public String getTipoDato() { return tipoDato; }
}