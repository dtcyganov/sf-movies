function initMap() {
    var map = new google.maps.Map($('#map')[0], {
        center: {lat: 37.7749295, lng: -122.4194155}, // San Francisco
        zoom: 12,
    });

    var autoCompleteControl = createAutoCompleteControl(map);
    map.controls[google.maps.ControlPosition.TOP_LEFT].push(autoCompleteControl);
}

function createAutoCompleteControl(map) {
    var div = document.createElement('div');
    var input = $('<input></input>').
        attr({type: 'text'});

    input.autocomplete({
        autoFocus: true,
        delay: 0,
        minLength: 0,
        source: "/api/v1/suggest-movies-names",

        response: function(event, ui) {
            $.each(ui.content, function(key,value) {
                value.value = value.title;
                value.label = value.title;
            });
        },

        select: function(event, ui) {
            var url = ui.item.id;
            alert("Selected movie : " + ui.item.title + " #" + ui.item.id);
        },
    });
    input.addClass('topic-title');


    return $('<div></div>').append(input)[0];
}
