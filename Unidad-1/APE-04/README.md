# Practica 04 - Simulacion del modelo SIR con Java y NetLogo

Proyecto de la asignatura **Simulacion**. Implementa un modelo epidemiologico **SIR** (Susceptibles, Infectados y Recuperados) en **Java**, aplicando la arquitectura **MVC**. Los resultados se exportan a **CSV** y se visualizan progresivamente mediante **NetLogo 7**.

> **Alcance del grupo:** Java desarrolla la simulacion y NetLogo importa y representa los datos; no se implementa Python en este proyecto.

## Objetivo

Representar la evolucion de una enfermedad en una poblacion cerrada, registrar los estados S, I y R durante 100 pasos y visualizar los resultados obtenidos en Java utilizando NetLogo.

## Parametros

| Parametro | Valor |
|---|---:|
| Poblacion total | 100 |
| Susceptibles iniciales | 95 |
| Infectados iniciales | 5 |
| Recuperados iniciales | 0 |
| Beta (probabilidad de contagio por contacto) | 0.30 |
| Gamma (probabilidad de recuperacion por paso) | 0.10 |
| Pasos de simulacion | 100 |
| Semilla aleatoria | `2026L` |

La semilla fija hace reproducible la secuencia pseudoaleatoria al ejecutar el mismo codigo con los mismos parametros. **100 pasos** significan una sola simulacion con 100 actualizaciones y **101 registros** (incluido el estado inicial en tiempo 0).

## Estructura del proyecto

```text
SIMULACION_04/
├── src/
│   ├── controlador/
│   │   └── ControladorSir.java
│   ├── modelo/
│   │   ├── EstadoSalud.java
│   │   ├── ExportadorCsv.java
│   │   ├── Individuo.java
│   │   ├── ModeloSir.java
│   │   └── RegistroSir.java
│   ├── vista/
│   │   └── VistaConsola.java
│   └── Main.java
├── modelo_sir_netlogo.nlogox
├── resultados_java.csv
└── README.md
```

## Arquitectura MVC

| Componente | Archivos | Responsabilidad |
|---|---|---|
| Modelo | `EstadoSalud`, `Individuo`, `ModeloSir`, `RegistroSir` | Representar a los individuos, aplicar las reglas SIR y registrar los estados. |
| Exportacion | `ExportadorCsv` | Guardar el historial de la simulacion en un archivo CSV. |
| Vista | `VistaConsola` | Mostrar los parametros y un resumen de los resultados en consola. |
| Controlador | `ControladorSir` | Coordinar el modelo, la vista y la exportacion. |
| Entrada | `Main` | Establecer los parametros e iniciar la ejecucion. |

## Funcionamiento de la simulacion

1. Crear 100 individuos: 95 susceptibles y 5 infectados.
2. Registrar los estados iniciales en el tiempo 0.
3. En cada paso, cada infectado selecciona al azar un contacto distinto. Si el contacto era susceptible, se infecta con probabilidad **beta = 0.30**.
4. Cada individuo infectado al inicio del paso se recupera con probabilidad **gamma = 0.10**.
5. Aplicar simultaneamente los nuevos estados para evitar que un individuo recien infectado transmita la enfermedad en el mismo paso.
6. Contar susceptibles, infectados y recuperados; guardar el registro.
7. Repetir hasta el tiempo 100 y exportar todos los registros al CSV.

Se conserva la poblacion total: **S + I + R = 100**. Es una simulacion estocastica, basada en individuos y de tiempo discreto; no resuelve directamente las ecuaciones diferenciales del modelo SIR.

## Requisitos

- JDK instalado (comandos `javac` y `java`).
- NetLogo 7 para abrir el archivo `.nlogox` y visualizar las graficas.

## Ejecutar Java

Desde la carpeta raiz `SIMULACION_04`:

```bash
javac -encoding UTF-8 -d bin -sourcepath src src/Main.java
java -cp bin Main
```

El programa presenta un resumen en consola y genera o actualiza `resultados_java.csv` en la carpeta desde la que se ejecuta Java.

**Formato del CSV:**

```csv
tiempo,susceptibles,infectados,recuperados
0,95,5,0
```

El archivo contiene una fila de encabezado y 101 filas de datos, desde el tiempo 0 hasta el 100.

## Visualizar resultados en NetLogo

1. Abrir `modelo_sir_netlogo.nlogox` con NetLogo 7.
2. En la interfaz, presionar **CARGAR JAVA** y seleccionar `resultados_java.csv`.
3. Presionar **SIMULAR** (boton con opcion *Forever*) para reproducir progresivamente los registros.
4. Usar **LIMPIAR** para borrar los datos y las curvas antes de iniciar otra reproduccion.

NetLogo muestra cuatro graficas:

- **Evolucion SIR**: las tres curvas en conjunto.
- **Susceptibles**: S(t).
- **Infectados**: I(t).
- **Recuperados**: R(t).

**Nota:** el boton SIMULAR de NetLogo **no vuelve a calcular contagios**. Solo recorre las filas del CSV generado por Java y las dibuja paso a paso.

## Resultados y consideraciones

- Java genera los datos y NetLogo los representa sin modificarlos.
- Cambiar la semilla puede producir trayectorias distintas, incluso manteniendo beta, gamma y la poblacion inicial.
- Mantener la semilla `2026L` y las mismas condiciones permite reproducir los resultados de esta implementacion.
- Un paso de simulacion es una unidad discreta de tiempo; la guia no fija una equivalencia obligatoria con dias.

## Referencia

Guia de Actividades Practico-Experimentales Nro. 004, asignatura Simulacion: *Analisis comparativo de herramientas de simulacion mediante un modelo comun*.
