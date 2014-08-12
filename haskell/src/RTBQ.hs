{-
Created       : 2014 Jul 29 (Tue) 07:16:51 by Harold Carr.
Last Modified : 2014 Aug 12 (Tue) 16:26:18 by Harold Carr.
-}

{-# LANGUAGE OverloadedStrings #-}

module RTBQ where

import           Data.List                       (nub)
import           Data.Maybe                      (fromJust)
import           Data.RDF.Types                  (LValue (..), Node (..))
import           Data.Text                       as T (pack, unpack)
import           Database.HSparql.Connection
import           Database.HSparql.QueryGenerator

qAll :: IO (Maybe [[BindingValue]])
qAll = sendQuery q
  where
    q = do
        s <- var; p <- var; o <- var
        triple s p o
        return SelectQuery { queryVars = [s, p, o] }

qBook1 :: IO (Maybe [[BindingValue]])
qBook1 = sendQuery q
  where
    q = do
        dc <- prefix "dc" (iriRef "http://purl.org/dc/elements/1.1/")
        ex <- prefix "ex" (iriRef "http://example/")
        x  <- var
        triple (ex .:. "book1")  (dc .:. "title") (T.pack "A new book")
        return SelectQuery { queryVars = [x] }

qq1 :: IO (Maybe [[BindingValue]])
qq1 = sendQuery q
  where
    q = do
        p <- var; o <- var
        triple (iriRef (T.pack "http://openhc.org/data/event/Slug_Magazine_Salt_Lake_City_Utah")) p o
        return SelectQuery { queryVars = [p, o] }

qq2 :: IO (Maybe [[BindingValue]])
qq2 = sendQuery q
  where
    q = do
        s <- var; p <- var
        triple s p (T.pack "Slug Magazine", T.pack "en")
        return SelectQuery { queryVars = [s, p] }

{-
This change enables programmatic construction of triples from existing Node values
and where variables are not known in advance.
Here is an example of usage (tested with jena-fuseki-1.0.0 as the SPARQL server):
-}
callTest :: IO (Maybe [[BindingValue]])
callTest = test (False, UNode (T.pack "http://openhc.org/data/event/Slug_Magazine_Salt_Lake_City_Utah"))
                (True , UNode (T.pack "http://xmlns.com/foaf/0.1/name"))
                (True , LNode (PlainLL (T.pack "Slug Magazine") (T.pack "en")))

test :: (Bool, Node) -> (Bool, Node) -> (Bool, Node) -> IO (Maybe [[BindingValue]])
test (isSVar, sval) (isPVar, pval) (isOVar, oval) = sendQuery q
  where
    q = do
        svar <- var
        pvar <- var
        ovar <- var
        triple (if isSVar then Var' svar else RDFNode sval)
               (if isPVar then Var' pvar else RDFNode pval)
               (if isOVar then Var' ovar else RDFNode oval)
        return SelectQuery { queryVars = map fst $ filter snd (zip [svar, pvar, ovar] [isSVar, isPVar, isOVar]) }

dbAddress, dbQueryAddress :: String
dbAddress      = "http://localhost:3030/ds/"
dbQueryAddress = dbAddress ++ "query"

sendQuery  :: Query SelectQuery -> IO (Maybe [[BindingValue]])
sendQuery  = selectQuery dbQueryAddress

tt  :: IO ( [String]
          , [String]
          , [String]
          )
tt = do
    qr <- qAll
    let strs = [ map extract lbv | lbv <- fromJust qr ]
    return (nub $ map (!!0) strs, nub $ map (!!1) strs, nub $ map (!!2) strs)

ttwv :: (String, BindingValue)
        -> IO ( [(String, BindingValue)]
              , [(String, BindingValue)]
              , [(String, BindingValue)]
              )
ttwv v@(_, Bound n) = do
    qr <- sendQuery q
    let pairs = [ map (\l -> (extract l, l)) lbv | lbv <- fromJust qr ]
    return ([v], nub $ map (!!0) pairs, nub $ map (!!1) pairs)
  where
    q = do
        svar <- var
        pvar <- var
        ovar <- var
        triple (RDFNode n)
               (Var' pvar)
               (Var' ovar)
        return SelectQuery { queryVars = [ pvar, ovar ] }

ttt :: IO ( [(String, BindingValue)]
          , [(String, BindingValue)]
          , [(String, BindingValue)]
          )
ttt = do
    qr <- qAll
    let pairs = [ map (\l -> (extract l, l)) lbv | lbv <- fromJust qr ]
    return (nub $ map (!!0) pairs, nub $ map (!!1) pairs, nub $ map (!!2) pairs)

-- LNode (PlainLL (T.pack "Slug Magazine") (T.pack "en"))

extract :: BindingValue -> String
extract (Bound v) = T.unpack $ case v of
    UNode x             -> x
    BNode x             -> x
--    BNodeGen x          -> show x
    LNode (PlainL  x)   -> x
    LNode (PlainLL x _) -> x
    LNode (TypedL  x _) -> x
extract _ = error "TODO"

-- End of file.
