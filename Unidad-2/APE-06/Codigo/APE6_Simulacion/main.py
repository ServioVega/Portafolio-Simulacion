#!/usr/bin/env python3
"""
APE-06 · Validación estadística de números pseudoaleatorios (Chi-cuadrado y
Kolmogorov-Smirnov) y estimación de π por Monte Carlo con el LCG.
Asignatura: Simulación · Unidad 2 · Práctica Nro. 06

Punto de entrada del programa (patrón MVC):

    Modelo       → Modelo/lcg.py              : generador congruencial lineal
                   Modelo/pruebas.py          : Chi-cuadrado y Kolmogorov-Smirnov
                   Modelo/montecarlo.py       : estimación de π
    Vista        → Vista/tabla.py             : tablas y mensajes en consola
                   Vista/grafica.py           : gráficas (matplotlib)
    Controlador  → Controlador/controlador.py : coordinación de los algoritmos

Uso:
    python3 main.py                      # ejecución completa (guía)
    python3 main.py --semilla 67890      # cambia la semilla
    python3 main.py --n 20000            # cambia la cantidad de valores
    python3 main.py --sin-graficas       # no genera imágenes
"""

import argparse

from Controlador.controlador import Controlador
from Modelo.lcg import SEMILLA_GUIA


def parsear_argumentos():
    parser = argparse.ArgumentParser(
        description="APE-06 · Validación Chi-cuadrado y KS del LCG (Python)")
    parser.add_argument("--semilla", type=int, default=SEMILLA_GUIA,
                        help="semilla inicial X(0) (por defecto 12345)")
    parser.add_argument("--n", type=int, default=10_000,
                        help="cantidad de números a validar (por defecto 10000)")
    parser.add_argument("--sin-graficas", action="store_true",
                        help="no genera las imágenes PNG")
    return parser.parse_args()


def main():
    args = parsear_argumentos()
    controlador = Controlador(semilla=args.semilla, n=args.n)
    controlador.ejecutar(sin_graficas=args.sin_graficas)


if __name__ == "__main__":
    main()
