## Capas

* Controller
* Service
* Repository
* Entity

### Controller
    
#### PacienteController
  * Esta interfaz es la capa exterior de la aplicacion, se encarga de interactuar
con otras aplicaciones, recibir y enviar los datos.

La funcion "BuscarPorNumeroDocumento" llama a un procedimiento almacenado en la Base de Datos, en el cual nosotros
pasamos como parametro el un numero de documento y la Funcion nos devuelve una lista de Pacientes.

```
@Procedure
List<Paciente> BuscarPorNumeroDocumento(int numerodocumento);
```
La funcion "InstanciarPaciente" llama a un procedimiento almacenado en la Base de Datos, donde pasamos como parametro
todos los datos del paciente que necesitamos para instanciarlo.
```
@Procedure
void InstanciarPaciente(String apellido, String nombre, int numerodocumento, int numerotramite,
                        char ejemplar, Timestamp fechaemision, Timestamp fechavto,
                        int idtipodoc, char sexo, Timestamp fechanac);
```

