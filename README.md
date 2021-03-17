# Campsite-Finder #
### Web app that generates a list of campgrounds within a certain radius of a location chosen by user, along with availability information (based on user travel dates) and Google ratings ###
	
1. **User enters an address, a radius that they are willing to travel, and planned travel dates**
2. **App converts address to lat / long using [Google Geocoding API](https://developers.google.com/maps/documentation/geocoding/overview)**
3. **App checks AWS database containing a list of campgrounds and corresponding lat / longs to find campgrounds within the radius (source info from [Recreation.gov RIDB project](https://ridb.recreation.gov/))**
4. **For campgrounds in radius, app does the following:**
	1. App makes calls to the [Recreation.gov RIDB API](https://ridb.recreation.gov/) to check availability at those campgrounds over the planned travel dates
	2. App makes calls to [Google Places API](https://developers.google.com/maps/documentation/places/web-service/overview) to get campground ratings
	3. *Future features:*
		1. *App makes calls to other rating sites to get campground rating (such as Tripadvisor)*
		2. *App makes calls to a weather data API to find projected weather (during trip dates)*
		3. *App makes calls to rec gov ratings and cell reception ratings info*
5. **App sorts list of campgrounds in radius by availability, then distance, and return as table to user**


***Screenshot 1: Input Page***
![](https://github.com/cfmundy644/Campsite-Finder/blob/main/screenshot1.PNG?raw=true)\
***Screenshot 2: Results Page***
![Results page](https://github.com/cfmundy644/Campsite-Finder/blob/main/screenshot2.PNG?raw=true)
