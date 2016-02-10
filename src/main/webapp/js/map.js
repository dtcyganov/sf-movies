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
    var div = document.createElement('div');
    var input = $('<input></input>').
        attr({type: 'text'}).
        addClass('topic-title');

    addAutoComplete(input, function(item) {
        $.each(markers, function(key,marker) {
            marker.setMap(null);
        });
        markers = [];
        $.each(item.locations, function(key,location) {
            var marker = new google.maps.Marker({
                position: location,
                map: map,
                title: location.name
            });
            markers.push(marker);
        });
    });

    return $('<div></div>').append(input)[0];
}
