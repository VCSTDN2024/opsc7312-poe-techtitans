Fusion App
Fusion is an application for meal planning and recipe management that aims to simplify users' grocery and cooking tasks. The app offers functions including meal planning, 
grocery list management, recipe browsing and storing, as well as nutritional data. Integration with Firebase ensures that users can securely save their data and manage their accounts.

Contributors
This project is a module program for OPSC7312:

ST10024454 (Matteo Pita)
ST10161340 (Tyler Friedman)
ST10046014 (Nicholas James Malan)
ST10043367 (Reece Cunningham)
ST10043352 (Shira Bome)
Minimum System Requirements
Operating System: Windows 8.1, 10, or 11

Hardware Requirements:

Processor: 1.8 GHz or faster processor. A quad-core processor or better is recommended.
RAM: Minimum 2 GB (8 GB recommended; 12.5 GB if running on a virtual PC).
Storage:
Minimum 800 MB for basic installation.
Complete installation requires around 210 GB of hard disk space.
Disk Preference: Install Visual Studio 2019 and the Windows OS on an SSD drive for improved performance.
Display Resolution: Minimum 720p (1280 x 720); a resolution of WXGA (1366 x 768) or higher is recommended.

Features

1. Browse and Search Recipes
Users can browse recipes on the HomePage by entering a search term or applying filters such as ingredients, meal type, and calorie limits.
Recipes are displayed in a grid format, and users can tap on a recipe to view its details.
The app uses Retrofit to fetch detailed recipe data including ingredients, steps, and nutritional information.

2. Recipe Details
The RecipeDetailsActivity provides in-depth information about a selected recipe. Users can view the:
Overview: A short description of the recipe.
Ingredients: A list of required ingredients.
Steps: Instructions on how to prepare the recipe.
Nutrition: Nutritional information for the recipe.
Users can swipe between these sections using ViewPager2.
Recipe images are displayed using Glide, and users can add recipes to their meal plans or mark them as favorites.

3. Meal Planning
The MealPlannerPage allows users to plan meals for specific days of the week.
On the ChooseTimePage, users select a meal time (breakfast, lunch, or dinner) for a particular day.
MealPlannerMeal and MealTimeActivity provide interfaces for selecting a recipe and adding it to the meal plan stored in Firebase.

4. Shopping List Management
The ShoppingListPage allows users to view their shopping list, organized by categories.
Users can expand and collapse categories, check off purchased items, and dynamically add or remove items from their list.
All changes to the shopping list are synced with Firebase, ensuring that users' shopping lists are stored and updated in real-time.

5. Nutritional Information
NutritionFragment displays detailed nutritional information for each recipe. Nutrients are grouped into categories for easier browsing.
NutrientAdapter and NutrientCategoryAdapter are used to organize and display the nutrient data in a RecyclerView.

6. User Authentication
Users can sign up or log in using Firebase Authentication.
The SignupPage allows users to create an account by providing a username, email, and password. Firebase handles user authentication and data storage.

7. Profile Management and Settings
In the SettingsPage, users can manage their profile, change their settings, or log out of the app.
Users can also delete their account by re-entering their password for confirmation. Once an account is deleted, the user's data is also removed from Firebase.

8. Upcoming Features
LanguagePage: In future versions, users will be able to change the app’s language (not yet implemented).
NotificationsPage: A future feature will allow users to manage notifications for the app (not yet implemented).

9. Firebase Integration
Fusion uses Firebase for:
User Authentication: Handles user sign-up, login, and account deletion.
Realtime Database: Stores user data such as meal plans, shopping lists, and recipes, ensuring all data is updated in real-time.

10. API Integration
The app uses Retrofit to interact with the Spoonacular API, which provides recipe data and nutritional information.

Setup Instructions
To run the Fusion app on your local machine, follow these steps:

Step 1: Clone the Repository
Open your terminal or command prompt.
Clone the Fusion repository to your local machine:
bash
Copy code
git clone <repository_url>

Step 2: Open the Project in Android Studio
Open Android Studio.
Select File > Open.
Navigate to the cloned repository folder and select it.

Step 3: Set Up Firebase
Create a Firebase project:
Go to Firebase Console and create a new project.
Register your app with Firebase and download the google-services.json file.
Add the google-services.json file to your project's app/ directory in Android Studio.
Add Firebase dependencies to your build.gradle files if they are not already added.

Step 4: Set Up Spoonacular API
Sign up for an API key at Spoonacular API.
Open your project in Android Studio.
Navigate to BuildConfig in the project.
Add your Spoonacular API key to BuildConfig.API_KEY.

Step 5: Sync the Project with Gradle
In Android Studio, click on File > Sync Project with Gradle Files to ensure all dependencies are correctly loaded.

Step 6: Run the App
Connect your Android device to your computer or set up an Android emulator.
In Android Studio, click on the Run button or press Shift + F10 to build and run the app on your device.

How to Use the Fusion App

Step 1: Sign Up or Log In
Upon launching the app, you will be directed to the Login Page.
If you already have an account, enter your login credentials (email and password) and tap Login.
If you are new, tap on Sign Up, fill in your details (username, email, and password), and create an account.
After successful login, you will be taken to the HomePage.

Step 2: Browse and Search for Recipes
In the HomePage, you can search for recipes by entering a keyword in the search bar and pressing the search icon.
You can also filter recipes based on meal type, calories, or ingredients using the filter options.
The app will display recipes in a grid layout. Scroll through the list to browse available recipes.

Step 3: View Recipe Details
Select a recipe from the grid to view detailed information.
The app will open the RecipeDetailsActivity, where you can view:
Overview: A brief summary of the recipe.
Ingredients: A list of ingredients required for the recipe.
Steps: Detailed cooking instructions.
Nutrition: Nutritional information, including categorized nutrients.

Step 4: Add Recipes to Meal Planner
From the RecipeDetailsActivity, tap on the Meal Planner button to add the recipe to your weekly meal plan.
Select a day of the week and a meal time (e.g., breakfast, lunch, dinner) for the recipe.
Your selected recipe will be saved to the meal plan.

Step 5: Manage Your Shopping List
Tap on the Shopping Cart icon in the bottom navigation bar to open your ShoppingListPage.
The shopping list is categorized by ingredients. You can expand or collapse categories to view items.
Check off items as you purchase them, or remove them from the list.
You can also add new items to your shopping list by tapping on the Add Item button.

Step 6: Track Nutritional Information
When viewing a recipe, tap on the Nutrition tab to view the recipe's detailed nutritional information.
Nutrients are categorized and displayed for easy reference.

Step 7: Manage Your Profile and Settings
Tap on the Settings icon in the bottom navigation bar to access your profile settings.
From the SettingsPage, you can:
Edit your profile information.
Log out of the app.
Delete your account (requires password confirmation).
(Future Features: Change language and manage notifications).

Step 8: Log Out or Delete Your Account
In the SettingsPage, you can log out by tapping on the Logout button.
To delete your account, tap on Delete Account and confirm your password. This will permanently remove your account and data from the app.
