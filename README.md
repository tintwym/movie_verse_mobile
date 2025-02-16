MovieVerse Mobile App

MovieVerse is a mobile application designed to provide users with movie information, reviews, and more.

Features

Browse popular movies

Search for movies by title

View detailed information about each movie

Save favorite movies

Tech Stack

Language: Kotlin

Framework: Android SDK

Architecture: MVVM

Networking: Retrofit

Dependency Injection: Hilt

Database: Room

Project Structure

movie_verse_mobile/
├── app/                # Main application module
│   ├── src/main/java/  # Application source code
│   ├── src/main/res/   # Resources (layouts, drawables, etc.)
├── build.gradle.kts    # Project build configuration
├── settings.gradle.kts # Gradle settings
└── gradle/             # Gradle wrapper files

Installation

Prerequisites

Android Studio (latest version recommended)

JDK 11 or later

Gradle (handled by Android Studio)

Steps

Clone the repository:

git clone https://github.com/your-repo/movie_verse_mobile.git

Open the project in Android Studio.

Sync Gradle files and install dependencies.

Run the application on an emulator or physical device.

API Integration

This application fetches movie data from an external API using Retrofit.
Ensure you have an API key and configure it in the appropriate file.

Contributing

Feel free to contribute to this project! Follow these steps:

Fork the repository.

Create a new branch for your feature.

Commit your changes.

Push to your fork and submit a pull request.

License

This project is licensed under the MIT License.

