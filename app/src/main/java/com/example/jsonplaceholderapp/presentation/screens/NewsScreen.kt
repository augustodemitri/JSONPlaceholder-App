package com.example.jsonplaceholderapp.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
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
import com.example.jsonplaceholderapp.domain.usecases.ArticleWithAuthor
import com.example.jsonplaceholderapp.presentation.components.CircularProgressComponent
import com.example.jsonplaceholderapp.presentation.components.UserAvatarComponent
import com.example.jsonplaceholderapp.presentation.viewmodel.NewsUiState
import com.example.jsonplaceholderapp.presentation.viewmodel.NewsViewModel

@Composable
fun NewsScreen(
    paddingValues: PaddingValues,
    onNavigateToNewsDetails: (id: Int) -> Unit,
    newsViewModel: NewsViewModel = hiltViewModel(),
) {
    val query by newsViewModel.query.collectAsState()
    val newsListState by newsViewModel.newsUiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        Image(
            painter = painterResource(id = R.drawable.app_screen_icon),
            contentDescription = "App Icon",
            alpha = 0.4f,
            modifier = Modifier
                .size(
                    width = 180.dp,
                    height = 60.dp
                )
                .align(Alignment.Start)
                .padding(start = 4.dp, top = 4.dp)
        )

        SearchBarComponent(query, newsViewModel)

        when (newsListState) {
            is NewsUiState.Loading -> {
                CircularProgressComponent()
            }

            is NewsUiState.ShowNewsList -> {
                val articleWithAuthors = (newsListState as NewsUiState.ShowNewsList).data
                NewsList(articlesWithAuthors = articleWithAuthors) { newsId ->
                    onNavigateToNewsDetails(newsId)
                }
            }

            is NewsUiState.Error -> {
                // TODO: Handle error state
            }
        }
    }
}

@Composable
private fun SearchBarComponent(
    query: String,
    newsViewModel: NewsViewModel
) {
    OutlinedTextField(
        value = query,
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = null)
        },
        onValueChange = { typedQuery ->
            newsViewModel.updateQuery(typedQuery)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
        placeholder = { Text(stringResource(id = R.string.search_bar_hint)) },
        singleLine = true
    )
}

@Composable
fun NewsList(
    articlesWithAuthors: List<ArticleWithAuthor>,
    onClick: (newsId: Int) -> Unit
) {
    LazyColumn(
        state = rememberLazyListState(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.Start
    ) {
        items(articlesWithAuthors) { article ->
            NewsCard(article, onClick = onClick)
        }
    }
}

@Composable
fun NewsCard(
    articleWithAuthor: ArticleWithAuthor,
    onClick: (newsId: Int) -> Unit
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onClick(articleWithAuthor.news.id) },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Text content on the left side
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Article title
                Text(
                    text = articleWithAuthor.news.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Author and source
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    UserAvatarComponent(initials = articleWithAuthor.author?.initials, modifier = Modifier.size(26.dp))

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "${articleWithAuthor.author?.firstName ?: "Anonymous"} ${articleWithAuthor.author?.lastName ?: "User"}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Published date
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.time_icon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(16.dp)
                            .padding(end = 4.dp)
                    )
                    Text(
                        text = articleWithAuthor.news.publishedDateFormatted,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // News image
            SubcomposeAsyncImage(
                model = articleWithAuthor.news.thumbnail,
                loading = { CircularProgressIndicator(modifier = Modifier.size(10.dp)) },
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        }
    }
}






