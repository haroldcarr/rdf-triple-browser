HCLOC  = $(shell hcLocation "ulhcd")
FLOC   = ${HCLOC}/java/apache/apache-jena-fuseki-3.14.0
TLOC   = ${HCLOC}/java/apache/tcpmon-1.0-bin
DLOC   = $(shell hcLocation "esync")/language/java/org/openhc/venuetime/metadata/

build :
	cabal build

run :
	cabal run

data : f load-data

f : tcpm fuseki

tcpm :
	java -cp ${TLOC}/build/tcpmon-1.0.jar  org.apache.ws.commons.tcpmon.TCPMon 3030 127.0.0.1 3029 &

fuseki : fusekiexec
	$(shell sleep 3)
	echo fuseki "$(shell lsof -i :3029)"

fusekiexec :
	( mkdir -p /tmp/rdf-triple-browser-fuseki-data ; \
	cd /tmp/rdf-triple-browser-fuseki-data ; \
	${FLOC}/fuseki-server --port 3029 --update --mem /ds & )

load-data :
	( cd ${DLOC} ; \
	${FLOC}/bin/s-put http://localhost:3030/ds/data --verbose default events.n3 ; \
	${FLOC}/bin/s-put http://localhost:3030/ds/data           default participants.n3 ; \
	${FLOC}/bin/s-put http://localhost:3030/ds/data           default places.n3 ; \
	${FLOC}/bin/s-put http://localhost:3030/ds/data           default sponsors.n3 )
