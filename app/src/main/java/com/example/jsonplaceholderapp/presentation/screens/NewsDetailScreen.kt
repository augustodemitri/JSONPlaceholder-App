package com.example.jsonplaceholderapp.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import com.example.jsonplaceholderapp.R
import com.example.jsonplaceholderapp.presentation.components.CircularProgressComponent
import com.example.jsonplaceholderapp.presentation.components.UserAvatarComponent
import com.example.jsonplaceholderapp.presentation.viewmodel.NewsDetailViewModel
import com.example.jsonplaceholderapp.presentation.viewmodel.NewsDetailsUiState
import com.example.jsonplaceholderapp.domain.model.News
import com.example.jsonplaceholderapp.domain.usecases.CommentWithUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsDetailsScreen(
    newsId: Int?,
    popBack: () -> Unit,
    newsDetailViewModel: NewsDetailViewModel = hiltViewModel()
) {
    val newsDetailsUiState by newsDetailViewModel.newsDetailsState.collectAsState()
    val showBottomSheet by newsDetailViewModel.showBottomSheet.collectAsState()
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(newsId) {
        newsId?.let { newsDetailViewModel.setNewsId(it) }
    }

    LaunchedEffect(showBottomSheet) {
        if (showBottomSheet) {
            sheetState.show()
        } else {
            sheetState.hide()
        }
    }

    when (newsDetailsUiState) {
        is NewsDetailsUiState.Loading -> {
            CircularProgressComponent(
                modifier = Modifier.size(50.dp)
            )
        }

        is NewsDetailsUiState.ShowNewsDetail -> {
            val state = (newsDetailsUiState as NewsDetailsUiState.ShowNewsDetail)
            NewsDetailContent(
                newsDetail = state.news,
                comments = state.comments,
                popBack = popBack,
                onCommentClick = {
                    if (state.comments.isNotEmpty()) {
                        newsDetailViewModel.toggleBottomSheet()
                    }
                }
            )

            if (showBottomSheet) {
                BottomSheet(
                    state,
                    sheetState,
                    onDismiss = {
                        newsDetailViewModel.dismissBottomSheet()
                    }
                )
            }
        }

        is NewsDetailsUiState.Error -> {
            ErrorScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    uiState: NewsDetailsUiState.ShowNewsDetail,
    sheetState: SheetState,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            ModalBottomSheetHeader(onDismiss)

            HorizontalDivider()

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                items(uiState.comments) { comment ->
                    CommentCard(comment)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun NewsDetailContent(
    newsDetail: News,
    comments: List<CommentWithUser>,
    popBack: () -> Unit,
    onCommentClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 100.dp)
            .verticalScroll(scrollState)
    ) {
        // Header
        SectionHeader(newsDetail.publishedDateFormatted, popBack = popBack)

        Spacer(modifier = Modifier.height(8.dp))

        // News Content
        SubcomposeAsyncImage(
            model = newsDetail.image,
            contentDescription = null,
            loading = { CircularProgressComponent(modifier = Modifier.size(20.dp)) },
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.Gray)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = newsDetail.title,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = newsDetail.content,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Post comments
        CommentsRow(comments, onCommentClick)
    }
}

@Composable
private fun CommentsRow(comments: List<CommentWithUser>, onCommentClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onCommentClick) {
            Icon(
                painter = painterResource(id = R.drawable.comment_icon),
                modifier = Modifier.size(20.dp),
                contentDescription = null
            )
        }
        Text("${comments.count()}")
        Spacer(modifier = Modifier.width(16.dp))
    }
}

@Composable
private fun SectionHeader(formattedDate: String, popBack: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        // Back Button
        IconButton(onClick = { popBack.invoke() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Spacer(modifier = Modifier.weight(1f))
        Text(formattedDate, fontSize = 14.sp)
    }
}

@Composable
fun ModalBottomSheetHeader(onDismiss: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(id = R.string.comments_section_title),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        IconButton(onClick = onDismiss) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun CommentCard(commentWithUser: CommentWithUser) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {

                UserAvatarComponent(
                    initials = commentWithUser.user.initials,
                    modifier = Modifier.size(26.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Display user's name
                Text(
                    text = "${commentWithUser.user.firstName} ${commentWithUser.user.lastName}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = commentWithUser.comment.content,
                fontSize = 14.sp
            )
        }
    }
}


