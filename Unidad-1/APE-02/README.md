APE-02: Comparacion de Modelos de Crecimiento Poblacional

Comparacion de modelos deterministas, estocasticos, discretos y continuos

Asignatura: Simulacion | Ciclo 5 | FEIRNNR
1. Descripcion del Proyecto

Sistema que compara cuatro paradigmas de simulacion aplicados al crecimientopoblacional, usando como entrada la serie observada:

datos = [1000, 1100, 1250, 1400, 1600, 1850, 2100]

De esa serie se derivan los parametros: P0 = 1000 (primer dato) y la tasa decrecimiento r = 0.1318 (promedio de las tasas observadas). Los cuatro modelos:
Modelo	Ecuacion	Caracteristica
Deterministico	P(t) = P0 * e^(r*t)	Sin aleatoriedad
Discreto	P[k+1] = P[k] + r * P[k]	Cambios por periodos
Estocastico	P[k+1] = P[k] + r*P[k] + ruido	Ruido N(0, sigma)
Continuo (Euler)	P[i+1] = P[i] + dt * r * P[i]	Aproximacion numerica

El proyecto sigue el patron Modelo-Vista-Controlador (MVC): el modelo contienelos cuatro modelos matematicos, la vista muestra la tabla comparativa y lagrafica, y el controlador coordina ambos.
2. Tecnologias Utilizadas

    Python 3: lenguaje principal
    NumPy: calculo numerico y generacion de numeros aleatorios
    Matplotlib: grafica comparativa

Este proyecto no maneja credenciales, claves ni contrasenas.
3. Instalacion

cd Codigo/APE2_Simulacionpython3 -m venv venvsource venv/bin/activatepip install -r requirements.txt

4. Ejecucion / Modo de Uso

Con el entorno virtual activo:

python3 main.py

El programa imprime la tabla comparativa (datos reales y los cuatro modelospor periodo) y muestra la grafica de las cinco series en una sola figura.
5. Estructura del Proyecto

APE-02/├── README.md               → este archivo├── Reporte_Tecnico/        → reporte tecnico de la practica├── img/                    → capturas del funcionamiento└── Codigo/APE2_Simulacion/    ├── main.py             → punto de entrada del programa    ├── Modelo/             → poblacion.py: los cuatro modelos matematicos    ├── Vista/              → tabla.py y grafica.py: presentacion    └── Controlador/        → controlador.py: coordinacion MVC

6. Informacion Adicional

Tabla de resultados:

Tabla de resultados

Grafica comparativa:

Grafica comparativa
7. Declaracion de uso de IA

En el desarrollo de esta practica se utilizo asistencia de IA generativa(Claude, de Anthropic) como apoyo. Distribucion del aporte:
Actividad	Responsable
Enunciado, decisiones de diseno y estructura MVC	Estudiante
Revisión Del Código 	IA, con direccion del estudiante
Creacion de archivos, ejecucion y verificacion	Estudiante
Comprension del codigo para sustento	Estudiante
  
Prompts utilizados (clic para ver)
"Necesito que me ayudes revisando este APE fue algo similar al APE01 que realizamos, el mismo patron de diseño, asi que ayudame a revisar si puedo mejorar algo del código si hay alguna observación que tengas sobre el APE me avisas"  
