    personas

    BBDD -> extraemos -> procesamiento -> KAFKA
        
    KAFKA <- extraemos -> procesamiento -> BBDD
    

# Apache KAFKA: Sistema de mensajería basado en pull. (RabbitMQ es otro sistema de mensajería... basado en push)

Kafka es el whatsapp de los programas (opensource y gratuito)
En Kafka creamos TOPICS (un tópico es un lugar donde un emisor -o varios- van mandando mensajes)... SIMILAR A UN GRUPO DE WHATSAPP
Los que mandan mensajes les llamamos PRODUCTORES.

Los que leen mensajes (que puede haber muchos) son los CONSUMERS.

Cuando un consumidor lee de un grupo de whatsapp (TOPIC), lee un dato en nombre de un GRUPO de consumidores.
Los consumidores van en grupo a leer datos.
Yo puedo tener un grupo de whatsapp al que me llegan mensajes... pedidos de zapatos.
Voy a tener solo a una persona leyendo del grupo en mi empresa (despachando pedidos)?
Si vendo poco si.. si vendo mucho, quizás no dé a basto... y tengo 2 personas... leyendo del grupo de whatsapp(TOPIC)
Claro... pero no quiero a las 2 personas leyendo los mismos mensajes.

KAFKA tiene una cosa bonita. Kafka guarda internamente la referencia de los datos que se van leyendo a nivel de cada grupo de consumidores.
ESO SE LO COME KAFKA.

De forma que las 2 personas (o 7) que tengo tramitando pedidos de los zapatos cuando leen los mensajes lo hacen como si fueran la misma persona (unidad organzativa dentro de la empresa)
Es decir, si una persona de esa unidad organizativa (grupo de consumidores: despachadores de pedidos) ya ha leido un mensaje
que kafka lo anote de forma que si otro `despachador de pedidos` pide un dato, no se le entregue el mismo.

# Comunicaciones entre personas (o programas)

    Emisor -----> Destinatario
    
            síncrona                    Llamada de teléfono. Ambos, emisor y receptor deben estar presentes en el 
                                        mismo momento del tiempo para poder realizar la comunicación.
                                            Si el destinatario no esta presente, el emisor necesita hacer un retry.
                                                Esto implica que el emisor debe reterner (recordar) el mensaje que 
                                                quiere enviar al destinatario. Y además estreso al destinatario
                híbrido especial
                Hay veces que usamos un mecanismo ASINCRONO para establecer una comunicación SINCRONA
                    No voy a tomar decisión hasta que mi madre conteste (lo que ocurre con las comunicaciones síncronas)
                    Pero puedo mandarle un mensaje asíncrono (un whatsapp) (comunicación asíncrona)

    Emisor -----> sistema de mensajería < ----- Destinatario
    
                    √                           √√
            asíncrona                   Envío de un email / Whatsapp. Ambos, emisor y receptor no tienen porque estar presentes
                                        en el mismo momento del tiempo para poder realizar la comunicación.
                                            De hecho, el emisor no manda el mensaje al receptor... 
                                                en su lugar lo manda a un sistema de mensajería
                                        Si uso un sistema de mensajería CONFIABLE ( es decir que tenga muchas probabilidades de estar operativo cuando voy a usarlo)
                                            entonces, yo (emisor) puedo mandar mi mensaje y olvidarme... El sistema de mensajería me permite olvidarme de ello.
                                            Me garantiza la entrega del mensaje tan pronto como esté disponible el destinatario.

El hecho de usar una modo u otro de comunicación no siempre es algo que puedo decidir... en ocasiones es algo imperativo.

IMAGINAD QUE VOY AL MERCADONA, hago la compra... y quiero pagar... con mi tarjeta en el tpv.
Esa comunicación debe ser síncrona o asíncrona? SINCRONA. Por qué?
    El cajero no me dejará salir de la tienda (al menos con la compra bajo el brazo) sin tener confirmación.
    Es más... él/ella no hará otra cosa hasta no recibir la confirmación (o denegación) del cargo.
    
    Qué pasa si la pasarela de pago está caída?
        Me voy sin la compra
    
IMAGINAD QUE ESTOY EN UN PEAJE, entro, hago mis kms... salgo ... y para ello, debo pagar... con mi tarjeta en el tpv.
Esa comunicación debe ser síncrona o asíncrona? ASINCRONA. Por qué?
    
    Qué pasa si la pasarela de pago está caída?
        No me dejan salir??? CUIDAO! ESO ES UN DELITO
    De hecho por eso en los peajes solo se admiten tarjetas de débito/crédito (no prepago)
    
Iván ------> mensaje ----> servidor de whatsapp - - - - - > Madre de Iván
                            √
                           servidor de whatsapp2
                           
                           servidor de whatsapp3
                           
Al mandar un mensaje yo Iván, a Whatsapp, no quiero que me dé el ok hasta que se haya guardado en todos los servidores.
    ESTO ME GARANTIZA (o mejor dicho, aumente mi confianza) en la entrega del mensaje, a costa de qué?
        - 1. que haya varios servidores a la vez... eso es pasta (y mucha) pero no para mi.. (Iván) me la pela!
        - 2. tiempo... no es lo mismo que en cuentito uno reciba el dato me dé el OK, que tener que esperar a 7.
También puedo decirle... en cuanto lo reciba uno, que me dé el ok.
    Menos garantia... más rápido...
    Confiemos en que KAFKA internamente replique el dato rápido antes de que el meteorito caiga.
También puedo decirle... en cuanto lo reciba uno, y lo haya guardado en su HDD (no vale que lo tenga en RAM) me de el OK


Los datos dentro de un TOPIC de KAFKA se guardan en PARTICIONES...
Es como si los pedidos de zapatos los voy apuntando en distintos cuadernos.
Aquí hay 2 cosas que hace el kafka:
    - Reparte los pedidos entre los cuadernos disponibles (BALANCEO / ENRUTAMIENTO).
      El mismo pedido NO ESTA EN 2 cuadernos
    - En paralelo va haciendo REPLICAS de cada cuaderno.., 
      por si un cuaderno sale ardiendo, que el pedido esté apuntado en otro.
Tengo 7 cuadernos para apuntar pedidos de zapatos...
Kafka solo me deja que un consumidor de un grupo pueda estar simultaneamente usando un cuaderno.
Si tengo un grupo con 7 consumidores, leyendo de un topic que solo tiene 4 particiones...
TENDRE a 3 consumidores de brazos cruzaos... No van a tener cuadernos diosponible.

Al crear un tópico de kafka se configura el numero de particiones que tiene ese tópico.
Eso es algo que no haremos nosotros... Eso también se puede cambiar a posteriori (con esfuerzo). 
Pero tampoco lo haremos nosotros.

Como yo intente usar más consumidores que particiones... estoy haciendo el PRINGAO!

Un dato en kafka que se guarda tiene 2 cosas:
- metadatos (internos de kafka... como por ejemplo un ID, fecha)
- clave (key)                   NIFI: Flowfile:     ATRIBUTOS
- valor (contenidor)                                CONTENIDO

Cuando escribo en KAFKA, tengo que decir lo que va en la clave... y lo que va como valor.
        Desde NIFI, como valor irá el contenido del FLOWFILE (si uso un publicador de tipo Record, elegiré el formato)
        Lo que si configuraremos es la clave

Cuando leo desde NIFI puedo hacer que:
    - El contenido del mensaje en KAFKA se convierta en el contenido del flowfile
    - Como contenido del flowfile se guarde un objeto (WRAPPER) que contenga tanto la clave como el valor guardados en kafka

Al guardar algo, damos la clave y el valor.
La clave suele ser más ligera... que sirve para la toma de decisiones.

La clave (key) NO ES UNICO en KAFKA... al menos kafka no lo exige