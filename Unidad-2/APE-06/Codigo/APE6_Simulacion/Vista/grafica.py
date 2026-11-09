"""
Vista · Gráficas con matplotlib para la Práctica Nro. 06.

Genera dos imágenes (paso 14 del algoritmo KS):
  · histograma de frecuencias observadas vs esperadas (Chi-cuadrado),
  · distribución acumulada empírica vs teórica (Kolmogorov-Smirnov).
"""

import os

import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

from Modelo import montecarlo, pruebas

plt.rcParams["font.sans-serif"] = ["Noto Sans SC", "DejaVu Sans"]
plt.rcParams["axes.unicode_minus"] = False


def _salida(nombre):
    base = os.environ.get(
        "APE6_IMG", os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "..", "..", "img"))
    os.makedirs(base, exist_ok=True)
    return os.path.join(base, nombre)


def graficar_histograma(res_chi, titulo="Histograma de frecuencias (Chi-cuadrado)"):
    """Gráfica de barras: frecuencias observadas vs frecuencia esperada."""
    k = res_chi["k"]
    centros = [(i + 0.5) / k for i in range(k)]
    fig, ax = plt.subplots(figsize=(8, 4.5), constrained_layout=True)
    ax.bar(centros, res_chi["observadas"], width=1.0 / k, alpha=0.65,
           color="#2b7bba", edgecolor="black", label="Observada")
    ax.axhline(res_chi["esperada"], color="#d62728", linestyle="--", linewidth=2,
               label=f"Esperada = {res_chi['esperada']:.0f}")
    ax.set_title(titulo)
    ax.set_xlabel("Intervalo")
    ax.set_ylabel("Frecuencia")
    ax.legend(loc="upper right", bbox_to_anchor=(1.0, 1.12), ncols=2)
    ruta = _salida("chi_cuadrado_frecuencias.png")
    fig.savefig(ruta, dpi=150)
    plt.close(fig)
    return ruta


def graficar_ks(secuencia, res_ks, titulo="Kolmogorov-Smirnov: F empírica vs F teórica"):
    """Gráfica de la CDF empírica escalonada contra la CDF teórica F(x)=x."""
    ordenados = sorted(secuencia)
    n = len(ordenados)
    xs = [0.0] + ordenados + [1.0]
    ys = [0.0] + [i / n for i in range(1, n + 1)] + [1.0]

    fig, ax = plt.subplots(figsize=(8, 4.5), constrained_layout=True)
    ax.step(xs, ys, where="post", color="#2b7bba", linewidth=1.2,
            label="F empírica Fn(x)")
    ax.plot([0, 1], [0, 1], color="#d62728", linestyle="--", linewidth=2,
            label="F teórica F(x) = x")
    u = res_ks["estadistico"]
    x_d = min(0.95, max(ordenados))
    ax.hlines([x_d - u, x_d + u], 0, 1, colors="#2ca02c", linestyles=":",
              linewidth=1.2)
    ax.text(0.02, x_d + u + 0.02, f"D = {u:.6f}", color="#2ca02c", fontsize=10)
    ax.set_title(titulo)
    ax.set_xlabel("x")
    ax.set_ylabel("F(x)")
    ax.set_xlim(0, 1)
    ax.set_ylim(0, 1.05)
    ax.legend(loc="lower right")
    ruta = _salida("kolmogorov_smirnov_cdf.png")
    fig.savefig(ruta, dpi=150)
    plt.close(fig)
    return ruta


def graficar_pi(filas, titulo="Monte Carlo: estimación de π según N"):
    """Convergencia del π estimado hacia π real para cada N."""
    ns = [f["n"] for f in filas]
    fig, ax = plt.subplots(figsize=(8, 4.5), constrained_layout=True)
    ax.plot(ns, [f["pi_py"] for f in filas], "o-", color="#2b7bba", label="π Python")
    ax.plot(ns, [f["pi_java"] for f in filas], "s--", color="#ff7f0e", label="π Java")
    ax.axhline(montecarlo.PI_REAL, color="#d62728", linestyle=":",
               linewidth=2, label="π real")
    ax.set_xscale("log")
    ax.set_title(titulo)
    ax.set_xlabel("N (puntos, escala log)")
    ax.set_ylabel("π estimado")
    ax.legend(loc="upper right", bbox_to_anchor=(1.0, 1.12), ncols=3)
    ruta = _salida("montecarlo_pi_convergencia.png")
    fig.savefig(ruta, dpi=150)
    plt.close(fig)
    return ruta
