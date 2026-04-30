package com.example.eventos.core

sealed class ResponseService<out T> {
    data class Success<T>(val data: T) : ResponseService<T>()
    data class Error(val error: String) : ResponseService<Nothing>()
    object Loading : ResponseService<Nothing>()
}

/*Succes recibe un objeto de tipo T, un generico que puede ser de cualquier tipo.
* Success recibe un genérico, y sale un ResponsiveService genérico.
*
* Error recibe un string con el error. Es un string porque puede ser un error de conexion, un error de autenticacion, etc
* Error, al llegar hasta la UI es mejor manejarlo como texto, no es necesario que sea algo más. Solo se mostrará el mensaje del error
*
* Loading es un objeto de tipo object porque no recibe ningun dato porque solo es un estado de carga.
* Loading no es una clase porque ocupa un inicializador, es mejor que sea un objeto. Se mostrará solo el loader para  cuando cargue la operación
*/
