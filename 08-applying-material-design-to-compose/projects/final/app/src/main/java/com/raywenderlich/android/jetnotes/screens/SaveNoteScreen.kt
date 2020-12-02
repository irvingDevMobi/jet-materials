/*
 * Copyright (c) 2020 Razeware LLC
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 * 
 * This project and source code may use libraries or frameworks that are
 * released under various Open-Source licenses. Use of those libraries and
 * frameworks are governed by their own individual licenses.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.raywenderlich.android.jetnotes.screens

import androidx.compose.foundation.Icon
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.ui.tooling.preview.Preview
import com.raywenderlich.android.jetnotes.R
import com.raywenderlich.android.jetnotes.domain.model.ColorModel
import com.raywenderlich.android.jetnotes.domain.model.NEW_NOTE_ID
import com.raywenderlich.android.jetnotes.domain.model.NoteModel
import com.raywenderlich.android.jetnotes.routing.JetNotesRouter
import com.raywenderlich.android.jetnotes.routing.Screen
import com.raywenderlich.android.jetnotes.ui.components.ColorWidget
import com.raywenderlich.android.jetnotes.util.fromHex
import com.raywenderlich.android.jetnotes.viewmodel.MainViewModel
import androidx.compose.runtime.savedinstancestate.savedInstanceState

@Composable
fun SaveNoteScreen(viewModel: MainViewModel) {

  val noteEntry: NoteModel by viewModel.noteEntry
    .observeAsState(NoteModel())

  val colors: List<ColorModel> by viewModel.colors
    .observeAsState(listOf())

  val bottomDrawerState: BottomDrawerState =
    rememberBottomDrawerState(BottomDrawerValue.Closed)

  val moveNoteToTrashDialogShownState: MutableState<Boolean> = savedInstanceState { false }

  Scaffold(
    topBar = {
      val isEditingMode: Boolean = noteEntry.id != NEW_NOTE_ID
      SaveNoteTopAppBar(
        isEditingMode = isEditingMode,
        onBackClick = { JetNotesRouter.navigateTo(Screen.Notes) },
        onSaveNoteClick = { viewModel.saveNote(noteEntry) },
        onOpenColorPickerClick = { bottomDrawerState.open() },
        onDeleteNoteClick = { moveNoteToTrashDialogShownState.value = true }
      )
    },
    bodyContent = {
      BottomDrawerLayout(
        drawerState = bottomDrawerState,
        drawerContent = {
          ColorPicker(
            colors = colors,
            onColorSelect = { color ->
              val newNoteEntry = noteEntry.copy(color = color)
              viewModel.onNoteEntryChange(newNoteEntry)
            }
          )
        },
        bodyContent = {
          Content(
            note = noteEntry,
            onNoteChange = { updateNoteEntry ->
              viewModel.onNoteEntryChange(updateNoteEntry)
            }
          )
        }
      )

      if (moveNoteToTrashDialogShownState.value) {
        AlertDialog(
          onDismissRequest = { moveNoteToTrashDialogShownState.value = false },
          title = {
            Text("Move note to trash?")
          },
          text = {
            Text(
              "Are you sure you want to " +
                  "move this note to trash?"
            )
          },
          confirmButton = {
            TextButton(onClick = {
              viewModel.moveNoteToTrash(noteEntry)
            }) {
              Text("Confirm")
            }
          },
          dismissButton = {
            TextButton(onClick = {
              moveNoteToTrashDialogShownState.value = false
            }) {
              Text("Dismiss")
            }
          }
        )
      }
    }
  )
}

@Composable
private fun SaveNoteTopAppBar(
  isEditingMode: Boolean,
  onBackClick: () -> Unit,
  onSaveNoteClick: () -> Unit,
  onOpenColorPickerClick: () -> Unit,
  onDeleteNoteClick: () -> Unit
) {
  TopAppBar(
    title = {
      Text(
        text = "Save Note",
        color = MaterialTheme.colors.onPrimary
      )
    },
    navigationIcon = {
      IconButton(onClick = onBackClick) {
        Icon(asset = Icons.Filled.ArrowBack)
      }
    },
    actions = {
      // Save note action icon
      IconButton(onClick = onSaveNoteClick) {
        Icon(
          asset = Icons.Default.Check,
          tint = MaterialTheme.colors.onPrimary
        )
      }

      // Open color picker action icon
      IconButton(onClick = onOpenColorPickerClick) {
        Icon(
          asset = vectorResource(
            id = R.drawable.ic_baseline_color_lens_24
          ),
          tint = MaterialTheme.colors.onPrimary
        )
      }

      // Delete action icon (show only in editing mode)
      if (isEditingMode) {
        IconButton(onClick = onDeleteNoteClick) {
          Icon(
            asset = Icons.Default.Delete,
            tint = MaterialTheme.colors.onPrimary
          )
        }
      }
    }
  )
}

@Composable
private fun Content(
  note: NoteModel,
  onNoteChange: (NoteModel) -> Unit
) {
  Column(modifier = Modifier.fillMaxSize()) {
    ContentTextField(
      label = "Title",
      text = note.title,
      onTextChange = { newTitle ->
        onNoteChange.invoke(note.copy(title = newTitle))
      }
    )

    ContentTextField(
      modifier = Modifier
        .heightIn(max = 240.dp)
        .padding(top = 16.dp),
      label = "Body",
      text = note.content,
      onTextChange = { newContent ->
        onNoteChange.invoke(note.copy(content = newContent))
      }
    )

    val canBeCheckedOff: Boolean = note.isCheckedOff != null
    CanBeCheckedOffComponent(
      isChecked = canBeCheckedOff,
      onCheckedChange = { canBeCheckedOffNewValue ->
        val isCheckedOff: Boolean? =
          if (canBeCheckedOffNewValue) false else null
        onNoteChange.invoke(
          note.copy(isCheckedOff = isCheckedOff)
        )
      }
    )

    PickedColorComponent(color = note.color)
  }
}

@Composable
private fun ContentTextField(
  modifier: Modifier = Modifier,
  label: String,
  text: String,
  onTextChange: (String) -> Unit
) {
  TextField(
    value = text,
    onValueChange = onTextChange,
    label = { Text(label) },
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 8.dp),
    backgroundColor = MaterialTheme.colors.surface
  )
}

@Composable
private fun CanBeCheckedOffComponent(
  isChecked: Boolean,
  onCheckedChange: (Boolean) -> Unit
) {
  Row(
    Modifier
      .padding(8.dp)
      .padding(top = 16.dp)
  ) {
    Text(
      text = "Can note be checked off?",
      modifier = Modifier.weight(1f)
    )
    Switch(
      checked = isChecked,
      onCheckedChange = onCheckedChange,
      modifier = Modifier.padding(start = 8.dp)
    )
  }
}

@Composable
private fun PickedColorComponent(color: ColorModel) {
  Row(
    Modifier
      .padding(8.dp)
      .padding(top = 16.dp)
  ) {
    Text(
      text = "Picked color",
      modifier = Modifier
        .weight(1f)
        .align(Alignment.CenterVertically)
    )
    ColorWidget(
      color = Color.fromHex(color.hex),
      size = 40.dp,
      border = 1.dp,
      modifier = Modifier.padding(4.dp)
    )
  }
}

@Composable
private fun ColorPicker(
  colors: List<ColorModel>,
  onColorSelect: (ColorModel) -> Unit
) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
      text = "Color picker",
      fontSize = 18.sp,
      fontWeight = FontWeight.Bold,
      modifier = Modifier.padding(8.dp)
    )
    ScrollableColumn(modifier = Modifier.fillMaxWidth()) {
      for (color in colors) {
        Color(color, onColorSelect)
      }
    }
  }
}

@Composable
fun Color(
  color: ColorModel,
  onColorSelect: (ColorModel) -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(
        onClick = {
          onColorSelect.invoke(color)
        }
      )
  ) {
    ColorWidget(
      Color.fromHex(color.hex),
      size = 80.dp,
      border = 2.dp,
      modifier = Modifier.padding(10.dp)
    )
    Text(
      text = color.name,
      fontSize = 22.sp,
      modifier = Modifier
        .padding(horizontal = 16.dp)
        .align(Alignment.CenterVertically)
    )
  }
}

@Preview
@Composable
fun SaveNoteTopAppBarPreview() {
  SaveNoteTopAppBar(
    isEditingMode = false,
    onBackClick = {},
    onSaveNoteClick = {},
    onOpenColorPickerClick = {},
    onDeleteNoteClick = {}
  )
}

@Preview
@Composable
fun ContentPreview() {
  Content(
    note = NoteModel(title = "Title", content = "content"),
    onNoteChange = {}
  )
}

@Preview
@Composable
fun ContentTextFieldPreview() {
  ContentTextField(label = "Title", text = "", onTextChange = {})
}

@Preview
@Composable
fun CanBeCheckedOffComponentPreview() {
  CanBeCheckedOffComponent(false) {}
}

@Preview
@Composable
fun PickedColorComponentPreview() {
  PickedColorComponent(ColorModel.DEFAULT)
}

@Preview
@Composable
fun ColorPickerPreview() {
  ColorPicker(
    colors = listOf(
      ColorModel.DEFAULT,
      ColorModel.DEFAULT,
      ColorModel.DEFAULT
    )
  ) { }
}

@Preview
@Composable
fun ColorItemPreview() {
  Color(ColorModel.DEFAULT) {}
}