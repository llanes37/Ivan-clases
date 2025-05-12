# UT1 - Hilos en Java: Teoría y Fragmentos Clave

Este documento reúne los **conceptos teóricos** más importantes sobre hilos en Java y fragmentos de código esenciales para tu estudio.

---

## 1. ¿Qué es un Hilo (Thread)?

* Unidad ligera de ejecución dentro de la JVM.
* Comparte memoria con otros hilos del mismo proceso.
* Permite **concurrencia** (multitarea dentro de la misma aplicación).

```java
// Ejemplo mínimo: crea y arranca un hilo
public class MiHilo extends Thread {
    @Override
    public void run() {
        System.out.println("Hola desde MiHilo");
    }
}

// En main:
new MiHilo().start();
```

---

## 2. Ciclo de Vida de un Hilo

1. **NEW**: instanciado, no iniciado.
2. **RUNNABLE**: tras `start()`, listo para ejecutarse.
3. **BLOCKED / WAITING / TIMED\_WAITING**: espera por monitor, `sleep()`, `join()`, I/O.
4. **TERMINATED**: ha completado `run()` o ha sido interrumpido.

```java
Thread hilo = new Thread(() -> {/*...*/});  // NEW
hilo.start();                                // RUNNABLE
// hilo.sleep(1000);                       // TIMED_WAITING
// hilo.join();                            // WAITING
// llega al final de run()               // TERMINATED
```

---

## 3. `start()` vs `run()`

* **`start()`**: crea un hilo del sistema operativo y llama a `run()` en paralelo.
* **`run()`**: ejecuta el método en el hilo actual, sin concurrencia.

```java
UT1_HiloSimple miHilo = new UT1_HiloSimple();
miHilo.start(); // Concurrencia real
miHilo.run();   // Ejecución secuencial en main
```

---

## 4. `sleep()` e `interrupt()`

* **`Thread.sleep(millis)`**: pausa el hilo actual sin liberar CPU.
* **`interrupt()`**: señala interrupción, provoca `InterruptedException` si está en sleep/join/wait.

```java
try {
    Thread.sleep(500);
} catch (InterruptedException e) {
    System.out.println("Hilo interrumpido");
}
```

---

## 5. `join()`

Espera a que otro hilo termine.

```java
hilo.start();
hilo.join();  // main se bloquea hasta que hilo acaba
```

---

## 6. Prioridad de Hilo

* Rango: `Thread.MIN_PRIORITY (1)` a `Thread.MAX_PRIORITY (10)`.
* `thread.setPriority(int)` puede influir en scheduling, pero no garantiza orden.

```java
Thread hilo = new Thread(...);
hilo.setPriority(Thread.MAX_PRIORITY);
hilo.start();
```

---

## 7. Sincronización y Condiciones de Carrera

* **`synchronized`**: protege secciones críticas.
* **Race condition**: múltiples hilos modifican datos compartidos sin control.

```java
public class Contador {
    private int count = 0;
    public synchronized void increment() {
        count++;
    }
}
```

---

## 8. Runnable y ExecutorService

* Alternativa a `Thread`: implementar `Runnable` o `Callable`.
* `ExecutorService` gestiona pools de hilos.

```java
ExecutorService pool = Executors.newFixedThreadPool(3);
pool.submit(() -> System.out.println("Tarea ejecutada"));
pool.shutdown();
```

---

## 9. ¿Por qué usar hilos? Casos reales

* **Servidores web**: atender múltiples peticiones.
* **Interfaces gráficas**: no bloquear la UI.
* **Cálculos paralelos**: aprovechar múltiples núcleos.
* **I/O asincrónico**: descargas y lecturas sin bloquear.

---

> **Tip de estudio:** Copia cada fragmento en tu IDE, ejecútalo y modifica los valores (tiempos, bucles, prioridades) para observar el comportamiento.

---

## Fragmentos Clave para Práctica Rápida

1. **Extender Thread**

   ```java
   public class MiHilo extends Thread {
       @Override
       public void run() {
           System.out.println("Paso 1");
       }
   }
   ```

2. **Implementar Runnable**

   ```java
   Runnable tarea = () -> System.out.println("Desde Runnable");
   new Thread(tarea).start();
   ```

3. **Uso de join() con timeout**

   ```java
   hilo.join(500);
   System.out.println("Esperé max 0.5s");
   ```

4. **Sincronización básica**

   ```java
   public synchronized void metodoCritico() { /* ... */ }
   ```

5. **Interrupción**

   ```java
   hilo.interrupt();
   ```
