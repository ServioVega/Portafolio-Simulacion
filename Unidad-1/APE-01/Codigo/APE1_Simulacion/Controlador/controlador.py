from Modelo import lluvia
from Vista import tabla as vista_tabla
from Vista import grafica as vista_grafica


class Controlador:

    def ejecutar(self):
        # 1) Calcular el modelo con los datos de la practica
        resultados = lluvia.procesar(lluvia.DATOS)

        # 2) Mostrar la tabla en consola
        vista_tabla.mostrar_tabla(resultados, "RESULTADOS  I = 0.5H + 0.3N + 0.2Tf")

        # 3) Mostrar la grafica
        vista_grafica.graficar(resultados)
