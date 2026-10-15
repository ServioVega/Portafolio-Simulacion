# -*- coding: utf-8 -*-
"""Vista de texto: tabla comparativa de los cuatro modelos."""


def mostrar_tabla(res, titulo="RESULTADOS"):
    print("\n" + "=" * 95)
    print(titulo.center(95))
    print("=" * 95)
    print(f"Parametros: P0 = {res['P0']}, r = {res['r']:.4f}, "
          f"sigma = {res['sigma']}, dt = {res['dt']}")
    print("-" * 95)
    print(f"{'t':<5}{'Real':<12}{'Determ.':<12}{'Discreto':<12}"
          f"{'Estocastico':<14}{'Continuo(Euler)':<16}")
    print("-" * 95)
    for i in range(len(res["t"])):
        print(f"{res['t'][i]:<5.0f}{res['datos'][i]:<12.1f}{res['det'][i]:<12.1f}"
              f"{res['dis'][i]:<12.1f}{res['est'][i]:<14.1f}"
              f"{res['cont_tab'][i]:<16.1f}")
    print("-" * 95)

