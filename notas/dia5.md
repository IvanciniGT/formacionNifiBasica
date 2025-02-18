# Recordatorio sobre Apache NIFI

## FlowFile

Es la unidad de tyransporte de información en Apache Nifi...
Lo que circula entre procesadores... a lo largo de nuestro flujo de procesamiento de datos.

Tenía 2 partes:
- Atributos / Metadatos
- Contenido <- FORMATO? Esto lo configuramos nosotros... Puede ser un JSON, AVRO, XML, CSV...
  Aunque hay un tipo de contenido muy especial que nos ofrece Apache NIFI: ENTIDAD (ESQUEMA DE DATOS)
    Al trabajar con entidades salen los conceptos de READER / WRITER

## Esquema de una entidad

Nos da la estructura de un documento / unidad de información:
Qué campos tiene, de qué tipo son esos campos.

Los esquemas los podemos tener cargados dentro de Apache NIFI: En un SchemaRegistry
Hay varios disponibles dentro de NIFI: AvroSchemaRegistry

## Processors

Son los programas que hacen algo sobre nuestros datos... o que los crean/cargan.

## Relaciones/Conectores

Son lo que permite el envío de datos / flowfiles de un processor a otro.
Llevan asociada una cola de almacenamiento de información, de forma que si el procesador de salida no está disponibles
los datos se queden en esa cola, hasta que lo esté.

## ProcessGroups

Son agrupaciones de procesadores con sus conexiones.
Caso que necesitasen recibir información del exterior, había que configurar un INPUT PORT
Caso que necesitasen mandar datos al exterior (de vuelta a quién haya solicitado la ejecución del processgroup)
hay que configurar un OUTPUT PORT

## FUNNEL

Nos permiten agrupar dos o más salidas de unos procesadores para su envío a un nuevo procesador.

## Controller Services

Son progrmas que configuramos para que se ejecuten FUERA (de forma independiente) al flujo que estamos creando en NIFI.
Lo habitual es que tengamos varios de ellos... y los processors de nuestro flujo los utilice:
- Registros de esquemas
- Programas para formatear datos
- Programas para leer datos con un formato determinado
- Guardar credenciales
- Gestionar conexiones a BBDD

---

# Buenas prácticas:

- Usar una nomenclatura adecuada en los campos NOMBRE (procesador, cola, service)
    - Procesadores: llevan por nombre un VERBO
    - Colas: llevan por nombre un SUSTANTIVO
- En elementos que requieran algo adicional de información: USAR LA CAJA DE COMENTARIOS.
- Separar conceptualmente tareas (creando PROCESSGROUPS)
- Esos process groups deben ser REUTILIZABLES... y en ocasiones eso implica:
    - Configurarles varios InputPorts
    - Configurarles varios OutputPorts
- Usar MUCHO, MUCHISIMO, CASI POR TODOS LADOS: el procesador LogAttribute
    - Ésto no tiene porque penalizar el rendimiento... Si le pongo un nivel de log bajo.
- Usar MUCHO, MUCHISIMO, SIEMPRE: El procesador que GENERA DATOS DE PRUEBA
  y se queda en el flujo

---
    
    DESARROLLO DE UN FLUJO <> PRUEBAS -> OK  ->  REFACTORIZAR <> PRUEBAS -> OK
    <---------- 50% del trabajo ---------->     <------ 50% del trabajo ------>
                8 horas                                     8 horas 
                
El objetivo es dejar un programa FACIL DE:
- Leer
- Mantener
- Modificar
- Evolucionar


---

Lo primero que haremos será:
0. Pequeña introducción a Contenedores y Docker.
1. Empezar a jugar con BBDD: PostgreSQL
        Lo primero será tener una BBDD instalada con la que poder jugar.
        Lo siguiente será dentro de esa BBDD tener tablas y datos.
2. Seguiremos jugando con Apache KAFKA
        Lo primero será tener un cluster de Kafka instalado.
        Aquí no crearemos a priori datos.
        Haremos programas en NIFI que carguen datos en KAFKA y luego haremos programas que LEAN datos de KAFKA
3. Trabajo con ficheros
        Los datos que vayamos leyendo de los sistemas anteriores los volcaremos a ficheros
        Leeremos esso ficheros para trabajar.

---

Para hacer eso vamos a usar DOCKER y CONTENEDORES.

# Instalación de software

        App1 + App2 + App3              Problemas muy importantes:
    ---------------------------             - Imaginar que App1 tiene un bug (error)
        Sistema Operativo                           App pone la CPU al 100%: App1 ----> OFFLINE
    ---------------------------                                              App2 y App3 --- > OFFLINE
            HIERRO                          - Imaginad que App1 y App3 tienen distintos requerimientos / incompatibles entre si

# Máquinas virtuales

        App1    |  App2 + App3          Problemas importantes:
    ---------------------------             - Complejidad de la instalación SUBE
        SO 1    |    SO 2                   - Mantenimiento de la instalación no es el mismo
    ---------------------------             - Desperdicio de recursos en el HIERRO
        MV 1    |    MV 2                   - Merma en el rendimiento de las apps
    ---------------------------     
        Hipervisor:
        VirtualBox, hyperV, VMWare
        Citrix, KVM...
    ---------------------------     
        Sistema Operativo           
    ---------------------------     
            HIERRO                  

# Contenedores


        App1    |  App2 + App3
    --------------------------- 
        C 1     |    C 2        
    ---------------------------     
       Gestor de contenedores:
       Docker, podman, containerd
       crio
    ---------------------------     
      Sistema Operativo Linux         
    ---------------------------     
            HIERRO       

Un contenedor es un entorno AISLADO dentro de un Sistema Operativo (LINUX) donde ejecutar procesos.
Las aplicaciones que se ejecutan dentro de un contenedor están en comunicación directa con el
Sistema Operativo del host.
Pero a pesar de ello, se ejecutan en un entorno aislado (como si fueran máquinas virtuales)
Son mucho más ligeros que las máquinas virtuales.

AISLADO:
    - El contenedor tiene su propia dirección IP
    - El contenedor tiene su propio sistema de archivos (como su propio HDD) -> HDD local

Los contenedores se crean mediante IMAGENES DE CONTENEDOR.

# Imágen de contenedor:

Es un archivo comprimido (.tar) que lleva dentro un programa YA PREINSTALADO por un fabricante.

    
    Quiero instalar PostgreSQL en mi ordenador Windows... de forma tradicional:
    1. Descargar el instalador
    2. Ejecuto el instalador -> INSTALACION c:\Archivos de programa\PostgreSQL > .zip -> email
    3. Ejecuto el programa

Las imágenes de contenedor las encontramos en REGISTROS DE REPOSITORIOS DE IMAGENES DE CONTENEDOR.
El más famoso se llama docker hub

---

Kubernetes < Openshift (distro de kubernetes de la gente de REDHAT)
           < Karbon    (distro de kubernetes de la gente de NUTANIX)
           < Tanzú     (distro de kubernetes de la gente de VMWARE)
           < ....
           
         Kubernetes es un gestor de gestores de contenedores para entornos de producción.
         
         
----

# De donde sale la IP de un contenedor:

- 172.17.0.3 .... De qué red se ha sacado esa IP? A qué red se ha pinchado nuestro contenedor?

    
    +-----------------------------------------------------------------------------------------------+--- red de la empresa: 192.168.0.0/16 
    |                                                                                               |   
  192.168.100.101                                                                                   |
    |            :8080 ->  172.17.0.3:80                                                       192.168.100.102                                                                                        |
  IvanPC - 172.17.0.1 ----------+---- red virtual de docker (similar a la de localhost)          MenchuPC
    |                           |           172.17.0.0/24                                           curl http://192.168.100.101:8080
  127.0.0.1 (localhost)      172.17.0.3
    |                           |
    |                           nginx:80
    |
    |
    |- Loopback (es una red virtual, que permite comunciaciones internas en una máquina)

Para que Menchu (que más adelante será el NIFI) pueda conectarse al Nginx(qué más adelante será el postgres o el kafka), hemos de configurar un NAT:
- Una redirección de puertos
 

---

En nifi, para conectar con el Postgres: CADENA JDBC
jdbc:postgres://172.31.32.23:5432/db
jdbc:oracle://172.31.32.23:5432/db
jdbc:mysql://172.31.32.23:5432/db
jdbc:mariadb://172.31.32.23:5432/db
