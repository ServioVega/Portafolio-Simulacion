"""
Controlador · Coordinación de la Práctica Nro. 06 (patrón MVC).

Pasos que ejecuta (siguiendo la guía):

  1. Genera 10 000 números pseudoaleatorios con el LCG (Algoritmo 1).
  2. Aplica la prueba Chi-cuadrado (Algoritmo 2).
  3. Aplica la prueba Kolmogorov-Smirnov + gráficas (Algoritmo 3).
  4. Compara la secuencia Python contra la secuencia Java (mismos parámetros).
  5. Construye la tabla de Resultados Esperados: π por Monte Carlo,
     error y tiempos, para N = 1 000 … 1 000 000, en Python y Java.
  6. Exporta los CSV a la carpeta Resultados/.
"""

import csv
import os
import time

from Modelo import montecarlo, pruebas
from Modelo.lcg import A_GUIA, C_GUIA, M_GUIA, SEMILLA_GUIA, LCG
from Vista import grafica, tabla

RAIZ_APE = os.path.dirname(os.path.abspath(__file__))   # .../APE-06/Codigo/APE6_Simulacion/Controlador
CARPETA_RESULTADOS = os.path.join(RAIZ_APE, "..", "..", "..", "Resultados")
CARPETA_IMG = os.path.join(RAIZ_APE, "..", "..", "..", "img")

N_GUIA = 10_000                     # cantidad de números de la guía
NIVEL_N = [1_000, 10_000, 100_000, 1_000_000]
PRIMEROS = 1_000                    # valores usados en la comparación Python/Java


class Controlador:
    def __init__(self, semilla=SEMILLA_GUIA, n=N_GUIA):
        self.semilla = semilla
        self.n = n
        os.makedirs(CARPETA_RESULTADOS, exist_ok=True)

    # ------------------------------------------------------------------
    def ejecutar(self, sin_graficas=False):
        lcg = LCG(semilla=self.semilla)

        # ---------- Algoritmo 1: generación ----------
        tabla.imprimir_titulo("APE-06 · Práctica 06 · Validación Chi-cuadrado y "
                              "Kolmogorov-Smirnov (PYTHON)")
        print(f"Parámetros LCG: a={lcg.a}, c={lcg.c}, m={lcg.m}, X(0)={lcg.semilla_inicial}")
        t_gen = time.perf_counter()
        secuencia = lcg.generar_secuencia(self.n)
        t_gen = time.perf_counter() - t_gen
        tabla.imprimir_muestra(secuencia)
        print(f"  Tiempo de generación de {self.n:,} números: {t_gen:.4f} s")

        # ---------- Algoritmo 2: Chi-cuadrado ----------
        res_chi = pruebas.chi_cuadrado(secuencia)
        tabla.imprimir_chi(res_chi)

        # ---------- Algoritmo 3: Kolmogorov-Smirnov ----------
        res_ks = pruebas.kolmogorov_smirnov(secuencia)
        tabla.imprimir_ks(res_ks)

        self._guardar_pruebas(res_chi, res_ks)

        # ---------- Gráficas ----------
        if not sin_graficas:
            try:
                r1 = grafica.graficar_histograma(res_chi)
                r2 = grafica.graficar_ks(secuencia, res_ks)
                print(f"\nGráficas guardadas en:\n  {os.path.normpath(r1)}\n  {os.path.normpath(r2)}")
            except Exception as e:                     # servidor sin entorno gráfico, etc.
                print(f"\n(Aviso) No se pudieron generar las gráficas: {e}")

        # ---------- Comparación Python vs Java ----------
        self._comparar_con_java(secuencia)

        # ---------- Tabla de Resultados Esperados (Monte Carlo π) ----------
        filas = self._tabla_pi()
        tabla.imprimir_tabla_pi(filas)
        self._guardar_tabla_pi(filas)
        if not sin_graficas:
            try:
                r3 = grafica.graficar_pi(filas)
                print(f"\n  {os.path.normpath(r3)}")
            except Exception as e:
                print(f"\n(Aviso) No se pudo generar la gráfica de convergencia: {e}")

        tabla.imprimir_titulo("Fin de la ejecución (Python)")

    # ------------------------------------------------------------------
    def _comparar_con_java(self, secuencia_py):
        """Compara los primeros valores con los exportados por el programa Java."""
        ruta = os.path.join(CARPETA_RESULTADOS, "java_secuencia.csv")
        if not os.path.exists(ruta):
            print("\n(Aviso) Ejecuta primero la versión Java para comparar secuencias:")
            print("        cd Codigo/APE6_Simulacion_Java && ./compilar.sh && ./ejecutar.sh")
            return
        with open(ruta, newline="", encoding="utf-8") as f:
            vals_java = [float(row[1]) for row in csv.reader(f) if row[0] != "i"]
        n_cmp = min(PRIMEROS, len(vals_java), len(secuencia_py))
        identicas = all(
            abs(secuencia_py[i] - vals_java[i]) <= 1e-12 for i in range(n_cmp)
        )
        tabla.imprimir_comparacion_secuencias(
            secuencia_py[: min(n_cmp, 10)], vals_java[: min(n_cmp, 10)], identicas)

        ruta_sal = os.path.join(CARPETA_RESULTADOS, "comparacion_secuencias.csv")
        with open(ruta_sal, "w", newline="", encoding="utf-8") as f:
            w = csv.writer(f)
            w.writerow(["i", "python_u_i", "java_u_i", "iguales"])
            for i in range(n_cmp):
                ig = abs(secuencia_py[i] - vals_java[i]) <= 1e-12
                w.writerow([i + 1, f"{secuencia_py[i]:.10f}", f"{vals_java[i]:.10f}", ig])
        print(f"  Comparación completa guardada en: {os.path.normpath(ruta_sal)}")

    # ------------------------------------------------------------------
    def _tabla_pi(self):
        """Monte Carlo π en Python para cada N (tiempo = generar + estimar)."""
        filas = []
        print()
        for n in NIVEL_N:
            lcg = LCG(semilla=self.semilla)
            t0 = time.perf_counter()
            secuencia = lcg.generar_secuencia(n)
            pi_est, error = montecarlo.estimar_pi(secuencia)
            t_py = time.perf_counter() - t0
            filas.append({"n": n, "pi_py": pi_est, "err_py": error, "t_py": t_py,
                          "pi_java": None, "err_java": None, "t_java": None})
            print(f"  Python · N = {n:>9,} → π ≈ {pi_est:.6f}  error = {error:.6f}  t = {t_py:.4f} s")
        self._guardar_tabla_pi_python(filas)
        return self._mezclar_java(filas)

    def _guardar_tabla_pi_python(self, filas):
        """CSV intermedio que lee la versión Java para mezclar la tabla."""
        ruta = os.path.join(CARPETA_RESULTADOS, "python_tabla_pi.csv")
        with open(ruta, "w", newline="", encoding="utf-8") as f:
            w = csv.writer(f)
            w.writerow(["n", "pi_estimado", "error", "tiempo_seg"])
            for x in filas:
                w.writerow([x["n"], f"{x['pi_py']:.6f}", f"{x['err_py']:.6f}",
                            f"{x['t_py']:.4f}"])

    def _mezclar_java(self, filas):
        """Añade a la tabla los resultados Java si ya existen en Resultados/."""
        ruta = os.path.join(CARPETA_RESULTADOS, "java_tabla_pi.csv")
        if not os.path.exists(ruta):
            return filas
        with open(ruta, newline="", encoding="utf-8") as f:
            datos = {int(row["n"]): row for row in csv.DictReader(f)}
        for fila in filas:
            d = datos.get(fila["n"])
            if d:
                fila["pi_java"] = float(d["pi_estimado"])
                fila["err_java"] = float(d["error"])
                fila["t_java"] = float(d["tiempo_seg"])
        return filas

    # ------------------------------------------------------------------
    def _guardar_pruebas(self, res_chi, res_ks):
        ruta = os.path.join(CARPETA_RESULTADOS, "python_resultados_pruebas.csv")
        with open(ruta, "w", newline="", encoding="utf-8") as f:
            w = csv.writer(f)
            w.writerow(["prueba", "estadistico", "parametro", "valor_p", "decision"])
            w.writerow(["Chi-cuadrado", f"{res_chi['estadistico']:.6f}",
                        f"gl={res_chi['gl']}", f"{res_chi['valor_p']:.6f}",
                        res_chi["decision"]])
            w.writerow(["Kolmogorov-Smirnov", f"{res_ks['estadistico']:.6f}",
                        f"n={res_ks['n']}", f"{res_ks['valor_p']:.6f}",
                        res_ks["decision"]])
        # frecuencias del chi-cuadrado
        ruta = os.path.join(CARPETA_RESULTADOS, "python_frecuencias_chi.csv")
        with open(ruta, "w", newline="", encoding="utf-8") as f:
            w = csv.writer(f)
            w.writerow(["intervalo", "lim_inf", "lim_sup", "observada", "esperada"])
            for i, o in enumerate(res_chi["observadas"]):
                w.writerow([i + 1, i / res_chi["k"], (i + 1) / res_chi["k"],
                            o, f"{res_chi['esperada']:.1f}"])
        # primeros 1000 valores para comparar con Java
        lcg = LCG(semilla=self.semilla)
        ruta = os.path.join(CARPETA_RESULTADOS, "python_secuencia.csv")
        with open(ruta, "w", newline="", encoding="utf-8") as f:
            w = csv.writer(f)
            w.writerow(["i", "u_i"])
            for i in range(PRIMEROS):
                w.writerow([i + 1, f"{lcg.siguiente_u():.15f}"])
        print(f"\nResultados Python guardados en: {os.path.normpath(CARPETA_RESULTADOS)}")

    def _guardar_tabla_pi(self, filas):
        ruta = os.path.join(CARPETA_RESULTADOS, "tabla_pi_python_java.csv")
        with open(ruta, "w", newline="", encoding="utf-8") as f:
            w = csv.writer(f)
            w.writerow(["n", "pi_python", "error_python", "tiempo_python_s",
                        "pi_java", "error_java", "tiempo_java_s"])
            for x in filas:
                w.writerow([x["n"], f"{x['pi_py']:.6f}", f"{x['err_py']:.6f}",
                            f"{x['t_py']:.4f}",
                            "" if x["pi_java"] is None else f"{x['pi_java']:.6f}",
                            "" if x["err_java"] is None else f"{x['err_java']:.6f}",
                            "" if x["t_java"] is None else f"{x['t_java']:.4f}"])
