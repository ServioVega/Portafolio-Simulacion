# APE-07 · Estimación de resultados mediante el método de Monte Carlo

| | |
|---|---|
| **Asignatura** | Simulación |
| **Ciclo** | 5 |
| **Unidad** | U2 · Generación de números pseudoaleatorios y generación de variables aleatorias |
| **Práctica** | Nro. 07 |
| **Docente** | José O. Guamán Q. |

**Objetivo:** aplicar el método de Monte Carlo para obtener aproximaciones
numéricas mediante experimentos aleatorios. De él se desprenden los objetivos
específicos de la guía: implementar un experimento Monte Carlo, analizar la
influencia del tamaño de muestra sobre el error y comparar los resultados
obtenidos en Python y Java.

**Nota de estructura:** a diferencia de los APE anteriores, esta práctica no
requiere Vista (no se generan gráficas ni GUI); la «pequeña tabla» para
visualizar los datos se imprime en consola y los resultados completos se
exportan en CSV a `Resultados/`.

---

## 1. Estructura del repositorio

```
APE-07/
├── README.md               → Este archivo
├── Reporte_Tecnico/        → Reporte técnico de la práctica
├── Resultados/             → CSV de tablas, secuencias y comparaciones
└── Codigo/
    ├── APE7_Simulacion/            → Implementación en PYTHON (MVC, sin Vista)
    │   ├── main.py                 → Punto de entrada del programa
    │   ├── requirements.txt        → Sin dependencias (solo biblioteca estándar)
    │   ├── Modelo/                 → lcg.py (generador) y montecarlo.py (π y σ teórico)
    │   └── Controlador/            → controlador.py: experimentos, tiempos, tabla y CSV
    └── APE7_Simulacion_Java/       → Implementación en JAVA (MVC, sin vista)
        ├── Main.java               → Punto de entrada del programa
        ├── modelo/                 → LCG.java y MonteCarlo.java
        ├── controlador/            → Controlador.java
        ├── compilar.sh             → Compila (javac -encoding UTF-8 ...)
        ├── ejecutar.sh             → Ejecuta (java -Dfile.encoding=UTF-8 Main)
        └── limpiar.sh              → Borra los archivos .class
```

**Cómo ejecutar** (probado en Linux/Nobara desde la terminal o VS Code):

```bash
# ---- Python ----
cd Unidad-2/APE-07/Codigo/APE7_Simulacion
python3 main.py                     # ejecución completa (guía)
python3 main.py --semilla 67890     # cambia la semilla del experimento

# ---- Java ----
cd Unidad-2/APE-07/Codigo/APE7_Simulacion_Java
./compilar.sh                       # javac (requiere JDK 11+)
./ejecutar.sh                       # java
./limpiar.sh                        # borra los .class
```

Ambos programas escriben sus CSV en `Resultados/`. Si se ejecutan los dos con
la misma semilla, cada uno detecta los CSV del otro y **compara automáticamente
las secuencias y mezcla la tabla de π, error y tiempos**.

---

## 2. Fundamento y correspondencia con la guía

El método de Monte Carlo genera puntos al azar dentro de un cuadrado y cuenta
cuántos pertenecen al círculo inscrito de radio 1 centrado en el origen. Como
el cociente de áreas es área_círculo / área_cuadrado = π/4, el estimador es:

```
π ≈ 4 · (Nc / N)        Error absoluto = |π estimado − π real|
```

donde `Nc` es el número de puntos dentro del círculo y `N` el total de puntos.
Con el LCG se generan valores U ∈ [0, 1) que se transforman a coordenadas del
cuadrado [-1, 1)² con `x = 2·u1 − 1`, `y = 2·u2 − 1`; el punto pertenece al
círculo si `x² + y² ≤ 1`. Cada punto consume un par (u1, u2): **N puntos → 2N
números pseudoaleatorios**. La variante es equivalente al cuarto de círculo
usado en el APE-06 (el cuadrado inscrito contiene 4 cuadrantes iguales).

| Elemento de la guía | Python | Java |
|---|---|---|
| Generador pseudoaleatorio LCG: `X(n+1) = (a·X(n)+c) mod m`, `U = X/m` | `Modelo/lcg.py` | `modelo/LCG.java` |
| Experimento Monte Carlo: π ≈ 4·Nc/N, error absoluto | `Modelo/montecarlo.py · estimar_pi()` | `modelo/MonteCarlo.java · estimarPi()` |
| Tamaños de muestra: 1 000, 10 000, 100 000, 1 000 000 de puntos | `Controlador/controlador.py · NIVEL_N` | `controlador/Controlador.java · NIVEL_N` |
| Variables a registrar: π estimado, error, tiempo Python, tiempo Java | `controlador.py` | `Controlador.java` |
| Implementación cruzada y tabla comparativa | lee los CSV de Java | lee los CSV de Python |

Parámetros del LCG (los mismos del APE-05 y APE-06, clásicos de *Numerical
Recipes*): `a = 1664525`, `c = 1013904223`, `m = 2³² = 4 294 967 296`, semilla
`X(0) = 12345`. El tiempo se mide internamente con `time.perf_counter` en
Python y `System.nanoTime` en Java, sobre la generación de la secuencia más el
conteo de puntos (sin incluir la escritura de archivos). Como referencia del
error esperado se calcula la desviación estándar teórica del estimador,
`σ(N) = √(π(4−π)/N) ≈ 1.6421/√N`, que decrece como 1/√N.

---

## 3. Resultados esperados

### 3.1 Verificación Python = Java (misma secuencia con los mismos parámetros)

Con la semilla 12345 ambos programas exportan sus primeros 1 000 valores; al
ejecutar el segundo, compara valor a valor (`Resultados/comparacion_secuencias.csv`):

```
Secuencias idénticas: SÍ ✔   (1000 de 1000 valores iguales)
```

| i | U(i) Python | U(i) Java | | i | U(i) Python | U(i) Java |
|---|---|---|---|---|---|---|
| 1 | 0.0204026857 | 0.0204026857 | | 6 | 0.1124616691 | 0.1124616691 |
| 2 | 0.0165478482 | 0.0165478482 | | 7 | 0.4958894053 | 0.4958894053 |
| 3 | 0.5431557945 | 0.5431557945 | | 8 | 0.5483488443 | 0.5483488443 |
| 4 | 0.6349040561 | 0.6349040561 | | 9 | 0.5961001171 | 0.5961001171 |
| 5 | 0.9100295138 | 0.9100295138 | | 10 | 0.7834504161 | 0.7834504161 |

Cumple el resultado esperado de la guía: «obtener dos implementaciones
funcionales que produzcan la misma secuencia cuando utilizan los mismos
parámetros». Por eso el π estimado y el error son **exactamente iguales** en
los dos lenguajes para cada N.

### 3.2 Tabla de Resultados Esperados: estimación de π por Monte Carlo

Tabla solicitada en la guía, completada con las ejecuciones de ambos programas
(semilla 12345; tiempos de referencia, varían según la máquina):

| N (puntos) | π estimado | Error absoluto | Tiempo Python (s) | Tiempo Java (s) |
|---|---|---|---|---|
| 1 000 | 3.188000 | 0.046407 | 0.0008 | 0.0007 |
| 10 000 | 3.151600 | 0.010007 | 0.0087 | 0.0016 |
| 100 000 | 3.133240 | 0.008353 | 0.0839 | 0.0118 |
| 1 000 000 | 3.141948 | 0.000355 | 0.8662 | 0.0252 |

(π real = 3.141593; la tabla cruzada completa, con ambos π y el σ teórico,
queda en `Resultados/tabla_pi_python_java.csv`.)

### 3.3 Influencia del tamaño de muestra sobre el error

Error observado frente a la desviación estándar teórica σ(N) ≈ 1.6421/√N:

| N | Error observado | σ teórico | error/σ |
|---|---|---|---|
| 1 000 | 0.046407 | 0.051930 | 0.89 |
| 10 000 | 0.010007 | 0.016422 | 0.61 |
| 100 000 | 0.008353 | 0.005193 | 1.61 |
| 1 000 000 | 0.000355 | 0.001642 | 0.22 |

Todos los errores observados quedaron por debajo de 2σ. Multiplicar N ×10
reduce σ ÷√10 ≈ ×0.316: de σ = 0.0519 (N = 10³) a σ = 0.00164 (N = 10⁶),
la precisión mejora unas 32 veces (3 órdenes de N → 1.5 órdenes de error).

### 3.4 Experimento complementario: el mismo N con distintas semillas

Para evidenciar el carácter aleatorio de cada ejecución se repitió el
experimento con N = 10 000 puntos y cinco semillas (`*_error_por_semilla.csv`;
idéntico en ambos lenguajes):

| Semilla | π estimado | Error absoluto |
|---|---|---|
| 12345 | 3.151600 | 0.010007 |
| 67890 | 3.134000 | 0.007593 |
| 246810 | 3.132400 | 0.009193 |
| 13579 | 3.121600 | 0.019993 |
| 999983 | 3.132400 | 0.009193 |

Con el mismo tamaño de muestra el error varió entre 0.0076 y 0.0200; además,
la mejor ejecución de N = 10 000 (error 0.007593, semilla 67890) **superó en
precisión a la de N = 100 000** (error 0.008353, semilla 12345): aumentar N
mejora el resultado *esperado*, no garantiza que cada ejecución concreta sea
más precisa (pregunta 7 del análisis).

---

## 4. Análisis de resultados (preguntas de la guía)

**1. ¿Cómo cambia la estimación de π cuando aumenta el número de puntos?**
La estimación oscila alrededor del valor real: 3.188000 (N = 1 000) →
3.151600 → 3.133240 → 3.141948 (N = 1 000 000). No se acerca de forma
monótona —con N = 100 000 el valor quedó por debajo de π—, pero la
*magnitud* del error sí tiende a reducirse (0.0464 → 0.0100 → 0.0084 →
0.0004), que es lo que garantiza el método. Con N = 1 000 000 los primeros
cuatro dígitos (3.141) ya coinciden con π.

**2. ¿Qué relación existe entre el tamaño de muestra y el error absoluto?**
El error esperado decrece como 1/√N: multiplicar N por 100 reduce el error
esperado solo por 10. Se observa en la Tabla 3.3: σ pasó de 0.051930 a
0.001642 cuando N creció de 10³ a 10⁶. La desviación de una ejecución
concreta respecto de σ (error/σ entre 0.22 y 1.61) confirma que el error
observado es una variable aleatoria que fluctúa alrededor de σ, no una
cantidad fija.

**3. ¿En cuál de los tamaños de muestra se obtuvo una mejor aproximación de π?**
En N = 1 000 000: π ≈ 3.141948, con error absoluto 0.000355, el menor de los
cuatro niveles y unas 130 veces menor que el de N = 1 000. Es el
comportamiento esperado: más puntos → menor varianza del estimador.

**4. ¿Qué lenguaje presentó menor tiempo de ejecución?**
Java en los cuatro niveles, y la brecha crece con N: 1.1× (N = 1 000), 5.4×,
7.1× y hasta **34.4× en N = 1 000 000** (0.0252 s frente a 0.8662 s). En
N = 1 000 la diferencia casi desaparece porque la JVM arranca «en frío»: las
primeras iteraciones se interpretan hasta que el compilador JIT genera código
nativo, coste que se amortiza en muestras grandes. Java compila a bytecode
optimizado por JIT; CPython interpreta cada instrucción.

**5. ¿Por qué los resultados obtenidos en Python y Java pueden ser diferentes?**
En esta práctica **no lo fueron** (ni π ni error), porque ambos implementan
el mismo LCG con aritmética entera de 64 bits exacta: `a·X + c < 7.15·10¹⁵`
no desborda `long` ni la precisión de los enteros de Python, y la división
`X/m` produce el mismo double en ambos. Si se usaran generadores distintos
(por ejemplo `random` de Python frente a `java.util.Random`), los resultados
sí diferirían: cada generador produce una secuencia diferente y, el error de
Monte Carlo siendo aleatorio, cada secuencia daría un π ligeramente distinto
(±σ). También podrían diferir los *tiempos*, que dependen del hardware y de
la implementación del lenguaje, no del algoritmo.

**6. ¿Qué importancia tiene la generación de números pseudoaleatorios en el
método Monte Carlo?**
Es el corazón del método: los puntos deben estar uniformemente distribuidos
en el cuadrado para que la fracción Nc/N reproduzca el cociente de áreas π/4.
Un generador sesgado o correlacionado introduce un error sistemático que **no
desaparece** al aumentar N: por muchos puntos que se generen, convergerían a
un valor incorrecto. Por eso en la unidad se validan los generadores (χ², KS
en el APE-06) antes de usarlos; el LCG de *Numerical Recipes* superó esas
pruebas y aquí produjo una estimación con error 0.0004 en N = 10⁶.

**7. ¿El aumento del número de puntos garantiza que cada ejecución obtenga un
resultado más cercano al valor real?**
No. Garantiza que el error *esperado* disminuya (en promedio, como 1/√N), pero
cada ejecución es un experimento aleatorio cuyo error fluctúa alrededor de σ.
El experimento por semillas lo demuestra: con N = 10 000, la semilla 67890
logró error 0.007593, menor que el 0.008353 de la corrida de N = 100 000. Una
muestra pequeña «con suerte» puede superar a una grande; lo que sí garantiza
el N grande es una *probabilidad mayor* de estar cerca de π y un error medio
menor (la corrida de N = 10⁶ estuvo a 0.22σ, muy por dentro de las demás).

**8. ¿Qué aplicaciones reales puede tener el método Monte Carlo en ingeniería,
economía, informática y ciencias?**
Cualquier sistema cuyo comportamiento dependa del azar o cuyo cálculo
determinista sea intratable: en ingeniería, evaluación de confiabilidad de
redes eléctricas, análisis de resistencia de materiales y colas en procesos
industriales (el M/M/1 del APE-03 es la contraparte determinista); en
economía, valoración de opciones financieras y gestión de riesgo (simular
miles de escenarios de mercado); en informática, estimación de tiempos de
respuesta en servidores, pruebas de estrés y *renderizado* por trazado
de rayos; en ciencias, física de partículas, dosimetría en radioterapia,
química computacional y epidemiología (modelos estocásticos SIR). En todos
ellos el patrón es el mismo de esta práctica: muestrear al azar, medir y
promediar, con un error que decrece como 1/√N.

---

## 5. Conclusiones

1. Se implementó el experimento Monte Carlo de la guía en los dos lenguajes
   con la misma arquitectura MVC (sin Vista, con tabla de resultados en
   consola) y el mismo LCG: ambas versiones produjeron **la misma secuencia
   (1 000 de 1 000 valores idénticos) y exactamente el mismo π estimado y
   error en los cuatro tamaños de muestra**, cumpliendo el resultado esperado
   de la guía.
2. La influencia del tamaño de muestra sobre el error quedó caracterizada por
   la teoría y por los datos: el error esperado σ(N) = 1.6421/√N decrece como
   1/√N, y el error observado bajó de 0.0464 (N = 1 000) a 0.000355
   (N = 1 000 000), quedando siempre dentro de ±2σ. Mejorar la precisión es
   caro: reducir el error a la mitad exige cuadruplicar el número de puntos.
3. Java fue más rápido en todos los niveles y hasta **34.4 veces** en
   N = 1 000 000 (0.0252 s frente a 0.8662 s), gracias a su compilación a
   bytecode optimizada por JIT; en N = 1 000 la ventaja casi desaparece por
   el arranque en frío de la JVM. Python, en cambio, requirió menos código y
   sin compilación.
4. El experimento con distintas semillas mostró el carácter aleatorio de cada
   corrida: con el mismo N = 10 000 el error varió entre 0.0076 y 0.0200, y
   una ejecución de N = 10 000 superó a una de N = 100 000. Aumentar N mejora
   el resultado esperado, no el de cada ejecución en particular.
5. La calidad del generador pseudoaleatorio es determinante: solo un
   generador uniforme y sin correlaciones garantiza que la fracción Nc/N
   converja a π/4 y no a un valor sesgado; los resultados obtenidos son una
   evidencia empírica adicional de que el LCG usado es adecuado.

---

## 6. Preguntas de control

**1. ¿Qué es Monte Carlo?**
Es una familia de métodos numéricos que aproximan un resultado mediante
**experimentos aleatorios repetidos**: en lugar de resolver el problema de
forma determinista, se generan muestras al azar de un modelo (aquí, puntos
uniformes en un cuadrado), se mide la magnitud de interés en cada muestra
(pertenecer al círculo) y se promedia (π ≈ 4·Nc/N). Su error decrece como
1/√N con el número de muestras y su campo de estudio conecta la probabilidad
con el cálculo numérico; toma su nombre del casino de Mónaco por el papel
central del azar. Es útil cuando el problema es demasiado complejo para una
solución analítica pero fácil de simular.

**2. ¿Por qué el error disminuye al aumentar las muestras?**
Por la ley de los grandes números: la frecuencia Nc/N converge hacia la
probabilidad real π/4 cuando N crece. Más formalmente, Nc sigue una
Binomial(N, π/4) y el estimador π̂ = 4·Nc/N tiene desviación estándar
σ = √(π(4−π)/N): cada vez que N se multiplica por 100, σ se divide por 10.
Es un decrecimiento lento (por eso Monte Carlo necesita muchas muestras), y
es *estadístico*: reduce el error aleatorio, no un posible sesgo del
generador.

**3. ¿Qué relación existe entre precisión y costo computacional?**
Están ligados por la raíz cuadrada: para ganar un dígito decimal de precisión
(reducir el error ×10) hay que multiplicar las muestras ×100, y por tanto el
tiempo de cómputo ×100. En esta práctica pasar de 10⁵ a 10⁶ puntos mejoró el
error de 0.0084 a 0.0004 a costa de multiplicar el tiempo por 10 (0.0839 s →
0.8662 s en Python). Por eso conviene acelerar cada muestra (aquí Java fue
34× más rápido) o usar técnicas de reducción de varianza, que logran la misma
precisión con menos muestras.

**4. ¿Qué diferencias se observaron entre Python y Java?**
Resultados: **ninguna** (misma secuencia, mismo π, mismo error), porque el
algoritmo y la aritmética son idénticos. Rendimiento: Java fue más rápido
(1.1× con N = 1 000 hasta 34.4× con N = 1 000 000) al compilar a bytecode
optimizado por JIT frente a la interpretación de CPython. Desarrollo: Python
necesitó menos código y sin paso de compilación; Java exige clases y
compilar, aunque los scripts `compilar.sh`/`ejecutar.sh` lo automatizan. En
resumen: mismo modelo matemático, distinto coste de ejecución y de desarrollo.

**5. ¿En qué otros problemas puede utilizarse Monte Carlo?**
En la estimación de integrales y áreas complejas (la base matemática de este
experimento generaliza a cualquier dimensión), en física de partículas y
radioterapia (transporte de radiación), en finanzas (valoración de opciones y
riesgo de carteras), en ingeniería de telecomunicaciones y colas (tiempos de
espera bajo demanda aleatoria), en informática (análisis de algoritmos
randomizados, trazado de rayos en gráficos), en estadística bayesiana (método
MCMC) y en toma de decisiones con incertidumbre (simulación de escenarios).
Siempre que se pueda definir un modelo estocástico cuya media responda a la
pregunta de interés, Monte Carlo es candidato.

---

## 7. Archivos generados (Resultados/)

| Archivo | Contenido |
|---|---|
| `python_tabla_pi.csv` / `java_tabla_pi.csv` | π, error y tiempo propios de cada lenguaje |
| `tabla_pi_python_java.csv` | Tabla cruzada mezclada con σ teórico |
| `python_secuencia.csv` / `java_secuencia.csv` | Primeros 1 000 valores U(i) de cada lenguaje |
| `comparacion_secuencias.csv` | Comparación valor a valor Python vs Java |
| `python_error_por_semilla.csv` / `java_error_por_semilla.csv` | Experimento con 5 semillas (N = 10 000) |
