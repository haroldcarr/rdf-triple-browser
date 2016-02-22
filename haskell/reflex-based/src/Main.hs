{-# LANGUAGE DeriveGeneric       #-}
{-# LANGUAGE OverloadedStrings   #-}
{-# LANGUAGE RecursiveDo         #-}

module Main where

import           Control.Applicative        ((<$>), (<|>))
import qualified Control.Monad              as M (join, when)
import qualified Data.Aeson                 as A (decode)
import qualified Data.Aeson.Types           as AT (FromJSON)
import qualified Data.ByteString.Lazy.Char8 as BSC8
import qualified Data.ByteString.Lazy       as BSL
import qualified Data.Function              as DF (on)
import qualified Data.List                  as L (elemIndex, isPrefixOf, nub, sortOn, transpose)
import qualified Data.Map                   as Map (Map, fromList)
import qualified Data.Maybe                 as MB (fromJust, fromMaybe)
import qualified Data.Text                  as T (pack, toLower, unpack)
import qualified GHC.Generics               as G (Generic)
import qualified Network.URI                as N (escapeURIString, isUnescapedInURI)
import           Prelude                    as P
import           Reflex                     as R
import           Reflex.Dom                 as RD

data SPO = SUB | PRE | OBJ deriving Show

--             SUB    PRE    OBJ
data Req = Req String String String deriving Show

--               raw (for debug)      subject  predicate object
data Resp = Resp [(String, [String])] [String] [String] [String] deriving Show

main = mainWidget $ do
    elAttr "iframe" (Map.fromList [("style", "display: none;")]) blank
    divClass "main" $ do
        url        <- fmap RD.value
                           (textInput $ def {_textInputConfig_initialValue = "http://localhost:3030/ds/query"
                                            ,_textInputConfig_attributes   =
                                             constDyn $ Map.fromList [("id","sparqlURL")]})
        eSubmit    <- button "Submit"
        dFrameAttr <- divClass "spoPanels" $ do
            rec
                dSubSel   <- mkSPOPanel SUB (fmap (\(Resp _ s _ _) -> s) eResp)
                dPreSel   <- mkSPOPanel PRE (fmap (\(Resp _ _ p _) -> p) eResp)
                dObjSel   <- mkSPOPanel OBJ (fmap (\(Resp _ _ _ o) -> o) eResp)
                dReqSPSel <- combineDyn Req dSubSel   dPreSel
                dReq      <- combineDyn ($) dReqSPSel dObjSel
                eResp     <- requesting url (leftmost [updated dReq, tagDyn dReq eSubmit])
                dCurSel   <- holdDyn "http://haroldcarr.com/"
                                      (ffilter ("http" `L.isPrefixOf`)
                                               (leftmost [updated dSubSel, updated dPreSel, updated dObjSel]))
                dFrmAttr  <- mapDyn (\x -> Map.fromList [("top","target"),("src",x)]) dCurSel
                -- rest at this level is all debug
                M.when False $ do
                    divClass "showRequest"    (display dReq)
                    divClass "showQuery"      (display =<< mapDyn (urlEncode . toString) dReq)
                    divClass "showSparqlResp" (display =<< foldDyn (\(Resp sparql _ _ _) _ -> sparql) [] eResp)
                    -- because of ffilter above, this won't show non-URL selections
                    divClass "showSelection"  (display dCurSel)
            return dFrmAttr
        el "frameset" $
            elDynAttr "frame" dFrameAttr blank
        return ()

mkSPOPanel :: MonadWidget t m => SPO -> Event t [String]-> m (Dynamic t String)
mkSPOPanel spo eContent = divClass (show spo ++ "Panel") $ do
  rec
    dSelection <- holdDyn (fromSPO spo) (leftmost [fmap snd eSelection, fromSPO spo <$ eResetBtn])
    divClass (show spo ++ "Selection") $ dynText dSelection
    eResetBtn  <- button "*"
    dTglBtnVal <- mkToggleBtn "-" "+"
    dContent   <- holdDyn mempty eContent
    dContentB  <- combineDyn expandContract dTglBtnVal dContent
    eSelection <- divClass (show spo ++ "List") $
       _dropdown_change <$>
           dropdown (0,"") dContentB (def & dropdownConfig_attributes
                                            .~ constDyn (Map.fromList [("style", "width:300px")
                                                                      ,("size", "15")]))
  return dSelection

requesting :: MonadWidget t m => Dynamic t String -> Event t Req -> m (Event t Resp)
requesting url req = do
    let eReq  = attachDynWith mkReq url req
    eRespRaw <- performRequestsAsync eReq
    return $ fmap handleResponse (respDebug eRespRaw)
  where
    mkReq url req =
        (req -- for debugging only
        ,xhrRequest "GET"
                    (url ++ "?query=" ++ (urlEncode . toString) req)
                    (setHeaders def ("Accept" =: "application/sparql-results+json")))

handleResponse :: (Req, XhrResponse) -> Resp
handleResponse (req, xhrResp) =
    let resp = case _xhrResponse_responseText xhrResp of
            Just x  -> traverseResults
                           (MB.fromJust $ A.decode (BSC8.pack (T.unpack x)) :: SparqlResults)
            Nothing -> []
    in distributeResponse req resp

distributeResponse (Req s p o) resp  =
    Resp resp (getSPO "subject" resp s) (getSPO "predicate" resp p) (getSPO "object" resp o)

getSPO spo r d =
    MB.fromMaybe [d] (lookup spo r)

setHeaders (XhrRequestConfig h u p r s) hdrs =
    XhrRequestConfig hdrs u p r s

fromSPO x = case x of
    SUB -> "?subject"
    PRE -> "?predicate"
    OBJ -> "?object"

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

expandContract :: Bool -> [String] -> Map.Map (Int, String) String
expandContract b v =
    -- retain List sort via ordered int keys in Map
    Map.fromList (map (\p@(n,x) -> (p, if b then x else shorten x)) sorted)
  where
    lowShort = shorten . T.unpack . T.toLower . T.pack
    sorted   = zip [1 .. ] (L.sortOn lowShort v)

respDebug x =
     if True
        then traceEventWith (T.unpack . MB.fromJust . _xhrResponse_responseText . snd) x
        else x

urlEncode = handleSharp . N.escapeURIString N.isUnescapedInURI
  where
    handleSharp []       = []
    handleSharp ('#':xs) = "%23" ++ handleSharp xs
    handleSharp   (x:xs) =    x  :  handleSharp xs

mkToggleBtn :: MonadWidget t m => String -> String -> m (Dynamic t Bool)
mkToggleBtn on off = do
    -- e is HTML element
    rec (e,_)        <- elAttr' "button" mempty $ dynText label
        currentState <- toggle False (domEvent Click e)
        label        <- mapDyn (\x -> if x then on else off) currentState
    return currentState

------------------------------------------------------------------------------
-- Utility

-- | Strip off everything before '#' (the first one if multiples),
--   or, if no '#' is present, strip off everything before the last '/',
--   or, if neither '#' or '/' is present, return the given String.
--
-- >>> shorten "http://www.geonames.org/ontology#postalCode"
-- "postalCode"
--
-- >>> shorten "http://www.geonames.org/ontology#postalCode/"
-- "postalCode/"
--
-- >>> shorten "http://xmlns.com/foaf/0.1/homepage"
-- "homepage"
--
-- >>> shorten "http://xmlns.com/foaf/0.1/homepage/"
-- "homepage"
--
-- >>> shorten "Music Garage"
-- "Music Garage"

shorten :: String -> String
shorten s0 = drop (cutIndex + 1) s
  where
    s        = if '#' `elem` s0 then s0 else removeTrailing '/' s0
    cutIndex = MB.fromJust (elemIndexEnd '#' s <|> elemIndexEnd '/' s <|> Just (-1))

elemIndexEnd :: Eq a => a -> [a] -> Maybe Int
elemIndexEnd a as = (length as - 1 -) <$> L.elemIndex a (reverse as)

removeTrailing :: Eq a => a -> [a] -> [a]
removeTrailing a as | last as == a = take (length as - 1) as
                    | otherwise    = as
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

--                                    SPO     values
traverseResults :: SparqlResults -> [(String, [String])]
traverseResults (SparqlResults (VarsObject vs) (BindingsVector bs)) =
    P.zip vs $ P.map L.nub $ L.transpose $ traverseBindings (P.map selectFun vs) bs

traverseBindings vs bs = case bs of
    []      -> []
    (b:bs') -> P.map (\f -> Main.value $ MB.fromJust $ f b) vs : traverseBindings vs bs'
