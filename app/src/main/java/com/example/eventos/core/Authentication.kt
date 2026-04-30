package com.example.eventos.core

import com.google.firebase.auth.FirebaseUser

/*
 * Este archivo es como una "lista de tareas" que debe cumplir el sistema de usuarios.
 * 
 * Sirve para organizar cómo vamos a entrar (login) o crear cuentas (registro).
 * Al ser una 'interface', solo anotamos qué funciones necesitamos, pero el código 
 * real que habla con Firebase se escribe en otro lugar.
 */

interface Authentication {
    
    /*
     * Función para entrar a la aplicación (Login).
     * @param email El correo que escribe el usuario.
     * @param password La contraseña que escribe el usuario.
     * 
     * Nota: Es 'suspend' porque internet puede tardar un poco y no queremos que la app se trabe.
     */
    suspend fun requestLogin(email: String, password: String): ResponseService<FirebaseUser>

    /*
     * Función para crear una cuenta nueva (Registro).
     * @param email El correo nuevo.
     * @param password La contraseña nueva.
     * 
     * Devuelve una "respuesta" que nos dirá si el usuario se creó bien o si hubo un error.
     */
    suspend fun requestSignUp(email: String, password: String): ResponseService<FirebaseUser>
}

/* Acá lo cambiamos a ResponseService. Necesitamos poner que tipo de dato debe regresarle si es success, como estábamos usando FirebaseUser ese será nuestro tipo.
Ya no es "?" porque no es opcional porque si es exitoso a fuerzas debe mandar el tipo
*/
