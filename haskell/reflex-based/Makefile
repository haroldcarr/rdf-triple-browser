FUSEKI = jena-fuseki1-1.3.1
FLOC   = /tmp/${FUSEKI}
DLOC   = $(shell hcLocation "esync")/java/org/openhc/venuetime/metadata/

wo :
	~/try-reflex/work-on ghcjs ./../reflex-based

c :
	cabal configure --ghcjs

b :
	cabal build

css :
	cp index.html dist/build/RTB/RTB.jsexe

v o :
	/usr/bin/open -a "/Applications/Google Chrome.app" './dist/build/RTB/RTB.jsexe/index.html'

data : f load-data

f : uzf tcpm fuseki

uzf :
	( pushd /tmp ; \
	rm -rf ${FUSEKI} ; \
	unzip ~/Downloads/${FUSEKI}-distribution.zip )

tcpm :
	java -cp ~/.m2/repository/ws-commons/tcpmon/1.0/tcpmon-1.0.jar  org.apache.ws.commons.tcpmon.TCPMon 3030 127.0.0.1 3029 &

fuseki : fusekiexec
	$(shell sleep 3)
	echo fuseki "$(shell lsof -i :3029)"

fusekiexec :
	( cd ${FLOC} ; \
	./fuseki-server --port 3029 --update --mem /ds & )

load-data :
	( cd ${DLOC} ; \
	${FLOC}/s-put http://localhost:3030/ds/data --verbose default events.n3 ; \
	${FLOC}/s-put http://localhost:3030/ds/data           default participants.n3 ; \
	${FLOC}/s-put http://localhost:3030/ds/data           default places.n3 ; \
	${FLOC}/s-put http://localhost:3030/ds/data           default sponsors.n3 )
