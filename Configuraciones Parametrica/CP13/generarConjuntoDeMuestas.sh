#!/bin/bash
JAR=$1
INSTANCIAS=$2
REPETICIONES=$3
for ((i=1; i<=$INSTANCIAS;i++)) do
	#Copio la instancia con el nombre correcto para que sea leida por el ecj
	cp Instancias/Intancia\ $i.in Instancias/IntanciaPrueba.in
	echo 'cp Instancias/Intancia\ $i.in Instancias/IntanciaPrueba.in'
	#Ejecuto las repeticiones del ecj
	bash generarMuestrasDeInstancia.sh  $1 $REPETICIONES
    #Renombro la salida segun la instancia
    mv Muestras/fitness.out Muestras/$i.out
    echo 'mv Muestras/fitness.out Muestras/$i.out'
done | tee timing.log
