"""
Controlador · Coordinación del patrón MVC para el APE-05.

Sigue, en orden, los 8 pasos de la sección 5.1 "Implementación
computacional" de la guía de la práctica:

    1. Seleccionar a, c, m y semilla.
    2. Generar 10 000 valores en Python.
    3. Implementar el mismo algoritmo en Java (carpeta APE5_Simulacion_Java).
    4. Comparar las primeras 20 observaciones.
    5. Graficar la secuencia.
    6. Analizar el período.
    7. Cambiar la semilla.
    8. Comparar resultados.

El Controlador pide datos al Modelo (LCG) y los entrega a las Vistas
(tabla y grafica); además lee/escribe los CSV de la carpeta Resultados/.
"""

import csv
from pathlib import Path

from Modelo.lcg import LCG
from Vista import grafica, tabla


class Controlador:
    """Coordina el Modelo (LCG) y las Vistas (tabla y grafica)."""

    #: Semilla alternativa usada en el paso 7 (cambio de semilla).
    SEMILLA_ALTERNATIVA = 987654321

    def __init__(self, semilla=LCG.SEMILLA_GUIA,
                 semilla_alternativa=SEMILLA_ALTERNATIVA, n=10000):
        self.semilla = semilla
        self.semilla_alternativa = semilla_alternativa
        self.n = n
        self.pares = None               # [(X(n), U(n))] semilla principal
        self.pares_alternativos = None  # [(X(n), U(n))] semilla del paso 7

        # Raíz de la práctica: este archivo está en
        # APE-05/Codigo/APE5_Simulacion/Controlador/ → sube 3 niveles.
        self.raiz = Path(__file__).resolve().parents[3]
        self.carpeta_img = self.raiz / "img"
        self.carpeta_resultados = self.raiz / "Resultados"
        self.carpeta_img.mkdir(parents=True, exist_ok=True)
        self.carpeta_resultados.mkdir(parents=True, exist_ok=True)

    # ------------------------------------------------------------------
    # Flujo principal
    # ------------------------------------------------------------------
    def ejecutar(self, solo_comparar=False):
        tabla.cabecera("Python")

        if solo_comparar:
            self._comparar_con_java(self.semilla)
            self._comparar_con_java(self.semilla_alternativa)
            tabla.cierre("Python")
            return

        lcg = LCG(semilla=self.semilla)

        self._paso_1_parametros(lcg)
        self._paso_2_generar(lcg)
        tabla.nota_paso_3_java()
        self._paso_4_primeras_20()
        self._paso_5_graficas()
        self._paso_6_periodo(lcg)
        self._paso_7_cambiar_semilla()
        self._paso_8_comparar()
        tabla.cierre("Python")

    # ------------------------------------------------------------------
    # Pasos de la guía
    # ------------------------------------------------------------------
    def _paso_1_parametros(self, lcg):
        tabla.paso(1, "Seleccionar parámetros (a, c, m y semilla)")
        tabla.parametros(lcg)

    def _paso_2_generar(self, lcg):
        tabla.paso(2, f"Generar {tabla.fmt(self.n)} valores en Python")
        self.pares = lcg.generar(self.n)
        ruta = self._ruta_resultado("python", self.semilla)
        self._guardar_csv(ruta, self.pares)
        tabla.archivo_guardado(self._relativa(ruta), len(self.pares))

    def _paso_4_primeras_20(self):
        tabla.paso(4, "Comparar las primeras 20 observaciones")
        tabla.primeras_observaciones(
            self.pares, 20, titulo=f"Python · semilla {self.semilla}")
        self._comparar_con_java(self.semilla)

    def _paso_5_graficas(self):
        tabla.paso(5, "Graficar la secuencia")
        us = [u for _, u in self.pares]
        rutas = [
            (self.carpeta_img / f"secuencia_python_semilla_{self.semilla}.png",
             lambda r: grafica.grafica_secuencia(us, self.semilla, r)),
            (self.carpeta_img / f"dispersion_python_semilla_{self.semilla}.png",
             lambda r: grafica.grafica_dispersion(us, self.semilla, r)),
            (self.carpeta_img / f"histograma_python_semilla_{self.semilla}.png",
             lambda r: grafica.grafica_histograma(us, self.semilla, r)),
        ]
        for ruta, dibujar in rutas:
            dibujar(ruta)
            tabla.archivo_guardado(self._relativa(ruta))

    def _paso_6_periodo(self, lcg):
        tabla.paso(6, "Analizar el período")

        # a) Análisis teórico: teorema de Hull-Dobell (1962)
        cumple, condiciones = lcg.verificar_hull_dobell()
        tabla.hull_dobell(condiciones, cumple, lcg.m)

        # b) Confirmación empírica con "saltos" de k pasos en O(log k)
        if cumple:
            x_final = lcg.saltar(lcg.m)
            x_medio = lcg.saltar(lcg.m // 2)
            auto_ok = (lcg.saltar(1000) == self.pares[999][0]
                       if self.n >= 1000 else None)
            tabla.confirmacion_periodo(lcg.m, x_final, x_medio,
                                       lcg.semilla_inicial, auto_ok)

        # c) Sin repeticiones dentro de los valores generados
        estados = [x for x, _ in self.pares]
        tabla.sin_repeticiones(len(set(estados)), len(estados))

        # d) Demostración con módulo pequeño (detección empírica de ciclos)
        mu_bueno, lam_bueno = LCG.detectar_ciclo(13, 7, 16, 1)
        mu_malo, lam_malo = LCG.detectar_ciclo(6, 0, 32, 1)
        tabla.demo_ciclos(mu_bueno, lam_bueno, mu_malo, lam_malo)

        # e) El conteo exhaustivo de los 2^32 estados se hace en Java
        tabla.nota_conteo_java(lcg.m)

    def _paso_7_cambiar_semilla(self):
        tabla.paso(7, f"Cambiar la semilla: {self.semilla} → {self.semilla_alternativa}")
        lcg2 = LCG(semilla=self.semilla_alternativa)
        self.pares_alternativos = lcg2.generar(self.n)
        ruta = self._ruta_resultado("python", self.semilla_alternativa)
        self._guardar_csv(ruta, self.pares_alternativos)
        tabla.archivo_guardado(self._relativa(ruta), len(self.pares_alternativos))
        tabla.primeras_observaciones(
            self.pares_alternativos, 10,
            titulo=f"Python · semilla {self.semilla_alternativa}")
        tabla.secuencias_distintas(self.pares[:10], self.semilla,
                                   self.pares_alternativos[:10],
                                   self.semilla_alternativa)

    def _paso_8_comparar(self):
        tabla.paso(8, "Comparar resultados")

        stats1 = LCG.estadisticas([u for _, u in self.pares])
        stats2 = LCG.estadisticas([u for _, u in self.pares_alternativos])
        tabla.comparacion_estadisticas(self.semilla, stats1,
                                       self.semilla_alternativa, stats2)

        ruta = (self.carpeta_img /
                f"comparacion_semillas_{self.semilla}_vs_{self.semilla_alternativa}.png")
        grafica.grafica_comparacion_semillas(
            [u for _, u in self.pares], self.semilla,
            [u for _, u in self.pares_alternativos], self.semilla_alternativa,
            ruta)
        tabla.archivo_guardado(self._relativa(ruta))

        coincidieron = self._comparar_con_java(self.semilla)
        self._comparar_con_java(self.semilla_alternativa)
        tabla.conclusiones(self.semilla, self.semilla_alternativa, self.n,
                           coincidieron)

    # ------------------------------------------------------------------
    # Comparación con Java (pasos 4 y 8)
    # ------------------------------------------------------------------
    def _comparar_con_java(self, semilla):
        """Compara los CSV de Python y Java para una semilla dada.

        Devuelve True si la comparación pudo realizarse y coincidieron
        todos los valores.
        """
        ruta_py = self._ruta_resultado("python", semilla)
        ruta_ja = self._ruta_resultado("java", semilla)
        if not ruta_ja.exists():
            tabla.aviso_falta_java(ruta_ja)
            return False
        if not ruta_py.exists():
            tabla.aviso_falta_archivo(ruta_py)
            return False

        pares_py = self._leer_csv(ruta_py)
        pares_ja = self._leer_csv(ruta_ja)
        cantidad = min(len(pares_py), len(pares_ja))

        filas = []
        coincidentes = 0
        for i in range(cantidad):
            (x_py, u_py), (x_ja, u_ja) = pares_py[i], pares_ja[i]
            coincide = (x_py == x_ja) and (u_py == u_ja)
            coincidentes += 1 if coincide else 0
            filas.append((i + 1, x_py, u_py, x_ja, u_ja, coincide))

        tabla.comparacion_primeras_20(filas[:20], semilla)
        tabla.resumen_comparacion(coincidentes, cantidad, semilla)

        ruta_comp = (self.carpeta_resultados /
                     f"comparacion_python_java_semilla_{semilla}.csv")
        self._guardar_csv_comparacion(ruta_comp, filas)
        tabla.archivo_guardado(self._relativa(ruta_comp), len(filas))

        return coincidentes == cantidad and cantidad > 0

    # ------------------------------------------------------------------
    # Utilidades de archivos
    # ------------------------------------------------------------------
    def _ruta_resultado(self, lenguaje, semilla):
        return self.carpeta_resultados / f"{lenguaje}_lcg_semilla_{semilla}.csv"

    def _relativa(self, ruta):
        """Ruta mostrada en consola, relativa a la raíz de la práctica."""
        try:
            return ruta.relative_to(self.raiz)
        except ValueError:
            return ruta

    @staticmethod
    def _guardar_csv(ruta, pares):
        with open(ruta, "w", newline="", encoding="utf-8") as f:
            escritor = csv.writer(f, lineterminator="\n")
            escritor.writerow(["n", "X(n)", "U(n)"])
            for n, (x, u) in enumerate(pares, start=1):
                escritor.writerow([n, x, f"{u:.12f}"])

    @staticmethod
    def _guardar_csv_comparacion(ruta, filas):
        with open(ruta, "w", newline="", encoding="utf-8") as f:
            escritor = csv.writer(f, lineterminator="\n")
            escritor.writerow(["n", "X(n) Python", "U(n) Python",
                               "X(n) Java", "U(n) Java", "coincide"])
            for n, x_py, u_py, x_ja, u_ja, coincide in filas:
                escritor.writerow([n, x_py, f"{u_py:.12f}", x_ja,
                                   f"{u_ja:.12f}", "SI" if coincide else "NO"])

    @staticmethod
    def _leer_csv(ruta):
        with open(ruta, newline="", encoding="utf-8") as f:
            lector = csv.reader(f)
            next(lector, None)  # salta el encabezado
            return [(int(fila[1]), float(fila[2]))
                    for fila in lector if fila]
