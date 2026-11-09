"""
Modelo · Generador Congruencial Lineal (LCG) de la Práctica Nro. 06.

Relación recursiva:   X(n+1) = (a · X(n) + c) mod m
Valor normalizado:    U(n)   = X(n) / m   ∈ [0, 1)

Implementación idéntica (paso a paso) a la versión Java para que ambas
produzcan exactamente la misma secuencia con los mismos parámetros.
"""

#: Parámetros de la práctica (los mismos usados en el APE-05).
A_GUIA = 1664525
C_GUIA = 1013904223
M_GUIA = 2 ** 32            # 4 294 967 296
SEMILLA_GUIA = 12345


class LCG:
    """Generador Congruencial Lineal configurable."""

    def __init__(self, semilla=SEMILLA_GUIA, a=A_GUIA, c=C_GUIA, m=M_GUIA):
        self._validar(semilla, a, c, m)
        self.semilla_inicial = semilla
        self.a = a
        self.c = c
        self.m = m
        self.x = semilla

    @staticmethod
    def _validar(semilla, a, c, m):
        if m <= 0:
            raise ValueError("m debe ser mayor que 0")
        if not 0 <= a < m:
            raise ValueError("a debe cumplir 0 <= a < m")
        if not 0 <= c < m:
            raise ValueError("c debe cumplir 0 <= c < m")
        if not 0 <= semilla < m:
            raise ValueError("la semilla debe cumplir 0 <= X(0) < m")

    def siguiente_x(self):
        """Paso 5-7 de la guía: multiplicar, sumar y calcular el residuo."""
        self.x = (self.a * self.x + self.c) % self.m
        return self.x

    def siguiente_u(self):
        """Paso 8 de la guía: dividir entre m para obtener un número en [0, 1)."""
        return self.siguiente_x() / self.m

    def generar_secuencia(self, n):
        """Genera n números pseudoaleatorios normalizados en [0, 1)."""
        return [self.siguiente_u() for _ in range(n)]
