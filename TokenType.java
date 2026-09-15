public enum TokenType {
    // Palabras reservadas
    PROG, DECL, INICIO, END, IMPDIG, IMPCAD, LEERDIG,
    SI, MIENTRAS, IMPBOOL, // Nuevas agregadas
    // Tipos
    TIPO,
    // Operadores aritméticos y de asignación
    MAS, MENOS, MUL, DIV, ASIG, 
    // Operadores relacionales (Nuevos)
    IGUAL_QUE, MENOR, MAYOR,
    // Signos
    PC, COMA, PAREN_OPEN, PAREN_CLOSE,
    // Valores
    ID, CENT, VAL_BOOL, // VAL_BOOL para verdadero/falso
    EOF, ERROR
}