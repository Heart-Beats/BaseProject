package com.hl.baseproject.compose

import android.content.res.Configuration
import android.os.Bundle
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.hl.arch.base.ComposeBaseActivity
import com.hl.utils.getRandomString
import com.hl.utils.showImage

/**
 * @author  张磊  on  2023/04/18 at 11:55
 * Email: 913305160@qq.com
 */
class ComposeActivity : ComposeBaseActivity() {

	@Composable
	override fun Content(savedInstanceState: Bundle?) {
		InitPage()
	}

	@Preview(
		showBackground = true,
		name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
	)
	@Preview(showBackground = true)
	@Composable
	private fun InitPage() {
		val messageList = List(50) {
			if (it == 0) {
				Message(author = "安卓", msgBody = "Jetpack Compose")
			} else {

				Message(author = "安卓$it", msgBody = "Jetpack Compose ${getRandomString(it * 5)}")
			}
		}

		AppComposeTheme {
			Surface(modifier = Modifier.fillMaxSize()) {
				MessageList(messageList)
			}
		}

	}

	private data class Message(
		val avatar: String = "https://i.pinimg.com/originals/97/83/36/9783367be1fd1c73f74832d7a524b5b9.jpg",
		val author: String,
		val msgBody: String
	)

	@Composable
	private fun MessageList(messageList: List<Message>) {
		LazyColumn {
			this.items(messageList) { message ->
				MessageCard(message)
			}
		}
	}


	@Composable
	private fun MessageCard(message: Message) {
		Card(
			colors = CardDefaults.cardColors(
				containerColor = MaterialTheme.colorScheme.onPrimary
			),
			elevation = CardDefaults.cardElevation(10.dp),
			modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
		) {
			Row(modifier = Modifier.padding(10.dp)) {
				AsyncImage(
					model = message.avatar,
					contentDescription = "测试头像",
					contentScale = ContentScale.Crop,
					modifier = Modifier
						.size(50.dp)
						.clip(CircleShape)
						.border(1.5.dp, MaterialTheme.colorScheme.secondary, CircleShape)
						.clickable {
							this@ComposeActivity.showImage(null, message.avatar)
						}
				)

				Spacer(modifier = Modifier.width(8.dp))

				var isExpanded by rememberSaveable { mutableStateOf(false) }

				Column(modifier = Modifier.fillMaxWidth()) {

					Row(modifier = Modifier.fillMaxWidth()) {
						Text(
							modifier = Modifier.weight(1F),
							text = message.author,
							style = MaterialTheme.typography.titleMedium.copy(
								color = Color(0xFF00A381)
							),
						)

						IconButton(onClick = {
							isExpanded = !isExpanded
						}) {
							Icon(
								imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
								contentDescription = if (isExpanded) "展开" else "折叠"
							)
						}

					}

					Spacer(modifier = Modifier.height(4.dp))

					val surfaceColor by animateColorAsState(
						if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
					)

					Surface(
						shape = MaterialTheme.shapes.medium,
						color = surfaceColor,
						shadowElevation = 1.dp,
						modifier = Modifier
							.animateContentSize(
								animationSpec = spring(
									dampingRatio = Spring.DampingRatioMediumBouncy,
									stiffness = Spring.StiffnessLow
								)
							)
							.padding(1.dp)
					) {
						Text(
							text = message.msgBody,
							style = MaterialTheme.typography.bodyMedium,
							maxLines = if (isExpanded) Int.MAX_VALUE else 1,
							modifier = Modifier.padding(4.dp)
						)
					}
				}
			}
		}
	}
}