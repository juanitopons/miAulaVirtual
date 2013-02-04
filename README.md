miAulaVirtual
=============

Android native App that connect to the virtual classroom of the University of Valencia and pull up student's information through scrapping (JSoup Library) and presents a friendly-mobile interface to be able to download archives in Android devices.

<i>Omnia sunt communia</i>

## Licenses ##
The code licensed here under the GNU General Public License, version 3 [GPL-3.0](http://www.gnu.org/licenses/gpl-3.0.html) has been developed by Juan Pons.

It is a standard version of miAulaVirtual, whose development and adaptation should be undertaken by those who implement it, corresponding to the specified license and without maintenance, legal or any other responsibility of Juan Pons.

Developed with usage of: Java, XML, Android, JSoup's licensed library and other licensed Java libraries.

## Current version ##
Version not yet fully functional

Android version compatibility: 2.0 and onward

## Features ##
+ University of Valencia <b>persistent</b> login
+ Virtual classroom hierarchy navigation
+ Documents download
+ Students' communities folders: grouping as a unique folder (giving priority to subjects folders)
+ Subjec regex pattern clean and replace (available desactivation through 'Preferences')

## To do ##
+ Save instance of the activity on screen orientation change
+ Change Android back button functionality: For going hierarchically back in navigation and not to exit the application
+ Add internet 3G, WIFI (not) access exception - <b><i>DONE</i></b>
+ Add virtual classroom 'down' (offline or unreachable) exception (Response.statusMessage())
+ Add content provider for the opening of documents after download
+ Remove keyboard just after sending the login
+ Check SDCARD space before downloading - <b><i>DONE</i></b>
+ Check if there is SDCARD. 
+ <b><i>ListView</i> Search<b/>
+ Clean code
+ Add 'cancel' button while downloading - <b><i>DONE</i></b>
+ Preference option for filtering empty folders
+ Preference option for language (Spanish, Catalan, English)
+ Folder direct access widget

## Change Log ##
None yet

## Known issues ##
+ <b>SOLVED: <i>Buffered download</i></b> - Memory allocation problem in downloading large files
+ <b>SOLVED: <i>Added support for ZEN style cookie</i></b> - Error getting resoucerse identifier

## Upcoming features ##
+ Task management
+ Cool calendar (?)
+ Webmail integration (?)
+ Remote repository of college notes created by students through the application (?)
+ New activity where student can see a list of already downloaded documents and they have not deleted
