/**
 * 📖 TEORÍA: HILOS EN JAVA
 * ================================================
 * • Hilo (Thread): unidad ligera de ejecución dentro de un proceso JVM.
 *   - Permite tareas concurrentes (paralelas o cooperativas) compartiendo memoria.
 * • Ciclo de vida de un hilo:
 *   1. NEW: instanciado pero no iniciado.
 *   2. RUNNABLE: tras start(), listo para ejecutarse cuando el scheduler lo elija.
 *   3. BLOCKED/WAITING/TIMED_WAITING: espera por I/O, sleep(), join(), monitor o wait().
 *   4. TERMINATED: ha completado run() o ha sido interrumpido.
 * • start() vs run():
 *   - start(): crea un nuevo hilo en el SO y llama a run() en paralelo.
 *   - run(): ejecuta el método en el hilo actual (sin concurrencia).
 * • sleep(millis): pausa la ejecución del hilo actual sin liberar CPU.
 * • interrupt(): solicita interrupción, provocando InterruptedException en sleep(), join(), wait().
 * • join(): espera a que otro hilo termine antes de continuar.
 * • Prioridad de hilo (min 1, max 10, por defecto 5): influye en el scheduling, no es garantía.
 * • Sincronización:
 *   - synchronized: bloquea acceso a secciones críticas para evitar condiciones de carrera.
 *   - Race condition: ocurre cuando múltiples hilos acceden y modifican recursos sin control.
 * • ThreadGroup y ExecutorService: formas avanzadas de agrupar y gestionar hilos.
 * • Future y Callable: permite obtener resultados de tareas ejecutadas por hilos.
 *
 * • Usos reales de hilos en programación:
 *   - Servidores web y aplicaciones concurrentes: atienden múltiples peticiones simultáneas.
 *   - Interfaces gráficas (GUI): mantienen la UI responsiva manejando eventos en hilos separados.
 *   - Procesamiento paralelo: cálculos intensivos distribuidos en varios núcleos.
 *   - Operaciones de I/O asincrónicas: descargas de ficheros, lectura/escritura sin bloquear.
 *
 * • ¿Por qué estudiar hilos?
 *   - Mejorar rendimiento aprovechando hardware multinúcleo.
 *   - Gestionar tareas que esperan recursos externos sin congelar la aplicación.
 *   - Dominar sincronización y evitar errores de concurrencia.
 *   - Fundamentos de programación concurrente y paralela en sistemas reales.
 */

/**
 * 🧵 UT1 - Programación de Servicios y Procesos (PSP)
 * ================================================
 * 📌 Tema: Hilos básicos en Java
 * ------------------------------------------------
 * Este archivo muestra cómo crear y ejecutar dos hilos simples
 * extendiendo la clase `Thread`, con comentarios en cada línea,
 * iconos y ejercicios adicionales para profundizar en la práctica.
 */

public class UT1_HiloSimple {

    /**
     * 🧵 Clase interna que representa un hilo personalizado
     */
    static class HiloExtendido extends Thread {
        private final String nombreHilo; // 🏷️ Nombre del hilo para identificarlo

        public HiloExtendido(String nombreHilo) {
            this.nombreHilo = nombreHilo; // 📥 Guarda el nombre pasado como parámetro
        }

        @Override
        public void run() {
            for (int i = 1; i <= 10; i++) { // 🔄 Bucle que se repite 10 veces
                System.out.println("[" + nombreHilo + "] Ejecutando paso " + i); // 🖨️ Imprime paso actual
                try {
                    Thread.sleep(1000); // 💤 Espera 1 segundo entre pasos
                } catch (InterruptedException e) {
                    System.out.println("[" + nombreHilo + "] Hilo interrumpido."); // ⚠️ Mensaje de interrupción
                    return; // 🔚 Termina el hilo si es interrumpido
                }
            }
            System.out.println("[" + nombreHilo + "] Finalizado."); // ✔️ Finaliza ejecución
        }
    }

    /**
     * 🧪 Método principal que lanza los hilos
     */
    public static void main(String[] args) {
        System.out.println("Inicio del programa principal\n"); // 🚀 Marca inicio del main

        HiloExtendido hilo1 = new HiloExtendido("Hilo-A"); // 🧵 Crea hilo con nombre "Hilo-A"
        HiloExtendido hilo2 = new HiloExtendido("Hilo-B"); // 🧵 Crea hilo con nombre "Hilo-B"

        hilo1.start(); // ▶️ Arranca ejecución de hilo1
        hilo2.start(); // ▶️ Arranca ejecución de hilo2
        
        try {
            hilo1.join(); // ⏳ Espera que hilo1 finalice
            System.out.println("[Main] Hilo-A ha terminado, continúo.");
        } catch (InterruptedException e) {
            System.out.println("Main interrumpido al hacer join."); // ⚠️ Error si es interrumpido
        }

        System.out.println("[Main] Sigo con otras tareas mientras Hilo-B sigue:"); // 🔁 Continúa main
        for (int j = 1; j <= 3; j++) {
            System.out.println("[Main] Tarea " + j + " en paralelo"); // 📌 Ejecuta tareas mientras Hilo-B trabaja
            try {
                Thread.sleep(700); // 💤 Pausa entre tareas
            } catch (InterruptedException e) {
                System.out.println("Main interrumpido durante tareas.");
            }
        }

        try {
            hilo2.join(); // 🔚 Espera que hilo2 finalice antes de cerrar
        } catch (InterruptedException ignored) {}

        System.out.println("\nFin del programa principal"); // 🏁 Fin del main
    }
}

/*
 * 🎯 EJERCICIOS ADICIONALES PARA EL ALUMNO:
 * ----------------------------------------
 * 1️⃣ Prioridad de hilos (Thread#setPriority)
 *    a) Asigna a cada hilo una prioridad distinta (2 y 8).
 *    b) Observa e informa el orden de ejecución varias veces.
 *
 * 2️⃣ Extiende con Runnable (lambda)
 *    a) Crea dos Runnable anónimos que hagan 5 pasos.
 *    b) Usa Thread t = new Thread(miRunnable) y lanza ambos.
 *
 * 3️⃣ Sincronización de recurso compartido
 *    a) Implementa un contador estático incrementado por 2 hilos.
 *    b) Usa synchronized para proteger el método de incremento.
 *
 * 4️⃣ Condición de carrera intencionada
 *    a) Quita synchronized del ejercicio 3.
 *    b) Ejecuta y anota los valores incorrectos obtenidos.
 *
 * 5️⃣ Grupo de hilos (ThreadGroup)
 *    a) Crea un ThreadGroup llamado "GrupoPSP".
 *    b) Añade 3 hilos al grupo y fanion.
 *
 * 6️⃣ Interrupción desde main
 *    a) Arranca un hilo que haga 20 pasos.
 *    b) Tras 3 segundos, haz hilo.interrupt() y controla la salida.
 *
 * 7️⃣ ExecutorService básico
 *    a) Crea un pool de 3 hilos.
 *    b) Envía 5 tareas Runnable y cierra el pool con shutdown().
 *
 * 8️⃣ Future y Callable
 *    a) Define un Callable que retorna un String.
 *    b) Envía al ExecutorService y obtén el resultado con get().
 *
 * 9️⃣ join con timeout
 *    a) Usa hilo.join(500) antes de imprimir "Timeout superado".
 *
 * 🔟 EJERCICIO FINAL (MÁS SENCILLO)
 *    - Crea un solo hilo que imprima tu nombre 5 veces.
 *    - Usa Thread.sleep(500) entre impresiones.
 */