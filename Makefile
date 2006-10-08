#
# Created       : 2006 Jul 26 (Wed) 14:50:24 by Harold Carr.
# Last Modified : 2006 Oct 08 (Sun) 14:07:38 by Harold Carr.
#

SRCDIR		= ./src
BINDIR		= ./bin
OUTDIR		= ./www
TOMCATDIR	= ./tomcat
TMPDIR		= ./tmp
WAR_FILE	= ./differentity.war
URL		= com.differentity.Main
PKG_PATH	= com/differentity
SERVER_PATH	= $(SRCDIR)/$(PKG_PATH)/server

JAVA_HOME	= $(ALT_BOOTDIR)/bin
JAVA		= $(JAVA_HOME)/java
JAVAC		= $(JAVA_HOME)/javac
GWT_HOME	= $(shell hcGwtHome)
GWT_WIDGETS_JAR = $(shell hcGwtWidgetsJar)
PSEP		= $(shell hcPathSep)
GWT_DEV         = $(GWT_HOME)/gwt-dev-windows.jar
GWT_SERVLET     = $(GWT_HOME)/gwt-servlet.jar
GWT_USER        = $(GWT_HOME)/gwt-user.jar
JENA_CP		= $(shell hcJenaClasspath)
DEPLOY_DIR	= $(shell hcTomcatHome)/webapps

SERVER_FILES	= \
	$(SERVER_PATH)/Jena.java \
	$(SERVER_PATH)/ServiceImpl.java

CP		= "$(JENA_CP)$(PSEP)$(SRCDIR)$(PSEP)$(BINDIR)$(PSEP)$(GWT_DEV)$(PSEP)$(GWT_USER)$(PSEP)$(GWT_SERVLET)$(PSEP)$(GWT_WIDGETS_JAR)"
GWT_COMPILER	= $(JAVA) -cp $(CP) com.google.gwt.dev.GWTCompiler
GWT_COMPILER_HELP = $(GWT_COMPILER) --help
GWT_COMPILE	= $(GWT_COMPILER) -out $(OUTDIR) $(URL)

SERVER_COMPILE	= $(JAVAC) -cp $(CP) -sourcepath $(SRCDIR) -d $(BINDIR) $(SERVER_FILES)

GWT_SHELLER	= $(JAVA)  -cp $(CP) com.google.gwt.dev.GWTShell
GWT_SHELLER_HELP= $(GWT_SHELLER) -help
GWT_SHELL	= $(GWT_SHELLER) -out $(OUTDIR) $(URL)/Main.html

####
#### 
####

all :
	-@echo "Usage: make [ sc | gc | gs ]"

gc :
	$(GWT_COMPILE)

gch :
	$(GWT_COMPILER_HELP)

sc : $(BINDIR)
	-echo $(SERVER_COMPILE)
	$(SERVER_COMPILE)

gs :
	$(GWT_SHELL)&

gsh :
	$(GWT_SHELLER_HELP)

war : FORCE
	./war

####
#### Tomcat
####

tb : FORCE
	hcTomcatStart

te : FORCE
	hcTomcatStop

undeploy : FORCE
	rm -rf $(DEPLOY_DIR)/differentity*

deploy : FORCE
	cp differentity.war $(DEPLOY_DIR)

ta : te undeploy tb deploy

####
#### Misc.
####

$(BINDIR) : FORCE
	mkdir -p $(BINDIR)

clean : FORCE
	rm -rf $(BINDIR) $(OUTDIR) $(TOMCATDIR) $(TMPDIR) $(WAR_FILE)

FORCE :

# End of file.
