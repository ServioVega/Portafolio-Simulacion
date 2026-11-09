"""
Modelo · Estimación de π por Monte Carlo usando el LCG (Práctica Nro. 06).

Método: se generan pares (u1, u2) uniformes en [0, 1)². Un punto cae
dentro del cuarto de círculo de radio 1 si  u1² + u2² ≤ 1.  Entonces:

    π ≈ 4 · (puntos dentro) / (puntos totales)

Error absoluto = |π estimado − π real|.
"""

import math

PI_REAL = math.pi


def estimar_pi(secuencia):
    """
    Estima π a partir de una secuencia de números pseudoaleatorios [0, 1).

    Usa los números en pares: (u[0], u[1]), (u[2], u[3]), ...
    Si la secuencia tiene una cantidad impar, se descarta el último valor.
    """
    n_pares = len(secuencia) // 2
    dentro = 0
    for i in range(n_pares):
        u1 = secuencia[2 * i]
        u2 = secuencia[2 * i + 1]
        if u1 * u1 + u2 * u2 <= 1.0:
            dentro += 1
    if n_pares == 0:
        return 0.0, 0.0
    pi_estimado = 4.0 * dentro / n_pares
    error = abs(pi_estimado - PI_REAL)
    return pi_estimado, error
