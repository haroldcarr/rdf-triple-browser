{-# LANGUAGE DeriveGeneric     #-}
{-# LANGUAGE GADTs #-}
{-# LANGUAGE OverloadedStrings #-}
{-# LANGUAGE QuasiQuotes #-}
{-# LANGUAGE RecursiveDo #-}
{-# LANGUAGE ScopedTypeVariables #-}
{-# LANGUAGE TemplateHaskell #-}

module Main where

import qualified Data.Aeson                 as A (decode)
import qualified Data.Aeson.Types           as AT (FromJSON)
import qualified Data.ByteString.Lazy.Char8 as BSC8
import qualified Data.ByteString.Lazy       as BSL
import qualified Data.List                  as L (isPrefixOf, nub, transpose)
import qualified Data.Map                   as Map
import           Data.Map                   (Map)
import qualified Data.Maybe                 as MB (fromJust, fromMaybe)
import qualified Data.Text                  as T (unpack)
import           Data.Text.Encoding         as T
import qualified GHC.Generics               as G (Generic)
import           Network.URI                as N (escapeURIString, isUnescapedInURI)
import           Prelude                    as P
import           Reflex                     as R
import           Reflex.Dom                 as RD
import           Reflex.Dom.Time
import           Control.Monad (join)

mkToggleBtn :: MonadWidget t m => String -> String -> m (Dynamic t Bool)
mkToggleBtn on off = do
    -- e is HTML element
    rec (e,_) <- elAttr' "button" mempty $
            dynText label
        currentState <- toggle False (domEvent Click e)
        label <- mapDyn (\x -> if x then on else off) currentState
    return currentState

data SPO = SUB | PRE | OBJ

fromSPO x = case x of
    SUB -> "?subject"
    PRE -> "?predicate"
    OBJ -> "?object"

mkSPOPanel :: MonadWidget t m => SPO -> Event t [String]-> m (Dynamic t String)
mkSPOPanel spo contentE = divClass "spoPanel" $ do
  rec
    panel <- holdDyn (fromSPO spo) (leftmost [selection, fromSPO spo <$ resetBtn])
    divClass "spoValue" $ dynText panel
    resetBtn <- button "*"
    tglBtnVal <- mkToggleBtn "+" "-"
    content <- holdDyn mempty (fmap (Map.fromList . join zip) contentE)
    selection <- divClass "spoList" $
       fmap _dropdown_change $ dropdown "" content (def & dropdownConfig_attributes .~ (constDyn ("size" =: "5")))
  return panel

main = mainWidget $ do
    elAttr "iframe" (Map.fromList [("style", "display: none;")]) blank
    divClass "main" $ do
        -- Dynamic String
        url <- fmap RD.value
                    (textInput $ def & textInputConfig_initialValue .~ "http://localhost:3030/ds/query")
        -- Event ()
        btn <- button "Submit"
        rec
            s <- mkSPOPanel SUB (fmap (\(Resp _ s _ _) -> s) resp)
            p <- mkSPOPanel PRE (fmap (\(Resp _ _ p _) -> p) resp)
            o <- mkSPOPanel OBJ (fmap (\(Resp _ _ _ o) -> o) resp)
            f <- combineDyn Req s p
            req <- combineDyn ($) f o
            -- this line replaces the previous two lines (but is SLOW to compile)
            -- req <- [mkDyn| Req $s $p $o  |]
            -- TMP : show the Request
            divClass "REQ" (display req)
            md <- mapDyn (urlEncode . toString) req
            -- TMP : show the query
            divClass "query" (display md)
            resp <- requesting url (leftmost [updated req, tagDyn req btn])
            -- TMP : show sparql response
            q <- (foldDyn (\(Resp sparql s p o) _ -> sparql) [] resp)
            divClass "results" (display q)
            -- widgetHold blank (fmap (\x -> text $ show x) resp)
            linkedData <- holdDyn "http://haroldcarr.com/" (leftmost [updated s, updated p, updated o])
            frameattr <- mapDyn (\x -> if "http" `L.isPrefixOf` x
                                       then Map.fromList [("top","target"),("src",x)]
                                       else Map.fromList [("top","target")])
                                linkedData
            -- TMP : show the link given to the browser frame
            display linkedData
        el "frameset" $ do
            elDynAttr "frame" frameattr blank
    return ()

data Req = Req String String String deriving Show

varOrEmpty []        = ""
varOrEmpty x@('?':_) = x
varOrEmpty _         = ""

bracket x@('h':'t':'t':'p':_) = "<" ++ x ++ ">"
bracket x@('?':_)             = x
bracket x                     = '"' : x ++ "\""

toString :: Req -> String
toString (Req s p o) =
    unwords ["SELECT",  varOrEmpty s, varOrEmpty p, varOrEmpty o,
             "WHERE {", bracket    s, bracket    p, bracket    o, ".}"]

--               raw                  subject  predicate object
data Resp = Resp [(String, [String])] [String] [String] [String] deriving Show

requesting :: MonadWidget t m => Dynamic t String -> Event t Req -> m (Event t Resp)
requesting url req = do
    -- attachDynWith :: (a -> b -> c) ->  Dynamic a -> Event b -> Event c
    let r = attachDynWith mkReq url req
    -- performRequestAsync :: Event XhrRequest -> m (Event XhrResponse)
    x <- performRequestsAsync r
    let resp = fmap handleResponse (traceEventWith (T.unpack . MB.fromJust . _xhrResponse_responseText . snd) x)
    return resp
  where
    mkReq url req =
        -- xhrRequest :: String -> String -> XhrRequestConfig -> XhrRequest
        (req
        ,xhrRequest "GET"
                   (url ++ "?query=" ++ (urlEncode . toString) req)
                   -- _xhrRequestConfig_headers :: Map String String
                   (setHeaders def ("Accept" =: "application/sparql-results+json")))

handleResponse :: (Req, XhrResponse) -> Resp
handleResponse (req, xhrResp) =
    let resp = case _xhrResponse_responseText xhrResp of
                   Just x  -> traverseResults (MB.fromJust $ A.decode (BSC8.pack (T.unpack x)) :: SparqlResults)
                   Nothing -> []
    in distributeResponse req resp

distributeResponse (Req s p o) resp  =
    Resp resp (getSPO "subject" resp s) (getSPO "predicate" resp p) (getSPO "object" resp o)

getSPO spo r d =
    MB.fromMaybe [d] (lookup spo r)

setHeaders (XhrRequestConfig h u p r s) hdrs =
    XhrRequestConfig hdrs u p r s

urlEncode = escapeURIString isUnescapedInURI
    {-
    case x of
    [] -> []
    (' ':xs) -> "%20" ++ urlEncode xs
    ('?':xs) -> "%3F" ++ urlEncode xs
    ('{':xs) -> "%7B" ++ urlEncode xs
    ('}':xs) -> "%7D" ++ urlEncode xs
    ('<':xs) -> "%3C" ++ urlEncode xs
    ('>':xs) -> "%3E" ++ urlEncode xs
    (':':xs) -> "%3A" ++ urlEncode xs
    ('/':xs) -> "%2F" ++ urlEncode xs
    ('#':xs) -> "%23" ++ urlEncode xs
    ('@':xs) -> "%40" ++ urlEncode xs
    (x:xs)   -> x : urlEncode xs
   -}
------------------------------------------------------------------------------

data SparqlResults = SparqlResults {
      head    :: VarsObject
    , results :: BindingsVector
    } deriving (G.Generic, Show)

data VarsObject = VarsObject {
      vars :: [String]
    } deriving (G.Generic, Show)

data BindingsVector = BindingsVector {
      bindings :: [Binding]
    } deriving (G.Generic, Show)

data Binding = Binding {
      subject   :: Maybe BindingValue
    , predicate :: Maybe BindingValue
    , object    :: Maybe BindingValue
    } deriving (G.Generic, Show)

data BindingValue = BindingValue {
      -- type :: String
      value :: String
    } deriving (G.Generic, Show)

instance AT.FromJSON SparqlResults
instance AT.FromJSON VarsObject
instance AT.FromJSON BindingsVector
instance AT.FromJSON Binding
instance AT.FromJSON BindingValue

selectFun ("subject")   = subject
selectFun ("predicate") = predicate
selectFun ("object")    = object
selectFun x             = error ("not supported: " ++ x)

traverseResults (SparqlResults (VarsObject vs) (BindingsVector bs)) =
    P.zip vs $ P.map L.nub $ L.transpose $ traverseBindings (P.map selectFun vs) bs

traverseBindings vs bs = case bs of
    []      -> []
    (b:bs') -> P.map (\f -> Main.value $ MB.fromJust $ f b) vs : traverseBindings vs bs'

{-
urlEncode " SELECT  ?x0 ?x1 ?x2 WHERE {<http://openhc.org/data/event/Slug_Magazine_Salt_Lake_City_Utah> ?x1 ?x2 .} Limit 100"

"%20SELECT%20%20%3Fx0%20%3Fx1%20%3Fx2%20WHERE%20%7B%3Chttp%3A%2F%2Fopenhc.org%2Fdata%2Fevent%2FSlug_Magazine_Salt_Lake_City_Utah%3E%20%3Fx1%20%3Fx2%20.%7D%20Limit%20100"

"%20SELECT%20%20%3Fx0%20%3Fx1%20%3Fx2%20WHERE%20%7B%3Chttp%3A%2F%2Fopenhc.org%2Fdata%2Fevent%2FSlug_Magazine_Salt_Lake_City_Utah%3E%20%3Fx1%20%3Fx2%20.%7D%20Limit%20100" == "%20SELECT%20%20%3Fx0%20%3Fx1%20%3Fx2%20WHERE%20%7B%3Chttp%3A%2F%2Fopenhc.org%2Fdata%2Fevent%2FSlug_Magazine_Salt_Lake_City_Utah%3E%20%3Fx1%20%3Fx2%20.%7D%20Limit%20100"

   " SELECT  ?x0 ?x1 ?x2 WHERE {<http://openhc.org/data/event/Slug_Magazine_Salt_Lake_City_Utah> ?x1 ?x2 .} Limit 100"
-}
