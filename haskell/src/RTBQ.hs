{-
Created       : 2014 Jul 29 (Tue) 07:16:51 by Harold Carr.
Last Modified : 2014 Aug 17 (Sun) 20:44:01 by Harold Carr.
-}

{-# LANGUAGE OverloadedStrings #-}

module RTBQ where

import           Data.List                       (nub)
import           Data.Maybe                      (fromJust)
import           Data.RDF.Types                  (Node (..))
import           Database.HSparql.Connection
import           Database.HSparql.QueryGenerator

query2 :: String
          -> (Bool, BindingValue) -> (Bool, BindingValue) -> (Bool, BindingValue)
          -> IO ( [BindingValue]
                , [BindingValue]
                , [BindingValue]
                )
query2 url (isSVar,Bound s) (isPVar,Bound p) (isOVar, Bound o) =
    query1 url (isSVar, s) (isPVar, p) (isOVar, o)

query1 :: String
         -> (Bool, Node) -> (Bool, Node) -> (Bool, Node)
         -> IO ( [BindingValue]
               , [BindingValue]
               , [BindingValue]
               )
query1 url s@(isSVar,sn) p@(isPVar,pn) o@(isOVar,on) = testing $ query0 url s p o
  where
    testing qr = do
        qr' <- qr
        let r = fromJust qr'
        return ( ite r isSVar 0 sn
               , ite r isPVar 1 pn
               , ite r isOVar 2 on
               )
    ite r i t e = if i then nub $ map (!!t) r else [Bound e]

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

-- End of file.
