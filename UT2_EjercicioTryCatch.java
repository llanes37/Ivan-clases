/******************************************************************************************
 *                        📚 **TEORÍA Y CONCEPTOS SOBRE TRY-CATCH EN JAVA**
 * ──────────────────────────────────────────────────────────────────────────────
 * Este programa está diseñado para repasar y aplicar el manejo de excepciones en Java.
 * Se muestran ejemplos de algunas excepciones comunes, y en cada uno se incluyen sugerencias
 * para que el alumno modifique el código y practique lo aprendido.
 * 
 * Principales excepciones:
 *   1️⃣ ArithmeticException      - Ocurre al dividir por cero.
 *   2️⃣ ArrayIndexOutOfBoundsException - Ocurre al acceder a un índice inválido en un array.
 *   3️⃣ NumberFormatException    - Al convertir una cadena no numérica a número.
 *   4️⃣ FileNotFoundException    - Al intentar leer un archivo que no existe.
 *   5️⃣ NullPointerException     - Al acceder a métodos o atributos de un objeto nulo.
 * 
 * 🚀 ¡Explora cada ejemplo, lee la teoría y modifica el código según las sugerencias para aprender más!
 ******************************************************************************************/

 import java.util.Scanner;

 public class UT2_EjercicioTryCatch {
 
     public static void main(String[] args) {
         // Descomenta cada método para probar los ejemplos individualmente.
         // ejemploDivideNumeros();
         // ejemploAccederIndiceArray();
         // ejemploConvertirStringANumero();
         // ejemploLeerArchivo();
         ejemploObtenerElementoNull();
         ejemploLanzarExcepcionPersonalizada();
     }
 
     // ---------------------------------------------------------------
     // ! Ejemplo: División de números con manejo de ArithmeticException
     // ---------------------------------------------------------------
     /**
      * 📌 TEORÍA:
      * - La división por cero es una operación inválida y lanza ArithmeticException.
      * - Es importante capturar esta excepción para evitar que el programa se detenga abruptamente.
      *
      * 🔵 EJERCICIO PARA EL ALUMNO:
      * Modifica este método para que, en caso de que se intente dividir por cero, se vuelva a
      * solicitar el segundo número hasta que se ingrese un valor distinto de cero.
      */
     public static void ejemploDivideNumeros() {
         Scanner sc = new Scanner(System.in);
         try {
             System.out.print("Introduce un número: ");
             int num1 = sc.nextInt(); // Leer primer número
             System.out.print("Introduce otro número: ");
             int num2 = sc.nextInt(); // Leer segundo número
 
             // Intentamos dividir por el segundo número
             int resultado = num1 / num2;
             System.out.println("El resultado de la división es: " + resultado);
         } catch (ArithmeticException e) {
             System.out.println("⚠️ Error: No se puede dividir por cero.");
         } catch (Exception e) {
             System.out.println("⚠️ Error general: " + e.getMessage());
         }
     }
 
     // ---------------------------------------------------------------
     // ! Ejemplo: Manejo de ArrayIndexOutOfBoundsException
     // ---------------------------------------------------------------
     /**
      * 📌 TEORÍA:
      * - Los arrays tienen un tamaño fijo.
      * - Acceder a un índice fuera del rango válido lanza ArrayIndexOutOfBoundsException.
      *
      * 🔵 EJERCICIO PARA EL ALUMNO:
      * Modifica este método para que, si se detecta un índice inválido, se solicite al usuario
      * ingresar un índice válido y se vuelva a intentar el acceso.
      */
     public static void ejemploAccederIndiceArray() {
         try {
             int[] numeros = {1, 2, 3};
             System.out.println("Accediendo al elemento 5: " + numeros[5]);
         } catch (ArrayIndexOutOfBoundsException e) {
             System.out.println("⚠️ Error: Índice fuera de los límites del arreglo.");
         }
     }
 
     // ---------------------------------------------------------------
     // ! Ejemplo: Manejo de NumberFormatException
     // ---------------------------------------------------------------
     /**
      * 📌 TEORÍA:
      * - La conversión de una cadena a un número requiere que la cadena tenga un formato válido.
      * - Si la cadena contiene caracteres no numéricos, se lanza NumberFormatException.
      *
      * 🔵 EJERCICIO PARA EL ALUMNO:
      * Crea un método similar que intente convertir varias cadenas a números y almacene solo
      * aquellos que se conviertan correctamente en un array. Muestra el array resultante.
      */
     public static void ejemploConvertirStringANumero() {
         try {
             String texto = "abc";  // Texto no numérico
             int numero = Integer.parseInt(texto);  // Lanza NumberFormatException
             System.out.println("Número convertido: " + numero);
         } catch (NumberFormatException e) {
             System.out.println("⚠️ Error: El formato de número no es válido.");
         }
     }
 
     // ---------------------------------------------------------------
     // ! Ejemplo: Intentar leer un archivo inexistente - FileNotFoundException
     // ---------------------------------------------------------------
     /**
      * 📌 TEORÍA:
      * - Al intentar abrir un archivo que no existe se lanza FileNotFoundException.
      * - Es importante capturar esta excepción para informar al usuario y manejar el error.
      *
      * 🔵 EJERCICIO PARA EL ALUMNO:
      * Implementa un método que, en caso de que el archivo no exista, lo cree y escriba datos
      * de ejemplo en él.
      */
     public static void ejemploLeerArchivo() {
         try {
             java.io.File archivo = new java.io.File("archivo_inexistente.txt");
             java.util.Scanner lector = new java.util.Scanner(archivo);  // Lanza FileNotFoundException
             while (lector.hasNextLine()) {
                 System.out.println(lector.nextLine());
             }
         } catch (java.io.FileNotFoundException e) {
             System.out.println("⚠️ Error: El archivo no se encuentra.");
         }
     }
 
     // ---------------------------------------------------------------
     // ! Ejemplo: Manejo de NullPointerException
     // ---------------------------------------------------------------
     /**
      * 📌 TEORÍA:
      * - Una NullPointerException ocurre cuando se intenta utilizar un objeto que es null.
      * - Es fundamental validar que los objetos no sean nulos antes de usarlos.
      *
      * 🔵 EJERCICIO PARA EL ALUMNO:
      * Modifica este método para comprobar si el objeto es null antes de acceder a su método,
      * y en caso afirmativo, mostrar un mensaje de advertencia sin lanzar la excepción.
      */
     public static void ejemploObtenerElementoNull() {
         try {
             String texto = null;  // Inicializamos la variable como null
             // Intentamos acceder a la longitud del texto, lo que genera NullPointerException
             System.out.println("Longitud del texto: " + texto.length());
         } catch (NullPointerException e) {
             System.out.println("⚠️ Error: Intentaste acceder a un objeto nulo.");
         }
     }
 
     // ---------------------------------------------------------------
     // ! Ejemplo Extra: Lanzar una Excepción Personalizada
     // ---------------------------------------------------------------
     /**
      * 📌 TEORÍA:
      * - Además de capturar excepciones predefinidas, se pueden lanzar excepciones personalizadas
      *   para indicar errores específicos en tu aplicación.
      * - Para ello se utiliza la palabra clave "throw" seguida de una instancia de una excepción.
      *
      * 🔵 EJERCICIO PARA EL ALUMNO:
      * Crea una excepción personalizada extendiendo la clase Exception y utiliza esa excepción en
      * este método en lugar de IllegalArgumentException.
      */
     public static void ejemploLanzarExcepcionPersonalizada() {
         Scanner sc = new Scanner(System.in);
         try {
             System.out.print("Introduce tu edad: ");
             int edad = sc.nextInt();
             if (edad < 0) {
                 // Lanzamos una excepción personalizada si la edad es negativa
                 throw new IllegalArgumentException("La edad no puede ser negativa.");
             }
             System.out.println("Tu edad es: " + edad);
         } catch (IllegalArgumentException e) {
             System.out.println("⚠️ Error: " + e.getMessage());
         } catch (Exception e) {
             System.out.println("⚠️ Error general: " + e.getMessage());
         }
     }
 }
 