package com.example.listadedesejosapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

@Composable
fun WishlistScreen(
    listaDesejos: List<DesejoItem>,
    onAdicionarItem: (DesejoItem) -> Unit,
    onNavigateToDetails: (Int) -> Unit,
    onToggleComprado: (Int) -> Unit // Função que avisa a navegação para marcar o item
) {
    var nomeItem by rememberSaveable { mutableStateOf("") }
    var precoItem by rememberSaveable { mutableStateOf("") }
    var linkLoja by rememberSaveable { mutableStateOf("") }

    val erroNome = if (nomeItem.isNotEmpty() && nomeItem.length < 3) "Nome muito curto" else null
    val isFormValid = nomeItem.isNotBlank() && precoItem.isNotBlank() && linkLoja.isNotBlank() && erroNome == null

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Adicionar Novo Desejo", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)

        OutlinedTextField(
            value = nomeItem, onValueChange = { nomeItem = it },
            label = { Text("Nome do Produto") },
            isError = erroNome != null, supportingText = { if (erroNome != null) Text(erroNome) },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = precoItem, onValueChange = { precoItem = it },
            label = { Text("Preço (R$)") }, modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        OutlinedTextField(
            value = linkLoja, onValueChange = { linkLoja = it },
            label = { Text("Link da Loja (ex: amazon.com/...)") }, modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
        )

        Button(
            onClick = {
                onAdicionarItem(DesejoItem(nomeItem, precoItem, linkLoja))
                nomeItem = ""; precoItem = ""; linkLoja = ""
            },
            enabled = isFormValid, modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 48.dp)
        ) {
            Text("Adicionar à Wishlist")
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        if (listaDesejos.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                Spacer(modifier = Modifier.height(16.dp))
                Text("A sua wishlist está vazia.", style = MaterialTheme.typography.titleMedium)
                Text("Adicione produtos para começar.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                itemsIndexed(listaDesejos) { index, item ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { onNavigateToDetails(index) },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.nome,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    textDecoration = if (item.isComprado) TextDecoration.LineThrough else TextDecoration.None
                                )
                                Text(text = "R$ ${item.preco}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                            }

                            Checkbox(
                                checked = item.isComprado,
                                onCheckedChange = { onToggleComprado(index) }
                            )
                        }
                    }
                }
            }
        }
    }
}