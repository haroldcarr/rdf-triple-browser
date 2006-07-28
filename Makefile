#
# Created       : 2006 Jul 26 (Wed) 14:50:24 by Harold Carr.
# Last Modified : 2006 Jul 28 (Fri) 16:32:38 by Harold Carr.
#

JAVA_HOME	= $(ALT_BOOTDIR)/bin
JAVA		= $(JAVA_HOME)/java
JAVAC		= $(JAVA_HOME)/javac
GWT_HOME	= $(shell hcGwtHome)
PSEP		= $(shell hcPathSep)
GWT_DEV		= $(GWT_HOME)/gwt-dev-windows.jar
GWT_RUN		= $(GWT_HOME)/gwt-user.jar
CP		= "./src$(PSEP)./bin$(PSEP)$(GWT_DEV)$(PSEP)$(GWT_RUN)"

GWT_COMPILE	= $(JAVA) -cp $(CP) com.google.gwt.dev.GWTCompiler -out ./www * com.differentity.Main

SERVER_COMPILE	= $(JAVAC) -cp $(CP) -sourcepath ./src -d ./bin  src/com/differentity/server/MyServiceImpl.java 

GWT_SHELL	= $(JAVA)  -cp $(CP) com.google.gwt.dev.GWTShell -out ./www * com.differentity.Main/Main.html

all :
	-@echo "Usage: make [ sc | gc | gs ]"

gc :
	$(GWT_COMPILE)

sc :
	-echo $(SERVER_COMPILE)
	$(SERVER_COMPILE)

gs :
	$(GWT_SHELL)&

# End of file.
