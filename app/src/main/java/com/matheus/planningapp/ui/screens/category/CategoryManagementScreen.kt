package com.matheus.planningapp.ui.screens.category

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import com.matheus.planningapp.ui.screens.components.stardardBackground
import com.matheus.planningapp.ui.theme.PageDesignSettings
import com.matheus.planningapp.ui.theme.strings.LocalStrings
import com.matheus.planningapp.ui.theme.strings.StringsRepository
import com.matheus.planningapp.viewmodel.category.CategoryViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryManagementScreen(
    categoryViewModel: CategoryViewModel = koinViewModel(),
    onMenuClick: () -> Unit,
) {
    val strings: StringsRepository = LocalStrings.current

    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = strings.categoryManagementTitle,
                        style =
                            TextStyle(
                                fontSize = PageDesignSettings.mediumTitle,
                                color = MaterialTheme.colorScheme.primary,
                            ),
                    )
                },
                actions = {
                    IconButton(
                        onClick = onMenuClick,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = strings.menuButton,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(PageDesignSettings.largeIconSize),
                        )
                    }
                },
            )
        },
        content = { paddingValues ->
            CategoryContent(
                modifier =
                    Modifier
                        .padding(paddingValues)
                        .stardardBackground(),
                categoryViewModel = categoryViewModel,
                onShowSnackbar = { message ->
                    scope.launch {
                        snackBarHostState.showSnackbar(message)
                    }
                },
            )
        },
    )
}
