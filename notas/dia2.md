
# Apache Nifi

Herramienta para el procesamiento de datos:
- Consola web para configurar los procesos
- Permite operar en cluster:
    - Alta disponibilidad
    - Escalabilidad
- Gran configuración relativa a la seguridad
- Ofrece amplias capacidaddes de monitorización

## Conceptos claves:

### FlowFile

Es la unidad de información en Apache Nifi:
- Atributos
- Contenido

### Procesadores

Son los programas que trabajan sobre esos datos:
- Obtener datos (crear flowfiles)
- Transformar datos
- Guardar datos (en distintos destinos)
- Filtrar
- ...

Apache Nifi trae montonon de ellos.
PERO ADEMAS permite que creemos los nuestros propios... programado en JAVA, PYTHON, GROOVY

### Conectores

Las relaciones de flujo de datos entre Procesadores

---

Vamos a hacer la primera instalación. DOCKER (contenedores)

Copiamos el archivo docker-compose.yaml a una carpeta y en esa carpeta ejecutamos `docker compose up`
Para desinstalar `docker compose down`

Una vez instalado, debería abrir un navegador de internet y poner en la url: http://localhost:8080/nifi
PERO ESO SOLO FUNCIONARIA SI LO HE INSTALADO EN MI MAQUINA.

---

Relationships... configuración.

Cada procesador podrá ir unido a otros procesadores.... o no... dependiendo de su naturaleza y de nuestro caso de uso.

Hay procesadores que requieren relaciones de entrada (Upstream Connectors)... de una fuente de datos (flowfiles)

Hay procesadores que pueden mandanr datos a otros procesadores (Relationships).

Adicionalmente, en esos Relationships (FLECHAS DE SALIDA) podemos confgurar opciones adicionales... por ejemplo:
- El procesador que estoy configurando puede ejecutar su tarea y que esa tarea acabe SUCCESS (con éxito)
- Pero puede que al hacer su tarea se produzca un error.

Por ejemplo, podemos configurar que si ha habido un error:
- lo reintente de nuevo más tarde
- Aborte el trabajo (que descarte el flowfile)

---

# Regular expressions
Se basan en el concepto de patrón.

## Qué es un PATRON?

Un patrón lo definimos como un conjunto de subpatrones.

## Qué es un SUBPATRON?

Una secuencia de caracteres, seguida de un modificador de cantidad

Secuencia de caracteres                 Se interpreta como?
    hola                                    literalmente `hola`
    [hola]                                  un caracter entre h, o, l, a
    [a-z]                                   cualquier caracter entre la a y la z... pasando por la b, c... l ...
    [a-zA-Z]
    [0-57]                                  Cualquier caracter de 0,1,2,3,4,5,7
    [a-zñ]
    [a-zA-Z0-9ÁÑñ-]                         El guión va al final si se debe interpretar literalmente
    .                                       Cualquier caracter
    \w                                      equivalente a : [a-zA-Z0-9]
    \W                                      Cualquiera que no sea de los de arriba
    \d                                      equivalente a [0-9]
    \D                                      Cualquiera que no sea de los de arriba
    
Modificador de cantidad
    NADA                                    La secuencia anterior debe aparecer 1 sola vez                      1 vez
    ?                                       La secuencia anterior puede o no aparecer                           0-1 vez
    +                                       La secuencia anterior debe aparecer al menos 1 vez                  1-INFINITO
    *                                       La secuencia anterior puede no aparecer o aparecer muchas veces     0-INFINITO
    {3}                                     La secuencia anterior debe aparecer 3 veces
    {3,8}
    
Esos subpatrones puedes:
- Concatenarlos... escribiendo uno detrás del otro (en cuyo caso se aplican SECUENCIALMENTE)
- Separarlos por una |      en cuyo caso se aplican como opción... uno u otro.
- Agruparlos con parentesis (como las cuentas matemáticas 3+(4*8))

.+  ,.+
Ivan,47

  Segundo subpatron    
  -
.+,.+
-- --
^  TERCER SUBPATRON
PRIMER SUBPATRON

---

Quiero que en nuestro ejemplo de proceso, se genere un archivo dentro de la carpeta:
/tmp/nifi/generados/NOMBRE DE LA PERSONA/EDAD DE LA PERSONA.json

1 - Vamos a necesitar tener un atributo con la edad de la persona... pero no un atributo cualquiera: 
    Necesito el atributo filename con el valor de la edad de la persona
2 - Necesitamos generar un atributo nuevo con el nombre de la persona (para usarlo en el directorio)
---

Apache NIFI me permite usar EXPRESIONES en muchos sitios... entre ellos, en las propiedades de los procesadores.

${<atributo>:<funcion>(<argumentos>)}
${now()}
${filename}
${uuid()}