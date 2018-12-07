import pandas as pd
import scipy.stats as stats
import numpy as np
from numpy.core.multiarray import ndarray

cantEjecuciones = 2
# creo el vector conjunto de muestas
conjuntoMuestras = []
#print("Conjunto de Muestas: " + conjuntoMuestras)
# para cada archivo (corresponde a la ejecucion del algoritmo para una semilla CantEjecuciones veces)
for nombreArchivo in range(1, 4):
	print("Leo Archivo %d" % nombreArchivo)
	# creo un vector muestra para guardar los resultados (representa una mudestra)
	muesta = []
	# Abro el archivo
	with open("Muestras/"+str(nombreArchivo)+".out") as archivo:
		leido = archivo.readlines()
		leido= [x.strip() for x in leido]
		for x in leido:
			muesta.append(float(x))
		print("\tSolucion: " + str(muesta))
		# para cada linea
		# leo cada linea agreagando el valor de la solucion al vector muestra
	# cierro el archivo
	archivo.close()
	print("Muestra: " + str(muesta))
	# agrego el vector muestra al conjunto de muestras
	conjuntoMuestras.append(muesta)

print
print("Conjunto de Muestas: " + str(conjuntoMuestras))
conjuntoMuestras=np.asarray(conjuntoMuestras)
resultado=stats.friedmanchisquare(*(conjuntoMuestras[i, :] for i in range(conjuntoMuestras.shape[0])))
# conjuntoMuestras[0],conjuntoMuestras[1],conjuntoMuestras[2])
print(resultado)


# ejecuto el test de friedman para el conjunto de muestras

# conjuntoMuestras = np.array([[2, 2, 3], [1, 1, 1] ,[4, 5, 6]])
# resultado=stats.friedmanchisquare(*(conjuntoMuestras[i, :] for i in range(conjuntoMuestras.shape[0])))
# print resultado
