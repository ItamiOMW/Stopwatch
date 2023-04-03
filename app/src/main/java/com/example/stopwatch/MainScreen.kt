package com.example.stopwatch

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.stopwatch.service.ServiceHelper
import com.example.stopwatch.service.StopwatchService
import com.example.stopwatch.service.StopwatchState


@Composable
fun MainScreen(
    stopwatchService: StopwatchService,
) {

    val application = LocalContext.current
    val stopwatchServiceState = stopwatchService.state
    val hours = stopwatchService.hours
    val minutes = stopwatchService.minutes
    val seconds = stopwatchService.seconds

    Scaffold() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.fillMaxHeight(0.2f))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedCounter(
                    count = hours,
                    style = MaterialTheme.typography.headlineLarge.copy(color = MaterialTheme.colorScheme.secondary)
                )
                Text(
                    text = ":",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
                AnimatedCounter(
                    count = minutes,
                    style = MaterialTheme.typography.headlineLarge.copy(color = MaterialTheme.colorScheme.secondary)
                )
                Text(
                    text = ":",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
                AnimatedCounter(
                    count = seconds,
                    style = MaterialTheme.typography.headlineLarge.copy(color = MaterialTheme.colorScheme.secondary)
                )
            }
            Spacer(modifier = Modifier.fillMaxHeight(0.65f))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButtonWithText(
                    text = when (stopwatchServiceState) {
                        StopwatchState.STARTED -> stringResource(R.string.action_pause)
                        StopwatchState.STOPPED -> stringResource(R.string.action_resume)
                        else -> stringResource(R.string.action_start)
                    },
                    iconId = if (stopwatchServiceState == StopwatchState.STARTED) R.drawable.pause else R.drawable.play
                ) {
                    ServiceHelper.triggerForegroundService(
                        application,
                        if (stopwatchServiceState == StopwatchState.STARTED) StopwatchService.ACTION_STOP_SERVICE
                        else StopwatchService.ACTION_START_SERVICE
                    )
                }
                if (stopwatchServiceState != StopwatchState.CANCELED) {
                    IconButtonWithText(
                        text = stringResource(R.string.action_reset),
                        iconId = R.drawable.reset
                    ) {
                        ServiceHelper.triggerForegroundService(
                            application,
                            StopwatchService.ACTION_CANCEL_SERVICE
                        )
                    }
                }

            }
        }
    }
}


@Composable
fun IconButtonWithText(
    text: String,
    iconId: Int,
    onClick: () -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        FilledIconButton(
            onClick = {
                onClick()
            },
            modifier = Modifier
                .size(70.dp)
                .shadow(3.dp, CircleShape),
            colors = IconButtonDefaults.filledIconButtonColors(
                contentColor = MaterialTheme.colorScheme.tertiary,
                containerColor = MaterialTheme.colorScheme.tertiary
            ),
            shape = CircleShape
        ) {
            Icon(
                painter = painterResource(id = iconId),
                contentDescription = text,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.secondary,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary.copy(0.5f)),
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimatedCounter(
    count: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.headlineLarge,
) {
    var oldCount by rememberSaveable() {
        mutableStateOf(count)
    }

    SideEffect {
        oldCount = count
    }

    Row(modifier = modifier) {
        val oldCountString = oldCount
        for (i in count.indices) {
            val oldChar = oldCountString.getOrNull(i)
            val newChar = count[i]
            val char = if (oldChar == newChar) {
                oldCountString[i]
            } else {
                count[i]
            }
            AnimatedContent(
                targetState = char,
                transitionSpec = {
                    slideInVertically { it } with slideOutVertically { -it }
                }
            ) { char ->
                Text(
                    text = char.toString(),
                    style = style,
                    softWrap = false
                )
            }
        }
    }
}