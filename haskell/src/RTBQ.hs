{-
Created       : 2014 Jul 29 (Tue) 07:16:51 by Harold Carr.
Last Modified : 2014 Aug 02 (Sat) 08:13:03 by Harold Carr.
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
        triple s p ((T.pack "Slug Magazine"), (T.pack "en"))
        return SelectQuery { queryVars = [s, p] }

dbAddress, dbQueryAddress :: String
dbAddress         = "http://localhost:3030/ds/"
dbQueryAddress    = dbAddress ++ "query"

sendQuery  :: Query SelectQuery -> IO (Maybe [[BindingValue]])
sendQuery  = selectQuery dbQueryAddress


tt :: IO ([String], [String], [String])
tt = do
    qr <- qAll
    let strs = [ map extract lbv | lbv <- fromJust qr ]
    return (nub $ map (!!0) strs, nub $ map (!!1) strs, nub $ map (!!2) strs)

-- LNode (PlainLL (T.pack "Slug Magazine") (T.pack "en"))

extract :: BindingValue -> String
extract (Bound v) = T.unpack $ case v of
    UNode x             -> x
    BNode x             -> x
--    BNodeGen x          -> show x
    LNode (PlainL x)    -> x
    LNode (PlainLL x _) -> x
    LNode (TypedL x _)  -> x
extract _ = error "TODO"

-- End of file.
