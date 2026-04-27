package com.example.cskh

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val (title, content) = extractArticle(intent)
        setContent {
            App(pendingArticleTitle = title, pendingArticleContent = content)
        }
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val (title, content) = extractArticle(intent)
        setContent {
            App(pendingArticleTitle = title, pendingArticleContent = content)
        }
    }

    private fun extractArticle(intent: android.content.Intent?): Pair<String?, String?> {
        val title = intent?.getStringExtra("article_title")
        val content = intent?.getStringExtra("article_content")
        return Pair(title, content)
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}