package com.matheus.planningapp.ui.screens.recurrence

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.matheus.planningapp.R
import com.matheus.planningapp.data.recurrence.CommitmentRecurrenceDataClass
import com.matheus.planningapp.ui.theme.PageDesignSettings
import com.matheus.planningapp.ui.theme.strings.LocalStrings
import com.matheus.planningapp.ui.theme.strings.StringsRepository
import com.matheus.planningapp.util.enums.FrequencyEnum
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurrenceViewDialog(
    recurrence: CommitmentRecurrenceDataClass?,
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
) {
    if (recurrence == null) return

    val strings: StringsRepository = LocalStrings.current

    val commitmentStartDateTime: LocalDateTime = recurrence.startDateTime.toLocalDateTime(TimeZone.currentSystemDefault())
    val startTimeString = String.format(Locale.US, strings.hourFormat, commitmentStartDateTime.hour, commitmentStartDateTime.minute)
    val commitmentEndDateTime: LocalDateTime = recurrence.endDateTime.toLocalDateTime(TimeZone.currentSystemDefault())
    val endTimeString = String.format(Locale.US, strings.hourFormat, commitmentEndDateTime.hour, commitmentEndDateTime.minute)

    var recurrenceText = "${strings.recurrenceFrequencyField}: ${recurrence.frequency.label}"
    when (recurrence.frequency) {
        FrequencyEnum.CUSTOMIZED -> recurrenceText += " - ${strings.recurrenceIntervalField}: ${recurrence.interval}"
        FrequencyEnum.MONTHLY -> recurrenceText += " - ${strings.recurrenceDayOfMonthField}: ${recurrence.dayOfMonth}"
        FrequencyEnum.WEEKLY -> {
            recurrenceText += " - ${strings.recurrenceWeekDaysField}: ${
                recurrence.dayOfWeekList.joinToString(", ") { dayOfWeek -> dayOfWeek.label }
            }"
        }
        else -> recurrenceText
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Column {
                    Text(
                        text = recurrence.title,
                        fontSize = PageDesignSettings.largeText,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSecondary,
                    )

                    HorizontalDivider()

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondary.copy(alpha = .6f),
                            modifier =
                                Modifier
                                    .size(PageDesignSettings.smallIconSize)
                                    .padding(end = PageDesignSettings.smallIconClip),
                        )

                        Text(
                            text = recurrenceText,
                            fontSize = PageDesignSettings.mediumText,
                            color = MaterialTheme.colorScheme.onSecondary.copy(alpha = .6f),
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.outline_nest_clock_farsight_analog_24),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondary.copy(alpha = .6f),
                            modifier =
                                Modifier
                                    .size(PageDesignSettings.smallIconSize)
                                    .padding(end = PageDesignSettings.smallIconSize),
                        )

                        Text(
                            text = "$startTimeString — $endTimeString",
                            fontSize = PageDesignSettings.mediumText,
                            color = MaterialTheme.colorScheme.onSecondary.copy(alpha = .6f),
                        )
                    }
                }
            },
            text = {
                Column {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.secondary,
                                    shape = RoundedCornerShape(PageDesignSettings.mediumIconClip),
                                ).padding(PageDesignSettings.mediumPaddingValue),
                    ) {
                        Text(
                            text = recurrence.description ?: "",
                            fontSize = PageDesignSettings.smallText,
                            color = MaterialTheme.colorScheme.onSecondary,
                        )
                    }

                    Spacer(modifier = Modifier.height(PageDesignSettings.extraLargePaddingValue))

                    HorizontalDivider()
                }
            },
            confirmButton = {},
            dismissButton = {
                Button(
                    onClick = onDismissRequest,
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.secondary,
                        ),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = strings.dismissButton,
                        fontSize = PageDesignSettings.largeText,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.onBackground,
        )
    }
}
