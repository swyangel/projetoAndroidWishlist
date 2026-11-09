package com.example.listadedesejosapp

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import java.io.Serializable

data class DesejoItem(
    val nome: String,
    val preco: String,
    val link: String,
    val isComprado: Boolean = false
) : Serializable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val sistemaEscuro = isSystemInDarkTheme()
    var isDarkTheme by rememberSaveable { mutableStateOf(sistemaEscuro) }

    MaterialTheme(
        colorScheme = if (isDarkTheme) darkColorScheme() else lightColorScheme()
    ) {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val rotaAtual = navBackStackEntry?.destination?.route

        var listaDesejos by rememberSaveable { mutableStateOf(listOf<DesejoItem>()) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Minha Wishlist") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    )
                )
            },
            bottomBar = {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceVariant) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.List, contentDescription = "Lista") },
                        label = { Text("Desejos") },
                        selected = rotaAtual == "home",
                        onClick = { if (rotaAtual != "home") navController.navigate("home") }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                        label = { Text("Perfil") },
                        selected = rotaAtual == "perfil",
                        onClick = { if (rotaAtual != "perfil") navController.navigate("perfil") }
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("home") {
                    WishlistScreen(
                        listaDesejos = listaDesejos,
                        onAdicionarItem = { novoItem -> listaDesejos = listaDesejos + novoItem },
                        onNavigateToDetails = { index -> navController.navigate("detalhes/$index") },
                        onToggleComprado = { index ->
                            val listaAtualizada = listaDesejos.toMutableList()
                            val itemAntigo = listaAtualizada[index]
                            listaAtualizada[index] = itemAntigo.copy(isComprado = !itemAntigo.isComprado)
                            listaDesejos = listaAtualizada
                        }
                    )
                }
                composable("detalhes/{index}") { backStackEntry ->
                    val indexStr = backStackEntry.arguments?.getString("index") ?: "0"
                    val index = indexStr.toIntOrNull() ?: 0
                    val item = listaDesejos.getOrNull(index)

                    if (item != null) {
                        DetalhesScreen(
                            itemNome = item.nome,
                            itemLink = item.link,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
                composable("perfil") {
                    val totalComprados = listaDesejos.count { it.isComprado }
                    PerfilScreen(
                        totalDesejos = listaDesejos.size,
                        totalComprados = totalComprados,
                        isDarkTheme = isDarkTheme,
                        onThemeChange = { isDarkTheme = it }
                    )
                }
            }
        }
    }
}