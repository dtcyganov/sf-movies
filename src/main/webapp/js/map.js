var map;
var markers = [];

function initMap() {
    map = new google.maps.Map($('#map')[0], {
        center: {lat: 37.7749295, lng: -122.4194155}, // San Francisco
        zoom: 12,
    });

    var autoCompleteControl = createAutoCompleteControl(map);
    map.controls[google.maps.ControlPosition.TOP_LEFT].push(autoCompleteControl);
}

function createAutoCompleteControl(map) {
    var input = $('<input></input>').
        attr({type: 'text'}).
        addClass('topic-title');

    addAutoComplete(input, function(item) {
        $.each(markers, function(key,marker) {
            marker.setMap(null);
        });
        markers = [];
        var controls = map.controls[google.maps.ControlPosition.RIGHT_TOP];
        clearArray(controls);

        $.each(item.locations, function(key,location) {
            markers.push(new google.maps.Marker({
                position: location,
                map: map,
                title: location.name
            }));
        });

        controls.push(createMovieDescriptionControl(map, item));
    });

    return $('<div></div>').append(input)[0];
}

function createMovieDescriptionControl(map,movie) {

    var title = movie.title + ' (' + movie.releaseYear + ')';

    var controlInfo = $('<div></div>')
        .addClass('movie-description-content')
        .append(personsElement('Director', 'Directors', movie.directors))
        .append(personsElement('Writer', 'Writers', movie.writers)).append($('<br/>'))
        .append(personsElement('Actors', 'Actors', movie.actors)).append($('<br/>'))
        .append(infoElement('Production company', movie.productionCompany))
        .append(infoElement('Distributor', movie.distributor));

    return $('<div></div>')
        .append(
            $('<div></div>')
                .addClass('movie-description-control')
                .append(
                    $('<div>' + title + '</div>').
                        addClass('movie-description-title')
                )
                .append(controlInfo)
        )[0];
}

function personsElement(fieldName, fieldNamePlural, items) {
    if (items == undefined || items == null || items.length == 0) {
        return $('<div></div>');
    }

    return $('<div></div>')
        .append($('<b>' + (items.length == 1 ? fieldName : fieldNamePlural) + ':</b>'))
        .append($('<br/>'))
        .append(
            $.map(items, function(v) {
                return $('<span style="color: #136CB2">' + v.name + '</span><br/>');
            })
        )
}

function infoElement(fieldName, fieldValue) {
    if (fieldValue == null || fieldValue == undefined) {
        return $('<div></div>');
    }
    return $('<div><b>' + fieldName + ':</b><br/>' + fieldValue + '<br/></div>');
}

function clearArray(arr) {
    while (arr.length > 0) {
        arr.pop();
    }
}