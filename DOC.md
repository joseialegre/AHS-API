## Capas

* Controller
* Service
* Repository
* Entity

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
La funcion ***getPacienteResponse*** es la principal funcion de la clase ***servicePaciente***

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


