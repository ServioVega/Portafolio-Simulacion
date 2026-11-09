#!/usr/bin/env bash
# Ejecuta la práctica 06 (Java) desde la carpeta del proyecto Java.
# La carpeta Resultados/ e img/ se resuelven relativas a APE-06/.
set -e
cd "$(dirname "$0")"
java -Dfile.encoding=UTF-8 -cp bin Main "$@"
