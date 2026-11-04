# APE-05 · Implementación y análisis del Generador Congruencial Lineal (LCG) en Python y Java

| | |
|---|---|
| **Asignatura** | Simulación |
| **Ciclo** | 5 |
| **Unidad** | U2 · Generación de números pseudoaleatorios y generación de variables aleatorias |
| **Práctica** | Nro. 05 |
| **Docente** | José O. Guamán Q. |

**Objetivo:** comprender el funcionamiento de un generador pseudoaleatorio mediante la
implementación del método LCG:

1. Implementar un generador LCG en Python.
2. Implementar el mismo algoritmo en Java.
3. Comparar las secuencias generadas y analizar sus propiedades.

---

## 1. Estructura del repositorio

```
APE-05/
├── README.md               → Este archivo
├── Reporte_Tecnico/        → Reporte técnico de la práctica (próxima entrega)
├── img/                    → Gráficas generadas y capturas del funcionamiento
├── Resultados/             → Datos generados (CSV) y tablas de comparación Python/Java
└── Codigo/
    ├── APE5_Simulacion/            → Implementación en PYTHON (patrón MVC)
    │   ├── main.py                 → Punto de entrada del programa
    │   ├── requirements.txt        → Dependencia: matplotlib
    │   ├── Modelo/                 → lcg.py: lógica matemática (recursión, Hull-Dobell, saltos, ciclos)
    │   ├── Vista/                  → tabla.py y grafica.py: presentación
    │   └── Controlador/            → controlador.py: coordinación MVC de los pasos 1 a 8
    └── APE5_Simulacion_Java/       → Implementación en JAVA (patrón MVC)
        ├── Main.java               → Punto de entrada del programa
        ├── modelo/                 → LCG.java: lógica matemática
        ├── vista/                  → Tabla.java y Grafica.java (gráficas PNG con Java2D, sin librerías externas)
        ├── controlador/            → Controlador.java: coordinación MVC de los pasos 1 a 8
        ├── compilar.sh             → Compila (javac -encoding UTF-8 ...)
        ├── ejecutar.sh             → Ejecuta (java -Dfile.encoding=UTF-8 Main)
        └── limpiar.sh              → Borra los archivos .class
```

## 2. Fundamento teórico (resumen)

El **Generador Congruencial Lineal** utiliza la relación recursiva:

```
X(n+1) = (a · X(n) + c) mod m          U(n) = X(n) / m  ∈ [0, 1)
```

La calidad del generador depende de los parámetros y de la semilla. El
**teorema de Hull-Dobell (1962)** garantiza el período máximo **τ = m** si y
solo si: (1) `gcd(c, m) = 1`; (2) todo factor primo `p` de `m` divide a
`a − 1`; (3) si `4 | m` entonces `4 | (a − 1)`.

Parámetros usados en esta práctica (los indicados en la guía, clásicos de
*Numerical Recipes*):

| Parámetro | Valor | Rol |
|---|---|---|
| `a` | 1664525 | multiplicador |
| `c` | 1013904223 | incremento |
| `m` | 2³² = 4294967296 | módulo |
| `X(0)` | 12345 | semilla (987654321 en el paso 7) |

## 3. Requisitos

- **Python 3.8+** con `matplotlib` → `pip install -r Codigo/APE5_Simulacion/requirements.txt`
- **JDK 8 o superior** (`javac` y `java` en el PATH)
- Sistema operativo Linux o macOS (según la guía)

> **Instalación del JDK en Nobara / Fedora:** `sudo dnf install java-21-openjdk-devel`
> (el paquete `devel` incluye `java` y `javac`; verifique con `javac --version`).
> En Debian/Ubuntu: `sudo apt install openjdk-21-jdk`.

## 4. Cómo ejecutar

### 4.1 Python

```bash
cd Codigo/APE5_Simulacion
python3 main.py
```

### 4.2 Java

```bash
cd Codigo/APE5_Simulacion_Java
bash compilar.sh     # javac -encoding UTF-8 Main.java modelo/*.java vista/*.java controlador/*.java
bash ejecutar.sh     # java -Dfile.encoding=UTF-8 Main
```

> `ejecutar.sh` compila automáticamente si no existen los `.class` (o si el
> código cambió) y detecta si falta el JDK, indicando cómo instalarlo. Si
> `bash compilar.sh` responde *"'javac' no está instalado"*, instale el JDK:
> `sudo dnf install java-21-openjdk-devel` (Nobara/Fedora).

**Orden recomendado:** ejecutar primero **Java** y después **Python** (o
viceversa). Cada programa guarda sus valores en `Resultados/`; el segundo en
ejecutarse detecta el CSV del otro y construye automáticamente la tabla
comparativa del paso 4 y el resumen del paso 8. Con `--solo-comparar` se
repite únicamente esa comparación.

### 4.3 Opciones de línea de comandos

| Opción | Python | Java | Descripción |
|---|---|---|---|
| `--semilla X` | ✓ | ✓ | cambia la semilla principal (paso 1) |
| `--semilla-alternativa X` | ✓ | ✓ | semilla usada en el paso 7 (por defecto 987654321) |
| `--n X` | ✓ | ✓ | cantidad de valores a generar (por defecto 10000) |
| `--solo-comparar` | ✓ | ✓ | solo compara los CSV de ambos lenguajes |
| `--sin-periodo-empirico` | — | ✓ | omite el conteo exhaustivo de los 2³² estados |

> El conteo exhaustivo del período en Java recorre los 4 294 967 296 estados
> y tarda unos segundos (≈ 6-7 s en una CPU de escritorio).

## 5. Resultados obtenidos

### 5.1 Pasos 1 a 3 · Misma secuencia en ambos lenguajes

Cada programa genera 10 000 valores con `a = 1664525`, `c = 1013904223`,
`m = 2³²` y semilla `12345`. Los archivos `Resultados/python_lcg_semilla_12345.csv`
y `Resultados/java_lcg_semilla_12345.csv` son **idénticos byte a byte**.

### 5.2 Paso 4 · Primeras 20 observaciones (tabla comparativa)

Extracto de `Resultados/comparacion_python_java_semilla_12345.csv`
(la tabla completa, con las 10 000 filas, está en ese archivo):

| n | X(n) Python | X(n) Java | U(n) Python | U(n) Java | coincide |
|---:|---:|---:|---:|---:|:---:|
| 1 | 87628868 | 87628868 | 0.020402685739 | 0.020402685739 | SI |
| 2 | 71072467 | 71072467 | 0.016547848238 | 0.016547848238 | SI |
| 3 | 2332836374 | 2332836374 | 0.543155794498 | 0.543155794498 | SI |
| 4 | 2726892157 | 2726892157 | 0.634904056089 | 0.634904056089 | SI |
| 5 | 3908547000 | 3908547000 | 0.910029513761 | 0.910029513761 | SI |
| … | … | … | … | … | … |
| 20 | 4092090893 | 4092090893 | 0.952764156507 | 0.952764156507 | SI |

**Resultado: 10 000 de 10 000 valores idénticos (100,00 %)**, tanto con la
semilla 12345 como con la 987654321 (verificado también para el paso 7).

### 5.3 Paso 5 · Gráficas (carpeta `img/`)

- `secuencia_python_semilla_12345.png` / `secuencia_java_semilla_12345.png` — U(n) frente a n.
- `dispersion_python_semilla_12345.png` / `dispersion_java_semilla_12345.png` — pares (U(n), U(n+1)): nube uniforme, sin hiperplanos de Marsaglia visibles.
- `histograma_python_semilla_12345.png` / `histograma_java_semilla_12345.png` — 20 clases en [0, 1) alrededor de la frecuencia esperada (500).
- `comparacion_semillas_12345_vs_987654321.png` — efecto del cambio de semilla.

(Las de Python usan matplotlib; las de Java se dibujan con Java2D, sin
librerías externas.)

### 5.4 Paso 6 · Análisis del período

| Análisis | Resultado |
|---|---|
| Hull-Dobell, condición 1 | gcd(1013904223, 2³²) = 1 ✓ |
| Hull-Dobell, condición 2 | 1664524 mod 2 = 0 ✓ |
| Hull-Dobell, condición 3 | 1664524 mod 4 = 0 ✓ |
| **Período teórico** | **τ = m = 2³² = 4 294 967 296 (máximo)** |
| Verificación empírica (Java) | conteo exhaustivo del ciclo completo: τ = 4 294 967 296 ✓ (~6,6 s; el módulo 2³² se calcula con máscara de bits) |
| Confirmación (Python) | salto de 2³² pasos en O(log m): X(2³²) = X(0) ✓ y X(2³¹) ≠ X(0) ✓ |
| Repeticiones en los 10 000 valores | ninguna (10 000 estados distintos) ✓ |

Además, cada programa incluye una **demostración con módulo pequeño**
(detección empírica de ciclos): con `a=13, c=7, m=16` el período es 16 (= m,
Hull-Dobell se cumple), mientras que con `a=6, c=0, m=32` la secuencia
colapsa (cola μ = 5, período λ = 1) por violar las condiciones del teorema.

### 5.5 Pasos 7 y 8 · Cambio de semilla y comparación

Con la semilla 987654321 la secuencia cambia por completo, pero mantiene sus
propiedades estadísticas (10 000 valores):

| Estadístico | semilla 12345 | semilla 987654321 | Teórico U(0,1) |
|---|---:|---:|---:|
| media | 0.498770 | 0.500901 | 0.500000 |
| desviación estándar | 0.288118 | 0.287712 | 0.288675 |
| mínimo | 0.000059 | 0.000001 | 0.000000 |
| máximo | 0.999926 | 0.999804 | 1.000000 |

## 6. Preguntas de control (respuesta breve)

1. **¿Qué significa pseudoaleatorio?** Se dice de una secuencia producida por
   un algoritmo **determinista** que, sin ser azar verdadero, satisface los
   tests estadísticos de aleatoriedad. Una computadora digital es determinista
   (sin un fenómeno físico externo no puede producir azar verdadero), pero
   para simular basta con secuencias que *parezcan* aleatorias **y** sean
   reproducibles.
2. **¿Qué función cumple la semilla?** Es el estado inicial X(0): determina
   completamente la secuencia (misma semilla + mismos parámetros → misma
   secuencia, propiedad clave de reproducibilidad). Cambiarla desplaza la
   secuencia a otro punto del ciclo sin alterar sus propiedades estadísticas
   (lo verificado en el paso 7).
3. **¿Qué significa período?** Es el menor τ > 0 tal que X(τ) = X(0): a partir
   de ahí la secuencia se repite. Como los estados son finitos, τ ≤ m, y la
   calidad del generador exige que τ esté lo más cerca posible de m (con
   Hull-Dobell se garantiza τ = m).
4. **¿Qué ocurre si se utilizan parámetros inadecuados?** El período se
   reduce o colapsa, aparecen correlaciones y estructura geométrica
   (hiperplanos de Marsaglia), los bits bajos tienen períodos mucho más
   cortos y el generador se vuelve predecible (incluso recuperable
   algebraicamente: inseguro para criptografía). El caso ANSI de 1992 mostró
   sesgos del ~4 % en decisiones tomadas con un LCG mal parametrizado.
5. **¿Qué ventaja tiene implementar el mismo algoritmo en dos lenguajes?**
   Permite la **verificación cruzada**: dos cadenas de herramientas
   independientes deben producir exactamente la misma secuencia (aquí:
   10 000/10 000 valores idénticos), lo que da una confianza muy alta en la
   corrección del código. Además evidencia las fortalezas de cada lenguaje:
   Python resuelve el salto de 2³² pasos en O(log m) gracias a sus enteros de
   precisión arbitraria, y Java recorre el ciclo completo de 4 294 967 296
   estados en segundos con su aritmética nativa de 64 bits.

## 7. Archivos generados

| Carpeta | Contenido |
|---|---|
| `Resultados/python_lcg_semilla_12345.csv` | 10 000 valores (n, X(n), U(n)) de Python |
| `Resultados/java_lcg_semilla_12345.csv` | 10 000 valores de Java (idéntico byte a byte al de Python) |
| `Resultados/python_lcg_semilla_987654321.csv`, `java_lcg_semilla_987654321.csv` | ídem para el paso 7 |
| `Resultados/comparacion_python_java_semilla_*.csv` | tabla comparativa completa (10 000 filas, columna `coincide`) |
| `img/*.png` | gráficas de los pasos 5, 7 y 8 (más las capturas que se agreguen) |

## 8. Notas técnicas

- La recursión se calcula con **aritmética entera exacta** en ambos lenguajes
  (enteros arbitrarios en Python; `long` de 64 bits en Java, donde
  `a·X + c ≤ ~7,2·10¹⁵ < Long.MAX_VALUE`), por eso las secuencias coinciden
  bit a bit.
- El redondeo a 12 decimales de U se hace en Java con `BigDecimal` y
  `HALF_EVEN` (el mismo criterio de Python): `String.format("%.12f")` no
  siempre redondea bien los casos límite (se detectó en la fila 9446 de la
  semilla 987654321 durante las pruebas).
- Los scripts `.sh` de Java usan `javac -encoding UTF-8` y
  `java -Dfile.encoding=UTF-8` para que los acentos se muestren bien en
  cualquier terminal.

## 9. Pendiente

- `Reporte_Tecnico/` se completará con el reporte técnico de la práctica
  (análisis detallado, conclusiones y evidencias) en la siguiente entrega.

## 10. Bibliografía

- Guía de Actividades Práctico-Experimentales Nro. 005 · Simulación · FEIRNNR.
- Diapositivas de la Semana 5 · Generadores pseudoaleatorios (Unidad 5 del curso).
- Hull, T. E. & Dobell, A. R. (1962). *Serial Correlation Coefficients of a Congruential Sequence*.
- Park, S. & Miller, K. (1988). *Random Number Generators: Good Ones Are Hard to Find*.
- Marsaglia, G. (1968). Descubrimiento de los hiperplanos paralelos en LCGs.
