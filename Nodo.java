import java.util.*;

public class Nodo {
    String valor;
    String tipoDato; 
    Object valorEvaluado; // Almacena el resultado matemático o lógico
    List<Nodo> hijos;

    public Nodo(String valor) {
        this.valor = valor;
        this.tipoDato = "void";
        this.valorEvaluado = null;
        this.hijos = new ArrayList<>();
    }

    public void agregarHijo(Nodo hijo) { hijos.add(hijo); }
    public String getValor() { return valor; }
    public List<Nodo> getHijos() { return hijos; }
    
    public void setTipoDato(String tipoDato) { this.tipoDato = tipoDato; }
    public String getTipoDato() { return tipoDato; }

    public void setValorEvaluado(Object valorEvaluado) { this.valorEvaluado = valorEvaluado; }
    public Object getValorEvaluado() { return valorEvaluado; }
}