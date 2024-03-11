# Programming-3
 University of Oulu course on API + server + database design and implementation. 2024

## Branches ##
Each week's assignments are stored in separate branches.

Starting from week-2, a self-signed certificate is required. Generate it using the following command:

```
keytool -genkey -alias alias -keyalg RSA -keystore keystore.jks -keysize 2048
Ensure that the first and last names are set to 'localhost'.
```
Additionally, for testing purposes, your browser's certificate is needed. You can find it under the padlock icon in Chrome. Export it and add it to the root of the project.



# Week-6 Functionality #

## Base Features ##


### Register and authenticate: ###

**Register a new user at https://localhost:8001/registration**


POST JSON:
```
{"username" : "username", "password" : "password", "email" : "user.email@for-contacting.com", "userNickname" : "tester"}
```

Example curl: 
```
curl -k -d "@user.json" https://localhost:8001/registration -H "Content-Type: application/json"
```

Passwords are hashed inside the server.


### Send and get sightseeing locations: ###


**Register a new user at https://localhost:8001/info**

POST JSON:
```
{"locationName" : "locationName", "locationDescription" : "locationDescription", "locationCity" : "locationCity", "locationCountry" : "Finland", "locationStreetAddress": "Kirkkokatu 3 A", "originalPostingTime" : "2024-02-17T21:09:51.123Z", "latitude" : "65.0580507136066", "longitude" : "25.469414591526057"}
```

Latitude and longitude coordinates are optional.

Example curl:
```
curl -k -d "@message.json" https://localhost:8001/info -H "Content-Type: application/json" -u username:password
```

GET Request:

Returns all the messages sent by the user, if wrong credentials are provided nothing is sent.

Example curl:
```
curl -k https://localhost:8001/info -H "Content-Type: application/json" -u username:password
```

## Additional Features ##


### Attach weather information to the message ###


POST JSON:

```
{"locationName" : "locationName", "locationDescription" : "locationDescription", "locationCity" : "locationCity", "locationCountry" : "Finland", "locationStreetAddress": "Kirkkokatu 3 A", "originalPostingTime" : "2024-02-17T21:09:51.123Z", "latitude" : "65.0580507136066", "longitude" : "25.469414591526057", "weather" : ""}
```

The server fetches the weather data based on the geographic coordinates using the provided API weatherserver-http.jar
It accepts the XLM format: 

```
<coordinates>
    <latitude>28.23333</latitude>
    <longitude>10.23344</longitude>
</coordinates>
```

Example curl:
```
curl -k -d "@messageWeather.json" https://localhost:8001/info -H "Content-Type: application/json" -u username:password
```

The server then returns the weather data on the next GET request.


### Update a message ###


POST JSON:
```
{"locationID" : 1, "locationName" : "locationName", "locationDescription" : "locationDescription", "locationCity" : "locationCity", "locationCountry" : "Finland", "locationStreetAddress": "Kirkkokatu 3 A", "originalPostingTime" : "2024-02-17T21:09:51.123Z", "originalPoster" : "tester", "updateReason" : "helloWhyDidIupdate"}
```

The update reason is optional.

The server checks if the message is originally made by the same person and updates the data to match the new message. It also adds the modification date and it can also fetch the weather data if requested. 

Example curl:

```
curl -k -d "@messageUpdate.json" https://localhost:8001/info -H "Content-Type: application/json" -u username:password
```

### Visit a location and get Top five locations ###

POST JSON
```
{"locationID" : 1, "locationVisitor" : "pekka"}
```

The server adds a visit to the location. Only registered users can visit locations.

Example curl:
```
curl -k -d "@messageVisit.json" https://localhost:8001/info -H "Content-Type: application/json" -u username:password
```

GET request:

See TopFive at https://localhost:8001/topfive

Example curl:
```
curl -k https://localhost:8001/topfive -H "Content-Type: application/json" -u username:password
```

The server returns the top five most visited locations.


JOOQ was better than working with direct SQL, but I prefer Springdata's approach where database schemas are defined within classes. It also offers some handy abstractions for databse queries, additionally Springdata JPA is database-agnostic. In this course, we were restricted to using only basic libraries, and SpringBoot was not recommended.  This lead me to miss some of its features although some things are simpler without the framework. In the end this was definitely one of the most enjoyable courses during my studies.
