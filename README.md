miAulaVirtual
=============

Android native App that connect to the virtual classroom of the University of Valencia and pull up student's information through scrapping (JSoup Library) and presents a friendly-mobile interface to be able to download archives in Android devices.

<i>Omnia sunt communia</i>

## Current version ##
Version not yet fully functional

Android version compatibility: 2.0 and onward

## Features ##
+ University of Valencia <b>persistent</b> login
+ Virtual classroom hierarchy navigation
+ Documents download
+ Students' communities folders: grouping as a unique folder (giving priority to subjects folders)
+ Subjec regex pattern clean and replace (available desactivation through 'Preferences')

## To do - <i>Done</i> ##
+ Save instance of the activity on screen orientation change
+ Change Android back button functionality: For going hierarchically back in navigation and not to exit the application
+ Add internet 3G, WIFI (not) access exception
+ Add virtual classroom 'down' (offline or unreachable) exception (Response.statusMessage())
+ Add content provider for the opening of documents after download
+ Remove keyboard just after sending the login
+ Check if there is SDCARD. Check SDCARD space before downloading.
+ <b><i>ListVie</i> Search<b/>
+ Clean code

## Change Log ##

## Known issues ##
+ Memory allocation problem in downloading large files
+ Error getting resoucerse identifier (Just in few beta testers. I'm working on it)

## Upcoming features ##
+ Task management
+ Cool calendar
+ Webmail integration (?)
+ Remote repository of college notes created by students through the application (?)
