# Validación de datos con acumulación de errores

## El caso de negocio

Antes de realizar una operación se deberían validar los datos de entrada para evitar errores de ejecución, problemas de seguridad, comportamientos inesperados, comprobar el formato de fechas, asegurar conversiones de tipos, etc.

Un ejemplo muy común se da en las aplicaciones que disponen de una capa [*API REST*](https://en.wikipedia.org/wiki/Representational_state_transfer). La información llega a la aplicación a través de la API en un formato, después se valida y se convierte a otro formato que la siguiente capa de aplicación pueda procesar.


## La validación como computación

De forma general la ejecución de una computación puede terminar de dos formas:
- **succeed** :white_check_mark: (con éxito)
- **failure** :x: (con fallo/s)

En el caso de la validación de datos se tiene la descripción de una computación que recibe la información que se quiere validar y que termina de la forma descrita anteriormente.

### Caso succeed
Significa que se han cumplido todas las reglas de validación aplicadas al tipo de entrada. Se devuelve el tipo de salida.

### Caso failure
Significa que al menos una regla de validación no se ha cumplido. Se devuelve una lista de errores con al menos un elemento.

Representación gráfica de la validación

![Validation effect diagram](http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/butcherless/tapir-learn/master/docs/validation-effect-diagram.puml)

## Un ejemplo de implementación

Se define una función que recibe los datos de entrada y devuelve el resultado de la validación que tiene dos canales correspondientes a los casos posibles. O bien **failure** o bien **succeed**.

`def validate(input: InputData): Validation[ValidationError,OutputData]`
 
En realidad `Validation[E,A]` es un alias para otro tipo en el que se ha omitido una lista no vacía y que es el siguiente:

`NelValidation[NonEmptyList[E],A]`

`type Validation[E,A] = NelValidation[NonEmptyList[E],A] // type alias`

De esta forma el tipo `Validation[E,A]` se hace más legible y una vez que se conoce la semántica de uso se da por supuesto que en el canal izquierdo se obtiene la lista no vacía de errores. La compilación es la misma puesto que son equivalentes.

El detalle los tipos es el siguiente:
- `InputData` contiene la información que se quiere validar
- `Validation[E,A]` contiene de forma abstracta los errores (canal izquierdo o **failure**) o el tipo de salida (canal derecho o **succeed**)
- `ValidationError` es el modelo de errores preciso para la validación. Normalmente una jerarquía que constituye un *ADT* o *Algebraic Data Type*.
- `NonEmptyList[T]` es una lista no vacía que contiene un tipo `T`. En este caso contiene la lista de errores que al menos contiene un elemento.
- `OutputData` contiene la información resultante de la validación exitosa

En resumen, después de validar nuestro tipo de entrada, `InputData`, se obtiene lo siguiente:

- **succeed**:`OutputData`, el tipo de salida, o bien
- **failure**: `NonEmptyList[ValidationError]`, la lista con al menos un error

##TODOes

- Smart constructor = new + predicate => MyDomainType
- Dependencies. Las reglas de validación pueden tener dependencias unas de otras. Fail fast, parallel

