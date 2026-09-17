public enum TokenType {
    // Palabras reservadas
    PROG, DECL, INICIO, END, IMPDIG, IMPCAD, LEERDIG,
    SI, MIENTRAS, IMPBOOL, 
    // Tipos
    TIPO,
    // Operadores aritméticos y de asignación
    MAS, MENOS, MUL, DIV, ASIG, 
    // Operadores relacionales
    IGUAL_QUE, MENOR, MAYOR,
    // Signos
    PC, COMA, PAREN_OPEN, PAREN_CLOSE,
    // Valores
    ID, CENT, VAL_BOOL, VAL_CAD, // Agregado VAL_CAD
    EOF, ERROR
}