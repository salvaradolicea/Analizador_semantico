import java.util.ArrayList;
import java.util.List;

public class GeneradorCodigoIntermedio {
    private List<Cuadruplo> cuadruplos = new ArrayList<>();
    private int tempContador = 1;
    private int labelContador = 1;

    private String nuevoTemp() {
        return "t" + (tempContador++);
    }

    private String nuevoLabel() {
        return "L" + (labelContador++);
    }

    public void generar(Nodo nodo) {
        if (nodo == null) return;
        procesar(nodo);
    }

    private String procesar(Nodo nodo) {
        if (nodo == null) return null;
        String val = nodo.getValor();

        switch (val) {
            case "PROGRAMA":
            case "DECL":
            case "BLOQUE":
            case "VAR_DECL":
            case "IDS":
                for (Nodo hijo : nodo.getHijos()) {
                    procesar(hijo);
                }
                return null;
                
            case "ASIGNACION":
                String var = nodo.getHijos().get(0).getValor();
                String expr = procesar(nodo.getHijos().get(1));
                cuadruplos.add(new Cuadruplo(":=", expr, null, var));
                return null;
                
            case "SI":
                String condSi = procesar(nodo.getHijos().get(0));
                String L_fin_si = nuevoLabel();
                // Salto condicional ifFalse evaluando la expresión
                cuadruplos.add(new Cuadruplo("ifFalse", condSi, null, L_fin_si));
                procesar(nodo.getHijos().get(1)); // Cuerpo del SI
                cuadruplos.add(new Cuadruplo("Label", null, null, L_fin_si));
                return null;
                
            case "MIENTRAS":
                String L_inicio = nuevoLabel();
                String L_fin = nuevoLabel();
                // Etiqueta de inicio del ciclo
                cuadruplos.add(new Cuadruplo("Label", null, null, L_inicio));
                String condM = procesar(nodo.getHijos().get(0));
                // Salto si la condición no se cumple
                cuadruplos.add(new Cuadruplo("ifFalse", condM, null, L_fin));
                procesar(nodo.getHijos().get(1)); // Cuerpo del MIENTRAS
                // Salto incondicional de regreso al inicio
                cuadruplos.add(new Cuadruplo("goto", null, null, L_inicio));
                // Etiqueta de fin del ciclo
                cuadruplos.add(new Cuadruplo("Label", null, null, L_fin));
                return null;
                
            case "IMPDIG":
            case "IMPCAD":
            case "IMPBOOL":
                String resImp = procesar(nodo.getHijos().get(0));
                cuadruplos.add(new Cuadruplo("print", resImp, null, null));
                return null;
                
            case "LEERDIG":
                String varLeer = nodo.getHijos().get(0).getValor();
                cuadruplos.add(new Cuadruplo("read", null, null, varLeer));
                return null;
                
            case "+": 
            case "-": 
            case "*": 
            case "/": 
            case "==": 
            case "<": 
            case ">":
                String arg1 = procesar(nodo.getHijos().get(0));
                String arg2 = procesar(nodo.getHijos().get(1));
                String temp = nuevoTemp();
                // Instrucción de 3 direcciones para operaciones
                cuadruplos.add(new Cuadruplo(val, arg1, arg2, temp));
                return temp;
                
            default:
                // Retorna constantes, booleanos, identificadores u operandos finales
                return val;
        }
    }

    public void imprimirCuadruplos() {
        System.out.println("\n--- CÓDIGO INTERMEDIO (CUÁDRUPLOS) ---");
        System.out.println(String.format("%-10s | %-10s | %-10s | %-10s", "OP", "ARG1", "ARG2", "RES"));
        System.out.println("-------------------------------------------------");
        for (Cuadruplo c : cuadruplos) {
            System.out.println(c.toString());
        }
        System.out.println("-------------------------------------------------\n");
    }
}