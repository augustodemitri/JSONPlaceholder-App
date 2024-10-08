﻿# JSONPlaceholder App

This app started as a take-home challenge to build something from scratch using the JSONPlaceholder API. The goal was to create key features like data fetching, designing the UI, and integrating it all with a RESTful API, while sticking to good practices in Android development.

## Description
This Android app, utilizes the JSONPlaceholder API, which provides mock data for news, users, and comments. 

The app features four main screens designed to manage and display this data effectively:
  1. News List and Search Screen: A home screen displaying a list of news articles, where users can search for specific news by title or content.
  2. News Detail Screen: A detailed view of a selected news article, providing in-depth information.
  3. User List Screen: A screen listing users retrieved from the API, where each user entry includes a button to view their location.
  4. User Location Map Screen: A map view powered by Google Maps, showing the location of the selected user.

## Getting Started

### Prerequisites

- **Google Cloud Account:** Ensure you are logged in to your Google Cloud account to generate your Google Maps API key.

### Clone the Repository

If you haven't already, clone the repository to your local machine

### Add Your Google Maps API Key

You can follow the steps provided by Google: https://developers.google.com/maps/documentation/android-sdk/start to generate your Google Map API key, then you should add it to your local.properties file.

## Project Structure

This project is organized following the principles of **Clean Architecture**, which separates the codebase into three main layers: `data`, `domain`, and `presentation`. The app also implements the **Model-View-ViewModel (MVVM)** pattern and leverages **Kotlin Flows** for handling asynchronous data streams.

### Layers:

1. **Data Layer (`data/`)**:
  - **API (`data/api/`)**: Contains service interfaces for making network requests using Retrofit.
    - `CommentsService.kt`, `NewsApiService.kt`, `UserService.kt`
  - **Repository Implementations (`data/repository/`)**: Contains the concrete implementations of the repositories that interact with the API services.
    - `CommentsRepositoryImpl.kt`, `NewsRepositoryImpl.kt`, `UserRepositoryImpl.kt`

2. **Domain Layer (`domain/`)**:
  - **Models (`domain/model/`)**: Core entities that represent the data used across the app.
    - `Comment.kt`, `News.kt`, `User.kt`
  - **Repository Interfaces (`domain/repository/`)**: Abstract definitions of the repositories, defining the contract for data operations.
    - `CommentsRepository.kt`, `NewsRepository.kt`, `UserRepository.kt`
  - **Use Cases (`domain/usecases/`)**: Classes that encapsulate business logic, orchestrating data flow between repositories and ViewModels.
    - `GetCommentsWithUsersUseCase.kt`, `GetNewsWithAuthorUseCase.kt`

3. **Presentation Layer (`presentation/`)**:
  - **Components (`presentation/components/`)**: Reusable UI components built with Jetpack Compose.
    - `BottomNavBarComponent.kt`, `CircularProgressComponent.kt`, `ScaffoldComponent.kt`, `UserAvatarComponent.kt`
  - **Navigation (`presentation/navigation/`)**: Contains the navigation graph for managing screen transitions.
    - `AppNavigationGraph.kt`
  - **Screens (`presentation/screens/`)**: Composable functions that define the UI for each screen in the app.
    - `ErrorScreen.kt`, `NewsDetailScreen.kt`, `NewsScreen.kt`, `UserLocationScreen.kt`

4. **Dependency Injection (`di/`)**:
    - Modules for providing dependencies across the app using Hilt.
        - `DispatchersModule.kt`, `NetworkModule.kt`, `RepositoryModule.kt`

### Dependencies

This project utilizes the following dependencies:

- **Retrofit**: For making HTTP requests and handling API calls.
- **Gson**: For converting JSON data to Java/Kotlin objects.
- **Google Maps**: To integrate Google Maps functionality, including utilities and Compose support.
- **Hilt**: For dependency injection to manage and provide dependencies across the app.
- **Navigation Component**: To handle navigation in Jetpack Compose.
- **Kotlin Coroutines**: For managing asynchronous tasks and concurrency.
- **Coil**: For image loading.
