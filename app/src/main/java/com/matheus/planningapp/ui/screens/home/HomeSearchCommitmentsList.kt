package com.matheus.planningapp.ui.screens.home

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import com.matheus.planningapp.data.commitment.CommitmentEntity
import com.matheus.planningapp.ui.theme.PageDesignSettings

fun LazyListScope.searchCommitmentsList(
    commitments: List<CommitmentEntity>,
    onReminderAction: (commitment: CommitmentEntity) -> Unit,
    onViewCommitment: (commitment: CommitmentEntity) -> Unit,
    onNavigateToUpdateCommitment: (commitmentId: Long) -> Unit,
    onDeleteCommitment: (commitment: CommitmentEntity) -> Unit,
) {
    items(commitments) { commitment ->

        Row(
            modifier =
                Modifier
                    .padding(
                        end = PageDesignSettings.extraLargePaddingValue,
                        start = PageDesignSettings.extraLargePaddingValue,
                        bottom = PageDesignSettings.extraLargePaddingValue,
                    ).height(IntrinsicSize.Min)
                    .heightIn(min = PageDesignSettings.mediumComponentSize),
        ) {
            CommitmentCard(
                commitmentEntity = commitment,
                onReminderAction = onReminderAction,
                onViewCommitment = onViewCommitment,
                onNavigateToUpdateCommitment = onNavigateToUpdateCommitment,
                onDeleteCommitment = onDeleteCommitment,
            )
        }
    }
}
