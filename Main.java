import java.util.*;

public class Main {

    public static void main(String[] args) {
        try {
            // 1 y 2. Leer programa fuente y Preprocesado
            List<String> programa = FileManager.leerArchivo("progfte.txt");
            StringBuilder sbAll = new StringBuilder();
            for (String l : programa) sbAll.append(l).append("\n");
            String todo = sbAll.toString();

            todo = todo.replaceAll("(?s)/\\*.*?\\*/", "");
            todo = todo.replaceAll("//.*(?=\\n)", "");

            String[] lineasLimpiasArr = todo.split("\\r?\\n");
            List<String> lineasLimpias = new ArrayList<>();
            for (String l : lineasLimpiasArr) {
                String t = l.replace("\t", " ").trim();
                if (!t.isEmpty()) lineasLimpias.add(t);
            }

            // 3. Ejecutar analizador léxico
            Lexer lexer = new Lexer();
            List<Token> tokens = lexer.analizar(lineasLimpias);

            // 4. Generar archivo progfte.dep
            StringBuilder depSb = new StringBuilder();
            for (int i = 0; i < lineasLimpias.size(); i++) {
                if (i > 0) depSb.append(' ');
                depSb.append(lineasLimpias.get(i));
            }
            FileManager.escribirArchivoDep("progfte.dep", Arrays.asList(depSb.toString()));

            // 5. Construir tabla de símbolos (Fase DECL y validación de doble declaración)
            List<EntradaSimbolo> tablaSimbolos = construirTablaSimbolos(tokens);
            SymbolTable symTable = new SymbolTable();
            
            try {
                for (EntradaSimbolo e : tablaSimbolos) {
                    symTable.agregar(e.getNombre(), e);
                }
            } catch (RuntimeException ex) {
                System.out.println(ex.getMessage());
                return; // Detenemos la ejecución si hay un error de declaración doble
            }

            // 6 y 7. Generar .tab y .tok
            String contenidoTab = generarArchivoTab(tablaSimbolos);
            FileManager.escribirArchivo("progfte.tab", Arrays.asList(contenidoTab.split("\n")));
            String contenidoTok = generarArchivoTok(tokens, lexer.getErrores());
            FileManager.escribirArchivo("progfte.tok", Arrays.asList(contenidoTok.split("\n")));
            System.out.println("Análisis léxico completado");

            // 8. Parser semántico
            Parser parser = new Parser(tokens, symTable);
            try {
                Nodo arbol = parser.parse();
                System.out.println("Análisis sintáctico y semántico correctos");
                imprimirArbol(arbol, 0);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<EntradaSimbolo> construirTablaSimbolos(List<Token> tokens) {
        List<EntradaSimbolo> tabla = new ArrayList<>();
        boolean dentroDecl = false;
        String tipoActual = "";
        int contador = 1;

        for (int i = 0; i < tokens.size(); i++) {
            Token t = tokens.get(i);
            if (t.getTipo() == TokenType.DECL) {
                dentroDecl = true;
                continue;
            }
            if (t.getTipo() == TokenType.INICIO) break;

            if (dentroDecl && t.getTipo() == TokenType.TIPO) {
                tipoActual = t.getLexema();
                i++;
                while (i < tokens.size() && tokens.get(i).getTipo() != TokenType.PC) {
                    Token actual = tokens.get(i);
                    if (actual.getTipo() == TokenType.ID) {
                        tabla.add(new EntradaSimbolo(
                            contador++, actual.getLexema(), tipoActual,
                            valorInicialPorTipo(tipoActual), 300, actual.getLinea()
                        ));
                    }
                    i++;
                }
            }
        }
        return tabla;
    }

    private static String generarArchivoTab(List<EntradaSimbolo> tabla) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-5s | %-20s | %-12s | %-10s | %-6s | %s%n",
            "No.", "VARIABLE", "TIPO", "VALOR INIT", "REF", "LINEA"));
        sb.append("-".repeat(75)).append("\n");
        for (EntradaSimbolo e : tabla) {
            sb.append(String.format("%-5d | %-20s | %-12s | %-10s | %-6d | %d%n",
                e.getNumero(), e.getNombre(), e.getTipo(), e.getValorInicial(), e.getReferencia(), e.getLinea()));
        }
        return sb.toString();
    }

    private static String generarArchivoTok(List<Token> tokens, List<ErrorLexico> errores) {
        StringBuilder sb = new StringBuilder();
        for (Token t : tokens) sb.append(t.toString()).append("\n");
        if (!errores.isEmpty()) {
            sb.append("\nERRORES LEXICOS:\n");
            for (ErrorLexico err : errores) sb.append(err.toString()).append("\n");
        }
        return sb.toString();
    }

    private static String valorInicialPorTipo(String tipo) {
        switch (tipo) {
            case "int": return "0";
            case "cad": return "\"\"";
            case "booleano": return "falso"; // Adaptado a español según rúbrica
            default: return "indefinido";
        }
    }

    public static void imprimirArbol(Nodo nodo, int nivel) {
        for (int i = 0; i < nivel; i++) System.out.print("  ");
        System.out.println(nodo.getValor());
        for (Nodo hijo : nodo.getHijos()) imprimirArbol(hijo, nivel + 1);
    }
}