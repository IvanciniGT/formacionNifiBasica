Estamos creando un programa asi sea meadiante una interfaz gráfica.

Un producto de software por definición es MANTENIBLE y EVOLUCIONABLE.

El hacer un programa que funcione es solo el 50% del trabajo.

    DESARROLLANDO <> PRUEBAS -> OK  -> REFACTORIZACION <> PRUEBAS -> OK

    ------------------------------     --------------------------------
          50% del trabajo                   50% del trabajo
             10 horas                          10 horas ->    DEUDA TECNICA

---

En ocasiones necesitamos hacer cosas que:
- No van intrinsecamente asociadas al flujo
- Que queremos poder reutilizar en varios sitios del flujo
- Que para facilitar el mantenimiento sacamos del flujo

Aquí entran los Controller Services del proceso.
Esos controller services son programas que puedo ejecutar de forma paralela al flujo del proceso.
Ofrecen utilidades que necesito para el flujo.

Imaginad que quiero abrir una conexión a una BBDD para hacer queries.
La quiero abrir dentro del flujo del proceso? En un momento concreto?
Podríamos pensar que si... si estuvieramos montando procesos MUY LINEALES en modo BATCH.

    ---> LEO FICHERO ---> TRANSFORMO DATOS ---> ABRO CONEXION A BBDD ---> ESCRIBO A BBDD ---> CIERRO CONEXION

Hay flujos que pueden necesitar usar la BBDD en distintos momentos.
Puede ser que esté en un proceso EN TIEMPO REAL, donde cuando me llega un dato no voy a abrir una conexion para escribir ese dato y luego la cierro.
Me llevo más tiempo abriendo la conexión que escribiendo... vaya ruina. Y pa'l siguiente dato lo mismo otra vez. NO TIENE SENTIDO.

Prefiero tener una (o varias) conexiones a BBDD preestablecidas... que usaré en mi proceso (en uno o distintos momentos).

Este tipo de cosas es lo que configuramos en los CONTROLLER SERVICES.

Tengo unos datos que leo en CSV -> JSON.

Puede ser que esa transformación la necesite en varios momentos... o desde varias fuentes.
Puedo definir un programa de transformación de datos.
Ese programa necesitará unos datos de origen... y el formato de destino

         PROGRAMA LECTOR                                                            PROGRAMA ESCRITOR
           ----------->                                                            ------------>
    DATOS ORIGEN ---> ENTIDAD (Ente abstracto que yo defino... los datos en si mismos) ----> FORMATO DESTINO
     FORMATO.           Los datos son independientes del formato de esos datos

PERSONA (YO) < --- ENTIDAD
        Me llamo Iván (nombre) y tengo 47 años (edad).

El YO representado en JSON:

    {                                   {
        "nombre: "Ivan",                    "datosCiviles": {
        "edad", 47                              "nombre": "Ivan"
    }                                       },
                                            "datosBiologicos": {
                                                "edad": 47
                                            }
                                        }

    {"nombre: "Ivan", "edad": 47}

O el YO representado en csv:

    nombre,edad
    Ivan, 47

O El YO representado en YAML

    nombre: Ivan
    edad:   47
    
En el mundo de los formatos de datos (XML, JSON, AVRO...) existe el concepto de SCHEMA

    XML Schema
    
    
Los controller services no se ejecutan, como si ocurre con los processors...
Los controller services se habilitan o deshabilitan... igual que los processors.
La diferencia es que al habilitar un controller service se queda ejecutándose en automático en segundo plano.. 
listo para ofrecer SERVICIO a quién lo necesite (nuestros processors)


Si mi CSV tuviera cabecera, le podría pedir al LECTOR de CSVs que infiera la estructura de datos (el esquema... los campos)

    nombre,edad
    Ivan, 47
    
        Una persona con:
            nombre: Iván
            edad: 47
Pero no tengo cabecera.. mi CSV es así:

    Ivan, 47

El lector puede leer los datos... pero sabe cómo se llama cada campo?
En un escenario como este, no podemos pedir que se infiera el esquema!


Una vez leídos los datos... si lo que he leído ya sñé que es el nombre y la edad, 
el programa que formatea a JSON puede usar esa información para generar el JSON:

    {"nombre: "Ivan", "edad": 47}   ES DECIR, EL ESCRITOR PUEDE INFERIR EL ESQUEMA de los datos.
                                    SERA UN ESQUEMA MUY BASICO... muy simplón... pero bueno.. posiblemente me sirve.

Necesitamos definir un esquema que asociar al lector... para que el lector ENTIENDA los datos que está leyendo.


DATO -> FILTRE EL DATO POR ALGO QUE VENGA EN EL CONTENIDO
            ^^^^
            
        Aquí estamos haciendo muchas cosas :
            - Extraer el dato
            - Filtrar por él
            
DATO -> EXTRAIGO ATRIBUTO -> FILTRE EL DATO POR EL ATRIBUTO
            ^^^^                        ^^^^^
            Extraigo el dato            Aplico el filtro
            
    Beneficios de la segunda opción:
    - Descompongo el problema en problemas más pequeños.
        PRINCIPIO: SOC ( Separation of Concerns )
        
No obstante, hay momentos en los que me puede interesar filtrar en base al contenido... Casos tantos hay como situaciones en la vida.

    LOGS -> Voy a procesar solo los que contengan la palabra ERROR
    
    Este es un caso típico donde filtro por contenido y no me complido.
    
---

## RouteOnAttribute

Se usa para varias cosas:
- Por un lado, puedo usarlo para FILTRAR!
- Pero también puedo usarlo para asignar trámites diferentes a flowfiles (datos) con disntitas necesidades de tramitación.

Ese procesador lo que genera son múltiples tipos de conexión.
Hasta ahora nos hemos encontrado con procesadores que generan siempre los mismos tipois de conexión FIJOS!!!:
- success
- failure
- matched
- unmatched
Cada uno traía los suyos... pero... eran fijos lo que trae.

El routeOnAttribute lo que hace es GENERAR NUEVOS TIPOS DE CONEXION SEGUN MIS ESPECIFICACIONES.
Cada tipo de conexión la defino como una propiedad (en la pestaña de properties del processor).
- El nombre de la propiedad es el nombre del TIPO DE CONEXION que voy a generar
- El valor de la propiedad es una expresión NIFI booleana que determina si un flowfile debe enrutarse a ese TIPO DE CONEXION


Si pongo solo una ruta... y para el caso UNMATCHED (que es el que viene por defecto) no pongo nada (TERMINATE)
Lo que tengo es un FILTRO

Si pongo varias rutas, cada una con una expresión, lo que tengo es un ENRUTADOR 
(envía los flofiles-datos- a distintos procesadores en base a los criterios especificados en cada TIPO DE CONEXION)