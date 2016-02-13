#SF Movies

Link to the service: [SF Movies](http://sf-movies-1212.appspot.com/).

##Description
The SF Movies service shows places where different movies have been filmed in San Francisco. 
The date for the service was taken from [DataSF](http://www.datasf.org/): [Film Locations](https://data.sfgov.org/Arts-Culture-and-Recreation-/Film-Locations-in-San-Francisco/yitu-d5am).

##Technologies
###Backend
* [AppEngine](https://cloud.google.com/appengine/) as a platform:
    * Applications loaded there scale well
    * The platform has good management and monitoring tools
    * Google provides a good data storage system for AppEngine application
* [Google Cloud Datastore](https://cloud.google.com/storage/) as a storage for movies data:
    * Distributed, scales well
    * Good availability
    * Supports indexes (it was very convenient to use it for and auto complete)
* Java as a backend language:
    * I like Java :)
* [The Google Maps Geocoding API] (https://developers.google.com/maps/documentation/geocoding) used for coding locations names to geo coordinates
(was used at data preparation step so it is not working in a cloud).

###Frontend
* [JQuery](https://jquery.com/): provides a bunch of convenient javascript helper functions 
* [JQuery UI](https://jqueryui.com/): provides an auto complete control
* [Google Maps JavaScript API](https://developers.google.com/maps/documentation/javascript/)

##Architecture features
* Movies are stored in a denormalized format. So we need only one db request for search.
* For autocomplete is used index on the movie field which contains all prefixes of each word of the movie.
On search phase user's request is split on words and these words are searched among movies prefixes.
It works fast (as we use index for search) and allows not to maintain reversed index manually. 
* Data storage layer reads/writes data from/to database. Http handlers uses data layer to work with entities (movies).
So the service is stateless (scales as well as storage do and the storage do it well).
