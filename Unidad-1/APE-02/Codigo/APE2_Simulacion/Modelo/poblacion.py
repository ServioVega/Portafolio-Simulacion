# -*- coding: utf-8 -*-
"""Modelos de crecimiento poblacional: deterministico, discreto,
estocastico y continuo (Euler)."""

import numpy as np

# Arreglo de entrada: poblacion observada del sistema
DATOS = [1000, 1100, 1250, 1400, 1600, 1850, 2100]


def estimar_r(datos):
    """Estima la tasa de crecimiento r como el promedio de las tasas
    observadas entre periodos consecutivos: (P[i+1] - P[i]) / P[i]."""
    tasas = [(datos[i + 1] - datos[i]) / datos[i] for i in range(len(datos) - 1)]
    return sum(tasas) / len(tasas)


def deterministico(P0, r, periodos):
    """Modelo deterministico (exponencial exacto): P(t) = P0 * e^(r*t).
    Sin aleatoriedad: misma entrada, misma salida siempre."""
    t = np.arange(0, periodos + 1)
    P = P0 * np.exp(r * t)
    return t, P


def discreto(P0, r, periodos):
    """Modelo discreto: la poblacion cambia en saltos por periodo.
    P[k+1] = P[k] + r * P[k]."""
    t = np.arange(0, periodos + 1)
    P = [P0]
    for _ in range(periodos):
        actual = P[-1]
        P.append(actual + r * actual)
    return t, np.array(P)


def estocastico(P0, r, sigma, periodos):
    """Modelo estocastico: agrega ruido aleatorio N(0, sigma) en cada paso.
    P[k+1] = P[k] + r*P[k] + ruido. La poblacion nunca es negativa."""
    t = np.arange(0, periodos + 1)
    P = [P0]
    for _ in range(periodos):
        ruido = np.random.normal(0, sigma)
        nueva = P[-1] + r * P[-1] + ruido
        if nueva < 0:
            nueva = 0
        P.append(nueva)
    return t, np.array(P)


def continuo_euler(P0, r, tiempo, dt):
    """Modelo continuo aproximado por Euler:
    P[i+1] = P[i] + dt * r * P[i], con paso dt."""
    t = np.arange(0, tiempo + dt / 2, dt)
    P = [P0]
    for _ in range(len(t) - 1):
        actual = P[-1]
        P.append(actual + dt * r * actual)
    return t, np.array(P)


def ejecutar_todos(datos=DATOS, sigma=50.0, dt=0.1):
    """Ejecuta los cuatro modelos y devuelve los resultados."""
    P0 = datos[0]
    r = estimar_r(datos)
    periodos = len(datos) - 1
    paso = int(round(1 / dt))

    t, det = deterministico(P0, r, periodos)
    _, dis = discreto(P0, r, periodos)
    _, est = estocastico(P0, r, sigma, periodos)
    t_cont, cont = continuo_euler(P0, r, periodos, dt)

    return {
        "datos": list(datos), "P0": P0, "r": r, "sigma": sigma, "dt": dt,
        "t": t, "det": det, "dis": dis, "est": est,
        "t_cont": t_cont, "cont": cont,
        "cont_tab": cont[::paso],
    }

