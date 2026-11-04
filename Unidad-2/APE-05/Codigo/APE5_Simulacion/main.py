#!/usr/bin/env python3
"""
APE-05 · Implementación y análisis del Generador Congruencial Lineal (LCG)
Asignatura: Simulación · Unidad 2 · Práctica Nro. 05

Punto de entrada del programa (patrón MVC):

    Modelo       → Modelo/lcg.py              : lógica matemática del LCG
    Vista        → Vista/tabla.py             : tablas y mensajes en consola
                   Vista/grafica.py           : gráficas (matplotlib)
    Controlador  → Controlador/controlador.py : coordinación de los pasos 1 a 8

Uso:
    python3 main.py                            # ejecución completa (guía)
    python3 main.py --semilla 67890            # cambia la semilla principal
    python3 main.py --n 5000                   # cambia la cantidad de valores
    python3 main.py --solo-comparar            # solo compara los CSV Python/Java
"""

import argparse

from Controlador.controlador import Controlador


def parsear_argumentos():
    parser = argparse.ArgumentParser(
        description="APE-05 · Generador Congruencial Lineal (LCG) en Python"
    )
    parser.add_argument(
        "--semilla", type=int, default=12345,
        help="semilla inicial X(0) (por defecto 12345, la de la guía)")
    parser.add_argument(
        "--semilla-alternativa", type=int, default=987654321,
        help="semilla del paso 7 'cambiar la semilla' (por defecto 987654321)")
    parser.add_argument(
        "--n", type=int, default=10000,
        help="cantidad de valores a generar (por defecto 10000)")
    parser.add_argument(
        "--solo-comparar", action="store_true",
        help="solo ejecuta la comparación Python vs Java (paso 8)")
    return parser.parse_args()


def main():
    args = parsear_argumentos()
    controlador = Controlador(
        semilla=args.semilla,
        semilla_alternativa=args.semilla_alternativa,
        n=args.n,
    )
    controlador.ejecutar(solo_comparar=args.solo_comparar)


if __name__ == "__main__":
    main()
