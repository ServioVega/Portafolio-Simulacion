# -*- coding: utf-8 -*-
"""Vista grafica: comparacion de los cuatro modelos."""

import matplotlib.pyplot as plt


def graficar(res, titulo="Comparacion de modelos de crecimiento poblacional"):
    plt.figure(figsize=(10, 5))

    plt.plot(res["t"], res["datos"], "ko", label="Datos reales")
    plt.plot(res["t"], res["det"], label="Deterministico")
    plt.plot(res["t"], res["dis"], "--", label="Discreto")
    plt.plot(res["t"], res["est"], label="Estocastico")
    plt.plot(res["t_cont"], res["cont"], label="Continuo (Euler)")

    plt.xlabel("Tiempo")
    plt.ylabel("Poblacion")
    plt.title(titulo)
    plt.grid(True)
    plt.legend()
    plt.tight_layout()
    plt.show()

