package com.example.sparksmartiiitbh.ui.theme.Utility

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LogScreen(
    title: String,
    logs: List<String>,
    isFuelLog: Boolean
) {
    val scrollState = rememberLazyListState()

    val processedLogs = logs.map { log ->
        log.split("|").map { part ->
            part.replace("Start:", "")
                .replace("End:", "")
                .replace("Duration:", "")
                .replace("Time:", "")
                .replace("Remaining:", "")
                .trim()
        }
    }

    LazyColumn(
        state = scrollState,
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        stickyHeader {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(16.dp)
            ) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isFuelLog) "Time" else "Start Time",
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = if (isFuelLog) "Fuel Change" else "End Time",
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = if (isFuelLog) "Remaining" else "Duration",
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        if (logs.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No logs available", fontSize = 16.sp, color = Color.Gray)
                }
            }
        } else {
            itemsIndexed(processedLogs) { index, logParts ->
                LogListItem(
                    parts = logParts,
                    isFuelLog = isFuelLog,
                    index = index + 1
                )
            }
        }
    }
}
