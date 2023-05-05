package com.hl.baseproject.compose.widgets

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
	hint: String = "",
	modifier: Modifier = Modifier,
	onValueChange: (String) -> Unit = {}
) {
	TextField(
		value = "",
		onValueChange = onValueChange,
		leadingIcon = {
			Icon(imageVector = Icons.Default.Search, contentDescription = null)
		},
		colors = TextFieldDefaults.textFieldColors(
			containerColor = MaterialTheme.colorScheme.surface
		),
		placeholder = {
			Text(hint)
		},
		modifier = modifier
			.fillMaxWidth()
			.heightIn(min = 56.dp)
	)
}

@Preview(name = "SearchBar")
@Composable
private fun PreviewSearchBar() {
	SearchBar(hint = "搜索")
}