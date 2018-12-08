#!/bin/bash
JAR=$1
REPETICION=$2
for((i=1; i<=$REPETICION;i++)) do
    #echo =============================
    #echo "Number $i: $(date +%Y-%m-%d-%H:%M:%S)"
    java -jar target/$JAR -from app/proyectoFinal/CP11.params
done | tee timing.log
