import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Interprete {
    private Map<String, Object> memoria = new HashMap<>();
    private Scanner scanner = new Scanner(System.in);

    public void interpretar(Nodo nodo) {
        if (nodo == null) return;

        switch (nodo.getValor()) {
            case "PROGRAMA":
            case "DECL":
            case "BLOQUE":
                for (Nodo hijo : nodo.getHijos()) interpretar(hijo);
                break;
                
            case "VAR_DECL":
                String tipo = nodo.getHijos().get(0).getValor();
                Nodo ids = nodo.getHijos().get(1);
                for (Nodo idNodo : ids.getHijos()) {
                    String nombre = idNodo.getValor();
                    if (tipo.equals("int")) memoria.put(nombre, 0);
                    else if (tipo.equals("cad")) memoria.put(nombre, "");
                    else if (tipo.equals("booleano")) memoria.put(nombre, false);
                }
                break;
                
            case "ASIGNACION":
                String varName = nodo.getHijos().get(0).getValor();
                Object valor = evaluar(nodo.getHijos().get(1));
                memoria.put(varName, valor);
                nodo.setValorEvaluado(valor); // Se guarda para imprimir en el árbol
                break;
                
            case "IMPDIG":
            case "IMPCAD":
            case "IMPBOOL":
                System.out.println(">> SALIDA: " + evaluar(nodo.getHijos().get(0)));
                break;
                
            case "LEERDIG":
                String readVar = nodo.getHijos().get(0).getValor();
                System.out.print(">> INGRESA UN VALOR PARA '" + readVar + "': ");
                int val = scanner.nextInt();
                memoria.put(readVar, val);
                nodo.setValorEvaluado(val);
                break;
                
            case "SI":
                boolean condSi = (Boolean) evaluar(nodo.getHijos().get(0));
                if (condSi) interpretar(nodo.getHijos().get(1));
                break;
                
            case "MIENTRAS":
                while ((Boolean) evaluar(nodo.getHijos().get(0))) {
                    interpretar(nodo.getHijos().get(1));
                }
                break;
        }
    }

    private Object evaluar(Nodo expr) {
        if (expr == null) return null;

        String val = expr.getValor();
        
        // 1. Constantes Numéricas
        if (expr.getTipoDato().equals("int") && val.matches("[0-9]+")) {
            int num = Integer.parseInt(val);
            expr.setValorEvaluado(num);
            return num;
        }
        
        // 2. Constantes Booleanas
        if (val.equals("verdadero")) { expr.setValorEvaluado(true); return true; }
        if (val.equals("falso")) { expr.setValorEvaluado(false); return false; }
        
        // 3. Constantes de Cadena
        if (expr.getTipoDato().equals("cad") && val.startsWith("\"")) {
            String str = val.substring(1, val.length() - 1);
            expr.setValorEvaluado(str);
            return str;
        }

        // 4. Leer Variables desde la Memoria
        if (memoria.containsKey(val)) {
            Object memVal = memoria.get(val);
            expr.setValorEvaluado(memVal);
            return memVal;
        }

        // 5. Operaciones Aritméticas y Lógicas
        if (val.equals("+") || val.equals("-") || val.equals("*") || val.equals("/") || 
            val.equals("==") || val.equals("<") || val.equals(">")) {
            
            Object izq = evaluar(expr.getHijos().get(0));
            Object der = evaluar(expr.getHijos().get(1));
            Object resultado = null;

            switch (val) {
                case "+": resultado = (Integer) izq + (Integer) der; break;
                case "-": resultado = (Integer) izq - (Integer) der; break;
                case "*": resultado = (Integer) izq * (Integer) der; break;
                case "/": 
                    if ((Integer) der == 0) {
                        throw new RuntimeException("Expresión indefinida (división por cero).");
                    }
                    resultado = (Integer) izq / (Integer) der; 
                    break;
                case "==": resultado = izq.equals(der); break;
                case "<": resultado = (Integer) izq < (Integer) der; break;
                case ">": resultado = (Integer) izq > (Integer) der; break;
            }
            
            expr.setValorEvaluado(resultado); // Guardamos el cálculo para el árbol
            return resultado;
        }

        return null;
    }
}
