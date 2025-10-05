package com.example.caritasapp.composables

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit

@Composable
fun HyperlinkText(
    modifier: Modifier = Modifier,
    fullText: String,
    hyperlinks: Map<String, String>,
    fontSize: TextUnit = TextUnit.Unspecified,
    textColor: Color = Color.Gray,
    textAlign: TextAlign = TextAlign.Start,
) {
    val uriHandler = LocalUriHandler.current
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

    val annotatedString = buildAnnotatedString {
        append(fullText)

        for ((linkText, linkUrl) in hyperlinks) {
            val startIndex = fullText.indexOf(linkText)
            if (startIndex == -1) continue
            val endIndex = startIndex + linkText.length

            addStyle(
                style = SpanStyle(
                    color = textColor,
                    textDecoration = TextDecoration.Underline
                ),
                start = startIndex,
                end = endIndex
            )
            addStringAnnotation(
                tag = "URL",
                annotation = linkUrl,
                start = startIndex,
                end = endIndex
            )
        }
    }

    BasicText(
        text = annotatedString,
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures { offset ->
                textLayoutResult?.let { layoutResult ->
                    val position = layoutResult.getOffsetForPosition(offset)
                    annotatedString.getStringAnnotations(tag = "URL", start = position, end = position)
                        .firstOrNull()?.let { annotation ->
                            uriHandler.openUri(annotation.item)
                        }
                }
            }
        },
        style = TextStyle(
            textAlign = textAlign,
            fontSize = fontSize,
            color = textColor
        ),
        onTextLayout = {
            textLayoutResult = it
        }
    )
}