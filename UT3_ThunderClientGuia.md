# ⚡ UT3 - Guía completa para usar Thunder Client con tu API REST

Esta guía explica cómo usar la extensión **Thunder Client** en Visual Studio Code para **probar tu servidor Java** hecho con `HttpServer` en UT3, sin usar Postman ni curl.

---

## 🔌 ¿Qué es Thunder Client?

Thunder Client es una extensión ligera de VS Code para hacer **peticiones HTTP** de forma visual:

* Similar a Postman pero integrada en VS Code.
* Perfecta para probar APIs REST (`GET`, `POST`, `PUT`, `DELETE`).
* Puedes ver fácilmente el cuerpo, cabeceras, estado de respuesta y más.

---

## ✅ Paso 1: Instalar Thunder Client

1. Abre VS Code.
2. Ve a la pestaña de **Extensiones** (icono de cuadrados o `Ctrl+Shift+X`).
3. Busca **Thunder Client**.
4. Haz clic en **Instalar**.

Una vez instalado, verás un icono de rayo ⚡ en la barra lateral izquierda.

---

## 🟢 Paso 2: Ejecutar tu servidor Java (UT3)

Ejecuta tu clase `ApiRestProductos.java` con:

```bash
javac ApiRestProductos.java
java ApiRestProductos
```

Si todo va bien, debe aparecer:

```
Servidor REST en http://localhost:8000/productos
```

---

## 🌐 Paso 3: Hacer peticiones con Thunder Client

### 1️⃣ Crear producto (POST)

* Método: `POST`
* URL: `http://localhost:8000/productos`
* Body:

```json
{
  "nombre": "Camiseta",
  "precio": 19.99
}
```

* Headers:

```
Content-Type: application/json
```

* Haz clic en **Send** y verás una respuesta como:

```json
{
  "id": 1,
  "nombre": "Camiseta",
  "precio": 19.99
}
```

---

### 2️⃣ Listar productos (GET)

* Método: `GET`
* URL: `http://localhost:8000/productos`
* Haz clic en **Send** y verás un array JSON:

```json
[
  {
    "id": 1,
    "nombre": "Camiseta",
    "precio": 19.99
  }
]
```

---

### 3️⃣ Obtener por ID (GET)

* Método: `GET`
* URL: `http://localhost:8000/productos/1`

---

### 4️⃣ Actualizar producto (PUT)

* Método: `PUT`
* URL: `http://localhost:8000/productos/1`
* Body:

```json
{
  "nombre": "Sudadera",
  "precio": 29.99
}
```

* Header: `Content-Type: application/json`

---

### 5️⃣ Eliminar producto (DELETE)

* Método: `DELETE`
* URL: `http://localhost:8000/productos/1`

---

## 🎯 Recomendaciones

* Puedes guardar cada petición como un **request individual** para organizar tus pruebas.
* Revisa siempre que el puerto sea correcto (`8000`) y que el servidor esté activo.
* Thunder Client te muestra: código de estado (`200 OK`, `201 Created`, `404 Not Found`, etc.), el cuerpo y los headers de respuesta.

---

## 📚 Qué aprendes usando Thunder Client

* Cómo interactúan cliente y servidor en una API REST.
* Qué cabeceras se usan (`Content-Type`, etc).
* Cómo se estructura una petición HTTP con JSON.
* Cómo responde tu API: formatos, errores, estado HTTP.

---

¿Listo para simular una API con autenticación o filtrar productos con parámetros? Puedes continuar extendiendo tu servidor sin complicarte. 💪
