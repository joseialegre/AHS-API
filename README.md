# Configuración

### Conexión a Base de Datos:
Configurar archivo ***application.properties***


`AHS-API\src\main\resources\application.properties`

```
spring.application.name=AHS-API
spring.datasource.url=jdbc:sqlserver://"databaseip";databaseName="dbname";encrypt=true;trustServerCertificate=true
spring.datasource.username="user"
spring.datasource.password="password"
spring.datasource.driverClassName=com.microsoft.sqlserver.jdbc.SQLServerDriver
```
Conexión al servidor:

`spring.datasource.url=jdbc:sqlserver://"databaseip";databaseName=scnr;encrypt=true;trustServerCertificate=true`

Configuración de usuario:

```spring.datasource.username="user"```

`spring.datasource.password="password"`

### Conexión del Cliente a la API:

#### Archivo ***scanner.js***
Función que conecta el cliente con la API.

Es necesario tener un input en el archivo HTML para pasarlo como parametro
una vez que llamamos a la función.

En la variable ***api-ip*** colocamos la IP y puerto donde la API está hosteada. Por defecto:
`http://192.168.254.240:3000`

### Request and Response

#### Request:
```
{
    "barcodeData": "00123456789@APELLIDOS@NOMBRES@S@12345678@E@01/01/2000@01/01/2015@123"
}
```
#### Response:
```
{
    "apellido":"APELLIDOS",
    "nombre":"NOMBRES",
    "numerodocumento":"12345678",
    "numerotramite":"00123456789",
    "ejemplar":"E",
    "fechaemision":"",
    "fechavto":"01/01/2015",
    "idtipodoc":"3"
    "sexo":S
    "fechanac":"01/01/2000"
    "registrado":"EL PACIENTE FUE REGISTRADO"
}
```

# Deploy

### Compilar aplicación de Java:

    mvn clean
    mvn clean package

### Crear archivo Dockerfile
```
- Usamos una imagen de OpenJDK como base
FROM openjdk:17-jdk-alpine

- Establecemos el directorio de trabajo en /app
WORKDIR /app

- Copiamos el archivo JAR generado por Maven al contenedor
 COPY target/AHS-API-0.0.1-SNAPSHOT.jar app.jar


- Exponemos el puerto 8080 para que la aplicación Spring Boot sea accesible
EXPOSE 8080

- Comando de entrada para ejecutar la aplicación Spring Boot
CMD ["java", "-jar", "app.jar"]

- Agregar la clase principal al contenedor
COPY src/main/java/com/example/AHSAPI/AhsApiApplication.java /app/src/main/java/com/example/AHSAPI/
```

### Crear la imagen docker:
    docker build -t ahs-api .

### Ejecutar el container: (en caso de prueba)
    docker run -p 8080:8080 ahs-api

### Para subir la imagen al repositorio:

    docker tag "nombre-de-la-imagen" joseialegre/ahs-api:latest
    docker image push joseialegre/ahs-api:latest

# Código

## Capas

* Controller
* Service
* Repository
* Entity

### Controller

#### PacienteController
* Esta clase es la mas superficial en la arquitectura. Es la que establece el End-Point

##### barcodeScanRequest()
```
@PostMapping()
public PacienteResponse barcodeScanRequest(@RequestBody BarcodeRequest barcodeRequest){

    return service.getPacienteResponse(barcodeRequest);
}
```
Esta funcion es lo primero que se ejecuta en la API. Llamando al servicio y devolviendo los resultados.


### Repository

#### PacienteRepository
* Esta interfaz es la capa que se encarga de interactuar
con la Base de Datos, en este caso, llamar a procedimientos almacenados.

##### <a name="BuscarPorNumeroDocumento"></a>BuscarPorNumeroDocumento()
La función ***BuscarPorNumeroDocumento*** llama a un procedimiento almacenado en la Base de Datos, en el cual nosotros
pasamos como parametro el un numero de documento y la Funcion nos devuelve una lista de Pacientes.

```
@Procedure
List<Paciente> BuscarPorNumeroDocumento(int numerodocumento);
```
##### <a name="instanciarPaciente"></a>instanciarPaciente()
La funcion ***instanciarPaciente*** llama a un procedimiento almacenado en la Base de Datos, donde pasamos como parametro
todos los datos del paciente que necesitamos para instanciarlo.
```
@Procedure
void InstanciarPaciente(String apellido, String nombre, int numerodocumento, int numerotramite,
                        char ejemplar, Timestamp fechaemision, Timestamp fechavto,
                        int idtipodoc, char sexo, Timestamp fechanac);
```

### Service

#### PacienteService
* En esta clase es donde se desarrolla gran parte de la lógica de la aplicacion. Esta clase
tiene la meta de servir a la "lógica de negocio".

##### getPacientesByNumeroDocumento()
La funcion ***getPacientesByNumeroDocumento*** llama a [BuscarPorNumeroDocumento](#a-namebuscarpornumerodocumentoabuscarpornumerodocumento),
almacena la lista de pacientes y la retorna
```
public List<Paciente> getPacientesByNumeroDocumento(int numerodocumento){
List<Paciente> pacientes = pacienteRepository.BuscarPorNumeroDocumento(numerodocumento);
return pacientes;
}
```

##### setPaciente()
Este metodo ***setPaciente*** recibe como parametro todos los datos del paciente y
llama a [instanciarPaciente](#a-nameinstanciarpacienteainstanciarpaciente)


```
public void setPaciente(String apellido, String nombre, int numerodocumento, int numerotramite,
                            char ejemplar, Timestamp fechaemision, Timestamp fechavto,
                            int idtipodoc, char sexo, Timestamp fechanac){
        pacienteRepository.InstanciarPaciente(apellido, nombre, numerodocumento, numerotramite,
                ejemplar, fechaemision, fechavto, idtipodoc, sexo, fechanac);
    }
```
##### getPacienteResponse()
La función ***getPacienteResponse*** es la principal función de la clase ***servicePaciente***

```
@Transactional
    public PacienteResponse getPacienteResponse(BarcodeRequest barcodeRequest){


        PacienteResponse pacienteResponse = barcodeRequest.barcodeToPaciente();

        if(pacienteResponse.getNumerodocumento() == 0){
            //Error 404 input invalido
            pacienteResponse.setRegistrado("Datos invalidos");
            return pacienteResponse;
        }
        try {
            List<Paciente> pacientes = getPacientesByNumeroDocumento(pacienteResponse.getNumerodocumento());
            if(pacientes.size()!=0){
                pacienteTreatment(pacientes,pacienteResponse);
            }else{
                instanciarPaciente(pacienteResponse);
            }
        }catch (Exception e){
            pacienteResponse.setRegistrado(e.toString());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return pacienteResponse;
        }
        return pacienteResponse;
    }
```

#### pacienteTreatment()
La función ***pacienteTreatment*** controla la lista de pacientes que la Base de Datos nos devuelve.

Controla si la lista esta vacia, en caso de que no lo esté busca los datos mas recientes. Si la lista está
vacia, el paciente se instancia llamando a la función ***instanciarPaciente()***.

La variable ***indexTemp*** ayuda a encontrar el registro mas reciente para luego compararlo con el ingresado.

```
public PacienteResponse pacienteTreatment(List<Paciente> pacientes, PacienteResponse pacienteResponse){
        int indexTemp=-1;
        for(int i=0;i<pacientes.size();i++){
            if(pacientes.get(i).getNumerotramite()==pacienteResponse.getNumerotramite()){
                //es el mismo numerotramite. no hago nada. muestro los datos guardados
                pacienteResponse.setRegistrado("PACIENTE ESTA ACTUALIZADO");
                return pacienteResponse;
            }else{
                if(indexTemp==-1){
                    indexTemp=i;
                }
                else{
                    if(TimestampComparison(pacientes.get(i).getFechaemision(),pacientes.get(indexTemp).getFechaemision())){
                        indexTemp=i;
                    }
                }
            }
        }
        if(indexTemp!=-1){
            if(TimestampComparison(pacienteResponse.getFechaemision(),pacientes.get(indexTemp).getFechaemision())){
                //insertar paciente
                return instanciarPaciente(pacienteResponse);
            }else{

                return pacienteMapping(pacientes,pacienteResponse,indexTemp);
            }
        }

        return pacienteResponse;
    }

```

#### TimestampComparison()
***TimestampComparison()*** es una función para comparar dos variables que sean de tipo Timestamp
Retorna **True** cuando *a* es mas reciente que *b*

```
public boolean TimestampComparison(Timestamp a,Timestamp b){
        int x = a.compareTo(b);
        if(x>0) return true;
        else return false;
    }
```

#### pacienteMapping()
La función ***pacienteMapping*** mapea los datos del paciente que estan en la lista segun indexTemp, a nuestro JSON 
de respuesta, la clase ***pacienteResponse***

```
public PacienteResponse pacienteMapping(List<Paciente> pacientes, PacienteResponse pacienteResponse, int indexTemp){
        pacienteResponse.setNombre(pacientes.get(indexTemp).getNombre());
        pacienteResponse.setApellido(pacientes.get(indexTemp).getApellido());
        pacienteResponse.setNumerodocumento(pacientes.get(indexTemp).getNumerodocumento());
        pacienteResponse.setNumerotramite(pacientes.get(indexTemp).getNumerotramite());
        pacienteResponse.setEjemplar(pacientes.get(indexTemp).getEjemplar());
        pacienteResponse.setFechaemision(pacientes.get(indexTemp).getFechaemision());
        pacienteResponse.setFechavto(pacientes.get(indexTemp).getFechavto());
        pacienteResponse.setIdtipodoc(pacientes.get(indexTemp).getIdtipodoc());
        pacienteResponse.setSexo(pacientes.get(indexTemp).getSexo());
        pacienteResponse.setFechanac(pacientes.get(indexTemp).getFechanac());
        pacienteResponse.setRegistrado("DATOS MAS RECIENTES");
        return pacienteResponse;
    }
```

### DTO
* En este directorio se encuentran clases de utilidad
#### BarcodeRequest
En esta clase procesamos el String que recibimos del FrontEnd y lo convertimos en informacion del paciente
```
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class BarcodeRequest {
    private String barcodeData;
    // este es el JSON
    public PacienteResponse barcodeToPaciente() {
        PacienteResponse pacienteResponse = new PacienteResponse();
        String[] parts;

        if (barcodeData.startsWith("0")) { // Formato 1
            parts = barcodeData.split("@|\"");
            if (parts.length >= 8) {
                try {
                    pacienteResponse.setNumerodocumento(Integer.parseInt(parts[4]));
                    pacienteResponse.setApellido(parts[1]);
                    pacienteResponse.setNombre(parts[2]);
                    pacienteResponse.setNumerotramite(Integer.parseInt(parts[0]));
                    pacienteResponse.setSexo(parts[3].charAt(0));
                    pacienteResponse.setFechanac(stringToDate(parts[6]));
                    pacienteResponse.setIdtipodoc(3);
                    pacienteResponse.setFechaemision(stringToDate(parts[7]));
                    pacienteResponse.setEjemplar(parts[5].charAt(0));

                } catch (Exception e) {
                    pacienteResponse.setNumerodocumento(0);
                    pacienteResponse.setRegistrado(e.toString());
                }
            }
        } else if (barcodeData.startsWith("@") || barcodeData.startsWith("\"")) {
            String barcodeData2 = barcodeData.replaceAll("\"", "@");// Formato 2
            System.out.println(barcodeData2.toString());
            parts = barcodeData2.split("@");
            if (parts.length >= 6) {
                try {
                    pacienteResponse.setNumerodocumento(Integer.parseInt(parts[1].trim()));
                    pacienteResponse.setApellido(parts[4]);
                    pacienteResponse.setNombre(parts[5]);
                    pacienteResponse.setEjemplar(parts[2].charAt(0));
                    pacienteResponse.setFechanac(stringToDate(parts[7]));
                    pacienteResponse.setSexo(parts[8].charAt(0));
                    pacienteResponse.setFechaemision(stringToDate(parts[9]));
                    pacienteResponse.setNumerotramite(Integer.parseInt(parts[10]));
                    pacienteResponse.setFechavto(stringToDate(parts[12]));
                    pacienteResponse.setIdtipodoc(3);
                } catch (Exception e) {
                    pacienteResponse.setError(e.toString());
                }
            }
        }
        return pacienteResponse;
    }

    private Timestamp stringToDate(String string){
        String dateString = string;

        try {
            // Parseamos el string a un objeto LocalDate
            LocalDate localDate = LocalDate.parse(dateString, java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            Timestamp timestamp = Timestamp.valueOf(localDate.atStartOfDay());
            return timestamp;
        }catch (Exception e ){
            LocalDate localDate = LocalDate.parse(dateString, java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            Timestamp timestamp = Timestamp.valueOf(localDate.atStartOfDay());
            return timestamp;
        }
        
    }

}
```

#### PacienteResponse
* Esta clase representa los datos obtenidos del documento, para luego representarlo en un formato JSON
```
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PacienteResponse {

    private String apellido;
    private String nombre;
    private int numerodocumento;
    private int numerotramite;
    private char ejemplar;
    private Timestamp fechaemision;
    private Timestamp fechavto;
    private int idtipodoc;
    private char sexo;
    private Timestamp fechanac;
    private String registrado;
    private String error;
}
```

### Entity
* Este directorio es donde definimos las entidades del proyecto. En este caso, el paciente.

#### Paciente
* Esta clase relaciona los datos obtenidos desde la Base de Datos para representar al paciente.

```
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pacscan")
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idpaciente")
    private int idpaciente;
    private String apellido;
    private String nombre;
    private int idtipodoc; //3
    private int numerodocumento;
    private Timestamp fechanac;
    private char sexo;
    private int numerotramite;
    private char ejemplar;
    private Timestamp fechaemision;
    private Timestamp fechavto;
    
}
```
