#!/usr/bin/env bash
# Ejecuta la práctica 07 (Java) desde la carpeta del proyecto Java.
# La carpeta Resultados/ se resuelve relativa a APE-07/.
set -e
cd "$(dirname "$0")"
java -Dfile.encoding=UTF-8 -cp bin Main "$@"
