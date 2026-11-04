#!/usr/bin/env bash
# Elimina los archivos .class generados al compilar.
cd "$(dirname "$0")"
rm -f Main.class modelo/*.class vista/*.class controlador/*.class
echo "Archivos .class eliminados."
