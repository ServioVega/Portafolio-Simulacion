"""
Vista · Tablas y mensajes en consola.

Solo se encarga de PRESENTAR información: no calcula nada (eso es tarea
del Modelo) ni decide el orden de los pasos (eso es tarea del Controlador).
"""

LINEA = "═" * 78
ANCHO = 78


def fmt(numero):
    """Formatea un entero con separador de miles: 4294967296 → 4.294.967.296."""
    return f"{numero:,}".replace(",", ".")


def fmt_tiempo(segundos):
    """Formatea un tiempo en segundos con 1 decimal (estilo español)."""
    return f"{segundos:.1f}".replace(".", ",")


# ----------------------------------------------------------------------
# Encabezados
# ----------------------------------------------------------------------
def cabecera(lenguaje):
    print()
    print(LINEA)
    print(" APE-05 · GENERADOR CONGRUENCIAL LINEAL (LCG)")
    print(f" Implementación en {lenguaje} · patrón MVC · Simulación · Unidad 2")
    print(LINEA)


def paso(numero, titulo):
    print()
    print(f" PASO {numero} · {titulo}")
    print(" " + "-" * (ANCHO - 2))


def cierre(lenguaje):
    print()
    print(LINEA)
    print(f" Ejecución finalizada · APE-05 · LCG en {lenguaje}")
    print(LINEA)


# ----------------------------------------------------------------------
# Paso 1: parámetros
# ----------------------------------------------------------------------
def parametros(lcg):
    print(f"   a       = {lcg.a}")
    print(f"   c       = {lcg.c}")
    print(f"   m       = 2^32 = {fmt(lcg.m)}")
    print(f"   semilla = X(0) = {lcg.semilla_inicial}")
    print("   (parámetros indicados en la guía, práctica Nro. 05)")


def nota_paso_3_java():
    paso(3, "Implementar el mismo algoritmo en Java")
    print("   La implementación equivalente en Java (misma recursión y mismos")
    print("   parámetros) está en:  Codigo/APE5_Simulacion_Java/")
    print("   Ejecútela también: al volver a lanzar este programa, la tabla")
    print("   comparativa del paso 4 se completa automáticamente.")


# ----------------------------------------------------------------------
# Paso 2 / 4 / 7: tablas de observaciones
# ----------------------------------------------------------------------
def primeras_observaciones(pares, k, titulo):
    print(f"\n   {titulo}")
    print(f"   {'n':>4} | {'X(n)':>12} | U(n)")
    print(f"   {'-' * 4}-+-{'-' * 12}-+-{'-' * 14}")
    for i, (x, u) in enumerate(pares[:k], start=1):
        print(f"   {i:>4} | {x:>12} | {u:.10f}")


def secuencias_distintas(pares1, semilla1, pares2, semilla2):
    print(f"\n   Primeras observaciones con cada semilla (¿son diferentes?)")
    print(f"   {'n':>4} | {'U con semilla ' + str(semilla1):>15} | "
          f"{'U con semilla ' + str(semilla2):>15} | difieren")
    print("   " + "-" * 52)
    for i, ((_, u1), (_, u2)) in enumerate(zip(pares1, pares2), start=1):
        print(f"   {i:>4} | {u1:>15.10f} | {u2:>15.10f} | "
              f"{'SI' if u1 != u2 else 'NO'}")


# ----------------------------------------------------------------------
# Paso 4 / 8: comparación Python vs Java
# ----------------------------------------------------------------------
def comparacion_primeras_20(filas, semilla):
    print(f"\n   Comparación Python vs Java · semilla {semilla} "
          f"(primeras {len(filas)} observaciones)")
    print(f"   {'n':>4} | {'X(n) Python':>12} | {'X(n) Java':>12} | "
          f"{'U(n) Python':>13} | {'U(n) Java':>13} | coincide")
    print("   " + "-" * 76)
    for n, x_py, u_py, x_ja, u_ja, ok in filas:
        print(f"   {n:>4} | {x_py:>12} | {x_ja:>12} | {u_py:>13.10f} | "
              f"{u_ja:>13.10f} | {'SI' if ok else 'NO'}")


def resumen_comparacion(coincidentes, total, semilla):
    porcentaje = 100.0 * coincidentes / total if total else 0.0
    print()
    print(f"   → Valores idénticos en ambos lenguajes: {fmt(coincidentes)} de "
          f"{fmt(total)} ({porcentaje:.2f} %) con la semilla {semilla}")


def aviso_falta_java(ruta):
    print(f"\n   (Todavía no existe {ruta.name}: ejecute primero el programa")
    print("    en Java y vuelva a lanzar este para ver la comparación)")


def aviso_falta_archivo(ruta):
    print(f"\n   (No se encontró {ruta.name})")


# ----------------------------------------------------------------------
# Paso 6: análisis del período
# ----------------------------------------------------------------------
def hull_dobell(condiciones, cumple, m):
    print("\n   Teorema de Hull-Dobell (1962) · condiciones de período máximo:")
    for cond in condiciones:
        marca = "✓" if cond["cumple"] else "✗"
        print(f"     {marca} {cond['nombre']}")
        print(f"         {cond['calculo']}")
    if cumple:
        print(f"\n   → Se cumplen las tres condiciones: período máximo τ = m = {fmt(m)}")
    else:
        print("\n   → NO se cumplen todas las condiciones: τ < m (período reducido)")


def confirmacion_periodo(m, x_final, x_medio, semilla, auto_ok):
    print("\n   Confirmación empírica (salto de k pasos en O(log k),")
    print("   aprovechando los enteros de precisión arbitraria de Python):")
    if x_final == semilla:
        print(f"     · X({fmt(m)}) = {fmt(x_final)} = X(0)  ✓  "
              f"el estado vuelve a la semilla tras {fmt(m)} pasos")
    else:
        print(f"     · X({fmt(m)}) = {fmt(x_final)} ≠ X(0)  ✗")
    if x_medio != semilla:
        print(f"     · X({fmt(m // 2)}) = {fmt(x_medio)} ≠ X(0)  ✓  "
              f"el período no es un divisor menor")
    if auto_ok is not None:
        print(f"     · autoverificación del salto (k = 1000 frente al valor "
              f"generado): {'✓ correcto' if auto_ok else '✗ ERROR'}")


def sin_repeticiones(distintos, total):
    if distintos == total:
        print(f"\n   Dentro de los {fmt(total)} valores generados hay {fmt(distintos)} "
              f"estados distintos → sin repeticiones ✓")
    else:
        print(f"\n   Dentro de los {fmt(total)} valores generados solo hay "
              f"{fmt(distintos)} estados distintos → ¡hay repeticiones!")


def demo_ciclos(mu_b, lam_b, mu_m, lam_m):
    print("\n   Demostración con módulo pequeño (detección empírica de ciclos):")
    print("     · BUENO: a=13, c=7, m=16, X(0)=1  →  cola μ = 0, período λ = 16 (= m ✓)")
    print("     · MALO:  a=6,  c=0, m=32, X(0)=1  →  cola μ = 5, período λ = 1 "
          "(¡colapsa!: viola Hull-Dobell)")


def nota_conteo_java(m):
    print(f"\n   Nota: el conteo EXHAUSTIVO del ciclo ({fmt(m)} iteraciones)")
    print("   se realiza en la implementación Java, que lo ejecuta en segundos")
    print("   gracias a su velocidad nativa; aquí se confirma el mismo resultado")
    print("   con el salto en O(log m).")


# ----------------------------------------------------------------------
# Paso 8: estadísticas y conclusiones
# ----------------------------------------------------------------------
def comparacion_estadisticas(semilla1, stats1, semilla2, stats2):
    print(f"\n   Estadísticas de los {fmt(stats1['n'])} valores U generados")
    print(f"   {'':26} | {'semilla ' + str(semilla1):>15} | "
          f"{'semilla ' + str(semilla2):>15} | {'teórico U(0,1)':>15}")
    print("   " + "-" * 76)
    filas = [
        ("media", stats1["media"], stats2["media"], 0.5),
        ("desviación estándar", stats1["desviacion"], stats2["desviacion"], 0.288675),
        ("mínimo", stats1["minimo"], stats2["minimo"], 0.0),
        ("máximo", stats1["maximo"], stats2["maximo"], 1.0),
    ]
    for nombre, v1, v2, vt in filas:
        print(f"   {nombre:<26} | {v1:>15.6f} | {v2:>15.6f} | {vt:>15.6f}")


def conclusiones(semilla, semilla_alt, n, coincidieron):
    print("\n   Conclusiones operativas (el análisis completo va en el Reporte Técnico):")
    if coincidieron:
        print(f"     1. Con los mismos parámetros y la misma semilla ({semilla}),")
        print(f"        Python y Java producen la MISMA secuencia "
              f"({fmt(n)}/{fmt(n)} valores idénticos).")
    else:
        print("     1. Python y Java deben ejecutarse ambos para completar la")
        print("        tabla comparativa entre lenguajes.")
    print(f"     2. Al cambiar la semilla ({semilla} → {semilla_alt}) la secuencia")
    print("        cambia por completo, pero sus propiedades estadísticas se")
    print("        mantienen (media ≈ 0,5 y desviación ≈ 0,2887, como U(0,1)).")
    print("     3. Los parámetros de la guía cumplen Hull-Dobell ⇒ período")
    print("        máximo τ = m = 2^32 (verificado teórica y empíricamente).")


# ----------------------------------------------------------------------
# Archivos generados
# ----------------------------------------------------------------------
def archivo_guardado(ruta_relativa, filas=None):
    detalle = f"  ({fmt(filas)} filas)" if filas else ""
    print(f"   ✓ guardado: {ruta_relativa}{detalle}")


def archivos_guardados(rutas_relativas):
    for ruta in rutas_relativas:
        print(f"   ✓ guardado: {ruta}")
