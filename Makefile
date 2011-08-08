#
# Created       : 2006 Jul 26 (Wed) 14:50:24 by Harold Carr.
# Last Modified : 2011 Aug 08 (Mon) 16:16:23 by carr.
#

##############################################################################
#
# COMMON Section
#
##############################################################################

TROWSER_PACKAGE	=	org.openhc.trowser
TROWSER_PKGDIR	=	org/openhc/trowser

GWT_PACKAGE	=	$(TROWSER_PACKAGE).gwt
GWT_PKGDIR	=	$(TROWSER_PKGDIR)/gwt
COMMON_PKG	=	$(GWT_PACKAGE).common
COMMON_PKGDIR	=	$(GWT_PKGDIR)/common
GWT_SERVER_PACKAGE = 	$(GWT_PACKAGE).server
GWT_SERVER_PKGDIR = 	$(GWT_PKGDIR)/server

##############################################################################
#
# GWT Section
#
##############################################################################

hcMakefiles=$(shell hcMakefiles)
include $(hcMakefiles)/itexDefs.gmk

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

#TOMCAT_WAR_NAME	= trowser
# Note - undeploy doesn't work with this name (have to remove "target" from path)
TOMCAT_WAR_NAME	= target/trowser-1.0-SNAPSHOT

###
### variables used by gwtRules
###

SRCDIR		= ./src/main/java
BINDIR		= ./bin
OUTDIR		= ./www
TOMCATDIR	= ./tomcat
URL		= $(GWT_PACKAGE).Main
ENTRY_PAGE	= Main.html
SERVER_PATH	= $(SRCDIR)/$(GWT_PKGDIR)/server

SERVER_FILES	= \
	$(SERVER_PATH)/FileUploaderServlet.java \
	$(SERVER_PATH)/Jena.java \
	$(SERVER_PATH)/ServiceImpl.java \
	$(SERVER_PATH)/ServiceImplDelegate.java

APACHE_FILE_UPLOAD = $(shell hcApacheCommonsFileUploadJar)
APACHE_IO	= $(shell hcApacheCommonsIOJar)
APACHE_COMMON	= $(APACHE_FILE_UPLOAD)$(PSEP)$(APACHE_IO)
GWT_CLASSPATH_EXTRAS = $(APACHE_COMMON)

###
### Rules
###

JENA_LIBS	= $(shell hcJenaClasspath)
WAR_LIBS	= $(JENA_LIBS)$(PSEP)$(APACHE_COMMON)

bgs : FORCE
	mkdir -p ./tomcat/webapps/ROOT
	cp ./$(RDF_FILE) ./tomcat/webapps/ROOT

war : FORCE
	hcMakeGwtServiceWar -d `pwd` -w $(TOMCAT_WAR_NAME).war  -t $(RDF_FILE) -l "$(WAR_LIBS)"

rdf$(ITEX_FILENAME_SUFFIX_PRIVATE) : FORCE
	rm -f $(RDF_FILE)
	cp hc$(ITEX_FILENAME_SUFFIX_PRIVATE).rdf $(RDF_FILE)

rdf$(ITEX_FILENAME_SUFFIX_PUBLIC) : FORCE
	rm -f $(RDF_FILE)
	cp hc$(ITEX_FILENAME_SUFFIX_PUBLIC).rdf $(RDF_FILE)

####
#### Misc.
####

clean :: FORCE
	rm -rf .gwt-cache $(BINDIR) $(OUTDIR) $(TOMCATDIR) $(TOMCAT_WAR_NAME).war
	rm -rf .classes

include $(hcMakefiles)/gwtDefs.gmk
include $(hcMakefiles)/gwtRules.gmk
include $(hcMakefiles)/tomcatDefs.gmk
include $(hcMakefiles)/tomcatRules.gmk
include $(hcMakefiles)/binDirForceRules.gmk

##############################################################################
#
# SWING Section
#
##############################################################################

#
# Created       : 2008 May 15 (Thu) 17:24:10 by Harold Carr.
# Last Modified : 2011 Aug 08 (Mon) 16:16:23 by carr.
#

SEP		=	$(shell hcPathSep)
TOPDIR		=	.

SWING_PACKAGE	=	$(TROWSER_PACKAGE).swing
SWING_PKGDIR	=	$(TROWSER_PKGDIR)/swing
SWING_CLIENT_PACKAGE =	$(SWING_PACKAGE).client
SWING_CLIENT_PKGDIR  =	$(SWING_PKGDIR)/client

FILES_java	= 	$(shell ls $(SRCDIR)/$(COMMON_PKGDIR)/*.java $(SRCDIR)/$(SWING_CLIENT_PKGDIR)/*.java $(SRCDIR)/$(GWT_SERVER_PKGDIR)/Jena.java $(SRCDIR)/$(GWT_SERVER_PKGDIR)/ServiceImplDelegate.java)

LIB		=	$(TOPDIR)/lib/swing
DJLIB		=	$(LIB)/djnativeswing
DJLIBLIB	=	$(DJLIB)/lib
DJLIBLIBJNA	=	$(DJLIBLIB)/jna/jna.jar$(SEP)$(DJLIBLIB)/jna/jna_WindowUtils.jar
DJLIBLIBSWT	=	$(DJLIBLIB)/swt/swt-3.4M5-win32-win32-x86.jar
DJNATIVESWING	= 	$(DJLIB)/DJNativeSwing.jar$(SEP)$(DJLIBLIBJNA)$(SEP)$(DJLIBLIBSWT)

APPFRAMEWORK	=	$(LIB)/appframework-1.0.3.jar
SWINGLAYOUT	=	$(LIB)/swing-layout-1.0.3.jar
SWINGWORKER	=	$(LIB)/swing-worker-1.1.jar
NBSWING		=	$(APPFRAMEWORK)$(SEP)$(SWINGLAYOUT)$(SEP)$(SWINGWORKER)

# GWT_SERVLET only needed for IsSerializable marker in common dir.
CLASSPATH_EXTRAS= 	$(shell hcJenaClasspath)$(SEP)$(DJNATIVESWING)$(SEP)$(NBSWING)$(SEP)$(GWT_SERVLET)

all :: classes cpfv

## TODO: parameterize .classes
cpfv :
	cp -r $(SRCDIR)/$(SWING_CLIENT_PKGDIR)/resources .classes/$(SWING_CLIENT_PKGDIR)

#DEBUG_CLASSFILES= true
#DEBUG	= -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5000

r run ::
	hcJavaCmd "$(CLASSDESTDIR)$(SEP)$(CLASSPATH_EXTRAS)" $(DEBUG) $(SWING_CLIENT_PACKAGE).Trowser

rf runs ::
	hcJavaCmd "$(CLASSDESTDIR)$(SEP)$(CLASSPATH_EXTRAS)" $(DEBUG) $(SWING_CLIENT_PACKAGE).Trowser

include $(hcMakefiles)/javaDefs.gmk
include $(hcMakefiles)/javaRules.gmk

# End of file.
