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
                cuadruplos.add(new Cuadruplo("ifFalse", condSi, null, L_fin_si));
                procesar(nodo.getHijos().get(1)); 
                cuadruplos.add(new Cuadruplo("Label", null, null, L_fin_si));
                return null;
                
            case "MIENTRAS":
                String L_inicio = nuevoLabel();
                String L_fin = nuevoLabel();
                cuadruplos.add(new Cuadruplo("Label", null, null, L_inicio));
                String condM = procesar(nodo.getHijos().get(0));
                cuadruplos.add(new Cuadruplo("ifFalse", condM, null, L_fin));
                procesar(nodo.getHijos().get(1)); 
                cuadruplos.add(new Cuadruplo("goto", null, null, L_inicio));
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
                cuadruplos.add(new Cuadruplo(val, arg1, arg2, temp));
                return temp;
                
            default:
                return val;
        }
    }

    public void imprimirCodigoTresDirecciones() {
        System.out.println("\n--- CÓDIGO DE TRES DIRECCIONES ---");
        for (Cuadruplo c : cuadruplos) {
            if (c.op.equals("Label")) {
                System.out.println(c.res + ":");
            } else if (c.op.equals("goto")) {
                System.out.println("goto " + c.res);
            } else if (c.op.equals("ifFalse")) {
                System.out.println("ifFalse " + c.arg1 + " goto " + c.res);
            } else if (c.op.equals(":=")) {
                System.out.println(c.res + " = " + c.arg1);
            } else if (c.op.equals("print")) {
                System.out.println("print " + c.arg1);
            } else if (c.op.equals("read")) {
                System.out.println("read " + c.res);
            } else { 
                System.out.println(c.res + " = " + c.arg1 + " " + c.op + " " + c.arg2);
            }
        }
        System.out.println("----------------------------------");
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