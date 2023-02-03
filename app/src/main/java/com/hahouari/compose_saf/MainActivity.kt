package com.hahouari.compose_saf

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.hahouari.compose_saf.ui.theme.Compose_safTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      Compose_safTheme {
        // A surface container using the 'background' color from the theme
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.background
        ) {
          SimpleScreen()
        }
      }
    }
  }
}

@Composable
fun SimpleScreen(modifier: Modifier = Modifier) {
  val context = LocalContext.current
  val isDirectoryPicked = remember { mutableStateOf(false) }
  val dirPickerLauncher = rememberLauncherForActivityResult(
    contract = PermissibleOpenDocumentTreeContract(true),
    onResult = { maybeUri ->
      maybeUri?.let { uri ->
        val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
          Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        if (checkUriPersisted(context.contentResolver, uri)) {
          context.contentResolver.releasePersistableUriPermission(uri, flags)
        }
        context.contentResolver.takePersistableUriPermission(uri, flags)
        // Do Something
        isDirectoryPicked.value = true
      }
    }
  )

  Column(
    modifier = modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
  ) {
    Button(onClick = { dirPickerLauncher.launch(Uri.EMPTY) }) {
      Text(text = "Open Directory Picker")
    }
    Spacer(modifier = Modifier.height(30.dp))
    when (isDirectoryPicked.value) {
      true -> Text(text = "Directory picking is success")
      false -> Text(text = "Waiting for you to choose a directory")
    }
  }
}

fun checkUriPersisted(contentResolver: ContentResolver, uri: Uri): Boolean {
  return contentResolver.persistedUriPermissions.any { perm -> perm.uri == uri }
}
