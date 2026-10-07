def indice(H, N, Tf):
    return 0.5*H + 0.3*N + 0.2*Tf


tabla =[

("6:00", 0.65, 0.40, 0.80),
("8:00", 0.70, 0.50, 0.70),
("10:00", 0.68, 0.45, 0.60),
("12:00", 0.60, 0.30, 0.40),
("14:00", 0.75, 0.70, 0.50),
("16:00", 0.85, 0.85, 0.60),
("18:00", 0.92, 0.95, 0.70),
("20:00", 0.88, 0.90, 0.65),
("22:00", 0.80, 0.75, 0.75),

]

def clasificar(I):
    if I < 0.40:
        return "Sin Lluvia"
    elif 0.40 <= I < 0.60:
        return "Baja Posibilidad"
    elif 0.60 <= I < 0.75:
        return "Lluvia Probable"
    else:
        return "Lluvia"

for hora, H, N, Tf in tabla:
    I = indice(H, N, Tf)
    print(f"{hora} | I = {I: .3f} | {clasificar(I)}")








