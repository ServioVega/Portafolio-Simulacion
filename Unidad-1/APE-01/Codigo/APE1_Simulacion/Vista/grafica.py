import matplotlib.pyplot as plt


def graficar(resultados, titulo="Modelo de prediccion de lluvia"):
    horas = [r["hora"] for r in resultados]
    indices = [r["I"] for r in resultados]

    plt.figure(figsize=(10, 5))
    plt.plot(horas, indices, marker="o", label="Índice I")

    plt.axhline(y=0.40, linestyle="--", color="orange", label="0.40 Baja posibilidad")
    plt.axhline(y=0.60, linestyle="--", color="green", label="0.60 Lluvia probable")
    plt.axhline(y=0.75, linestyle="--", color="blue", label="0.75 Lluvia")

    plt.xlabel("Hora")
    plt.ylabel("Índice de lluvia (I)")
    plt.title(titulo)
    plt.grid(True)
    plt.legend()
    plt.tight_layout()
    plt.show()
