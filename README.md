# Analizador_semantico
# Analizador Léxico, Sintáctico y Semántico (pf2025)

Este proyecto es un compilador básico implementado en Java que realiza análisis léxico, sintáctico y semántico mediante un enfoque de descenso recursivo. El lenguaje fuente está diseñado con palabras reservadas en español.

## 🚀 Características Principales

*   **Análisis Léxico:** Reconocimiento de tokens, operadores aritméticos, relacionales, identificadores y tipos de datos. Generación de reporte de errores léxicos.
*   **Análisis Sintáctico:** Construcción de un Árbol de Sintaxis Abstracta (AST) para representar la estructura del código.
*   **Análisis Semántico:**
    *   **Tabla de Símbolos:** Gestión de ámbitos, evitando doble declaración y asegurando que toda variable se declare antes de su uso.
    *   **Pila Semántica:** Validación de tipos en expresiones matemáticas y relacionales.
    *   **Compatibilidad de Tipos:** Verificación estricta en asignaciones (`T_izq == T_der`) y parámetros de impresión.
    *   **Control de Flujo:** Validación de sentencias `si` y `mientras` exigiendo que sus condiciones resuelvan a tipos `booleano`.

## 🛠️ Tecnologías

*   **Lenguaje:** Java (JDK 11+)
*   **Estructuras clave:** `HashMap` (Tabla de Símbolos), `Stack` (Pila Semántica), Árboles n-arios (AST).

## 📝 Estructura del Lenguaje Fuente

El compilador espera un archivo de texto (`progfte.txt`) con la siguiente estructura básica:

```text
pf2025
decl
    int numero, contador;
    booleano bandera;
inicio
    numero := 10;
    bandera := verdadero;
    
    si (numero > 5) inicio
        impdig(numero);
    end
    
    mientras (bandera == verdadero) inicio
        impbool(bandera);
        bandera := falso;
    end
end
