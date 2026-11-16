"""
Controlador · Coordinación de la Práctica Nro. 07 (patrón MVC, sin Vista).

A diferencia de los APE anteriores, la guía de esta práctica no exige
visualización gráfica: la «pequeña tabla» para observar los datos se
imprime en consola desde este controlador y los resultados completos se
exportan en CSV a la carpeta Resultados/.

Pasos que ejecuta (siguiendo la guía):

  1. Ejecuta el experimento Monte Carlo para estimar π con el LCG,
     para N = 1 000, 10 000, 100 000 y 1 000 000 de puntos.
  2. Registra por nivel: π estimado, error absoluto y tiempo de ejecución
     (generación de la secuencia + conteo de puntos, sin escritura de CSV).
  3. Exporta los primeros 1 000 valores U(i) para comparar la secuencia
     con la versión Java (mismos parámetros → misma secuencia).
  4. Repite el experimento con varias semillas (N = 10 000) para analizar
     cómo varía el error de una ejecución a otra.
  5. Si detecta los CSV de la versión Java, mezcla la tabla cruzada
     Python vs Java y compara las secuencias valor a valor.
  6. Exporta todos los CSV a la carpeta Resultados/.
"""

import csv
import os
import time

from Modelo import montecarlo
from Modelo.lcg import A_GUIA, C_GUIA, M_GUIA, SEMILLA_GUIA, LCG

RAIZ_APE = os.path.dirname(os.path.abspath(__file__))   # .../APE-07/Codigo/APE7_Simulacion/Controlador
CARPETA_RESULTADOS = os.path.join(RAIZ_APE, "..", "..", "..", "Resultados")

NIVEL_N = [1_000, 10_000, 100_000, 1_000_000]   # tamaños de muestra de la guía (puntos)
SEMILLAS_EXTRA = [12345, 67890, 246810, 13579, 999983]   # experimento complementario
N_SEMILLAS = 10_000                    # puntos por semilla en el experimento complementario
PRIMEROS = 1_000                       # valores usados en la comparación Python/Java


class Controlador:
    def __init__(self, semilla=SEMILLA_GUIA):
        self.semilla = semilla
        os.makedirs(CARPETA_RESULTADOS, exist_ok=True)

    # ------------------------------------------------------------------
    def ejecutar(self):
        tabla = self._experimentos()
        self._guardar_tabla_python(tabla)
        self._guardar_secuencia()
        self._comparar_con_java()
        self._mezclar_java(tabla)
        self._experimento_semillas()

        self._imprimir_tabla(tabla)
        self._guardar_tabla_mezclada(tabla)
        self._imprimir_cierre()

    # ------------------------------------------------------------------
    # Experimento Monte Carlo (guía: N = 1 000 … 1 000 000 de puntos)
    # ------------------------------------------------------------------
    def _experimentos(self):
        """Corre el experimento para cada N y mide π, error y tiempo."""
        print("=" * 70)
        print(f"APE-07 · Práctica 07 · Estimación de π por Monte Carlo (PYTHON)")
        print("=" * 70)
        print(f"Parámetros LCG: a={A_GUIA}, c={C_GUIA}, m={M_GUIA}, X(0)={self.semilla}")
        print(f"Método: π ≈ 4 · Nc / N (cuadrado [-1,1]² con círculo inscrito)")
        print(f"N puntos → 2N números pseudoaleatorios (cada punto usa un par u1, u2)")

        filas = []
        for n in NIVEL_N:
            lcg = LCG(semilla=self.semilla)
            t0 = time.perf_counter()
            secuencia = lcg.generar_secuencia(2 * n)          # 2N números → N puntos
            pi_est, error = montecarlo.estimar_pi(secuencia)
            t_py = time.perf_counter() - t0
            filas.append({"n": n, "pi_py": pi_est, "err_py": error, "t_py": t_py,
                          "pi_java": None, "err_java": None, "t_java": None})
            print(f"  Python · N = {n:>9,} puntos → π ≈ {pi_est:.6f}  "
                  f"error = {error:.6f}  t = {t_py:.4f} s")
        return filas

    def _imprimir_tabla(self, filas):
        """Pequeña tabla en consola con los datos de ambos programas."""
        print("\nTabla de Resultados Esperados: Monte Carlo π (Python vs Java)")
        cab = (f"  {'N':>10} | {'π estimado':>12} | {'Error':>10} | "
               f"{'t Python (s)':>13} | {'t Java (s)':>11}")
        print(cab)
        print("  " + "-" * (len(cab) - 2))
        for f in filas:
            if f["pi_java"] is None:
                print(f"  {f['n']:>10,} | {f['pi_py']:>12.6f} | {f['err_py']:>10.6f} | "
                      f"{f['t_py']:>13.4f} | {'— (ejecutar Java)':>11}")
            else:
                print(f"  {f['n']:>10,} | {f['pi_py']:>12.6f} | {f['err_py']:>10.6f} | "
                      f"{f['t_py']:>13.4f} | {f['t_java']:>11.4f}")
        # referencia teórica del error: σ(N) = sqrt(π(4−π)/N) ≈ 1.6421/√N
        print("\n  Referencia teórica del error: σ(N) = √(π(4−π)/N) ≈ 1.6421/√N")
        for f in filas:
            print(f"    N = {f['n']:>10,} → σ teórico = {montecarlo.error_teorico(f['n']):.6f}")

    def _imprimir_cierre(self):
        print("\nResultados Python guardados en: "
              f"{os.path.normpath(CARPETA_RESULTADOS)}")
        print("=" * 70)
        print("Fin de la ejecución (Python)")
        print("=" * 70)

    # ------------------------------------------------------------------
    # Exportación de CSV
    # ------------------------------------------------------------------
    def _guardar_tabla_python(self, filas):
        """CSV propio de Python; lo lee la versión Java para mezclar."""
        ruta = os.path.join(CARPETA_RESULTADOS, "python_tabla_pi.csv")
        with open(ruta, "w", newline="", encoding="utf-8") as f:
            w = csv.writer(f)
            w.writerow(["n", "pi_estimado", "error", "tiempo_seg"])
            for x in filas:
                w.writerow([x["n"], f"{x['pi_py']:.6f}", f"{x['err_py']:.6f}",
                            f"{x['t_py']:.4f}"])

    def _guardar_secuencia(self):
        """Primeros 1 000 valores U(i) para la comparación con Java."""
        lcg = LCG(semilla=self.semilla)
        ruta = os.path.join(CARPETA_RESULTADOS, "python_secuencia.csv")
        with open(ruta, "w", newline="", encoding="utf-8") as f:
            w = csv.writer(f)
            w.writerow(["i", "u_i"])
            for i in range(PRIMEROS):
                w.writerow([i + 1, f"{lcg.siguiente_u():.15f}"])

    def _guardar_tabla_mezclada(self, filas):
        ruta = os.path.join(CARPETA_RESULTADOS, "tabla_pi_python_java.csv")
        with open(ruta, "w", newline="", encoding="utf-8") as f:
            w = csv.writer(f)
            w.writerow(["n", "pi_python", "error_python", "tiempo_python_s",
                        "pi_java", "error_java", "tiempo_java_s", "error_teorico"])
            for x in filas:
                w.writerow([x["n"], f"{x['pi_py']:.6f}", f"{x['err_py']:.6f}",
                            f"{x['t_py']:.4f}",
                            "" if x["pi_java"] is None else f"{x['pi_java']:.6f}",
                            "" if x["err_java"] is None else f"{x['err_java']:.6f}",
                            "" if x["t_java"] is None else f"{x['t_java']:.4f}",
                            f"{montecarlo.error_teorico(x['n']):.6f}"])

    # ------------------------------------------------------------------
    # Comparación e integración con la versión Java
    # ------------------------------------------------------------------
    def _comparar_con_java(self):
        """Compara los primeros valores con los exportados por el programa Java."""
        ruta = os.path.join(CARPETA_RESULTADOS, "java_secuencia.csv")
        if not os.path.exists(ruta):
            print("\n(Aviso) Ejecuta primero la versión Java para comparar secuencias:")
            print("        cd Codigo/APE7_Simulacion_Java && ./compilar.sh && ./ejecutar.sh")
            return
        with open(ruta, newline="", encoding="utf-8") as f:
            vals_java = [float(row[1]) for row in csv.reader(f) if row[0] != "i"]
        lcg = LCG(semilla=self.semilla)
        vals_py = [lcg.siguiente_u() for _ in range(min(PRIMEROS, len(vals_java)))]
        n_cmp = min(len(vals_py), len(vals_java))
        identicas = all(
            abs(vals_py[i] - vals_java[i]) <= 1e-12 for i in range(n_cmp)
        )

        print("\nComparación Python vs Java (misma semilla y parámetros)")
        print(f"  {'i':>4} | {'Python U(i)':>18} | {'Java U(i)':>18}")
        for i in range(min(n_cmp, 10)):
            a, b = vals_py[i], vals_java[i]
            marca = "" if a == b else "  ← difieren"
            print(f"  {i + 1:>4} | {a:>18.10f} | {b:>18.10f}{marca}")
        print(f"\n  Secuencias idénticas: {'SÍ ✔' if identicas else 'NO ✘'} "
              f"({sum(1 for i in range(n_cmp) if abs(vals_py[i] - vals_java[i]) <= 1e-12)} "
              f"de {n_cmp} valores iguales)")

        ruta_sal = os.path.join(CARPETA_RESULTADOS, "comparacion_secuencias.csv")
        with open(ruta_sal, "w", newline="", encoding="utf-8") as f:
            w = csv.writer(f)
            w.writerow(["i", "python_u_i", "java_u_i", "iguales"])
            for i in range(n_cmp):
                ig = abs(vals_py[i] - vals_java[i]) <= 1e-12
                w.writerow([i + 1, f"{vals_py[i]:.10f}", f"{vals_java[i]:.10f}", ig])
        print(f"  Comparación completa guardada en: {os.path.normpath(ruta_sal)}")

    def _mezclar_java(self, filas):
        """Añade a la tabla los resultados Java si ya existen en Resultados/."""
        ruta = os.path.join(CARPETA_RESULTADOS, "java_tabla_pi.csv")
        if not os.path.exists(ruta):
            print("\n(Aviso) La tabla cruzada se completa al ejecutar también la versión Java.")
            return
        with open(ruta, newline="", encoding="utf-8") as f:
            datos = {int(row["n"]): row for row in csv.DictReader(f)}
        for fila in filas:
            d = datos.get(fila["n"])
            if d:
                fila["pi_java"] = float(d["pi_estimado"])
                fila["err_java"] = float(d["error"])
                fila["t_java"] = float(d["tiempo_seg"])

    # ------------------------------------------------------------------
    # Experimento complementario: variación del error según la semilla
    # ------------------------------------------------------------------
    def _experimento_semillas(self):
        """Mismo N (10 000 puntos) con distintas semillas: el error de una
        ejecución concreta varía; solo disminuye «en promedio» al crecer N."""
        print("\nExperimento complementario: N = 10 000 puntos con distintas semillas")
        filas = []
        for semilla in SEMILLAS_EXTRA:
            lcg = LCG(semilla=semilla)
            secuencia = lcg.generar_secuencia(2 * N_SEMILLAS)
            pi_est, error = montecarlo.estimar_pi(secuencia)
            filas.append({"semilla": semilla, "pi": pi_est, "error": error})
            print(f"  Semilla = {semilla:>9,} → π ≈ {pi_est:.6f}  error = {error:.6f}")

        ruta = os.path.join(CARPETA_RESULTADOS, "python_error_por_semilla.csv")
        with open(ruta, "w", newline="", encoding="utf-8") as f:
            w = csv.writer(f)
            w.writerow(["semilla", "n_puntos", "pi_estimado", "error", "error_teorico"])
            for x in filas:
                w.writerow([x["semilla"], N_SEMILLAS, f"{x['pi']:.6f}",
                            f"{x['error']:.6f}",
                            f"{montecarlo.error_teorico(N_SEMILLAS):.6f}"])
        print(f"  Guardado en: {os.path.normpath(ruta)}")
