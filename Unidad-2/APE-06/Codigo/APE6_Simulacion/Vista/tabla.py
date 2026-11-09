"""
Vista · Tablas y mensajes en consola para la Práctica Nro. 06.
"""

from Modelo import pruebas


def imprimir_titulo(texto):
    print("\n" + "=" * 70)
    print(texto)
    print("=" * 70)


def imprimir_subtitulo(texto):
    print("\n--- " + texto + " ---")


def imprimir_muestra(secuencia, cantidad=10):
    """Muestra los primeros 'cantidad' números pseudoaleatorios."""
    imprimir_subtitulo(f"Primeros {cantidad} números pseudoaleatorios U(0,1)")
    for i, u in enumerate(secuencia[:cantidad], start=1):
        print(f"  U({i:3d}) = {u:.10f}")
    print(f"  ... (total generados: {len(secuencia):,})")


def imprimir_chi(res):
    """Paso 14 del algoritmo 2: frecuencias, χ², gl y valor p."""
    imprimir_subtitulo("Prueba Chi-cuadrado (k = %d, gl = %d)" % (res["k"], res["gl"]))
    print(f"  {'Intervalo':<16}{'Observada':>12}{'Esperada':>12}")
    for i, o in enumerate(res["observadas"]):
        li, ls = i / res["k"], (i + 1) / res["k"]
        print(f"  [{li:.1f}, {ls:.1f})   {o:>12d}{res['esperada']:>12.1f}")
    print(f"\n  Estadístico χ²  = {res['estadistico']:.6f}")
    print(f"  Grados libertad = {res['gl']}")
    print(f"  Valor p         = {res['valor_p']:.6f}")
    print(f"  Decisión (α = {pruebas.ALFA}) → {res['decision']}")


def imprimir_ks(res):
    """Paso 13 del algoritmo 3: estadístico KS, valor p y decisión."""
    imprimir_subtitulo("Prueba Kolmogorov-Smirnov (n = %d)" % res["n"])
    print(f"  D+ = {res['d_mas']:.6f}")
    print(f"  D− = {res['d_menos']:.6f}")
    print(f"  D  = max(D+, D−) = {res['estadistico']:.6f}")
    print(f"  Valor p = {res['valor_p']:.6f}")
    print(f"  Decisión (α = {pruebas.ALFA}) → {res['decision']}")


def imprimir_tabla_pi(filas):
    """
    Tabla de Resultados esperados: π estimado, error y tiempos por lenguaje.

    filas: lista de diccionarios con llaves n, pi_py, err_py, t_py,
           pi_java, err_java, t_java (los Java pueden ser None).
    """
    imprimir_subtitulo("Tabla de Resultados Esperados: Monte Carlo π (Python vs Java)")
    cab = (f"  {'N':>10} | {'π (Python)':>12} | {'Error Py':>10} | "
           f"{'t Python (s)':>13} | {'π (Java)':>12} | {'Error Java':>10} | {'t Java (s)':>11}")
    print(cab)
    print("  " + "-" * (len(cab) - 2))
    for f in filas:
        if f["pi_java"] is None:
            java = "— (ejecutar Java)".rjust(35)
            print(f"  {f['n']:>10,} | {f['pi_py']:>12.6f} | {f['err_py']:>10.6f} | "
                  f"{f['t_py']:>13.4f} | {java}")
        else:
            print(f"  {f['n']:>10,} | {f['pi_py']:>12.6f} | {f['err_py']:>10.6f} | "
                  f"{f['t_py']:>13.4f} | {f['pi_java']:>12.6f} | {f['err_java']:>10.6f} | "
                  f"{f['t_java']:>11.4f}")


def imprimir_comparacion_secuencias(primeras_py, primeras_java, identicas):
    imprimir_subtitulo("Comparación Python vs Java (misma semilla y parámetros)")
    print(f"  {'i':>4} | {'Python U(i)':>18} | {'Java U(i)':>18}")
    for i, (a, b) in enumerate(zip(primeras_py, primeras_java), start=1):
        marca = "" if a == b else "  ← difieren"
        print(f"  {i:>4} | {a:>18.10f} | {b:>18.10f}{marca}")
    print(f"\n  Secuencias idénticas: {'SÍ ✔' if identicas else 'NO ✘'}")
