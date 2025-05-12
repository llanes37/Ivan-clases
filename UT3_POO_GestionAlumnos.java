import java.util.ArrayList;
import java.util.Scanner;

/**
 * ! Programación Orientada a Objetos en Java - Ejercicio Completo
 * ? Este archivo contiene un ejemplo de:
 * ? - Clases y Objetos
 * ? - Herencia y Polimorfismo
 * ? - Encapsulación con Getters y Setters
 * ? - Uso de ArrayList y Scanner para entrada de datos
 * ? - Implementación de estructuras de control (`if`, `switch`, `for`, `while`, `do-while`)
 * 
 * ! IMPORTANTE: Usa "Java Extension Pack" en VS Code para ejecutar este archivo.
 *
 * Autor: Joaquín
 */

// ****************************************************************************************
// ********************* TEORÍA: CONCEPTOS DE PROGRAMACIÓN ORIENTADA A OBJETOS ************
// ****************************************************************************************

/**
 * ? 1. **Clases y Objetos**:
 *      - Una clase define atributos y métodos.
 *      - Un objeto es una instancia de una clase.
 *
 * ? 2. **Herencia**:
 *      - Permite que una clase herede atributos y métodos de otra.
 *      - Se usa `extends`.
 *
 * ? 3. **Encapsulación**:
 *      - Protege los datos dentro de una clase.
 *      - Se accede a atributos con `getter` y `setter`.
 *
 * ? 4. **Polimorfismo**:
 *      - Permite que una subclase sobrescriba métodos de su superclase.
 *      - Se usa `@Override`.
 *
 * ? 5. **Estructuras de Control**:
 *      - `if-else`, `switch`, `for`, `while`, `do-while` para lógica de flujo.
 */

// ****************************************************************************************
// ********************* CLASES BASE Y SUBCLASES ******************************************
// ****************************************************************************************

/**
 * ! Clase Persona (Superclase)
 * ? Representa una persona con atributos básicos.
 */
class Persona {
    protected String nombre;
    protected int edad;

    // * Constructor
    public Persona(String nombre, int edad) {
        this.nombre = nombre;
        this.edad = edad;
    }

    // * Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    // * Método para mostrar información
    public void mostrarInformacion() {
        System.out.println("Nombre: " + nombre + ", Edad: " + edad);
    }
}

/**
 * ! Clase Estudiante (Subclase de Persona)
 * ? Representa a un estudiante que tiene un atributo adicional `curso`.
 */
class Estudiante extends Persona {
    private String curso;

    // * Constructor corregido
    public Estudiante(String nombre, int edad, String curso) {
        super(nombre, edad); // * Llamamos al constructor de Persona
        this.curso = curso;
    }

    // * Getter y Setter
    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    // * Método para mostrar información
    @Override
    public void mostrarInformacion() {
        super.mostrarInformacion();
        System.out.println("Curso: " + curso);

        // * Estructura de control switch
        switch (curso) {
            case "Matemáticas":
                System.out.println("📚 Estudiante de Matemáticas.");
                break;
            case "Historia":
                System.out.println("📖 Estudiante de Historia.");
                break;
            case "Física":
                System.out.println("🔬 Estudiante de Física.");
                break;
            default:
                System.out.println("📂 Curso no identificado.");
        }
    }
}

/**
 * ! Clase Profesor (Subclase de Persona)
 * ? Representa a un profesor con una materia específica.
 */
class Profesor extends Persona {
    private String materia;

    // * Constructor
    public Profesor(String nombre, int edad, String materia) {
        super(nombre, edad);
        this.materia = materia;
    }

    // * Getter y Setter
    public String getMateria() {
        return materia;
    }

    public void setMateria(String materia) {
        this.materia = materia;
    }

    // * Método para mostrar información
    @Override
    public void mostrarInformacion() {
        super.mostrarInformacion();
        System.out.println("📚 Materia que imparte: " + materia);
    }
}

// ****************************************************************************************
// ********************* CLASE PRINCIPAL **************************************************
// ****************************************************************************************

/**
 * ! Clase principal - POO_GestionAlumnos
 * ? Contiene la ejecución del programa.
 */
public class UT3_POO_GestionAlumnos {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // ! Lista de estudiantes
        ArrayList<Estudiante> estudiantes = new ArrayList<>();

        // ! Agregamos estudiantes
        estudiantes.add(new Estudiante("Juan", 20, "Matemáticas"));
        estudiantes.add(new Estudiante("María", 22, "Historia"));
        estudiantes.add(new Estudiante("Carlos", 19, "Física"));

        // ! Mostramos información de los estudiantes
        System.out.println("\n📋 Lista de Estudiantes:");
        for (Estudiante e : estudiantes) {
            e.mostrarInformacion();
            System.out.println("-------------------");
        }

        // ! Agregar nuevos estudiantes con bucle do-while
        System.out.println("\n📝 Añadir nuevos estudiantes (Escribe 'salir' para finalizar)");
        do {
            System.out.print("Nombre: ");
            String nombre = scanner.next();
            if (nombre.equalsIgnoreCase("salir")) break;

            System.out.print("Edad: ");
            int edad = scanner.nextInt();

            System.out.print("Curso: ");
            String curso = scanner.next();

            estudiantes.add(new Estudiante(nombre, edad, curso));
            System.out.println("✅ Estudiante agregado correctamente.");
        } while (true);

        // ! Mostrar la lista actualizada
        System.out.println("\n📋 Lista actualizada de estudiantes:");
        for (Estudiante e : estudiantes) {
            e.mostrarInformacion();
            System.out.println("-------------------");
        }

        scanner.close();
    }
}
