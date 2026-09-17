import java.util.*;

public class Parser {
    private List<Token> tokens;
    private int index = 0;
    private SymbolTable symbolTable;

    public Parser(List<Token> tokens, SymbolTable symbolTable) {
        this.tokens = tokens;
        this.symbolTable = symbolTable;
    }

    private Token actual() { return tokens.get(index); }
    private void avanzar() { if (index < tokens.size() - 1) index++; }

    private void consumir(TokenType tipo, String mensaje) {
        if (actual().getTipo() == tipo) avanzar();
        else throw syntaxErrorFor(actual(), mensaje);
    }

    private RuntimeException syntaxErrorFor(Token t, String mensaje) {
        return new RuntimeException("Error sintáctico en línea " + t.getLinea() + ": " + mensaje + " -> '" + t.getLexema() + "'");
    }

    public Nodo parse() {
        Nodo raiz = new Nodo("PROGRAMA");
        consumir(TokenType.PROG, "Se esperaba 'pf2025'");
        raiz.agregarHijo(declaraciones());
        raiz.agregarHijo(bloque());
        consumir(TokenType.EOF, "Fin de archivo esperado");
        return raiz;
    }

    private Nodo declaraciones() {
        Nodo nodo = new Nodo("DECL");
        consumir(TokenType.DECL, "Se esperaba 'decl'");
        while (actual().getTipo() == TokenType.TIPO) {
            nodo.agregarHijo(listaDeclaracion());
        }
        return nodo;
    }

    private Nodo listaDeclaracion() {
        Nodo nodo = new Nodo("VAR_DECL");
        Token tipo = actual();
        consumir(TokenType.TIPO, "Se esperaba tipo");
        nodo.agregarHijo(new Nodo(tipo.getLexema()));
        nodo.agregarHijo(idLista());
        consumir(TokenType.PC, "Falta ';'");
        return nodo;
    }

    private Nodo idLista() {
        Nodo nodo = new Nodo("IDS");
        Token id = actual();
        consumir(TokenType.ID, "Se esperaba identificador");
        nodo.agregarHijo(new Nodo(id.getLexema()));
        while (actual().getTipo() == TokenType.COMA) {
            consumir(TokenType.COMA, "Error en lista de IDs");
            Token id2 = actual();
            consumir(TokenType.ID, "Se esperaba ID");
            nodo.agregarHijo(new Nodo(id2.getLexema()));
        }
        return nodo;
    }

    private Nodo bloque() {
        Nodo nodo = new Nodo("BLOQUE");
        consumir(TokenType.INICIO, "Se esperaba 'inicio'");
        while (actual().getTipo() != TokenType.END && actual().getTipo() != TokenType.EOF) {
            nodo.agregarHijo(sentencia());
        }
        consumir(TokenType.END, "Se esperaba 'end'");
        return nodo;
    }

    private Nodo sentencia() {
        Token t = actual();
        switch (t.getTipo()) {
            case ID: return asignacion();
            case IMPDIG: return impresion();
            case IMPCAD: return impresionCad();
            case IMPBOOL: return impresionBool();
            case LEERDIG: return lectura();
            case SI: return sentenciaSi();
            case MIENTRAS: return sentenciaMientras();
            default: throw syntaxErrorFor(t, "Sentencia inválida");
        }
    }

    private Nodo sentenciaSi() {
        Nodo nodo = new Nodo("SI");
        Token t = actual();
        consumir(TokenType.SI, "Se esperaba 'si'");
        consumir(TokenType.PAREN_OPEN, "Falta '('");
        
        Nodo expr = expresion();
        if (!expr.getTipoDato().equals("booleano")) {
            throw new RuntimeException("Error SEMÁNTICO en línea " + t.getLinea() + ": La condición del 'si' debe ser obligatoriamente 'booleano', pero se recibió '" + expr.getTipoDato() + "'.");
        }
        nodo.agregarHijo(expr);
        
        consumir(TokenType.PAREN_CLOSE, "Falta ')'");
        nodo.agregarHijo(bloque());
        return nodo;
    }

    private Nodo sentenciaMientras() {
        Nodo nodo = new Nodo("MIENTRAS");
        Token t = actual();
        consumir(TokenType.MIENTRAS, "Se esperaba 'mientras'");
        consumir(TokenType.PAREN_OPEN, "Falta '('");
        
        Nodo expr = expresion();
        if (!expr.getTipoDato().equals("booleano")) {
            throw new RuntimeException("Error SEMÁNTICO en línea " + t.getLinea() + ": La condición del 'mientras' debe ser obligatoriamente 'booleano', pero se recibió '" + expr.getTipoDato() + "'.");
        }
        nodo.agregarHijo(expr);
        
        consumir(TokenType.PAREN_CLOSE, "Falta ')'");
        nodo.agregarHijo(bloque());
        return nodo;
    }

    private Nodo asignacion() {
        Nodo nodo = new Nodo("ASIGNACION");
        Token id = actual();
        consumir(TokenType.ID, "Se esperaba ID");

        if (!symbolTable.existe(id.getLexema())) {
            throw new RuntimeException("Error SEMÁNTICO en línea " + id.getLinea() + ": La variable '" + id.getLexema() + "' no ha sido declarada.");
        }
        
        String tipoVariable = symbolTable.obtenerTipo(id.getLexema());
        nodo.agregarHijo(new Nodo(id.getLexema()));
        consumir(TokenType.ASIG, "Falta ':='");

        Nodo expr = expresion();
        if (!tipoVariable.equals(expr.getTipoDato())) {
             throw new RuntimeException("Error SEMÁNTICO en línea " + id.getLinea() + 
                 ": Incompatibilidad de tipos. Intentas asignar un valor tipo '" + expr.getTipoDato() + "' a la variable '" + id.getLexema() + "' que fue declarada como '" + tipoVariable + "'.");
        }
        
        nodo.agregarHijo(expr);
        consumir(TokenType.PC, "Falta ';'");
        return nodo;
    }

    private Nodo impresion() {
        Nodo nodo = new Nodo("IMPDIG");
        Token t = actual();
        consumir(TokenType.IMPDIG, "Error en impresión");
        consumir(TokenType.PAREN_OPEN, "Falta '('");

        Nodo expr = expresion();
        if (!expr.getTipoDato().equals("int")) {
            throw new RuntimeException("Error SEMÁNTICO en línea " + t.getLinea() + ": La función 'impdig' es exclusiva para números enteros. Estás intentando imprimir un valor tipo '" + expr.getTipoDato() + "'.");
        }
        
        nodo.agregarHijo(expr);
        consumir(TokenType.PAREN_CLOSE, "Falta ')'");
        consumir(TokenType.PC, "Falta ';'");
        return nodo;
    }

    private Nodo impresionCad() {
        Nodo nodo = new Nodo("IMPCAD");
        Token t = actual();
        consumir(TokenType.IMPCAD, "Error en impresión de cadena");
        consumir(TokenType.PAREN_OPEN, "Falta '('");

        Nodo expr = expresion();
        if (!expr.getTipoDato().equals("cad")) {
            throw new RuntimeException("Error SEMÁNTICO en línea " + t.getLinea() + ": La función 'impcad' es exclusiva para texto. Estás intentando imprimir un valor tipo '" + expr.getTipoDato() + "'.");
        }
        
        nodo.agregarHijo(expr);
        consumir(TokenType.PAREN_CLOSE, "Falta ')'");
        consumir(TokenType.PC, "Falta ';'");
        return nodo;
    }

    private Nodo impresionBool() {
        Nodo nodo = new Nodo("IMPBOOL");
        Token t = actual();
        consumir(TokenType.IMPBOOL, "Error en impresión booleana");
        consumir(TokenType.PAREN_OPEN, "Falta '('");

        Nodo expr = expresion();
        if (!expr.getTipoDato().equals("booleano")) {
            throw new RuntimeException("Error SEMÁNTICO en línea " + t.getLinea() + ": La función 'impbool' es exclusiva para booleanos. Estás intentando imprimir un valor tipo '" + expr.getTipoDato() + "'.");
        }
        
        nodo.agregarHijo(expr);
        consumir(TokenType.PAREN_CLOSE, "Falta ')'");
        consumir(TokenType.PC, "Falta ';'");
        return nodo;
    }

    private Nodo lectura() {
        Nodo nodo = new Nodo("LEERDIG");
        consumir(TokenType.LEERDIG, "Error en lectura");
        consumir(TokenType.PAREN_OPEN, "Falta '('");

        Token id = actual();
        consumir(TokenType.ID, "Se esperaba ID");

        if (!symbolTable.existe(id.getLexema())) {
            throw new RuntimeException("Error SEMÁNTICO en línea " + id.getLinea() + ": La variable '" + id.getLexema() + "' no ha sido declarada.");
        }

        nodo.agregarHijo(new Nodo(id.getLexema()));
        consumir(TokenType.PAREN_CLOSE, "Falta ')'");
        consumir(TokenType.PC, "Falta ';'");
        return nodo;
    }

    private Nodo expresion() {
        Nodo nodo = expAritmetica();

        while (actual().getTipo() == TokenType.MENOR || actual().getTipo() == TokenType.MAYOR || actual().getTipo() == TokenType.IGUAL_QUE) {
            Token op = actual();
            avanzar();
            Nodo derecho = expAritmetica();
            
            if (!nodo.getTipoDato().equals("int") || !derecho.getTipoDato().equals("int")) {
                throw new RuntimeException("Error SEMÁNTICO en línea " + op.getLinea() + ": Los operadores relacionales solo pueden comparar variables de tipo 'int'.");
            }

            Nodo nuevo = new Nodo(op.getLexema());
            nuevo.setTipoDato("booleano");
            nuevo.agregarHijo(nodo);
            nuevo.agregarHijo(derecho);
            nodo = nuevo;
        }
        return nodo;
    }

    private Nodo expAritmetica() {
        Nodo nodo = termino();
        while (actual().getTipo() == TokenType.MAS || actual().getTipo() == TokenType.MENOS) {
            Token op = actual();
            avanzar();
            Nodo derecho = termino();
            
            if (!nodo.getTipoDato().equals("int") || !derecho.getTipoDato().equals("int")) {
                 throw new RuntimeException("Error SEMÁNTICO en línea " + op.getLinea() + ": No se puede sumar o restar un tipo '" + nodo.getTipoDato() + "' con un tipo '" + derecho.getTipoDato() + "'. Solo se permiten enteros.");
            }

            Nodo nuevo = new Nodo(op.getLexema());
            nuevo.setTipoDato("int");
            nuevo.agregarHijo(nodo);
            nuevo.agregarHijo(derecho);
            nodo = nuevo;
        }
        return nodo;
    }

    private Nodo termino() {
        Nodo nodo = factor();
        while (actual().getTipo() == TokenType.MUL || actual().getTipo() == TokenType.DIV) {
            Token op = actual();
            avanzar();
            Nodo derecho = factor();
            
            if (!nodo.getTipoDato().equals("int") || !derecho.getTipoDato().equals("int")) {
                 throw new RuntimeException("Error SEMÁNTICO en línea " + op.getLinea() + ": No se puede multiplicar o dividir un tipo '" + nodo.getTipoDato() + "' con un tipo '" + derecho.getTipoDato() + "'. Solo se permiten enteros.");
            }

            Nodo nuevo = new Nodo(op.getLexema());
            nuevo.setTipoDato("int");
            nuevo.agregarHijo(nodo);
            nuevo.agregarHijo(derecho);
            nodo = nuevo;
        }
        return nodo;
    }

    private Nodo factor() {
        Token t = actual();

        if (t.getTipo() == TokenType.CENT) {
            avanzar();
            Nodo n = new Nodo(t.getLexema());
            n.setTipoDato("int");
            return n;
        }
        
        if (t.getTipo() == TokenType.VAL_BOOL) {
            avanzar();
            Nodo n = new Nodo(t.getLexema());
            n.setTipoDato("booleano");
            return n;
        }

        // Nueva lógica para capturar variables de texto (Cadenas)
        if (t.getTipo() == TokenType.VAL_CAD) {
            avanzar();
            Nodo n = new Nodo(t.getLexema());
            n.setTipoDato("cad");
            return n;
        }

        if (t.getTipo() == TokenType.ID) {
            if (!symbolTable.existe(t.getLexema())) {
                throw new RuntimeException("Error SEMÁNTICO en línea " + t.getLinea() + ": La variable '" + t.getLexema() + "' no ha sido declarada.");
            }
            avanzar();
            Nodo n = new Nodo(t.getLexema());
            n.setTipoDato(symbolTable.obtenerTipo(t.getLexema()));
            return n;
        }

        if (t.getTipo() == TokenType.PAREN_OPEN) {
            consumir(TokenType.PAREN_OPEN, "Falta '('");
            Nodo nodo = expresion();
            consumir(TokenType.PAREN_CLOSE, "Falta ')'");
            return nodo;
        }

        throw syntaxErrorFor(t, "Falta operando o expresión inválida");
    }
}