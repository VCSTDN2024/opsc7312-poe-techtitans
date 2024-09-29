# **Fusion App**

Fusion is a meal planning and recipe management application designed to simplify grocery and cooking tasks. The app offers features such as meal planning, grocery list management, recipe browsing, and nutritional data tracking. With Firebase integration, users can securely store their data and manage accounts.

## **Contributors**
This project is developed as part of OPSC7312:

- **ST10024454** Matteo Pita  
- **ST10161340** Tyler Friedman  
- **ST10046014** Nicholas James Malan  
- **ST10043367** Reece Cunningham  
- **ST10043352** Shira Bome

## **Minimum System Requirements**
### **Operating System**
- Windows 8.1, 10, or 11

### **Hardware Requirements**
- **Processor:** 1.8 GHz or faster (Quad-core or better recommended)
- **RAM:** 2 GB minimum (8 GB recommended; 12.5 GB if using a virtual PC)
- **Storage:** 
  - Minimum 800 MB for basic installation  
  - Complete installation: ~210 GB  
  - SSD recommended for better performance
- **Display:** Minimum 720p (1280 x 720); WXGA (1366 x 768) or higher recommended

## **Features**

### 1. **Browse and Search Recipes**
- Users can browse recipes on the HomePage using search terms or filters like ingredients, meal type, and calories.
- Recipes are displayed in a grid format, and users can tap on any recipe for detailed information using Retrofit.

### 2. **Recipe Details**
- The RecipeDetailsActivity provides an overview, ingredients, steps, and nutritional information for selected recipes.
- Users can swipe between sections using ViewPager2 and view images with Glide.
- Recipes can be added to meal plans or marked as favorites.

### 3. **Meal Planning**
- Users can plan meals for specific days and meal times on the MealPlannerPage.
- Recipes can be added to meal plans, and meal planning data is stored in Firebase.

### 4. **Shopping List Management**
- Users can manage their shopping list on the ShoppingListPage, with items organized by category.
- Real-time syncing with Firebase ensures updates are instantly saved.
- This uses our own rest API which is hosted on render.com 

### 5. **Nutritional Information**
- The NutritionFragment displays detailed nutritional data, grouped into categories.
- NutrientAdapter and NutrientCategoryAdapter handle the display of nutrient data in a RecyclerView.

### 6. **User Authentication**
- Users can sign up or log in using Firebase Authentication. Account creation, login, and deletion are handled through Firebase.

### 7. **Profile Management and Settings**
- The SettingsPage allows users to manage their profile, log out, or delete their account.
- Account deletion removes all associated data from Firebase.

### 8. **Upcoming Features**
- **LanguagePage:** Future feature allowing users to change the app language (not yet implemented).
- **NotificationsPage:** Future feature for managing app notifications (not yet implemented).

### 9. **Firebase Integration**
- Firebase is used for user authentication and real-time database storage for meal plans, shopping lists, and more.

### 10. **API Integration**
- The app uses Retrofit to fetch recipe and nutritional data from the Spoonacular API.

## **Setup Instructions**

### Step 1: Clone the Repository
Clone the Fusion repository to your local machine:
```bash
git clone <repository_url>
