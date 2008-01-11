#
# Created       : 2006 Jul 26 (Wed) 14:50:24 by Harold Carr.
# Last Modified : 2008 Jan 10 (Thu) 16:24:58 by Harold Carr.
#

# tomcat order
# gm clean sc gc war tb deploy te undeploy

# gwt shell order
# gm clean sc bgs gs

###
### local variables
###

RDF_FILE	= rdf.rdf

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
ENTRY_PAGE	= Main.html
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
	cp ./($RDF_FILE) ./tomcat/webapps/ROOT

war : FORCE
	hcMakeGwtServiceWar -d `pwd` -w $(TOMCAT_WAR_NAME).war  -t $(RDF_FILE) -l `hcJenaClasspath`

rdf-private : FORCE
	rm -f $(RDF_FILE)
	cp hc-private.rdf $(RDF_FILE)

rdf-public : FORCE
	rm -f $(RDF_FILE)
	cp hc-public.rdf $(RDF_FILE)

####
#### Misc.
####

clean : FORCE
	rm -rf .gwt-cache $(BINDIR) $(OUTDIR) $(TOMCATDIR) $(TOMCAT_WAR_NAME).war

hcMakefiles=$(shell hcMakefiles)
include $(hcMakefiles)/gwtDefs.gmk
include $(hcMakefiles)/gwtRules.gmk
include $(hcMakefiles)/tomcatDefs.gmk
include $(hcMakefiles)/tomcatRules.gmk
include $(hcMakefiles)/binDirForceRules.gmk

# End of file.
