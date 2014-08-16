{-
Created       : 2014 Jul 29 (Tue) 07:16:51 by Harold Carr.
Last Modified : 2014 Aug 15 (Fri) 18:29:14 by Harold Carr.
-}

{-# LANGUAGE OverloadedStrings #-}

module RTBQ where

import           Data.List                       (nub)
import           Data.Maybe                      (fromJust)
import           Data.RDF.Types                  (LValue (..), Node (..))
import           Data.Text                       as T (pack, unpack)
import           Database.HSparql.Connection
import           Database.HSparql.QueryGenerator

{-
This change enables programmatic construction of triples from existing Node values
and where variables are not known in advance.
Here is an example of usage (tested with jena-fuseki-1.0.0 as the SPARQL server):
-}

endPoint :: String
endPoint = "http://localhost:3030/ds/query"

callTest :: IO (Maybe [[BindingValue]])
callTest = test endPoint
                (False, UNode (T.pack "http://openhc.org/data/event/Slug_Magazine_Salt_Lake_City_Utah"))
                (True , UNode (T.pack "http://xmlns.com/foaf/0.1/name"))
                (True, LNode (PlainLL (T.pack "Slug Magazine") (T.pack "en")))

test :: String -> (Bool, Node) -> (Bool, Node) -> (Bool, Node) -> IO (Maybe [[BindingValue]])
test url (isSVar, sval) (isPVar, pval) (isOVar, oval) = selectQuery url q
  where
    q = do
        svar <- var
        pvar <- var
        ovar <- var
        triple (if isSVar then Var' svar else RDFNode sval)
               (if isPVar then Var' pvar else RDFNode pval)
               (if isOVar then Var' ovar else RDFNode oval)
--        return SelectQuery { queryVars = map fst $ filter snd (zip [svar, pvar, ovar] [isSVar, isPVar, isOVar]) }
        return SelectQuery { queryVars = [svar, pvar, ovar] }

callTest' :: IO ( [(String, BindingValue)]
                , [(String, BindingValue)]
                , [(String, BindingValue)]
                )
callTest' = test' endPoint
                  (False, UNode (T.pack "http://openhc.org/data/event/Slug_Magazine_Salt_Lake_City_Utah"))
                  (True , UNode (T.pack "http://xmlns.com/foaf/0.1/name"))
                  (True , LNode (PlainLL (T.pack "Slug Magazine") (T.pack "en")))

test' :: String -> (Bool, Node) -> (Bool, Node) -> (Bool, Node) -> IO ( [(String, BindingValue)]
                                                                      , [(String, BindingValue)]
                                                                      , [(String, BindingValue)]
                                                                      )
test' url s@(isSVar,sn) p@(isPVar,pn) o@(isOVar,on) = testing $ test url s p o
  where
    testing qr = do
        qr' <- qr
        let pairs = [ map extract lbv | lbv <- fromJust qr' ]
        return ( ite pairs isSVar 0 sn
               , ite pairs isPVar 1 pn
               , ite pairs isOVar 2 on
               )
    ite pairs i t e = if i then nub $ map (!!t) pairs else [extract $ Bound e]


test'' :: String -> (Bool, BindingValue) -> (Bool, BindingValue) -> (Bool, BindingValue) -> IO ( [(String, BindingValue)]
                                                                                               , [(String, BindingValue)]
                                                                                               , [(String, BindingValue)]
                                                                                               )
test'' url (isSVar,Bound s) (isPVar,Bound p) (isOVar, Bound o) =
    test' url (isSVar, s) (isPVar, p) (isOVar, o)

ttwv :: String
        -> (String, BindingValue)
        -> IO ( [(String, BindingValue)]
              , [(String, BindingValue)]
              , [(String, BindingValue)]
              )
ttwv url v@(_, Bound n) = do
    putStrLn ("ttwv: " ++ url ++ " " ++ show v)
    qr <- selectQuery url q
    let pairs = [ map extract lbv | lbv <- fromJust qr ]
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

ttt :: String
       -> IO ( [(String, BindingValue)]
             , [(String, BindingValue)]
             , [(String, BindingValue)]
             )
ttt url = do
    qr <- qAll url
    let pairs = [ map extract lbv | lbv <- fromJust qr ]
    return (nub $ map (!!0) pairs, nub $ map (!!1) pairs, nub $ map (!!2) pairs)

qAll :: String -> IO (Maybe [[BindingValue]])
qAll url = selectQuery url q
  where
    q = do
        s <- var; p <- var; o <- var
        triple s p o
        return SelectQuery { queryVars = [s, p, o] }

tt :: IO ( [String]
         , [String]
         , [String]
         )
tt = do
  (a,b,c) <- ttt endPoint
  return (map fst a, map fst b, map fst c)

-- LNode (PlainLL (T.pack "Slug Magazine") (T.pack "en"))

extract :: BindingValue -> (String, BindingValue)
extract b@(Bound v) =
    let s = case v of
                (UNode x)             -> x
                (BNode x)             -> x
                --    BNodeGen x            -> show x
                (LNode (PlainL  x))   -> x
                (LNode (PlainLL x _)) -> x
                (LNode (TypedL  x _)) -> x
    in (T.unpack s, b)
extract Unbound   = (show Unbound, Unbound)

-- End of file.
