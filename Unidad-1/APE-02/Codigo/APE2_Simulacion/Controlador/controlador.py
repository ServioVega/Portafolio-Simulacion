import numpy as np

from Modelo import poblacion
from Vista import tabla as vista_tabla
from Vista import grafica as vista_grafica


class Controlador:

    def ejecutar(self):
        # Semilla fija: el estocastico da siempre el mismo resultado,
        # asi las capturas coinciden con el reporte tecnico.
        np.random.seed(42)

        res = poblacion.ejecutar_todos(poblacion.DATOS)
        vista_tabla.mostrar_tabla(res, "APE-02 - COMPARACION DE MODELOS")
        vista_grafica.graficar(res)
