package com.hl.baseproject.compose.widgets

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hl.baseproject.compose.AppComposeTheme
import com.hl.baseproject.compose.utils.clearFocusOnKeyboardDismiss

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
	hint: String = "",
	onValueChange: (String) -> Unit = {},
	modifier: Modifier = Modifier,
) {
	var text by rememberSaveable { mutableStateOf("") }

	TextField(
		value = text,
		onValueChange = {
			text = it
			onValueChange(it)
		},
		leadingIcon = {
			Icon(imageVector = Icons.Default.Search, contentDescription = null)
		},
		label = {
			Text(hint, color = MaterialTheme.colorScheme.secondary)
		},
		modifier = modifier
			.fillMaxWidth()
			.clip(RoundedCornerShape(10.dp))
			.heightIn(min = 56.dp)
			.clearFocusOnKeyboardDismiss()
	)
}

@Preview(name = "SearchBar")
@Composable
private fun PreviewSearchBar() {
	AppComposeTheme {
		SearchBar(hint = "搜索")
	}
}