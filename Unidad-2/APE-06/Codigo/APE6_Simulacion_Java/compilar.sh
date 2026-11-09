#!/usr/bin/env bash
# Compila la práctica 06 (Java). Requiere JDK 11 o superior.
set -e
cd "$(dirname "$0")"
mkdir -p bin
javac -encoding UTF-8 -d bin \
    Main.java \
    modelo/LCG.java modelo/Pruebas.java modelo/MonteCarlo.java \
    vista/Tabla.java vista/Grafica.java \
    controlador/Controlador.java
echo "Compilación correcta (clases en ./bin)"
