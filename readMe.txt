Project prototype "Infrastructure Status Visualizer"
It was used to demonstrate the display of events on the Russian Railways infrastructure.

Main functionality:

	Display of various types of events on the infrastructure.
	Set event display styles.
	Set filters for infrastructure events and save them for later use.
	Define groupings of events on the infrastructure and save them for later use.
	Build graphs on event tables.
	Build reports on the event table (including those with complex headers).
	Import tables into exel files with headers, groupings and styles preserved. 
	Etc. )))

Demo video (RU) is available at https://cloud.mail.ru/public/DkQg/X4tPN2wxn

To build the project, you need gwt-2.4.0 and smartgwt-5.0p
Library paths must be edited in build.properties build.xml.

The libraries themselves can be downloaded:
smartgwt-5.0p link: https://cloud.mail.ru/public/ivyc/LmXGdmyCR
gwt-2.4.0 link: https://cloud.mail.ru/public/nKga/4xuvKZ85X

To run, you need to download a demo database (~210M packed)
link: https://cloud.mail.ru/public/MBFJ/VSsQbQk7V

You can use Tomcat 6 to run the database jndi setup examples are in
context.xml server.xml
In addition, you can download the configured Tomcat 6, where in the server.xml file, correct the path in the url
resources to the demo database derbyDb
link: https://cloud.mail.ru/public/srnv/UGHc59hvm

