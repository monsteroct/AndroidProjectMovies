# Project Movies
The work fetch movies information from [The Movie Database (TMDb)](https://www.themoviedb.org/). Users can browse movies, check detail information including rating, trailers, comments, etc. Also, user can mark movies as favorite, and the movies will be cached locally for offline review.

## Quick Start

```
$git clone https://github.com/saluf/android-project-movies.git
```

### TMBb API 
<img src="https://www.themoviedb.org/assets/2/v4/logos/v2/blue_square_1-5bdc75aaebeb75dc7ae79426ddd9be3b2be1e342510f8202baf6bffa71d7f5c4.svg"  width="150" />

To run this project, you have to [get personal API key](https://www.themoviedb.org/documentation/api), and save it in string resources.

```xml
<resources>
    <string name="tmdb_api_key">YOUR_API_KEY</string>
</resources>
```

## Screenshots
<img src="/screenshots/movie_list.png" width="150" height="308"/> <img src="/screenshots/movie_detail.png" width="150" height="308"/>


## License
The work is under MIT License. Please check LICENSE for more information.
