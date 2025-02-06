
# Qué es Apache NIFI?

Es una herramienta para definir flujos de datos y procesar datos de acuerdo a esos flujos. Características:

- Interfaz web para la:
  - Definición de flujos
  - Monitoreo de flujos
- Permite trabajar on procesos en tiempo real y también en modo batch
- Opera en cluster (o no):
  - Alta disponibilidad: Capacidad para "tratar de garantizar" un determinado tiempo de servicio en un entorno de producción
  - Escalabilidad: Capacidad para "ajustar la infraestructura" y en consecuencia la capacidad de procesamiento de un sistema
- Muy orientado a la seguridad: Encriptado de datos a lo largo de su procesamiento
- Extensible. Por defecto viene configurada con un montón (+300) tipos de procesadores de trabajo.
    - Nos permite configurar (crear, diseñar) procesadores CUSTOM, escribiendo código en lenguajes de programación variados (Java, Python, Groovy, etc.)
- Gran capacidad de monitorización y gestión de errores

## Origen de NIFI

NIFI es un proyecto de la Apache Software Foundation, que nace en el año 2006 en la Agencia Nacional de Seguridad de los Estados Unidos (NSA). En 2014 se convierte en un proyecto de código abierto y en 2015 se convierte en un proyecto de la Apache Software Foundation.
- En 2016 se convierte en proyecto de primer nivel de la Apache Software Foundation.

Alrededor de NIFI hay productos que se han desarrollado para complementar su funcionalidad, como:
- Apache NIFI Registry: Sirve para gestionar versiones de flujos de datos (PROGRAMAS) en NIFI (Control de versiones basado en GIT)
---

Apache Software Foundation:
- Servidor WEB: httpd
- Servidor de aplicaciones JAVA: Tomcat
- Bigdata:
  - Hadoop
  - Spark
  - Hive
  - Kafka

---

# Conceptos importantes al trabajar con NIFI

## FlowFile

- Es la unidad de datos que se mueve a través de los flujos de datos en NIFI.
- Un flowfile está compuesto por dos partes:
  - Atributos: Metadatos que describen el contenido del flowfile.
    - Esos metadatos los podremos utilizar en el flujo.. principalmente para tomar decisiones.
  - Contenido: Los datos en sí.

## Processor

- Un programa que se ejecuta en NIFI como parte de un flujo de datos y que realiza una tarea específica.
- Hay muchos tipos de procesadores.. orientados a diferentes tipos de tareas:
  - Procesadores de entrada: Permiten leer datos de diferentes fuentes: BBDD, Kafka, HTTP, FICHEROS, etc.       EXTRACT
  - Procesadores de salida: Permiten escribir datos en diferentes destinos: BBDD, Kafka, HTTP, FICHEROS, etc.   LOAD
  - Procesadores de transformación: Permiten transformar los datos de diferentes maneras.                       TRANSFORM  
  - Procesadores de control: Permiten tomar decisiones en función de los datos que se están procesando.
  - Procesadores de enrutamiento: Permiten enviar los datos a diferentes destinos en función de ciertas condiciones.
  - Procesadores de filtrado: Permiten filtrar los datos en función de ciertas condiciones.

De por si, NIFI trae más de 300 procesadores de trabajo. Pero yo puedo crearme los míos propios... Intentaré NO HACERLO

Un flujo de procesamiento de datos en NIFI es un conjunto de procesadores conectados entre sí.
No penseís en un diagrama secuencial donde cada procesador se conecta a otro en una única dirección.

Nosotros, en NIFI, cada procesador se conecta a varios procesadores, y cada procesador puede recibir datos de varios procesadores.
Por ejemplo:
- Si un procesador tine problemas al procesar un flowfile, puede enviarlo a un procesador de error.
- Mientras que si el procesamiento ha sido correcto, puede enviarlo a un procesador de éxito.
- En ocasiones queremos enviar los datos de un procesador a varios procesadores simultáneamente.

## Connection / Relaciones entre procesadores

- Son los que definen el flujo de datos entre los procesadores.
- Los datos (Flowfiles) pueden quedar almacenados en una cola de espera (queue) entre dos procesadores. ESTO ES UNA CARACTERISTICA MUY POTENTE DE NIFI. En esas colas podremos definir distintos modelos de COLA: FIFO, LIFO, etc.

## Process Group

Agrupaciones lógicas de procesadores y conexiones. Nos permiten organizar los flujos de datos en NIFI de forma que facilitemos:
- La reutilización de esos subflujos de datos.
- La evolución/mantenimiento de los mismos de esos subflujos de datos.

---

ETL: Programas de extracción, transformación y carga de datos.
    E: Extraer datos de diferentes fuentes
    T: Transformar esos datos
    L: Cargar esos datos en diferentes destinos

Hay muchas variantes de lo que llamamos una ETL:
- ELT: Extraer, cargar y transformar
- ETLT: Extraer, transformar, cargar y transformar
- TEL: Transformar, extraer y cargar

Normalmente el concepto de ETL va muy asociado a proceso batch, pero en NIFI podemos trabajar en tiempo real.

--- 

# COLAS?

## Qué ofrecen las colas de trabajo? ASINCRONÍA en una comunicación

- Soporte a la Alta Disponibilidad
- Evitar la sobrecarga de los procesadores.
- Que no se pierdan los datos en caso de que un procesador falle.


# KAFKA

Es una herramienta de mensajería que soporta colas de mensajes. 
Es un sistema de mensajería distribuido que permite la publicación y suscripción de flujos de datos en tiempo real.

Es el whatsapp de los programas.

    IVAN                                                                        
    ------> Mando un mensaje por whatsapp -----> SERVIDOR DE WHATSAPP <----- LA MADRE DE IVAN
                                                 √                           √√


---

## FlowFile

Es la unidad fundamental de datos en NIFI. Un FlowFile representa un único objeto de datos (fragmento de información) que se mueve a través de un flujo de datos en NIFI. 

Un FlowFile consta de dos partes: 

### Contenido

- Es la carga útil, la información que se está trasportando y procesando.
- Puede ser de cualquier tipo: texto, imagen, json, xml, texto planto, binario, AVRO, etc.
- Esto se guarda en un componente de Apache NIFI llamado `Content Repository`.

### Atributos

- Son metadata que describen el contenido del FlowFile, o que simplemente asocio a ese FlowFile para poder tomar decisiones en el flujo de datos (acerca de su enrutamiento - a qué procesador lo voy a mandar-, acerca de su procesamiento - qué procesador voy a utilizar para procesarlo-).
- Estos metadatos se guardan en un componente de Apache NIFI llamado `Attribute Repository`. / `FlowFile Repository`.
  - Algunos de ellos son generados en auto. por NIFI, como el `filename`, `path`, `uuid`, `timestamp`, etc.
  - Otros los puedo generar yo mismo, como `tipo`, `propietario`, `fecha_creacion`, etc.

### Ciclo de vida de un FlowFile

A lo largo de un procesamiento, un flowfile puede pasar por diferentes estados / etapas:
- Created: Cuando se crea un FlowFile, se encuentra en este estado. Habrá un PROCESADOR que lo cree, normalmente desde una fuente de datos externa.
- In Queue: Cuando un FlowFile está en una cola de espera, se encuentra en este estado. En este estado, el FlowFile está esperando a ser procesado por un procesador.
  - Descartado / Caducado: Si un FlowFile está en una cola de espera y no se procesa en un tiempo determinado, se puede descartar: TTL (Time To Live).
- Los podremos :
  - Procesar
  - Enrutar
- Salida: Cuando un FlowFile ha sido procesado y enviado a un procesador de salida, se encuentra en este estado. Se guardará en BBDD, HTTP, Kafka, fichero...

### Monitorización de un FlowFile

Podemos monitorizar un FlowFile en NIFI, en cualquier momento, para ver su estado, su contenido, sus atributos, etc.
Hay distintos sitios/herramienats dentro de NIFI que nos permiten monitorizar un FlowFile:
- Data Provenance: Nos permite ver el historial de un FlowFile, desde que se creó hasta que se eliminó/procesó.
- Queue information: Nos permite ver el estado de una cola de espera, cuántos FlowFiles hay en ella, cuánto tiempo llevan esperando, etc.
- Bulletin Board: Nos permite ver los eventos que están ocurriendo en NIFI en tiempo real.

### Buenas prácticas al trabajar con flowfiles

- Evitar trabajar con archivos muy grandes: NIFI no está pensado para trabajar con archivos de gran tamaño.
  - NIFI prfiere trabajar con muchos archivos pequeños que con pocos archivos grandes.
- Evitar tocar mucho el contenido de un FlowFile: Dentro de lo posible, es mucho más eficiente el modificar los atributos de un FlowFile que su contenido.
- Hacer una buena configuración de TTL: Para evitar que los FlowFiles se queden en colas de espera durante mucho tiempo.
- Hay que monitorizar bien la memoria RAM... ya que los FlowFiles se guardan en memoria RAM cuando van a ser procesados.

---

## Processor

El PROCESSOR Es el componente principal de un flujo de datos en NIFI.
Un flujo constará de muchos procesadores conectados entre sí.

Esos procesadores podremos:
- Configurarlos:
  - Hay algunas opciones comunes a todos:
    - Si un procesador quiero que se ejecute en un único nodo o en varios.
    - Si un procesador puede abrir varios hilos de ejecución o no.
  - Pero luego, cada tipo de procesador tendrá sus propias opciones de configuración. Por ejemplo:
    - El procesador GETFILE, tendrá opciones de configuración para indicarle la carpeta de la que tiene que leer los ficheros.
    - El procesador PUTFILE, tendrá opciones de configuración para indicarle la carpeta en la que tiene que guardar los ficheros.

### Tipos de procesadores y ejemplos de ellos:

#### Procesadores de entrada

Obtienen datos de fuentes externas y los convierten en FlowFiles.
- GetFile: Lee ficheros de un directorio local.
- ListenHTTP: Escucha peticiones HTTP.
- ConsumeKafkaRecord: Lee mensajes de un topic de Kafka (las colas del kafka).

#### Procesadores de salida

Escriben los datos de los FlowFiles en destinos externos.
- PutFile: Escribe los FlowFiles en un directorio local.
- PutDatabaseRecord: Escribe los FlowFiles en una base de datos.
- PublishKafka: Escribe los FlowFiles en un topic de Kafka.

#### Procesadores de transformación

Modificar el contenido de los FlowFiles o sus atributos.
- ReplaceText: Reemplaza texto en el contenido de un FlowFile basado en expresiones regulares.
- EvaluateJsonPath: Evalúa una expresión JSONPath en el contenido de un FlowFile y guarda el resultado en un atributo.
- UpdateAttribute: Modifica los atributos de un FlowFile.
- TransformXml: Transforma el contenido de un FlowFile de XML a JSON o viceversa.

#### Procesadores de enrutamiento

Envían los FlowFiles a diferentes destinos en función de ciertas condiciones.

    PROCESADOR1 ----> PROCESADOR2
                ----> PROCESADOR3

        ^^^ Procesador de enrutamiento

- RouteOnAttribute: Enruta los FlowFiles en función de sus atributos.
- RouteOnContent: Enruta los FlowFiles en función de su contenido.
- ControlRate: Enruta los FlowFiles en función de la velocidad a la que llegan.

## Estados de los procesadores

Cada procesador puede estar en uno de los siguientes estados, de forma independiente a los demás procesadores:
- Stopped: El procesador está parado. No procesa flowfiles.
- Running: El procesador está en ejecución. Procesa flowfiles.
- Stopping: El procesador está parando. No procesa flowfiles.
- Disabled: El procesador está deshabilitado. No procesa flowfiles... ni tampoco puedo configurarlo.
- Invalid: El procesador está en un estado inválido. En la UI lo veré con un icono de exclamación.
            Puede deberse que un procesador esté en este estado a distintos motivos:
            - Propiedades mal configuradas o faltantes.
            - Relaciones mal configuradas o faltantes.

En la UI vamos a poder:
- Ver el estado de un procesador.
- PLAY/STOP un procesador.
- ENABLE/DISABLE un procesador.

## Configuración de un procesador

Al configurar un procesador tendremos que suministrar una serie de datos, organizados en PESTAÑAS: 5 pestañas:
- Settings: Configuración general del procesador.
  - Name: Nombre del procesador. <<<<<<< CUIDAO !!!!
  - Bulletin level: Nivel de log
  - Penalization: Tiempo que se penaliza un procesador si falla.
  - Yield duration: Tiempo que se espera antes de volver a intentar ejecutar un procesador después de que haya terminado de ejecutarse con los datos que tenía.
- Scheduling: Cómo/Cuándo se ejecuta el procesador
  - Opciones:
    - Timer driven: Se ejecuta cada X tiempo.
    - CRON driven:  Se ejecuta en determinados momentos según se defina por una expresión CRON.
    Algunos procesadores pueden tener más opciones de scheduling:
    - Event driven: Se ejecuta cuando se produce un evento.


Al final, un flujo de trabajo en NIFI tendrá entre un huevo y mogollón de PROCESADORES... Con tela de CONEXIONES entre ellos.
Aquello acaba pareciendo una tela de araña (como las de SPIDERMAN).
O tengo nombres MUY CLAROS para cada procesador o me pierdo.


---

# AVRO

Es un formato de transporte de datos (de la fundación apache).

Qué otros formatos de transporte de datos conocemos? JSON/XML
Tanto JSON como XML como AVRO llevan embebido no solo el dato, sino su estructura: CARACTERÍSTICA DE DISEÑO DE CADA UNO DE ESOS LENGUAJES. Por ejemplo, en CSV no es así... un CSV solo lleva el DATO y no su estructura.
Pero, evidentemente, el llevar embebida la estructura junto con el dato, hace que la UNIDAD DE DATO sea más pesada.

JSON/XML tienen un problemilla: SON FICHEROS/PAQUETES DE DATOS DE TEXTO, mientras que AVRO es un formato BINARIO.

Imaginad un DNI, de España. 1-8 números seguido de una letra.
Un DNI promedio ocupa 9 caracteres. Si lo guardamos en un fichero de texto, ocupará 9 bytes.

Pero... la letra de un DNI se puede calcular desde el número. Es el objetivo... es una huella del número.. que sirve para verificar que el número es correcto (Se calcula con el módulo - resto de la división entera- 23).
Cuánto ocuparía ese número guardado en binario... como bytes?
- 1 bytes cuántos números diferentes puedo representar? 256
- 2 bytes = 256 * 256 = 65.536
- 4 bytes = 256 * 256 * 256 * 256 = 4.294.967.296

## Cuánto ocupa un carácter en bytes?

Depende del juego de caracteres que estemos utilizando. 
- En ASCII un carácter ocupa 1 byte: 256 caracteres posibles: A, á, 9, $, etc.
- en UTF-8 un carácter ocupa de 1 a 4 bytes... dependiendo del caracter.
- ...

El estandar UNICODE que define todos los caracteres del mundo: +170.000 caracteres.
Ese estandar define varias formas de guardar / codificar esos caracteres:
- UTF-8: 1 a 4 bytes
  - Los básicos (los mismos que ASCII) ocupan 1 byte
  - Los que no son básicos ocupan 2 o 4 bytes: á=2bytes, (carácter chino)🤣=4bytes


Resumiendo:
- Si guardo el DNI como texto, ocupa 9 bytes.
- Si lo guardo como número, ocupa 4 bytes.
Estamos hablando de un 55% de ahorro de espacio.
... pero no sólo espacio:
- Tardaré la mitad en leerlo
- Tardaré la mitad en escribirlo
- Tardaré la mitad en transmitirlo

Si voy a mandar 1 DNI... me da igual...
Si estoy trabajando con millones de transacciones con cientos de campos... CUIDADO!

En el mundo BIGDATA, los formatos de texto(CSV, XML, JSON) no son los más eficientes. Nos interesan mucho más los formatos binarios:
- AVRO:    Internamente está optimizado para trabajar a nivel de registro/fila <--- KAFKA
- PARQUET: Internamente está optimizado para trabajar a nivel de columna       <--- Como resultado de procesar una colección de datos, lo que guardo es un fichero PARQUET

---
{
    "nombre": "Ivan",
    "edad": 47,
    "casado": true,
}
---

El campo nombre es de tipo? TEXTO, ya que en JSON, los datos entre comillas los interpretamos como texto.
El campo edad es de tipo? NÚMERO, ya que en JSON, los datos sin comillas y contienen sólo caracteres de tipo dígito y el "." los interpretamos como números.
El campo casado es de tipo? BOOLEANO, ya que en JSON, los textos true/false sin comillas los interpretamos como booleanos.

Cuánto ocupa un booleano al codificarlo en en JSON? 4 bytes: La letra t, que es un carácter, ocupa 1 byte. La letra r, que es un carácter, ocupa 1 byte. La letra u, que es un carácter, ocupa 1 byte. La letra e, que es un carácter, ocupa 1 byte.

Mientras que en AVRO, un booleano ocupa 1 bit.

--- 
### AVRO

|NOMBRE:Texto, Edad:Entero, Casado:Booleano|IVAN,47,true|
                                                     0|1
                                                 1 byte
                                                 00010000
                                            00000000 00000000 00000000 01010101

|NOMBRE:Texto, Edad:Entero, Casado:Booleano|IVAN,47,true|NOMBRE:Texto, Edad:Entero, Casado:Booleano|LUCAS,40,false|NOMBRE:Texto, Edad:Entero, Casado:Booleano|MENCHU,24,true|

### PARQUET
|NOMBRE:Texto, Edad:Entero, Casado:Booleano|IVAN,LUCAS,MENCHU|47,40,24|true,false,true|

---

# Almacenamiento en entornos de producción

Los HDD hoy en día son baratos o caros? ES LO MAS CARO EN UN ENTORNO DE PRODUCCION... CARO A RABIAR

Los HDD en un entorno empresarial son HDD caros..
Pero... no queda solo ahi.
Cuántas copias se hace de un DATO en un entorno de producción? Al menos 3.

Un TB en casa, (western blue), me cuesta 50 €
Un TB en un entorno empresarial, (western red), me cuesta 300 € por HDD... pero lo guardo en 3: 300 x 3 = 900 €
Y ahora BACKUPS: 900 x 3 = 2.700 €


---

# Qué era UNIX?

Un sistema operativo, que se hacía hasta hace 20 años por los lab BELL de la American Telephone and Telegraph Company (AT&T).
Eso dejó de hacerse.

# Qué es UNIX?

Unix hoy en día es una colección de ESTANDARES (POSIX + SUS) que definen una forma de crear sistemas operativos.
Hay muchos sistemas operativos que se crean basados en estas especificaciones:
- HP: HP-UX         (UNIX®)
- IBM: AIX          (UNIX®)
- Oracle: Solaris   (UNIX®)
- Apply: MacOS      (UNIX®)


## Linux NO ES UN SISTEMA OPERATIVO CERTIFICADO UNIX...

En el momento de su creación (HOY EN DIA NO... lleva una evolución en paralelo) se basaron en los estándares de UNIX para crear un sistema operativo.

# CRON

En POSIX, se define el concepto y la sintaxis de CRON.

Sirve para establecer periodos de tiempo en los que se ejecutan tareas.

La sintaxis básica de CRON es:
- 5 campos: minuto, hora, día del mes, mes, día de la semana
     * * * * * <- Cada minuto
     15 * * * * <- Cada hora

Luego hay una sintaxis extendida que tiene 7 campos:



PROCESADOR 1... que se ejecuta cada 5 minutos
Pero le pongo un yield duration de 4 minutos.

Al minuto 8:00 se ejecuta el procesador 1... y tarda 30 segundos en ejecutarse. Acaba a las 8:00:30
A eso se le suma el yield duration... 4 minutos... a las 8:04:30 . Hasta esa hora no volvería a ejecutarse aunque se programase la ejecución antes de esa hora. En nuestro caso, la siguiente ejecución programada es a las 8:05. Y esa hora, como es mayor que las 8:04:30, se ejecutaría.

En cambio si esa segunda ejecución tardase 2 minutos en ejecutarse.. acabaría a las 8:07:00... y al sumarle el yield duration... 4 minutos... a las 8:11:00... y la siguiente ejecución programada es a las 8:10... por lo que no se ejecutaría.
La siguiente en ejecutarse sería la de las 8:15.