#
# Created       : 2006 Jul 26 (Wed) 14:50:24 by Harold Carr.
# Last Modified : 2011 Aug 10 (Wed) 21:45:07 by carr.
#

# tomcat order
# tc tb deploy te undeploy clean

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
