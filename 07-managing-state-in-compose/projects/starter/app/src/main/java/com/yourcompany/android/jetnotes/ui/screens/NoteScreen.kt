package com.yourcompany.android.jetnotes.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.tooling.preview.Preview
import com.yourcompany.android.jetnotes.domain.model.NoteModel
import com.yourcompany.android.jetnotes.ui.components.Note
import com.yourcompany.android.jetnotes.ui.components.TopAppBar
import com.yourcompany.android.jetnotes.viewmodel.MainViewModel

@Composable
fun NoteScreen(
    viewModel: MainViewModel
) {
    val notes: List<NoteModel> by viewModel
        .notesNotInTrash
        .observeAsState(listOf())

    Column {
        TopAppBar(
            title = "JetNotes",
            icon = Icons.Filled.List,
            onIconClick = {}
        )
        NoteList(
            notes = notes,
            onNoteClick = { viewModel.onNoteClick(it) },
            onNoteCheckedChange = { viewModel.onNodeCheckedChange(it) }
        )
    }
}

@Composable
private fun NoteList(
    notes: List<NoteModel>,
    onNoteClick: (NoteModel) -> Unit,
    onNoteCheckedChange: (NoteModel) -> Unit
) {
    LazyColumn {
        items(count = notes.size) { noteIndex ->
            val note = notes[noteIndex]
            Note(
                note = note,
                onNoteClick = onNoteClick,
                onNoteCheckedChange = onNoteCheckedChange
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NoteListPreview() {
    NoteList(
        notes = listOf(
            NoteModel(1, "Note 1", "Content 1"),
            NoteModel(2, "Note 2", "Content 2"),
            NoteModel(3, "Note 3", "Content 3"),
        ),
        onNoteClick = {},
        onNoteCheckedChange = {}
    )
}
