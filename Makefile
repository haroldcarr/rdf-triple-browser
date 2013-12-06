#
# Created       : 2006 Jul 26 (Wed) 14:50:24 by Harold Carr.
# Last Modified : 2013 Oct 19 (Sat) 17:19:30 by carr.
#

# mvn clean install
# make tc tb deploy
# http://localhost:8080/triple-browser-gwt-1.0-SNAPSHOT/
# make te undeploy clean
# mvn clean

TOMCAT_WAR_NAME	= triple-browser-gwt-1.0-SNAPSHOT

tc : FORCE
	cp -f gwt/target/$(TOMCAT_WAR_NAME).war .

clean : FORCE
	rm -rf $(TOMCAT_WAR_NAME).war

hcMakefiles=$(shell hcMakefiles)
include $(hcMakefiles)/binDirForceRules.gmk
include $(hcMakefiles)/tomcatDefs.gmk
include $(hcMakefiles)/tomcatRules.gmk

# End of file.
