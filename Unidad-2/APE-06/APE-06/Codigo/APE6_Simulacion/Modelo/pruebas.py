"""
Modelo · Pruebas estadísticas de bondad de ajuste (Práctica Nro. 06).

  · Chi-cuadrado: uniformidad de las frecuencias en k = 10 subintervalos.
  · Kolmogorov-Smirnov: comparación de la F empírica con la F teórica U(0,1).

Hipótesis:   H0: X ~ U(0, 1)        H1: X ≁ U(0, 1)        α = 0.05

Los valores p se calculan con implementaciones propias (función gamma
incompleta regularizada y distribución de Kolmogorov) para que el resultado
sea reproducible en Java con el mismo algoritmo.
"""

import math

ALFA = 0.05
K_INTERVALOS = 10          # la guía fija k = 10 → gl = 9


# --------------------------------------------------------------------------
# Funciones especiales compartidas (idénticas en Java)
# --------------------------------------------------------------------------
def log_gamma(x):
    """Logaritmo natural de Γ(x) (aproximación de Lanczos)."""
    g = 7
    coef = [
        0.99999999999980993, 676.5203681218851, -1259.1392167224028,
        771.32342877765313, -176.61502916214059, 12.507343278686905,
        -0.13857109526572012, 9.9843695780195716e-6, 1.5056327351493116e-7,
    ]
    if x < 0.5:
        # Reflexión: Γ(x)·Γ(1−x) = π / sin(πx)
        return math.log(math.pi / math.sin(math.pi * x)) - log_gamma(1.0 - x)
    x -= 1.0
    a = coef[0]
    t = x + g + 0.5
    for i in range(1, g + 2):
        a += coef[i] / (x + i)
    return 0.5 * math.log(2.0 * math.pi) + (x + 0.5) * math.log(t) - t + math.log(a)


def gamma_inferior_series(a, x):
    """P(a, x) por la serie de potencias (para x < a + 1)."""
    ap = a
    suma = 1.0 / a
    delta = suma
    for _ in range(1000):
        ap += 1.0
        delta *= x / ap
        suma += delta
        if abs(delta) < abs(suma) * 1e-15:
            break
    return suma * math.exp(-x + a * math.log(x) - log_gamma(a))


def gamma_inferior_fraccion(a, x):
    """P(a, x) por la fracción continua de Lentz (para x >= a + 1)."""
    tiny = 1e-300
    b = x + 1.0 - a
    c = 1.0 / tiny
    d = 1.0 / b
    h = d
    for i in range(1, 1000):
        an = -i * (i - a)
        b += 2.0
        d = an * d + b
        if abs(d) < tiny:
            d = tiny
        c = b + an / c
        if abs(c) < tiny:
            c = tiny
        d = 1.0 / d
        delta = d * c
        h *= delta
        if abs(delta - 1.0) < 1e-15:
            break
    return math.exp(-x + a * math.log(x) - log_gamma(a)) * h


def p_gamma(a, x):
    """Función gamma incompleta inferior regularizada P(a, x)."""
    if x < 0 or a <= 0:
        raise ValueError("parámetros inválidos en p_gamma")
    if x == 0:
        return 0.0
    if x < a + 1.0:
        return gamma_inferior_series(a, x)
    return 1.0 - gamma_inferior_fraccion(a, x)


# --------------------------------------------------------------------------
# Prueba Chi-cuadrado
# --------------------------------------------------------------------------
def chi_cuadrado(secuencia, k=K_INTERVALOS):
    """
    Algoritmo 2 de la guía.

    Devuelve un diccionario con las frecuencias observadas/esperadas,
    el estadístico χ², los grados de libertad, el valor p y la decisión.
    """
    n = len(secuencia)
    amplitud = 1.0 / k
    observadas = [0] * k
    for u in secuencia:
        i = min(int(u / amplitud), k - 1)      # intervalo al que pertenece
        observadas[i] += 1

    esperada = n / k                          # paso 4 de la guía
    chi2 = 0.0
    for o in observadas:
        chi2 += (o - esperada) ** 2 / esperada   # paso 8 de la guía

    gl = k - 1                                # paso 9 de la guía: gl = 9
    valor_p = 1.0 - p_gamma(gl / 2.0, chi2 / 2.0)   # paso 10: p = P(χ² > χ²calc)
    decision = ("No se rechaza H0: los datos son compatibles con U(0,1)"
                if valor_p >= ALFA else
                "Se rechaza H0: los datos no siguen una U(0,1)")
    return {
        "prueba": "Chi-cuadrado",
        "k": k,
        "observadas": observadas,
        "esperada": esperada,
        "estadistico": chi2,
        "gl": gl,
        "valor_p": valor_p,
        "decision": decision,
    }


# --------------------------------------------------------------------------
# Prueba Kolmogorov-Smirnov
# --------------------------------------------------------------------------
def kolmogorov_smirnov(secuencia):
    """
    Algoritmo 3 de la guía.

    Devuelve D+ , D− , el estadístico D, el valor p y la decisión.
    """
    ordenados = sorted(secuencia)             # paso 2 de la guía
    n = len(ordenados)

    d_mas = 0.0
    d_menos = 0.0
    for i, u in enumerate(ordenados, start=1):
        f_empirica_sup = i / n                # i / n
        f_empirica_inf = (i - 1) / n          # (i − 1) / n
        f_teorica = u                         # F(x) = x para U(0,1)
        d_mas = max(d_mas, f_empirica_sup - f_teorica)
        d_menos = max(d_menos, f_teorica - f_empirica_inf)

    d = max(d_mas, d_menos)                   # paso 8 de la guía
    valor_p = p_ks(d, n)                      # paso 9 de la guía
    decision = ("No se rechaza H0: la F empírica es compatible con U(0,1)"
                if valor_p >= ALFA else
                "Se rechaza H0: la F empírica no corresponde a una U(0,1)")
    return {
        "prueba": "Kolmogorov-Smirnov",
        "d_mas": d_mas,
        "d_menos": d_menos,
        "estadistico": d,
        "n": n,
        "valor_p": valor_p,
        "decision": decision,
    }


def p_ks(d, n):
    """Valor p de la distribución de Kolmogorov (serie asintótica)."""
    if d <= 0:
        return 1.0
    t = math.sqrt(n) * d
    if t < 0.2:
        return 1.0
    suma = 0.0
    for j in range(1, 101):
        termino = 2.0 * (-1.0) ** (j - 1) * math.exp(-2.0 * (j * t) ** 2)
        suma += termino
        if abs(termino) < 1e-15:
            break
    return min(1.0, max(0.0, suma))
