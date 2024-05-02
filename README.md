La documentación está en desarrollo...
# Configuración

### Conexión a Base de Datos:
Configurar archivo ***application.properties***


`AHS-API\src\main\resources\application.properties`

```
spring.application.name=AHS-API
spring.datasource.url=jdbc:sqlserver://192.168.254.21:1433;databaseName=scnr;encrypt=true;trustServerCertificate=true
spring.datasource.username=scnr
spring.datasource.password=scnr01
spring.datasource.driverClassName=com.microsoft.sqlserver.jdbc.SQLServerDriver
```
Conexión al servidor:

`spring.datasource.url=jdbc:sqlserver://192.168.254.21:1433;databaseName=scnr;encrypt=true;trustServerCertificate=true`

Configuración de usuario:

```spring.datasource.username=scnr```

`spring.datasource.password=scnr01`

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
# Usamos una imagen de OpenJDK como base
FROM openjdk:17-jdk-alpine

# Establecemos el directorio de trabajo en /app
WORKDIR /app

# Copiamos el archivo JAR generado por Maven al contenedor
 COPY target/AHS-API-0.0.1-SNAPSHOT.jar app.jar


# Exponemos el puerto 8080 para que la aplicación Spring Boot sea accesible
EXPOSE 8080

# Comando de entrada para ejecutar la aplicación Spring Boot
CMD ["java", "-jar", "app.jar"]

# Agregar la clase principal al contenedor
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

S

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
....

### Documentation still in proccess... :)