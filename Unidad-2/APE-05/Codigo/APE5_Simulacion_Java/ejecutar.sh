#!/usr/bin/env bash
# Ejecuta el APE-05 en Java. Compila automáticamente si hace falta.
set -e
cd "$(dirname "$0")"

# --- Verificación: ¿está instalado Java? ---
if ! command -v java >/dev/null 2>&1; then
    echo "==================================================================="
    echo " ERROR: 'java' no está instalado."
    echo "==================================================================="
    echo
    echo " En Nobara / Fedora instala el JDK 21 con este comando:"
    echo
    echo "     sudo dnf install java-21-openjdk-devel"
    echo
    echo " (El paquete 'devel' incluye a la vez 'java' y 'javac')."
    echo
    exit 1
fi

# --- Compila automáticamente si no hay .class o si el código cambió ---
if [ ! -f Main.class ] || [ -n "$(find Main.java modelo vista controlador -name '*.java' -newer Main.class 2>/dev/null)" ]; then
    echo "· No hay clases compiladas (o el código cambió): compilando..."
    bash compilar.sh
    echo
fi

exec java -Dfile.encoding=UTF-8 Main "$@"
