def mostrar_tabla(resultados, titulo="RESULTADOS"):
    print("\n" + "=" * 90)
    print(titulo.center(90))
    print("=" * 90)
    print(f"{'Hora':<8}{'Humedad':<9}{'Nubosidad':<11}{'Temp °C':<9}"
          f"{'H':<7}{'N':<7}{'Tf':<7}{'Índice I':<10}{'Estado':<16}")
    print("-" * 90)
    for r in resultados:
        print(f"{r['hora']:<8}{r['humedad']:<9}{r['nubosidad']:<11}{r['temp']:<9}"
              f"{r['H']:<7.2f}{r['N']:<7.2f}{r['Tf']:<7.2f}"
              f"{r['I']:<10.3f}{r['estado']:<16}")
    print("-" * 90)
