#!/bin/bash

# Puertos que necesitamos liberar (5001 y 5002 en este ejemplo)
ports=(5001 5002)

# Liberar los puertos ocupados (si existen procesos en esos puertos)
for port in "${ports[@]}"; do
  pid=$(sudo lsof -t -i :$port)
  if [ -n "$pid" ]; then
    echo "Liberando puerto $port (PID: $pid)"
    sudo kill -9 $pid
  fi
done

# Ahora iniciar los middlewares
echo "Iniciando middleware 1..."
cd middleware1 && node app.js &

echo "Iniciando middleware 2..."
cd middleware2 && node app.js &
 
echo "Middlewares iniciados con éxito." &

echo "Iniciando mqtt-cratedb-listener, base de datos crate y servicio de medias"

cd mqtt-cratedb-listener && node mqtt-cratedb-listener.js &
cd mqtt-cratedb-listener && node crate.js &
cd mqtt-cratedb-listener && node servicio-medias.js &

echo "todo iniciado con éxito :)" 

