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
    * Google provides a good data storage system for AppEngine applications
* [Google Cloud Datastore](https://cloud.google.com/storage/) as a storage for movies data:
    * Distributed, scales well
    * Good availability
    * Supports indexes (it was very convenient to use an index for autocomplete feature)
* Java as a backend language:
    * I love Java :)
* [The Google Maps Geocoding API] (https://developers.google.com/maps/documentation/geocoding) used for coding locations names to geo coordinates
(was used at a data preparation step so it is not working in a cloud).

###Frontend
* [JQuery](https://jquery.com/): provides a bunch of convenient javascript helper functions 
* [JQuery UI](https://jqueryui.com/): provides an autocomplete control
* [Google Maps JavaScript API](https://developers.google.com/maps/documentation/javascript/)

##Architecture features
* Movies are stored in a denormalized format. So we need only one db request for search and get results.
* How autocomplete works: a movie class has method onSave, which called on db store. 
Inside the method created an array which contains all prefixes of each word in the movie title.
This array stored in the database in indexed field. When user request arrives, a search phrase split
on words and these words searched in that indexed field.
Datastore indexes are distributes, work fast and allows not to maintain reversed index manually. 
* Data storage layer reads/writes data from/to the cloud database. Http handlers uses data storage layer to work 
with entities (movies). So the service is stateless (scales as well as storage do and the storage do it well).
