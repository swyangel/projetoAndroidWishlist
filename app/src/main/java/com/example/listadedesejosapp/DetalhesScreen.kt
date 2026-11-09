package com.example.listadedesejosapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import java.io.File

@Composable
fun DetalhesScreen(itemNome: String, itemLink: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    val nomeArquivo = "foto_${itemNome.replace(" ", "_")}.png"
    val arquivoImagem = File(context.filesDir, nomeArquivo)

    var fotoBitmap by remember {
        mutableStateOf(if (arquivoImagem.exists()) BitmapFactory.decodeFile(arquivoImagem.absolutePath) else null)
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            fotoBitmap = bitmap
            context.openFileOutput(nomeArquivo, Context.MODE_PRIVATE).use { stream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            }
        }
    }

    Column(
        modifier = Modifier.padding(16.dp).fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Detalhes do Item", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        Text(itemNome, style = MaterialTheme.typography.titleLarge)

        if (itemLink.isNotBlank()) {
            OutlinedButton(
                onClick = {
                    try {
                        val linkFinal = if (itemLink.startsWith("http")) itemLink else "http://$itemLink"
                        uriHandler.openUri(linkFinal)
                    } catch (e: Exception) {
                    }
                }
            ) {
                Icon(Icons.Default.OpenInNew, contentDescription = "Abrir Site")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Visitar Loja")
            }
        }

        Button(
            onClick = { cameraLauncher.launch(null) },
            modifier = Modifier.defaultMinSize(minHeight = 48.dp)
        ) {
            Icon(Icons.Default.CameraAlt, contentDescription = "Câmera")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Fotografar o produto")
        }

        fotoBitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "Foto capturada",
                modifier = Modifier.fillMaxWidth().height(250.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Voltar para Wishlist")
        }
    }
}