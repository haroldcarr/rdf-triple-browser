{-
Created       : 2014 Jul 29 (Tue) 07:16:51 by Harold Carr.
Last Modified : 2014 Jul 29 (Tue) 09:20:21 by Harold Carr.
-}

{-# LANGUAGE OverloadedStrings #-}

module RTBQ where


import           Data.Maybe                      (fromJust)
import           Data.RDF.Types                  (LValue (..), Node (..))
import           Data.Text                       as T (Text, pack, unpack)
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

dbAddress, dbQueryAddress :: String
dbAddress         = "http://localhost:3030/ds/"
dbQueryAddress    = dbAddress ++ "query"

sendQuery  :: Query SelectQuery -> IO (Maybe [[BindingValue]])
sendQuery  = selectQuery dbQueryAddress


-- import Data.RDF.Types

tt :: IO ([String], [String], [String])
tt = do
    qr0 <- qAll
    let strs = [ map extract lbv | lbv <- fromJust qr0 ]
    return (map (!!0) strs, map (!!1) strs, map (!!2) strs)

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
