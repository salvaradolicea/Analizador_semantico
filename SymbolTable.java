import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private Map<String, EntradaSimbolo> tabla;

    public SymbolTable() {
        tabla = new HashMap<>();
    }

    public void agregar(String id, EntradaSimbolo entrada) {
        if (tabla.containsKey(id)) {
            throw new RuntimeException("Error semántico en línea " + entrada.getLinea() + 
                ": La variable '" + id + "' ya ha sido declarada.");
        }
        tabla.put(id, entrada);
    }

    public boolean existe(String id) { 
        return tabla.containsKey(id); 
    }

    public String obtenerTipo(String id) {
        if (existe(id)) return tabla.get(id).getTipo();
        return "indefinido";
    }

    public Map<String, EntradaSimbolo> getTabla() { return tabla; }
}