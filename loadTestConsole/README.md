# LoadTestConsole
Requisitos
----------
* Ubuntu Linux 12.04.5 LTS o superior
* JDK 1.8.0_121 o superior
* Apache Maven 3.3.9 o superior


Instalacion
-----------
1.- Desde una terminal dirigirse al directorio descompresion del loadTestConsole
	cd {DirectorioDescompresion}/loadTestConsole

2.- Agregar permisos de ejecucion al script LoadTestControl.sh
	sudo chmod 775 LoadTestControl.sh

3.- Instalar el programa
	./LoadTestControl.sh -i

4.- Ejecutar el programa
	./LoadTestControl.sh -r


Configuracion
-------------
Directorio de logs: 

loadTestConsole/
├── log
    ├── log.out
    └── Report.txt

Archivos de configuracion:

├── src
    ├── main
        └── resources
            ├── configuration.properties
            ├── log4j.properties
            ├── Report.txt
            ├── script1.xml
            ├── script2.xml
            ├── script.txt
            └── script.xml

