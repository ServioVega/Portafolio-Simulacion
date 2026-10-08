APE-01: Modelo de Predicción de Lluvia

Construcción y simulación computacional de un modelo matemático

Asignatura: Simulación | Ciclo 5 | FEIRNNR
1. Descripción del Proyecto

Sistema que simula el comportamiento de la atmósfera durante un día y predice en qué horas es probable que llueva. Para cada hora toma la humedad, nubosidad y temperatura, y calcula el índice de lluvia:

I = 0.5·H + 0.3·N + 0.2·Tf

    H, N: humedad y nubosidad normalizadas (0–1)
    Tf: factor de temperatura (tabla del enunciado, con interpolación lineal para temperaturas intermedias)

Cada valor de I se clasifica según la tabla de reglas:
Índice	Estado
I < 0.40	Sin lluvia
0.40 ≤ I < 0.60	Baja posibilidad
0.60 ≤ I < 0.75	Lluvia probable
I ≥ 0.75	Lluvia

El proyecto sigue el patrón arquitectónico Modelo–Vista–Controlador (MVC): el modelo contiene los cálculos, las vistas presentan los resultados y el controlador coordina ambos.
2. Tecnologías Utilizadas

    Python 3: lenguaje principal
    Matplotlib: gráfica del índice de lluvia

Este proyecto no maneja credenciales, claves ni contraseñas.
3. Instalación

cd Codigo/APE1_Simulacionpython3 -m venv venvsource venv/bin/activatepip install -r requirements.txt

4. Ejecución / Modo de Uso

Con el entorno virtual activo:

python3 main.py

El programa imprime en consola la tabla de resultados (H, N, Tf, índice I y estado de cada hora) y luego muestra la gráfica del índice con los tres umbrales de clasificación.
5. Estructura del Proyecto

APE-01/├── README.md               → este archivo├── Reporte_Tecnico/        → reporte técnico de la práctica├── img/                    → capturas del funcionamiento└── Codigo/APE1_Simulacion/    ├── main.py             → punto de entrada del programa    ├── Modelo/             → cálculos (índice, Tf, clasificación)    ├── Vista/              → presentación (tabla y gráfica)    └── Controlador/        → coordinación MVC

6. Información Adicional

Tabla de resultados:

Tabla de resultados

Gráfica del índice de lluvia:

Gráfica del índice
7. Declaración de uso de IA

En el desarrollo de esta práctica se utilizó asistencia de IA generativa (GLM-5.3, de Z.ai) como apoyo. Distribución del aporte:
Actividad	Responsable
Algoritmo inicial (fórmula, datos y clasificación)	Estudiante
Estructura MVC y requisitos del programa	Estudiante
Reorganización del código en MVC e interpolación de Tf	IA, con dirección del estudiante
Verificación de resultados y correcciones	Estudiante
Comprensión del código para sustento	Estudiante
  
Prompts utilizados
"Ayudame a mejorar este  avance pero no se que tal estara, el patron arquitectonico sera vista controlador, tengo 3 carpetas dentro de mi directorio APE1_Simulacion, Modelo, Vista, Controlador [...] te paso mas o menos como hice mi codigo para que me ayudes a mejorarlo"
"Ayudame a hacer una declaración de uso de IA y explícame el código y porque está así y para que funciona y todo eso necesito entenderlo"  
