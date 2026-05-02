# Movie Verse Mobile

Movie Verse Mobile is a Kotlin-based Android application that provides users with information about movies, including details, ratings, and recommendations.

This folder lives alongside **Movie Verse** web and ML projects in the parent workspace; see the root [`README.md`](../README.md) for the full layout.

## Features

- Browse popular and trending movies
- View detailed movie information
- Search for movies
- Save favorite movies for later
- Dark mode support

## Tech Stack

- **Language:** Kotlin
- **Architecture:** MVVM (Model-View-ViewModel)
- **Networking:** Retrofit
- **Dependency Injection:** Hilt
- **Asynchronous Operations:** Coroutines
- **Database:** Room
- **UI Components:** Jetpack Compose / XML (if applicable)

## Installation & Setup

1. Clone the repository:
   ```sh
   git clone https://github.com/your-repo/movie-verse-mobile.git
   cd movie-verse-mobile
   ```

2. Open the project in **Android Studio**.

3. Sync dependencies by clicking **"Sync Project with Gradle Files"**.

4. Run the application on an emulator or a physical device.

### Backend and TMDB

Movie metadata uses [The Movie Database (TMDB)](https://www.themoviedb.org/) directly from the app. Auth, profiles, favorites, watchlists, recommendations, and reviews are configured for **`http://10.0.2.2:8080/`** (host loopback from the Android emulator). You need a compatible API server on that port, or change the base URLs in the `data/api/` Retrofit clients and network security config for your environment. On a physical device, use your machine’s LAN IP instead of `10.0.2.2`.

## Usage

- Launch the app to explore trending and popular movies.
- Search for specific movies by title.
- Click on a movie to view details.
- Save movies to your favorites list.

## Contributing

1. Fork the repository.
2. Create a new branch (`feature/your-feature`).
3. Commit your changes.
4. Push to the branch and create a pull request.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
