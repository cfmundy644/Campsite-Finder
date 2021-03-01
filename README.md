# Campsite-Finder
Web app to pull availability (based on travel dates) and review data for campgrounds within a certain radius of a location chosen by user
	
User enters an address, a radius that they are willing to travel, and planned travel dates\
app converts address to lat / long\
app checks database (of campgrounds) to find campgrounds within the radius (Federally managed campgrounds only, from recreation.gov RIDB project)\	
for campgrounds in radius, app does the following:\
-app makes calls to the recreation.gov api to check availability at those campgrounds over the planned travel dates\
-app makes calls to google maps API to get campground ratings\
-Future feature: app makes calls to other rating sites to get campground rating (see if the dyrt has an API)\
-Future feature: app makes calls to the open weather API to find projected weather (on trip dates)\
-Future feature: app makes calls to rec gov ratings and cell reception ratings info\
App sorts list of campgrounds in radius by availability, then distance, and return as table to user\	
