# APE-06 · Validación estadística de números pseudoaleatorios mediante Chi-cuadrado y Kolmogorov-Smirnov

| | |
|---|---|
| **Asignatura** | Simulación |
| **Ciclo** | 5 |
| **Unidad** | U2 · Generación de números pseudoaleatorios y generación de variables aleatorias |
| **Práctica** | Nro. 06 |
| **Docente** | José O. Guamán Q. |

**Objetivo:** evaluar la calidad estadística de una secuencia de números
pseudoaleatorios mediante pruebas de bondad de ajuste (Chi-cuadrado y
Kolmogorov-Smirnov) para determinar si los valores generados con el LCG
presentan un comportamiento compatible con una distribución uniforme en [0, 1].

---

## 1. Estructura del repositorio

```
APE-06/
├── README.md               → Este archivo
├── Reporte_Tecnico/        → Reporte técnico de la práctica
├── img/                    → Gráficas generadas por ambos lenguajes
├── Resultados/             → CSV de pruebas, frecuencias y tablas Python/Java
└── Codigo/
    ├── APE6_Simulacion/            → Implementación en PYTHON (patrón MVC)
    │   ├── main.py                 → Punto de entrada del programa
    │   ├── requirements.txt        → Dependencia: matplotlib
    │   ├── Modelo/                 → lcg.py (Algoritmo 1), pruebas.py (Algoritmos 2 y 3),
    │   │                             montecarlo.py (estimación de π)
    │   ├── Vista/                  → tabla.py (consola) y grafica.py (matplotlib)
    │   └── Controlador/            → controlador.py: coordinación de los 3 algoritmos
    └── APE6_Simulacion_Java/       → Implementación en JAVA (patrón MVC)
        ├── Main.java               → Punto de entrada del programa
        ├── modelo/                 → LCG.java, Pruebas.java, MonteCarlo.java
        ├── vista/                  → Tabla.java y Grafica.java (PNG con Java2D)
        ├── controlador/            → Controlador.java
        ├── compilar.sh             → Compila (javac -encoding UTF-8 ...)
        ├── ejecutar.sh             → Ejecuta (java -Dfile.encoding=UTF-8 Main)
        └── limpiar.sh              → Borra los archivos .class
```

**Cómo ejecutar** (probado en Linux/Nobara desde la terminal o VS Code):

```bash
# ---- Python ----
cd Unidad-2/APE-06/Codigo/APE6_Simulacion
pip install -r requirements.txt     # solo matplotlib
python3 main.py                     # ejecución completa
python3 main.py --semilla 67890     # cambia la semilla

# ---- Java ----
cd Unidad-2/APE-06/Codigo/APE6_Simulacion_Java
./compilar.sh                       # javac (requiere JDK 11+)
./ejecutar.sh                       # java
./limpiar.sh                        # borra los .class
```

Ambos programas escriben sus CSV en `Resultados/` y sus gráficas en `img/`.
Si se ejecutan los dos con la misma semilla, cada uno detecta el CSV del otro
y **compara automáticamente las secuencias y mezcla la tabla de tiempos**.

---

## 2. Fundamento y correspondencia con el pseudocódigo de la guía

Hipótesis: `H0: X ~ U(0,1)` vs `H1: X ≁ U(0,1)`, con `α = 0.05`.

| Algoritmo de la guía | Python | Java |
|---|---|---|
| Algoritmo 1 · LCG: `X(n+1) = (a·X(n)+c) mod m`, `U = X/m` | `Modelo/lcg.py` | `modelo/LCG.java` |
| Algoritmo 2 · Chi-cuadrado (k = 10, gl = 9) | `Modelo/pruebas.py · chi_cuadrado()` | `modelo/Pruebas.java · chiCuadrado()` |
| Algoritmo 3 · Kolmogorov-Smirnov (D⁺, D⁻, D = max) | `Modelo/pruebas.py · kolmogorov_smirnov()` | `modelo/Pruebas.java · kolmogorovSmirnov()` |
| Paso 14 del Algoritmo 3 · gráfica F empírica vs F teórica | `Vista/grafica.py` | `vista/Grafica.java` |

Parámetros del LCG (los mismos del APE-05, clásicos de *Numerical Recipes*):
`a = 1664525`, `c = 1013904223`, `m = 2³² = 4 294 967 296`, semilla `X(0) = 12345`.

Los valores p se calculan con implementaciones propias idénticas en ambos
lenguajes (log-gamma de Lanczos, serie de potencias / fracción continua de
Lentz para la distribución χ², y la serie asintótica de Kolmogorov), por lo
que **ambos lenguajes arrojan exactamente los mismos estadísticos**.

---

## 3. Resultados esperados

### 3.1 Validación de la secuencia (n = 10 000, α = 0.05)

| Prueba | Estadístico | Parámetro | Valor p | Decisión |
|---|---|---|---|---|
| Chi-cuadrado | χ² = **14.160000** | gl = 9 | **0.116746** | No se rechaza H0 (uniforme) |
| Kolmogorov-Smirnov | D = **0.005960** (D⁺ = 0.005960, D⁻ = 0.005411) | n = 10 000 | **0.869508** | No se rechaza H0 (uniforme) |

Frecuencias observadas del Chi-cuadrado (esperada = 1000 por intervalo):

| Intervalo | [0.0,0.1) | [0.1,0.2) | [0.2,0.3) | [0.3,0.4) | [0.4,0.5) | [0.5,0.6) | [0.6,0.7) | [0.7,0.8) | [0.8,0.9) | [0.9,1.0) |
|---|---|---|---|---|---|---|---|---|---|---|
| Observada | 1016 | 993 | 1049 | 940 | 949 | 1063 | 1014 | 1016 | 984 | 976 |

Estos resultados son **idénticos en Python y en Java** (mismos parámetros,
misma aritmética entera de 64 bits).

### 3.2 Tabla de Resultados Esperados: estimación de π por Monte Carlo

La tabla solicitada en la guía, completada con las ejecuciones de ambos
programas (misma semilla 12345; tiempos medidos internamente con
`time.perf_counter` en Python y `System.nanoTime` en Java, sin incluir la
escritura de archivos; tiempos de referencia, varían según la máquina):

| N | π estimado | Error | Tiempo Python (s) | Tiempo Java (s) |
|---|---|---|---|---|
| 1 000 | 3.096000 | 0.045593 | 0.0004 | 0.0004 |
| 10 000 | 3.165600 | 0.024007 | 0.0038 | 0.0004 |
| 100 000 | 3.143120 | 0.001527 | 0.0385 | 0.0041 |
| 1 000 000 | 3.141384 | 0.000209 | 0.3952 | 0.0185 |

Ambos lenguajes producen **exactamente el mismo π estimado y el mismo error**
para cada N, porque usan la misma secuencia. Java es aproximadamente
**15–20 veces más rápido** en N = 1 000 000 (código compilado a máquina vs
interpretado por CPython).

### 3.3 Verificación Python = Java

Con la misma semilla y parámetros se compararon los primeros 1 000 valores
(`Resultados/comparacion_secuencias.csv`):

```
Secuencias idénticas: SÍ ✔   (1000 de 1000 valores iguales)
```

---

## 4. Conclusiones

1. El generador congruencial lineal con `a = 1664525`, `c = 1013904223`,
   `m = 2³²` pasa **ambas pruebas de bondad de ajuste** con la semilla 12345:
   el valor p del Chi-cuadrado (0.1167) y el de Kolmogorov-Smirnov (0.8695)
   superan α = 0.05, por lo que no se rechaza H0 y la secuencia es
   estadísticamente compatible con una U(0,1).
2. El Chi-cuadrado valida la **uniformidad** agrupando los datos en 10
   intervalos; su resultado depende de la elección de k. Kolmogorov-Smirnov
   compara la distribución acumulada empírica con la teórica **sin agrupar**,
   por lo que aprovecha mejor la información de la muestra (con n = 10 000 su
   valor p es mucho más alto y la decisión es más clara).
3. La estimación de π por Monte Carlo mejora al aumentar N: el error baja de
   0.0456 (N = 1 000) a 0.0002 (N = 1 000 000), consistente con el error
   esperado del método, que decrece como 1/√N.
4. Las implementaciones en Python y Java son **deterministas e idénticas**:
   usando los mismos parámetros generan la misma secuencia y los mismos
   resultados numéricos. La única diferencia apreciable es el tiempo de
   ejecución, a favor de Java para volúmenes grandes de datos.
5. Pasar las pruebas no "demuestra" que el generador sea aleatorio (ver
   pregunta 5); solo indica que, con α = 0.05, no se encontró evidencia
   estadística contra su uniformidad.

---

## 5. Preguntas de control

**1. ¿Qué es la hipótesis nula?**
Es la afirmación que se somete a prueba y que se asume verdadera hasta que la
evidencia estadística indique lo contrario. En esta práctica es
`H0: X ~ U(0,1)`, es decir, "los números generados siguen una distribución
uniforme en [0,1]". El análisis siempre parte de ella: las pruebas χ² y KS
solamente deciden si hay evidencia suficiente para rechazarla.

**2. ¿Qué representa el valor p?**
Es la probabilidad de obtener un resultado igual o más extremo que el
observado, **suponiendo que H0 es verdadera**. Un valor p grande (p ≥ 0.05)
significa que las desviaciones observadas son compatibles con el azar; un
valor p pequeño (p < 0.05) indica que tal desviación sería muy improbable
bajo H0 y por ello se rechaza. No es la probabilidad de que H0 sea verdadera.

**3. ¿Qué significa rechazar H₀?**
Significa que los datos observados son tan improbables bajo la hipótesis nula
(estadístico en la zona de rechazo, valor p < α) que concluyemos que H0 no
explica los datos. En esta práctica, rechazar H0 indicaría que la secuencia
del LCG **no** se comporta como una U(0,1) y el generador no pasa la prueba
de uniformidad. Es una conclusión con riesgo de error α = 0.05, no una
demostración absoluta.

**4. ¿Qué diferencia existe entre Chi-cuadrado y KS?**
Chi-cuadrado compara **frecuencias agrupadas**: divide [0,1] en k intervalos,
cuenta cuántos datos caen en cada uno y compara con la frecuencia esperada;
necesita elegir k (aquí 10, gl = 9) y pierde información por el agrupamiento.
Kolmogorov-Smirnov compara la **distribución acumulada empírica** Fn(x) con
la teórica F(x) usando la mayor diferencia absoluta D = sup|Fn(x) − F(x)|,
trabaja con los datos sin agrupar y no depende de intervalos; sin embargo,
su valor p clásico asume que F(x) está totalmente especificada (aquí F(x) = x).
En resumen: χ² es una prueba sobre frecuencias por intervalos y KS sobre la
función de distribución acumulada completa.

**5. ¿Por qué una prueba estadística no demuestra que un generador sea
verdaderamente aleatorio?**
Porque un generador pseudoaleatorio es un **algoritmo determinista**: la misma
semilla siempre reproduce la misma secuencia completa, predecible en
principio. Las pruebas (χ², KS) solo verifican *algunas* propiedades
estadísticas (uniformidad, en este caso) sobre una muestra finita; no detectan
dependencias estructurales (por ejemplo correlaciones entre valores
consecutivos, que un LCG sí tiene en dimensiones mayores — el fenómeno de las
"planos" de Marsaglia), ni pueden demostrar ausencia de patrones. Además,
"no rechazar H0" no prueba que H0 sea verdadera: solo indica que la muestra no
ofreció evidencia en contra. Por eso un generador se valida con un conjunto de
pruebas (uniformidad, independencia, corridas, etc.) y nunca con una sola.

---

## 6. Archivos generados (Resultados/ e img/)

| Archivo | Contenido |
|---|---|
| `python_resultados_pruebas.csv` / `java_resultados_pruebas.csv` | χ² y KS: estadístico, valor p y decisión |
| `python_frecuencias_chi.csv` / `java_frecuencias_chi.csv` | Frecuencias observadas/esperadas por intervalo |
| `python_secuencia.csv` / `java_secuencia.csv` | Primeros 1 000 valores U(i) de cada lenguaje |
| `comparacion_secuencias.csv` | Comparación valor a valor Python vs Java |
| `python_tabla_pi.csv` / `java_tabla_pi.csv` | π, error y tiempos de cada lenguaje |
| `tabla_pi_python_java.csv` | Tabla de Resultados Esperados mezclada |
| `img/chi_cuadrado_frecuencias.png` | Barras observadas vs esperada |
| `img/kolmogorov_smirnov_cdf.png` | F empírica vs F teórica (paso 14, Algoritmo 3) |
| `img/montecarlo_pi_convergencia.png` | Convergencia de π según N (Python y Java) |
