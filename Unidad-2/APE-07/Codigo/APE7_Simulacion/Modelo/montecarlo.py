"""
Modelo · Estimación de π por el método de Monte Carlo (Práctica Nro. 07).

Método (guía): se generan puntos al azar dentro de un cuadrado y se
determina cuáles pertenecen al círculo inscrito de radio 1 centrado en el
origen. Como el cociente de áreas es

    área círculo / área cuadrado  =  π·r² / (2r)²  =  π / 4,

el estimador del método es:

    π ≈ 4 · (Nc / N)

donde Nc es el número de puntos dentro del círculo y N el número total de
puntos generados.

Los números pseudoaleatorios U ∈ [0, 1) del LCG se transforman a
coordenadas del cuadrado [-1, 1) con  x = 2·u1 − 1  y  y = 2·u2 − 1;
un punto pertenece al círculo si  x² + y² ≤ 1.  (Esta variante es
equivalente al cuarto de círculo del cuadrado unitario usado en el APE-06:
el cuadrado [-1,1]² con el círculo inscrito contiene 4 cuadrantes iguales.)
"""

import math

PI_REAL = math.pi


def estimar_pi(secuencia):
    """
    Estima π a partir de una secuencia de números pseudoaleatorios [0, 1).

    Cada punto del experimento consume un par (u1, u2), de modo que N
    puntos requieren 2·N números. Si la secuencia tiene una cantidad
    impar de valores, se descarta el último.
    """
    n_puntos = len(secuencia) // 2
    dentro = 0
    for i in range(n_puntos):
        x = 2.0 * secuencia[2 * i] - 1.0
        y = 2.0 * secuencia[2 * i + 1] - 1.0
        if x * x + y * y <= 1.0:
            dentro += 1
    if n_puntos == 0:
        return 0.0, 0.0
    pi_estimado = 4.0 * dentro / n_puntos
    error = abs(pi_estimado - PI_REAL)
    return pi_estimado, error


def error_teorico(n):
    """
    Desviación estándar teórica del estimador π̂ = 4·Nc / N.

    Si Nc ~ Binomial(N, π/4), entonces Var(π̂) = 16·p·(1−p)/N = π(4−π)/N,
    de modo que el error esperado del método decrece como

        σ(N) = sqrt(π(4−π)/N) ≈ 1.6421 / √N .
    """
    if n <= 0:
        return 0.0
    p = math.pi / 4.0
    return math.sqrt(16.0 * p * (1.0 - p) / n)
