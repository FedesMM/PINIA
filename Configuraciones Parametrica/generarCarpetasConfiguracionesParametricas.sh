#!/bin/bash
for ((i=1; i<=30;i++)) do
	#Copio la instancia con el nombre correcto para que sea leida por el ecj
	cp -R CPBase CP$i
done 
