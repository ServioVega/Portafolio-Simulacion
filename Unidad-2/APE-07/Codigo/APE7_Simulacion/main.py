#!/usr/bin/env python3
"""
APE-07 · Estimación de resultados mediante el método de Monte Carlo
(estimación de π con puntos dentro de un círculo inscrito en un cuadrado).
Asignatura: Simulación · Unidad 2 · Práctica Nro. 07

Punto de entrada del programa (patrón MVC, sin Vista):

    Modelo       → Modelo/lcg.py              : generador congruencial lineal
                   Modelo/montecarlo.py       : estimación de π y error teórico
    Controlador  → Controlador/controlador.py : coordinación, tiempos y CSV
                   (la pequeña tabla de resultados se imprime en consola)

Uso:
    python3 main.py                      # ejecución completa (guía)
    python3 main.py --semilla 67890      # cambia la semilla del experimento
"""

import argparse

from Controlador.controlador import Controlador
from Modelo.lcg import SEMILLA_GUIA


def parsear_argumentos():
    parser = argparse.ArgumentParser(
        description="APE-07 · Estimación de π por Monte Carlo (Python)")
    parser.add_argument("--semilla", type=int, default=SEMILLA_GUIA,
                        help="semilla inicial X(0) (por defecto 12345)")
    return parser.parse_args()


def main():
    args = parsear_argumentos()
    controlador = Controlador(semilla=args.semilla)
    controlador.ejecutar()


if __name__ == "__main__":
    main()
