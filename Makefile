#
# Created       : 2006 Jul 26 (Wed) 14:50:24 by Harold Carr.
# Last Modified : 2007 Jun 03 (Sun) 22:21:41 by Harold Carr.
#

# tomcat order
# gm clean sc gc war tb deploy te undeploy

# gwt shell order
# gm clean sc gc bgs gs

###
### variables used by tomcatRules
###

TOMCAT_WAR_NAME	= differentity

###
### variables used by gwtRules
###

SRCDIR		= ./src
BINDIR		= ./bin
OUTDIR		= ./www
TOMCATDIR	= ./tomcat
URL		= com.differentity.Main
PKG_PATH	= com/differentity
SERVER_PATH	= $(SRCDIR)/$(PKG_PATH)/server

SERVER_FILES	= \
	$(SERVER_PATH)/Jena.java \
	$(SERVER_PATH)/ServiceImpl.java

###
### Rules
###

bgs : FORCE
	mkdir -p ./tomcat/webapps/ROOT
	cp ./all.rdf ./tomcat/webapps/ROOT

war : FORCE
	hcMakeGwtServiceWar -d `pwd` -w $(TOMCAT_WAR_NAME).war  -t all.rdf -l `hcJenaClasspath`

####
#### Misc.
####

clean : FORCE
	rm -rf $(BINDIR) $(OUTDIR) $(TOMCATDIR) $(TOMCAT_WAR_NAME).war

hcMakefiles=$(shell hcMakefiles)
include $(hcMakefiles)/gwtDefs.gmk
include $(hcMakefiles)/gwtRules.gmk
include $(hcMakefiles)/tomcatDefs.gmk
include $(hcMakefiles)/tomcatRules.gmk
include $(hcMakefiles)/binDirForceRules.gmk

# End of file.
