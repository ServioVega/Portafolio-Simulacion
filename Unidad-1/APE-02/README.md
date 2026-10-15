# APE-02: Comparación de Modelos de Crecimiento Poblacional

> **Comparación de modelos deterministas, estocásticos, discretos y continuos.**
> 
> **Asignatura:** Simulación | **Ciclo:** 5 | **Facultad:** FEIRNNR

---

## 1. Descripción del Proyecto

Este sistema compara cuatro paradigmas de simulación aplicados al crecimiento poblacional, utilizando como entrada la siguiente serie observada:

`datos = [1000, 1100, 1250, 1400, 1600, 1850, 2100]`

De esta serie se derivan los parámetros iniciales: 
* $P_0 = 1000$ (primer dato).
* Tasa de crecimiento $r = 0.1318$ (promedio de las tasas observadas).

### Modelos Evaluados

| Modelo | Ecuación | Característica |
| :--- | :--- | :--- |
| **Determinístico** | $P(t) = P_0 \cdot e^{rt}$ | Sin aleatoriedad |
| **Discreto** | $P_{k+1} = P_k + r \cdot P_k$ | Cambios por periodos definidos |
| **Estocástico** | $P_{k+1} = P_k + r \cdot P_k + \text{ruido}$ | Ruido distribuido normalmente $N(0, \sigma)$ |
| **Continuo (Euler)** | $P_{i+1} = P_i + dt \cdot r \cdot P_i$ | Aproximación numérica |

El proyecto sigue el patrón de diseño **Modelo-Vista-Controlador (MVC)**: 
* **Modelo:** Contiene la lógica de los cuatro modelos matemáticos.
* **Vista:** Muestra la tabla comparativa y la gráfica.
* **Controlador:** Coordina la interacción entre el modelo y la vista.

---

## 2. Tecnologías Utilizadas

* **Python 3:** Lenguaje de programación principal.
* **NumPy:** Cálculo numérico y generación de números aleatorios.
* **Matplotlib:** Generación de gráficas comparativas.

*Nota: Este proyecto no maneja credenciales, claves ni contraseñas.*

---

## 3. Instalación

Para ejecutar este proyecto de forma aislada, se recomienda usar un entorno virtual. Ejecuta los siguientes comandos en tu terminal:

```bash
# Navegar al directorio del código
cd Codigo/APE2_Simulacion

# Crear entorno virtual
python3 -m venv venv

# Activar el entorno virtual (Linux/macOS)
source venv/bin/activate
# En Windows usar: venv\Scripts\activate

# Instalar dependencias
pip install -r requirements.txt
```

---

## 4. Ejecución / Modo de Uso

Con el entorno virtual activo, ejecuta el archivo principal:

```bash
python3 main.py
```

El programa imprimirá la tabla comparativa (datos reales y los cuatro modelos por periodo) en la consola y desplegará una ventana con la gráfica de las cinco series en una sola figura.

---

## 5. Estructura del Proyecto

```text
APE-02/
├── README.md               → Este archivo
├── Reporte_Tecnico/        → Reporte técnico de la práctica
├── img/                    → Capturas del funcionamiento
└── Codigo/
    └── APE2_Simulacion/    
        ├── main.py         → Punto de entrada del programa
        ├── Modelo/         → poblacion.py: Lógica matemática
        ├── Vista/          → tabla.py y grafica.py: Presentación
        └── Controlador/    → controlador.py: Coordinación MVC
```

---


## 6. Declaración de uso de IA

En el desarrollo de esta práctica se utilizó asistencia de IA generativa (Claude, de Anthropic) como herramienta de apoyo. 

**Distribución del aporte:**

| Actividad | Responsable |
| :--- | :--- |
| Enunciado, decisiones de diseño y estructura MVC | Estudiante |
| Revisión del código | IA, con dirección del estudiante |
| Creación de archivos, ejecución y verificación | Estudiante |
| Comprensión del código para sustento | Estudiante |

<details>
<summary><b>💬 Ver Prompts utilizados (Clic para desplegar)</b></summary>
<br>

> *"Necesito que me ayudes revisando este APE fue algo similar al APE01 que realizamos, el mismo patron de diseño, asi que ayudame a revisar si puedo mejorar algo del código si hay alguna observación que tengas sobre el APE me avisas"*

</details>
