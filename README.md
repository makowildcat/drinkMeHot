![Alt text](/app/src/main/res/drawable-xhdpi/ic_launcher.png?raw=true "Icon")
# Drink Me Hot
_Android Geolocation App to find Café in Paris and New York_

Just a simple application (my first one) which show a list of café in a map. It gets the lists from open data.
- Café in Paris which offers coffee at €1 - [parisdata.opendatasoft.com](http://parisdata.opendatasoft.com/explore/dataset/liste-des-cafes-a-un-euro/)
- Sidewalk Café in New York - [nycopendata.socrata.com](https://nycopendata.socrata.com/)

## Features
- Quick and easy access the nearest café, and switch to the next one.
- Search for specific café based on the name or address.
- Save some café to a favorite list
- Switch between Paris or New York
- Support for different sizes/orientation of screens
- 5 languages: English, French, Chinese (traditional), Russian and German

![Alt text](/screenshot/ss_map.png?raw=true "Icon")
![Alt text](/screenshot/ss_menu.png?raw=true "Icon")

![Alt text](/screenshot/ss_landscape_near.png?raw=true "Icon")

## Known Issues
- Opendata from Paris and New York has changed, need an update to read it otherwise application (really) useless
- Code is maybe a bit ugly...

_Code refactoring is definitely needed, better use libraries to consume API such as OkHttp and should add some automated tests_
