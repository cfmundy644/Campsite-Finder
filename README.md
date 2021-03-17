# Campsite-Finder
Web app to pull availability (based on travel dates) and review data for campgrounds within a certain radius of a location chosen by user
	
1. User enters an address, a radius that they are willing to travel, and planned travel dates
2. App converts address to lat / long
3. App checks database (of campgrounds) to find campgrounds within the radius (Federally managed campgrounds only, from recreation.gov RIDB project)
4. For campgrounds in radius, app does the following:
	1. App makes calls to the recreation.gov api to check availability at those campgrounds over the planned travel dates\
	2. App makes calls to google maps API to get campground ratings
	3. Future feature: app makes calls to other rating sites to get campground rating (see if the dyrt has an API)
	4. Future feature: app makes calls to the open weather API to find projected weather (on trip dates)
	5. Future feature: app makes calls to rec gov ratings and cell reception ratings info
5. App sorts list of campgrounds in radius by availability, then distance, and return as table to user
