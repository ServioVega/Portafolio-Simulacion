# Tabla del enunciado: (temperatura °C, factor Tf)
TABLA_FACTOR_TEMP = [
    (10, 1.00), (12, 0.90), (14, 0.80), (16, 0.70), (18, 0.60),
    (20, 0.50), (22, 0.40), (24, 0.30), (26, 0.20), (28, 0.10),
]

# Datos de la practica: (hora, humedad %, nubosidad %, temperatura °C)
DATOS = [
    ("06:00", 65, 40, 14),
    ("08:00", 70, 50, 16),
    ("10:00", 68, 45, 18),
    ("12:00", 60, 30, 22),
    ("14:00", 75, 70, 20),
    ("16:00", 85, 85, 18),
    ("18:00", 92, 95, 16),
    ("20:00", 88, 90, 17),
    ("22:00", 80, 75, 15),
]


def normalizar(porcentaje):
    return porcentaje / 100.0


def factor_temperatura(temp):
    if temp <= TABLA_FACTOR_TEMP[0][0]:
        return TABLA_FACTOR_TEMP[0][1]      # <= 10 °C -> 1.00
    if temp >= TABLA_FACTOR_TEMP[-1][0]:
        return TABLA_FACTOR_TEMP[-1][1]     # >= 28 °C -> 0.10
    for i in range(len(TABLA_FACTOR_TEMP) - 1):
        t1, f1 = TABLA_FACTOR_TEMP[i]
        t2, f2 = TABLA_FACTOR_TEMP[i + 1]
        if t1 <= temp <= t2:
            return f1 + (f2 - f1) * (temp - t1) / (t2 - t1)


def indice(H, N, Tf):
    """I = 0.5H + 0.3N + 0.2Tf"""
    return 0.5 * H + 0.3 * N + 0.2 * Tf


def clasificar(I):
    if I < 0.40:
        return "Sin lluvia"
    if I < 0.60:
        return "Baja posibilidad"
    if I < 0.75:
        return "Lluvia probable"
    return "Lluvia"


def procesar(datos=DATOS):
    resultados = []
    for hora, humedad, nubosidad, temp in datos:
        H = normalizar(humedad)
        N = normalizar(nubosidad)
        Tf = factor_temperatura(temp)
        I = indice(H, N, Tf)
        resultados.append({
            "hora": hora, "humedad": humedad, "nubosidad": nubosidad,
            "temp": temp, "H": H, "N": N, "Tf": Tf,
            "I": I, "estado": clasificar(I),
        })
    return resultados
