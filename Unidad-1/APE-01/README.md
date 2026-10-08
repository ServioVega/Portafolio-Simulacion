# APE-01: Modelo de Predicción de Lluvia

> **Construcción y simulación computacional de un modelo matemático**  
> **Asignatura:** Simulación | **Ciclo:** 5 | **Facultad:** FEIRNNR

---

## 1. Descripción del Proyecto

Sistema que simula el comportamiento de la atmósfera durante un día y predice en qué horas es probable que llueva. Para cada hora toma la humedad, nubosidad y temperatura, y calcula el índice de lluvia:

$$I = 0.5 \cdot H + 0.3 \cdot N + 0.2 \cdot T_f$$

* **$H$, $N$:** Humedad y nubosidad normalizadas ($0 - 1$).
* **$T_f$:** Factor de temperatura (obtenido de la tabla del enunciado, con interpolación lineal para temperaturas intermedias).

Cada valor de $I$ se clasifica según la siguiente tabla de reglas:

| Índice | Estado |
| :--- | :--- |
| $I < 0.40$ | Sin lluvia |
| $0.40 \le I < 0.60$ | Baja posibilidad |
| $0.60 \le I < 0.75$ | Lluvia probable |
| $I \ge 0.75$ | Lluvia |

El proyecto sigue el patrón arquitectónico **Modelo–Vista–Controlador (MVC)**: 
* **Modelo:** Contiene los cálculos.
* **Vista:** Presenta los resultados.
* **Controlador:** Coordina ambos componentes.

---

## 2. Tecnologías Utilizadas

* **Python 3:** Lenguaje principal.
* **Matplotlib:** Gráfica del índice de lluvia.

> 🔒 **Nota de seguridad:** Este proyecto no maneja credenciales, claves ni contraseñas.

---

## 3. Instalación

Clona el repositorio e instala las dependencias ejecutando:

    cd Codigo/APE1_Simulacion
    python3 -m venv venv
    source venv/bin/activate
    pip install -r requirements.txt

---

## 4. Ejecución / Modo de Uso

Con el entorno virtual activo, ejecuta el punto de entrada:

    python3 main.py

El programa imprime en consola la tabla de resultados (H, N, Tf, índice I y estado de cada hora) y luego muestra la gráfica del índice con los tres umbrales de clasificación.

---

## 5. Estructura del Proyecto

    APE-01/
    ├── README.md                 # Archivo de documentación principal
    ├── Reporte_Tecnico/          # Reporte técnico de la práctica
    ├── img/                      # Capturas del funcionamiento
    └── Codigo/APE1_Simulacion/
        ├── main.py               # Punto de entrada del programa
        ├── Modelo/               # Cálculos (índice, Tf, clasificación)
        ├── Vista/                # Presentación (tabla y gráfica)
        └── Controlador/          # Coordinación MVC

---

## 6. Información Adicional

### Tabla de Resultados
![Tabla de resultados](img/tabla_resultados.png)

### Gráfica del Índice de Lluvia
![Gráfica del índice](img/grafica_indice.png)

---

## 7. Declaración de uso de IA

En el desarrollo de esta práctica se utilizó asistencia de IA generativa (**GLM-5.3**, de Z.ai) como apoyo.

### Distribución del Aporte

| Actividad | Responsable |
| :--- | :--- |
| Algoritmo inicial (fórmula, datos y clasificación) | Estudiante |
| Estructura MVC y requisitos del programa | Estudiante |
| Reorganización del código en MVC e interpolación de $T_f$ | IA, con dirección del estudiante |
| Verificación de resultados y correcciones | Estudiante |
| Comprensión del código para sustento | Estudiante |

### Prompts Utilizados

> *"Ayudame a mejorar este avance pero no se que tal estara, el patron arquitectonico sera vista controlador, tengo 3 carpetas dentro de mi directorio APE1_Simulacion, Modelo, Vista, Controlador [...] te paso mas o menos como hice mi codigo para que me ayudes a mejorarlo"*

> *"Ayudame a hacer una declaración de uso de IA y explícame el código y porque está así y para que funciona y todo eso necesito entenderlo"*
