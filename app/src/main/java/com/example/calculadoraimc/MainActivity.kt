package com.example.calculadoraimc

import android.os.Bundle
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import java.util.Locale

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

// Nav Host
@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "Ingreso_datos"
    ) {

        // Pantalla 1: Ingreso de datos
        composable(route = "Ingreso_datos") {
            PantallaIngreso(navController = navController)
        }

        // Pantalla 2: Resultados con parámetros
        composable(
            route = "resultados/{nombre}/{imc}",
            arguments = listOf(
                navArgument(name = "nombre") {
                    type = NavType.StringType
                },
                navArgument(name = "imc") {
                    type = NavType.FloatType
                }
            )
        ) { backStackEntry ->

            val nombre =
                backStackEntry.arguments?.getString("nombre") ?: "usuario"

            val imc =
                backStackEntry.arguments?.getFloat("imc") ?: 0f

            PantallaResultado(
                navController = navController,
                nombre = nombre,
                imc = imc
            )
        }
    }
}

// Pantalla 1: Ingreso de datos y Validación
@Composable
fun PantallaIngreso(navController: NavController) {

    var nombre by remember { mutableStateOf("") }
    var peso by remember { mutableStateOf("") }
    var altura by remember { mutableStateOf("") }
    var errorMensaje by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Calculadora de IMC",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Campo para el nombre
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = {
                Text("Nombre")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo para el peso
        OutlinedTextField(
            value = peso,
            onValueChange = { peso = it },
            label = {
                Text("Peso (kg)")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo para la altura
        OutlinedTextField(
            value = altura,
            onValueChange = { altura = it },
            label = {
                Text("Altura (m)")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Mensaje de validación
        if (errorMensaje.isNotEmpty()) {

            Text(
                text = errorMensaje,
                color = Color.Red,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón calcular
        Button(
            onClick = {

                val pesoFloat = peso.toFloatOrNull()
                val alturaFloat = altura.toFloatOrNull()

                // Validación
                if (
                    nombre.isBlank() ||
                    pesoFloat == null ||
                    alturaFloat == null ||
                    pesoFloat <= 0 ||
                    alturaFloat <= 0
                ) {

                    errorMensaje = "Ingrese valores válidos"

                } else {

                    errorMensaje = ""

                    // Cálculo IMC
                    val imcCalculado =
                        pesoFloat / (alturaFloat * alturaFloat)

                    // Codificar nombre
                    val nombreCodificado = URLEncoder.encode(
                        nombre,
                        StandardCharsets.UTF_8.toString()
                    )

                    // Navegación con múltiples parámetros
                    navController.navigate(
                        route = "resultados/$nombreCodificado/$imcCalculado"
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {

            Text(
                text = "Calcular",
                fontSize = 18.sp
            )
        }
    }
}

// Pantalla 2
@Composable
fun PantallaResultado(
    navController: NavController,
    nombre: String,
    imc: Float
) {

    val (categoria, colorCategoria) = when {

        imc < 18.5f ->
            Pair("Bajo peso", Color.Blue)

        imc in 18.5f..<25.0f -> {
            Pair("Peso normal", Color.Green)
        }

        imc in 25.0f..29.9f -> {
            Pair("Sobrepeso", Color.Yellow)
        }

        else ->
            Pair("Obesidad", Color.Red)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Hola $nombre, tu resultado es:",
            fontSize = 22.sp,
            fontWeight = FontWeight.Normal
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Mostrar IMC
        Text(
            text = String.format(Locale.US, "%.1f", imc),
            fontSize = 64.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Categoría IMC
        Text(
            text = categoria,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = colorCategoria
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Botón volver
        OutlinedButton(
            onClick = {
                navController.popBackStack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {

            Text(
                text = "Volver",
                fontSize = 18.sp
            )
        }
    }
}
