miAulaVirtual
=============

Android native App that connect to the virtual classroom of the University of Valencia and pull up student's information through scrapping (JSoup Library) and presents a friendly-mobile interface to be able to download archives in Android devices.

<i>Omnia sunt communia</i>

## Licenses ##
The code licensed here under the GNU General Public License, version 3 [GPL-3.0](http://www.gnu.org/licenses/gpl-3.0.html) has been developed by Juan Pons.

It is a standard version of miAulaVirtual, whose development and adaptation should be undertaken by those who implement it, corresponding to the specified license and without maintenance, legal or any other responsibility of Juan Pons.

Developed with usage of: Java, XML, Android, JSoup's licensed library and other licensed Java libraries.

## Current version ##
1.5

Android version compatibility: 2.3 and onward

## Features ##
+ University of Valencia <b>persistent</b> login
+ Virtual classroom hierarchy navigation
+ Documents download
+ Students' communities folders: grouping as a unique folder (giving priority to subjects folders)
+ Subjec regex pattern clean and replace (available desactivation through 'Preferences')
+ Open document after donwload
+ Compatibility with all virtual classroom themes and personalizations
+ Documents organized by subject
+ Documents manager (open, rename, delete, send, information)

## To do ##
+ Add image type
+ Let students select period days of task lists
+ Let student say if they want or not to open the file when it's downloaded
+ Remove keyboard just after sending the login
+ Clean code
+ Preference option for filtering empty folders
+ Preference option for language (Spanish, Catalan, English)
+ Folder direct access widget
+ Change Android back button functionality: For going hierarchically back in navigation and not to exit the application
+ <b><i>ListView</i> Search<b/>
+ Restrict orientation change on loading <b><i>DONE</i></b>
+ Save instance of the activity on screen orientation change <b><i>DONE</i></b>
+ Add internet 3G, WIFI (not) access exception - <b><i>DONE</i></b>
+ Add virtual classroom 'down' (offline or unreachable) exception (Response.statusMessage()) <b><i>DONE</i></b>
+ Add content provider for the opening of documents after download <b><i>DONE</i></b>
+ Check SDCARD space before downloading - <b><i>DONE</i></b>
+ Check if there is SDCARD. <b><i>DONE</i></b>
+ Add 'cancel' button while downloading - <b><i>DONE</i></b>

## Change Log ##
<b>1.5</b>
22-February-2013
+ Added File Manager (open, delete, rename, send/share, file properties)
+ Fixed communities bug
<b>1.0.2</b>
16-February-2013
+ Fixed last version bug
<b>1.0.1</b>
16-February-2013
+ Fixed some not very significant bugs
<b>1.0</b>
14-February-2013
+ Initial version

## Known issues ##
+ <b>SOLVED: <i>Buffered download</i></b> - Memory allocation problem in downloading large files
+ <b>SOLVED: <i>Added support for ZEN style cookie</i></b> - Error getting resoucerse identifier

## Upcoming features ##
+ Organize archive in subjects' folders <b><i>DONE</i></b>
+ Switch between years
+ Archive management <b><i>DONE</i></b>
+ Task management (Sync Students tasks with Google Tasks)
+ Cool calendar (?)
+ Webmail integration (?)
+ Remote repository of college notes created by students through the application (?)
