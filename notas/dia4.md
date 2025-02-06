# Grupos de procesos

Pensad en un grupo de procesos como si fuera en una FUNCION que hago en un programa.
Cuando creo funciones lo hago con un objetivo:
- Reutilizar
- Organizar mejor el código
 
Si quiero reutilizar bien... claro... yo la funcion la creo a día de hoy...
Pero muy tengo muy claro cómo se usará MAÑANA?

Es decir.. en nuestro caso...
Ahora mismo nos está llegando un CSV de una linea.
Quizás mañana lleguen CSVs de muchas lineas...
O quizás lleguen CSVs de una linea por un lado..y de muchas por otro!


TEnemos un flujo principal.
En un momento dato, necesitamos convertir datos de CSV a JSON... 
    y llamo a un PROCESS GROUP encargado de esa TAREA CONCRETA
Luego queremos filtrar por edad... y quedarnos con los mayores de edad
Y luego querré hacer con el dato (con laas personas mayores de edad)



                                     FF1  
    ----> FLOWFILE ----> TRAMITE 1  ---->   WAIT ---->Otra cosa
                                             v
                                             Cache : UUID FF1 Listo
                                             ^
                   ----> TRAMITE 2  ----> NOTIFY 
    
    
    CONTROL RATE   = Evitar pasarnos en el volumen de información que mandamos en una unidad de tiempo a un procesador concreto
    WAIT / NOTIFY  = Sincronizar procesos paralelos asociados a un flowfile