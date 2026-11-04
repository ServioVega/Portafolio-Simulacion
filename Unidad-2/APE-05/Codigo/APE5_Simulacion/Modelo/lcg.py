"""
Modelo · Lógica matemática del Generador Congruencial Lineal (LCG).

Relación recursiva:   X(n+1) = (a · X(n) + c) mod m
Valor normalizado:    U(n)   = X(n) / m   ∈ [0, 1)

Además del generador en sí, este módulo concentra el análisis matemático
que pide la práctica:

  · verificación del teorema de Hull-Dobell (garantía de período máximo),
  · cálculo del estado X(k) mediante "saltos" en O(log k),
  · detección empírica de ciclos (demostración con módulo pequeño),
  · estadísticas descriptivas de la secuencia normalizada.
"""

import math
from statistics import mean, pstdev

#: Valores teóricos de una uniforme U(0, 1), usados para comparar resultados.
MEDIA_TEORICA = 0.5
DESVIACION_TEORICA = 1.0 / math.sqrt(12.0)   # ≈ 0.288675


class LCG:
    """Generador Congruencial Lineal configurable."""

    # Parámetros indicados en la guía de la práctica Nro. 05
    # (son los parámetros clásicos de Numerical Recipes).
    A_GUIA = 1664525
    C_GUIA = 1013904223
    M_GUIA = 2 ** 32            # 4 294 967 296
    SEMILLA_GUIA = 12345

    def __init__(self, semilla=SEMILLA_GUIA, a=A_GUIA, c=C_GUIA, m=M_GUIA):
        self._validar(semilla, a, c, m)
        self.semilla_inicial = semilla
        self.a = a
        self.c = c
        self.m = m
        self.x = semilla

    # ------------------------------------------------------------------
    # Validación de parámetros
    # ------------------------------------------------------------------
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

    # ------------------------------------------------------------------
    # Generación
    # ------------------------------------------------------------------
    def siguiente(self):
        """Aplica la recursión X(n+1) = (a·X(n) + c) mod m y devuelve X(n+1)."""
        self.x = (self.a * self.x + self.c) % self.m
        return self.x

    def siguiente_u(self):
        """Devuelve el siguiente valor normalizado U = X/m ∈ [0, 1)."""
        return self.siguiente() / self.m

    def generar(self, n):
        """Genera n valores (X(n), U(n)) desde la semilla inicial.

        La semilla X(0) NO se cuenta como valor generado: el primer valor
        devuelto es X(1) = (a·X(0) + c) mod m, exactamente igual que en la
        implementación de Java (así ambas secuencias coinciden).
        """
        x = self.semilla_inicial
        pares = []
        for _ in range(n):
            x = (self.a * x + self.c) % self.m
            pares.append((x, x / self.m))
        return pares

    # ------------------------------------------------------------------
    # Análisis del período
    # ------------------------------------------------------------------
    def factores_primos(self):
        """Conjunto ordenado de factores primos de m (división por tanteo)."""
        factores = set()
        resto = self.m
        d = 2
        while d * d <= resto:
            while resto % d == 0:
                factores.add(d)
                resto //= d
            d += 1 if d == 2 else 2
        if resto > 1:
            factores.add(resto)
        return sorted(factores)

    def verificar_hull_dobell(self):
        """Verifica las tres condiciones del teorema de Hull-Dobell (1962).

        El LCG tiene período máximo τ = m SI Y SOLO SI:
          1) gcd(c, m) = 1 (c y m son primos relativos);
          2) todo factor primo p de m divide a (a − 1);
          3) si 4 divide a m, entonces 4 divide a (a − 1).

        Devuelve (cumple_todas, lista de condiciones con su detalle).
        """
        condiciones = []

        g = math.gcd(self.c, self.m)
        condiciones.append({
            "nombre": "1) gcd(c, m) = 1  (c y m primos relativos)",
            "calculo": f"gcd({self.c}, {self.m}) = {g}",
            "cumple": g == 1,
        })

        for p in self.factores_primos():
            condiciones.append({
                "nombre": f"2) todo primo p de m divide a (a − 1)  [p = {p}]",
                "calculo": f"({self.a} − 1) mod {p} = {(self.a - 1) % p}",
                "cumple": (self.a - 1) % p == 0,
            })

        if self.m % 4 == 0:
            condiciones.append({
                "nombre": "3) 4 divide a m  ⇒  4 divide a (a − 1)",
                "calculo": f"({self.a} − 1) mod 4 = {(self.a - 1) % 4}",
                "cumple": (self.a - 1) % 4 == 0,
            })

        return all(cond["cumple"] for cond in condiciones), condiciones

    def periodo_teorico(self):
        """Período garantizado por Hull-Dobell: m si se cumplen las condiciones."""
        cumple, _ = self.verificar_hull_dobell()
        return self.m if cumple else None

    def saltar(self, k):
        """Estado X(k) tras k iteraciones, calculado en O(log k).

        Forma cerrada de la recursión (para a ≠ 1):

            X(k) = ( a^k · X(0) + c · (a^k − 1) / (a − 1) ) mod m

        La suma geométrica (a^k − 1)/(a − 1) es un entero enorme; para
        reducirla módulo m sin perder exactitud se calcula
        a^k mod (m·(a−1)) y después se divide entre (a − 1), usando los
        enteros de precisión arbitraria de Python.
        """
        if self.a == 1:
            return (self.semilla_inicial + self.c * (k % self.m)) % self.m

        a_k = pow(self.a, k, self.m)
        modulo_aux = self.m * (self.a - 1)
        r = pow(self.a, k, modulo_aux)
        if r == 0:
            r = modulo_aux
        geometrica = ((r - 1) // (self.a - 1)) % self.m
        return (a_k * self.semilla_inicial + self.c * geometrica) % self.m

    @staticmethod
    def detectar_ciclo(a, c, m, semilla, limite=10 ** 6):
        """Detecta empíricamente la estructura de ciclos de un LCG pequeño.

        Recorre la secuencia anotando el paso en el que aparece cada estado;
        al reencontrar un estado ya visto obtiene:
            μ (mu)     = longitud de la cola antes de entrar al ciclo,
            λ (lambda) = longitud del ciclo (período).
        Solo para módulos pequeños (usa memoria proporcional a m).
        """
        if m > limite:
            raise ValueError("detección de ciclo con tabla solo para m pequeños")
        paso_del_estado = {}
        x = semilla
        n = 0
        while x not in paso_del_estado:
            paso_del_estado[x] = n
            x = (a * x + c) % m
            n += 1
        mu = paso_del_estado[x]
        return mu, n - mu

    # ------------------------------------------------------------------
    # Estadísticas descriptivas
    # ------------------------------------------------------------------
    @staticmethod
    def estadisticas(us):
        """Estadísticas descriptivas de los valores normalizados U ∈ [0, 1)."""
        return {
            "n": len(us),
            "media": mean(us),
            "desviacion": pstdev(us) if len(us) > 1 else 0.0,
            "minimo": min(us),
            "maximo": max(us),
        }
