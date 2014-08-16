{-
Created       : 2014 Jul 29 (Tue) 07:16:51 by Harold Carr.
Last Modified : 2014 Aug 16 (Sat) 12:38:03 by Harold Carr.
-}

{-# LANGUAGE OverloadedStrings #-}

module RTBQ where

import           Data.List                       (nub)
import           Data.Maybe                      (fromJust)
import           Data.RDF.Types                  (LValue (..), Node (..))
import           Data.Text                       as T (unpack)
import           Database.HSparql.Connection
import           Database.HSparql.QueryGenerator

query2 :: String
          -> (Bool, BindingValue) -> (Bool, BindingValue) -> (Bool, BindingValue)
          -> IO ( [(String, BindingValue)]
                , [(String, BindingValue)]
                , [(String, BindingValue)]
                )
query2 url (isSVar,Bound s) (isPVar,Bound p) (isOVar, Bound o) =
    query1 url (isSVar, s) (isPVar, p) (isOVar, o)

query1 :: String
         -> (Bool, Node) -> (Bool, Node) -> (Bool, Node)
         -> IO ( [(String, BindingValue)]
               , [(String, BindingValue)]
               , [(String, BindingValue)]
               )
query1 url s@(isSVar,sn) p@(isPVar,pn) o@(isOVar,on) = testing $ query0 url s p o
  where
    testing qr = do
        qr' <- qr
        let pairs = [ map extract lbv | lbv <- fromJust qr' ]
        return ( ite pairs isSVar 0 sn
               , ite pairs isPVar 1 pn
               , ite pairs isOVar 2 on
               )
    ite pairs i t e = if i then nub $ map (!!t) pairs else [extract $ Bound e]

query0 :: String
        -> (Bool, Node) -> (Bool, Node) -> (Bool, Node)
        -> IO (Maybe [[BindingValue]])
query0 url (isSVar, sval) (isPVar, pval) (isOVar, oval) = selectQuery url q
  where
    q = do
        svar <- var
        pvar <- var
        ovar <- var
        triple (if isSVar then Var' svar else RDFNode sval)
               (if isPVar then Var' pvar else RDFNode pval)
               (if isOVar then Var' ovar else RDFNode oval)
        return SelectQuery { queryVars = [svar, pvar, ovar] }

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
