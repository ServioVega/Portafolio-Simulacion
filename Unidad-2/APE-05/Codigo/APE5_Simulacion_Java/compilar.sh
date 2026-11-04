#!/usr/bin/env bash
# Compila la implementación Java del APE-05 (ejecutar desde cualquier carpeta).
set -e
cd "$(dirname "$0")"

# --- Verificación 1: ¿está instalado el compilador de Java (javac)? ---
if ! command -v javac >/dev/null 2>&1; then
    echo "==================================================================="
    echo " ERROR: 'javac' no está instalado (falta el JDK de Java)."
    echo "==================================================================="
    echo
    echo " En Nobara / Fedora instala el JDK 21 con este comando:"
    echo
    echo "     sudo dnf install java-21-openjdk-devel"
    echo
    echo " (En Debian/Ubuntu sería:  sudo apt install openjdk-21-jdk)"
    echo
    echo " Cuando termine la instalación, verifica con:"
    echo
    echo "     javac --version"
    echo
    echo " y vuelve a ejecutar:  bash compilar.sh"
    echo
    exit 1
fi

# --- Verificación 2: versión de Java suficientemente moderna ---
if ! javac --version >/dev/null 2>&1; then
    echo "ERROR: javac está instalado pero no funciona. Reinstala el JDK:"
    echo "    sudo dnf install java-21-openjdk-devel"
    exit 1
fi

javac -encoding UTF-8 Main.java modelo/*.java vista/*.java controlador/*.java
echo "Compilación exitosa. Ejecute ahora:  bash ejecutar.sh"
