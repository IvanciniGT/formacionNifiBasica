// Importaciones de librerías aadicionales que necesite mi script
import java.nio.charset.StandardCharsets  // Librería que tiene JAVA donde define Juegos de caracteres
import org.apache.nifi.processor.io.InputStreamCallback // Librería que trae nifi con la que definimos la forma
                                                        // en la que obtener el contenido de un flowfile
import org.apache.nifi.processor.io.OutputStreamCallback // Librería que trae nifi con la que definimos la forma
                                                        // en la que escribir el contenido de un flowfile
//import org.apache.commons.io.IOUtils;                 // Esta es una librería llamada Apache Commons con utilidades
                                                        // para trabajar desde java... con lecturas de datos

import groovy.json.JsonSlurper                          // Una librería que trae groovy para leer JSON.
import groovy.json.JsonOutput                           // Una librería que trae groovy para escribir JSON.
 

// AQUI PUEDO DEFINIR FUNCIONES ADICIONALES QUE USE MI SCRIPT

def procesarValorDelCampoTexto(String campo){
    return campo.toLowerCase()
    // Este trámite ya depende del escenario particular... puede ser formatearlo como hexadecimal
    // Podría ser también hacer un desempaquetado de un dato formateado en COMP-3 de COBOL.
}

// Esta función (CHATGPT) convierte un COMP3 en un String en hexaxadecimal
// El problema de estas funciones es que el tipo de dato interno no le controla SQL... ni llega a NIFI
// A nivel le llega un String -> byte[]... 
def convertPackedDecimalToString(String valorDelCampo, int scale) {
    def packed = valorDelCampo.getBytes();
    def result = new StringBuilder()
    for (int i = 0; i < packed.length; i++) {
        int value = packed[i] & 0xFF
        if (i < packed.length - 1) {
            // Cada byte (excepto el último) contiene dos dígitos.
            result.append((value >> 4) & 0x0F)
            result.append(value & 0x0F)
        } else {
            // En el último byte, el nibble superior es dígito y el inferior indica el signo.
            result.append((value >> 4) & 0x0F)
            int signNibble = value & 0x0F
            // Asumimos: 0x0D = negativo; 0x0C o 0x0F = positivo.
            if (signNibble == 0x0D) {
                result.insert(0, "-")
            }
        }
    }
    // Insertar el punto decimal según el scale
    if (scale > 0 && result.length() > scale) {
        result.insert(result.length() - scale, ".")
    }
    return result.toString()
}


// AQUI DEFINO EL SCRIPT
log.info("Procesamos el flowfile que trae datos que requieren de tratamiento especial")
// 0. Sacar el siguiente documento (flowfile) de la cola de entrada.
// En estos scripts que montamos en nifi, NIFI nos regala algunas variables para jugar.
// La más importante se llama session.

def siguienteFlowFile = session.get()     // Sacando el siguiente flowfile de la cola de entrada al script
if(!siguienteFlowFile) return           // Si no hay siguiente documento, acaba sin hacer nada.

// 1. Extraer el del contenido el valor del campo llamado "CAMPO".
// Para extraer el campo, necesitamos leer el contenido... como JSON.
// Pero antes de tratarlo como JSON, necesitamos propiamente extraer el contenido del flowfile.
// El contenido me lo da NIFI

def contenidoDelFlowFile = "" // Esta variable en este caso es un String
session.read(
        siguienteFlowFile, 
        {
            InputStream canalDeLectura -> contenidoDelFlowFile = canalDeLectura.getText(StandardCharsets.UTF_8.name())
        } as InputStreamCallback
    )

log.info("Ya hemos sacado el contenido del flowfile como TEXTO en el juego de caracteres UTF-8")
log.info(contenidoDelFlowFile)
// Si el contenido del flowfile fuese CSV, me serviría lo mismo de arriba
// Pero si el contenido fuese AVRO / O PARQUET, es decir, un formato binario
/*
def contenidoDelFlowFile = null// Esta variable contendría un byte[]
session.read(
        siguienteFlowFile, 
        {
            InputStream canalDeLectura -> contenidoDelFlowFile = IOUtils.toByteArray(canalDeLectura)
        } as InputStreamCallback
    )

log.info("Ya hemos sacado el contenido del flowfile como TEXTO en el juego de caracteres UTF-8")
log.info(contenidoDelFlowFile)
*/

// Posteriormente lo interpreto como JSON(ya que es el tipo de dato que llega... podría ser AVRO)
// Lo que tengo en este caso es un texto... Para sacar el valor del campo "CAMPO", necesitamos leer ese texto como JSON
// Hay una librería en Groovy llamada JSONSlurper que nos permite hacer ese trabajo

def lectorJson = new JsonSlurper()
def objetoJSON = lectorJson.parseText(contenidoDelFlowFile)
def valorDelCampo = objetoJSON.CAMPO

// En caso de estar trabajando con AVRO... la cosa cambia mucho!

log.info("Ya tengo el valor del campo: "+ valorDelCampo)
log.info("Es de tipo: "+ valorDelCampo.getClass().getName())

// 2. Darle un trámite adecuado al valor
def valorProcesado = procesarValorDelCampoTexto(valorDelCampo)
log.info("Ya tengo el valor del campo procesado: "+ valorProcesado)
// 3. Guardarlo como: Atributo / Contenido
session.putAttribute( siguienteFlowFile, "MI_ATRIBUTO", valorProcesado) // Esto en el caso de querer usar ese valor como atributo
// Si por el contrario quiero añadirlo al JSON
objetoJSON.get(0).CAMPO_PROCESADO = valorProcesado     // Modificar el objeto JSON
def objetoJSONComoTexto = JsonOutput.toJson(objetoJSON)// Convierto ese Objeto JSON a su representación textual
// Escribir ese texto como nuevo valor del flowfile (contenido)

session.write(
        siguienteFlowFile, 
        {
            OutputStream canalDeEscritura -> 
                canalDeEscritura.write(objetoJSONComoTexto.getBytes(StandardCharsets.UTF_8))
        } as OutputStreamCallback
    )



// 4. Enrutar el nuevo flowfile que vamos a generar a un canal de salida del script
session.transfer( siguienteFlowFile, REL_SUCCESS ) // También tenemos el canal de FAILURE