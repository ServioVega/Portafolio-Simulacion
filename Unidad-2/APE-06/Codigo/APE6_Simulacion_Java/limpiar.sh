#!/usr/bin/env bash
# Borra los archivos .class generados por compilar.sh
cd "$(dirname "$0")"
rm -rf bin
echo "Carpeta bin/ eliminada"
