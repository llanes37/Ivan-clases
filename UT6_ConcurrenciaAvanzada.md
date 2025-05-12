# 📘 UT6 - Concurrencia avanzada y servicios multicliente (Java puro)

Esta unidad profundiza en la gestión de múltiples tareas simultáneas, uso de hilos, ejecución programada y servidores concurrentes.

---

## 🧠 ¿Qué se estudia en UT6?

* Cómo ejecutar múltiples tareas al mismo tiempo (concurrentemente)
* Cómo devolver resultados desde tareas en segundo plano
* Cómo programar tareas repetitivas con tiempos definidos
* Cómo crear un servidor que pueda atender múltiples clientes simultáneamente

---

## 🔍 Conceptos clave

* **ExecutorService**: Pool de hilos para ejecutar tareas concurrentes
* **Runnable**: Tareas que no devuelven resultado
* **Callable + Future**: Tareas que devuelven un valor
* **ScheduledExecutorService**: Tareas programadas cada cierto tiempo
* **ServerSocket / Socket**: Comunicación TCP
* **Thread pools**: Reutilización de hilos
* **shutdown()**: Cierre controlado de servicios
* **scheduleAtFixedRate()**: Ejecutar cada X tiempo una tarea

---

## ⚙️ Estructura del archivo `.java`

Incluye un `main()` con menú de selección de demo:

1. Pool de hilos con Runnable
2. Callable + Future con valor de retorno
3. Tarea periódica programada
4. Servidor TCP multicliente

---

## 🔧 DEMO 1: Pool de hilos con Runnable

```java
ExecutorService pool = Executors.newFixedThreadPool(3);
for (int i = 1; i <= 5; i++) {
    final int tarea = i;
    pool.submit(() -> {
        System.out.println("Ejecutando tarea " + tarea);
        Thread.sleep(1000);
        System.out.println("Tarea " + tarea + " finalizada");
    });
}
pool.shutdown();
```

📌 Ejecuta 5 tareas con solo 3 hilos disponibles al mismo tiempo.

---

## 🔧 DEMO 2: Callable + Future

```java
Callable<String> tarea = () -> {
    Thread.sleep(1500);
    return "Resultado terminado";
};
Future<String> resultado = pool.submit(tarea);
System.out.println(resultado.get());
```

📌 Ejecuta tarea en segundo plano y recupera el resultado de forma segura.

---

## 🔧 DEMO 3: ScheduledExecutorService

```java
ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
Runnable tarea = () -> System.out.println("Ejecutada: " + new Date());
scheduler.scheduleAtFixedRate(tarea, 0, 2, TimeUnit.SECONDS);
```

📌 Ejecuta la misma tarea cada 2 segundos.

---

## 🔧 DEMO 4: Servidor TCP multicliente

```java
try (ServerSocket server = new ServerSocket(5000)) {
    while (true) {
        Socket cliente = server.accept();
        new Thread(() -> atender(cliente)).start();
    }
}
```

📌 El servidor acepta múltiples clientes y lanza un hilo por cada uno.

---

## 🧪 Ejercicios para practicar

1. Cambia el número de hilos del pool a 1 y mide diferencia.
2. Crea un Callable que calcule el factorial de un número.
3. Programa una tarea que se ejecute cada minuto.
4. Simula 20 clientes concurrentes al servidor TCP.
5. Modifica el servidor para que cierre tras atender 5 clientes.

---

## ✅ Conclusión

Esta unidad sienta las bases de programación concurrente:

* Mejora el rendimiento
* Permite tareas paralelas
* Sirve para servidores, interfaces, cálculos, juegos, etc.

👉 Dominar estas técnicas es clave para desarrollar aplicaciones reales escalables y modernas.
