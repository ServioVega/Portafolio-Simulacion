"""
Vista · Gráficas de la práctica (matplotlib).

Genera las imágenes del paso 5 ("graficar la secuencia") y del paso 8
(comparación entre semillas). Todas se guardan en la carpeta img/ de la
práctica. Se usa el backend "Agg" para poder generar las imágenes en
cualquier entorno, incluso sin interfaz gráfica (servidores Linux).
"""

import matplotlib

matplotlib.use("Agg")
import matplotlib.pyplot as plt  # noqa: E402  (debe importarse tras elegir el backend)

COLOR_PRINCIPAL = "#1f77b4"   # azul
COLOR_SECUNDARIO = "#d62728"  # rojo


def grafica_secuencia(us, semilla, ruta, lenguaje="Python"):
    """Paso 5: U(n) frente a n (los 10 000 valores de la secuencia)."""
    ns = range(1, len(us) + 1)
    fig, ax = plt.subplots(figsize=(10, 4.5), constrained_layout=True)
    ax.plot(ns, us, lw=0.35, color=COLOR_PRINCIPAL)
    ax.set_title(f"Secuencia U(n) generada por el LCG ({lenguaje}, semilla {semilla})")
    ax.set_xlabel("n")
    ax.set_ylabel("U(n)")
    ax.set_xlim(1, len(us))
    ax.set_ylim(-0.02, 1.02)
    ax.grid(alpha=0.3)
    fig.savefig(ruta, dpi=150)
    plt.close(fig)


def grafica_dispersion(us, semilla, ruta, lenguaje="Python"):
    """Dispersión de pares consecutivos (U(n), U(n+1)).

    Si los parámetros fueran malos, aquí aparecerían los "hiperplanos de
    Marsaglia"; con los parámetros de la guía se observa una nube uniforme.
    """
    fig, ax = plt.subplots(figsize=(5.8, 5.8), constrained_layout=True)
    ax.scatter(us[:-1], us[1:], s=1.6, color=COLOR_PRINCIPAL, alpha=0.5,
               edgecolors="none")
    ax.set_title(f"Dispersión (U(n), U(n+1))\nLCG {lenguaje} · semilla {semilla} · {len(us) - 1} pares")
    ax.set_xlabel("U(n)")
    ax.set_ylabel("U(n+1)")
    ax.set_xlim(0, 1)
    ax.set_ylim(0, 1)
    ax.set_aspect("equal")
    ax.grid(alpha=0.3)
    fig.savefig(ruta, dpi=150)
    plt.close(fig)


def grafica_histograma(us, semilla, ruta, lenguaje="Python", clases=20):
    """Histograma de los valores U: uniformidad de la secuencia."""
    fig, ax = plt.subplots(figsize=(7.5, 4.5), constrained_layout=True)
    ax.hist(us, bins=clases, range=(0, 1), color=COLOR_PRINCIPAL,
            edgecolor="white")
    esperado = len(us) / clases
    ax.axhline(esperado, color=COLOR_SECUNDARIO, ls="--", lw=1.5,
               label=f"frecuencia esperada = {esperado:.0f}")
    ax.set_title(f"Histograma de U(n) · LCG {lenguaje} · semilla {semilla} · {len(us)} valores")
    ax.set_xlabel("U(n)")
    ax.set_ylabel("frecuencia")
    ax.set_xlim(0, 1)
    ax.legend(loc="upper left", bbox_to_anchor=(1.02, 1.0), frameon=False)
    fig.savefig(ruta, dpi=150)
    plt.close(fig)


def grafica_comparacion_semillas(us1, semilla1, us2, semilla2, ruta, cantidad=60):
    """Paso 7-8: primeras observaciones con dos semillas distintas."""
    ns = range(1, cantidad + 1)
    fig, ax = plt.subplots(figsize=(10, 4.5), constrained_layout=True)
    ax.plot(ns, us1[:cantidad], "o-", ms=3.5, lw=1.0, color=COLOR_PRINCIPAL,
            label=f"semilla {semilla1}")
    ax.plot(ns, us2[:cantidad], "s-", ms=3.5, lw=1.0, color=COLOR_SECUNDARIO,
            label=f"semilla {semilla2}")
    ax.set_title(f"Cambio de semilla ({semilla1} → {semilla2}): primeras {cantidad} observaciones")
    ax.set_xlabel("n")
    ax.set_ylabel("U(n)")
    ax.set_xlim(1, cantidad)
    ax.set_ylim(-0.05, 1.05)
    ax.grid(alpha=0.3)
    ax.legend(loc="center left", bbox_to_anchor=(1.02, 0.5), frameon=False)
    fig.savefig(ruta, dpi=150)
    plt.close(fig)
