# Ringer App

This is an android application that changes the ringer mode based on the location of the user. It is not server dependent application. 

#### Features

- User can create a multiple user profile 
- Change ringer mode of the phone
  
  

### Instalation guide

---

In order to run the project you need to have to have [Mapbox account](https://www.mapbox.com/). **[How to get Mapbox API token.](https://docs.mapbox.com/help/tutorials/get-started-tokens-api/)** 

1. Clone this repository and import into **Android Studio**

```bash
git clone git@github.com:Mohammad-Safayet/Ringer-App.git
```

2. Add the mapbox api key to ```local.properties```

```kotlin
MAPBOX_API_TOKEN=YOUR_API_KEY
```

3. Then run the app using the **Run 'app'** button
   
   

### Screenshots

---





<img title="" src="https://github.com/Mohammad-Safayet/Ringer-App/blob/main/screenshots/landing_page_empty.jpg" alt="Landing Page Empty" width="163"> <img title="" src="https://github.com/Mohammad-Safayet/Ringer-App/blob/main/screenshots/profile_input_page.jpg" alt="Profile input page" width="163"> <img title="" src="https://github.com/Mohammad-Safayet/Ringer-App/blob/main/screenshots/clock_dialog.jpg" alt="" width="162">

<img title="" src="https://github.com/Mohammad-Safayet/Ringer-App/blob/main/screenshots/map_page.jpg" alt="" width="163"> <img title="" src="https://github.com/Mohammad-Safayet/Ringer-App/blob/main/screenshots/profile_input_page_filled.jpg" alt="" width="163"> <img title="" src="https://github.com/Mohammad-Safayet/Ringer-App/blob/main/screenshots/landing_page_with_profile.jpg" alt="" width="163">



### Built With

---

1. [Room](https://developer.android.com/jetpack/androidx/releases/room) - Android persistence library provides an abstraction layer over SQLite

2. [Coroutines](https://developer.android.com/kotlin/coroutines?gclsrc=ds&gclsrc=ds) - Library for concurrency design pattern

3. [Mapbox](https://www.mapbox.com/) - SDK to provider of custom online maps

4. [Livedata](https://developer.android.com/topic/libraries/architecture/livedata) - Library for observable data holder class

5. [Navigation Component](https://developer.android.com/guide/navigation?gclsrc=ds&gclsrc=ds) - Library to allow users to navigate between different pieces of content within app.