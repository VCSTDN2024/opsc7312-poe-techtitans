
![FUSION](https://github.com/user-attachments/assets/7957c876-3178-40d8-83dc-13a8cf8713f7)
# Fusion App

Fusion is a comprehensive recipe application with features like recipe search, ingredient management, meal planning, and personalized settings. The app incorporates Firebase for authentication and data storage, Retrofit for API calls, and a multi-language interface supporting English and Afrikaans.

## Table of Contents
1. [Features](#features)
2. [Technologies Used](#technologies-used)
3. [Setup & Installation](#setup--installation)
4. [Core Components](#core-components)
5. [Usage](#usage)
6. [API Integration](#api-integration)
7. [Firebase Integration](#firebase-integration)
8. [Biometric Authentication](#biometric-authentication)
9. [Multi-language Support](#multi-language-support)

## Features

### 1. User Authentication
   - **Firebase Authentication**: Users can register, log in, and securely log out.
   - **Biometric Authentication**: Users can enable biometric login (using fingerprint or face authentication, depending on the device's hardware). This allows for quick, secure access after initial login.

### 2. Recipe Management
   - **Recipe Search** (User feature 1): Search by keywords, filter by meal type, calorie count, and main ingredients. Results are displayed in a grid with detailed recipe views.
   - **Favorite Recipes** (User feature 2): Save favorite recipes to Firebase for quick access and locally cache them in SharedPreferences for offline access.
   - **Detailed Recipe View** (User feature 3): Displays recipe summary, ingredients, step-by-step instructions, and nutritional information.

### 3. Ingredient & Shopping List
   - **Ingredient Management** (User feature 4): Add recipe ingredients to a shopping list, which is stored in Firebase. Users can check off items as they are bought.
   - **Category Grouping**(User feature 5): Ingredients in the shopping list are grouped by category, with expandable views for organization.
   - **Firebase Sync**: Shopping list data is synced with Firebase, allowing real-time updates across devices.

### 4. Meal Planning
   - **Day and Meal Time Selection**(User feature 6): Users can plan meals by selecting days and meal times.
   - **Notification for Meal Prep**: Schedule notifications based on meal times to remind users when to start cooking.

### 5. Settings & Personalization
   - **Profile Management** (User feature 7): Users can edit profiles, and switch languauges.
   - **Language Settings**: Support for English and Afrikaans, allowing users to switch languages seamlessly.
   - **Unit Conversion**(User feature 8): Easily convert between metric and imperial units within the app.
   - **Account Deletion**: Confirm account deletion with re-authentication for security. Removes user data from Firebase.

### 6. Notifications
   - **Custom Notifications**: Scheduled notifications with custom sounds to remind users about upcoming meals.

## Technologies Used

- **Android SDK & Kotlin**: Core development platform and language.
- **Firebase**:
   - **Authentication** for user login and account management.
   - **Realtime Database** for storing user data, favorites, meal plans, and shopping lists.
- **Retrofit** for API calls to retrieve recipe data.
- **Glide** for efficient image loading.
- **TranslationUtil**: Custom utility for language translation and multi-language support.

## Setup & Installation

1. Clone the repository:
2. Open the project in Android Studio.
3. Set up your **Firebase project** and add `google-services.json` to `app/`.
4. Update `BuildConfig.API_KEY` in `RetrofitInstance.kt` with your spoonacular API key.
5. Run the app on an emulator or a connected device.

## Core Components

### Activity & Fragment Overview

1. **LoginPage & SignupPage**
   - Authentication screens supporting Firebase login, offline login, and biometric login.
   - On successful authentication, navigate to the `HomePage`.

2. **HomePage**
   - **Search and Filter**: Enter keywords or apply filters to fetch specific recipes using Retrofit.
   - **Bottom Navigation**: Navigate between the home, favorites, meal planner, shopping list, and settings pages.

3. **RecipeDetailsActivity**
   - Displays the full details of a selected recipe, organized in tabs (Overview, Ingredients, Steps, Nutrition).
   - Users can add recipes to favorites, view ingredients, and plan meals directly.

4. **MealPlannerPage & MealTimeActivity**
   - Plan meals by day and time, saving data to Firebase.
   - Schedule notifications based on meal preparation time and selected meal time.

5. **ShoppingListPage**
   - Displays categorized shopping items with expandable views.
   - Users can check off or remove items, with updates saved to Firebase.

6. **SettingsPage**
   - Manage account settings, including notifications, language preferences, and account deletion.
   - Navigate to pages like `editProfilePage`, `LanguagePage`, and `ConversionsPage`.

### Adapter Classes

- **RecipeAdapter**: Displays search results and favorite recipes in a grid layout.
- **NutrientAdapter & NutrientCategoryAdapter**: Organize and display nutritional data within categories on the `NutritionFragment`.

### Utility Classes

- **TranslationUtil**: Manages language loading and applies translations across UI components.
- **NotificationReceiver**: Configures and displays custom notifications.

## Usage

1. **Sign Up/Login**: Create an account or log in with Firebase authentication.
2. **Enable Biometric Authentication**: Prompt appears after the first login, allowing users to set up biometric login for quick access.
3. **Search Recipes**: Use the search bar on `HomePage` to find recipes by keywords and filters.
4. **View Details**: Tap a recipe to view its details, including ingredients, instructions, and nutritional data.
5. **Add to Shopping List**: Add ingredients to the shopping list and mark items as bought.
6. **Plan Meals**: Set up meals for each day, receive meal prep notifications.
7. **Settings Customization**: Manage language, notifications, and user profile settings.

## API Integration

- The app uses **Retrofit** to connect to a recipe API (replace `BuildConfig.API_KEY` with your actual key).
- Example endpoints:
   - **Search Recipes**: Fetch recipes based on keyword and filters.
   - **Recipe Details**: Retrieve detailed information, including nutrition.

## Firebase Integration

1. **Authentication**: User login, registration, and account deletion.
2. **Realtime Database**:
   - **User Data**: Stores profile info, favorites, and meal plans.
   - **Shopping List**: Saves shopping list items by category.
3. **Storage Security**: Rules ensure each user accesses only their data.

## Biometric Authentication

Fusion allows users to securely log in using biometric authentication. This feature enhances convenience and security by eliminating the need to enter a password for each login. Here's how it works:

1. **Enabling Biometric Login**: 
   - After the first successful login, the app prompts the user to enable biometric authentication.
   - Users can opt-in or skip this step; opting in saves their credentials for biometric login.

2. **Using Biometric Login**:
   - Once enabled, users can log in using biometrics instead of entering credentials.
   - Upon launching the app, the biometric prompt automatically appears, allowing users to authenticate quickly.

3. **Security & Re-authentication**:
   - Biometric login is secured with Firebase Authentication, ensuring credentials are verified with Firebase's backend.

4. **Implementation**:
   - Fusion uses `BiometricPrompt` for biometric authentication.
   - Stored credentials are encrypted, ensuring security even if the device is compromised.

## Multi-language Support

The app supports **English** and **Afrikaans**. Users can switch languages in the settings, with translations applied across all relevant UI components using `TranslationUtil`.

