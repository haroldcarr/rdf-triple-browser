{-# LANGUAGE DeriveGeneric     #-}
{-# LANGUAGE MonoLocalBinds    #-}
{-# LANGUAGE LambdaCase        #-}
{-# LANGUAGE OverloadedStrings #-}
{-# LANGUAGE RecursiveDo       #-}

module Main where

import           Control.Applicative         ((<|>))
import           Control.Lens                ((.~), (&))
import qualified Control.Monad               as M
import           Data.Aeson.Types            as AT (FromJSON)
import qualified Data.Function               as DF (on)
import qualified Data.List                   as L (elemIndex, isPrefixOf, nub, sortOn, transpose)
import qualified Data.Map                    as Map (Map, fromList)
import qualified Data.Maybe                  as MB (fromJust, fromMaybe)
import qualified Data.Text                   as T (Text, pack, toLower, unpack)
import qualified GHC.Generics                as G (Generic)
import           Network.URI                 as N (escapeURIString, isUnescapedInURI)
import           Prelude                     as P
import           Reflex                      as R
import           Reflex.Dom                  as RD

data SPO = SUB | PRE | OBJ deriving Show

--              SUB     PRE     OBJ
data Req = Req !String !String !String deriving Show

--                raw (for debug)       subject   predicate object
data Resp = Resp ![(String, [String])] ![String] ![String] ![String] deriving Show

main :: IO ()
main = mainWidget $ do
  urlInput <- inputElement
    $ def
    & inputElementConfig_initialValue .~ "http://localhost:3030/ds/query"
    & inputElementConfig_elementConfig . elementConfig_initialAttributes
      .~ Map.fromList [("id", "sparqlURL")]

  let url = _inputElement_value urlInput
  eSubmit <- button "Submit"
  dCurSel <- divClass "spoPanels" $ do
    rec
      dSubSel <- mkSPOPanel SUB $ (\(Resp _ s _ _) -> s) <$> eResp
      dPreSel <- mkSPOPanel PRE $ (\(Resp _ _ p _) -> p) <$> eResp
      dObjSel <- mkSPOPanel OBJ $ (\(Resp _ _ _ o) -> o) <$> eResp

      let dReq = Req <$> dSubSel <*> dPreSel <*> dObjSel

      eResp <- requestSparql url (leftmost [ updated dReq
                                           , tagPromptlyDyn dReq eSubmit
                                           ])
      dCurSel <- holdDyn "http://haroldcarr.com/"
                 (ffilter
                  ("http" `L.isPrefixOf`)
                  (leftmost
                   [ updated dSubSel
                   , updated dPreSel
                   , updated dObjSel
                   ]))
      -- Debug
      M.when False $ do
        divClass "showRequest"    $ display dReq
        divClass "showQuery"      $ display ((urlEncode . toString) <$> dReq)

        dSparqlResp <- foldDyn (\(Resp sparql _ _ _) _ -> sparql) [] eResp

        divClass "showSparqlResp" $ display dSparqlResp
        divClass "showSelection"  $ display dCurSel

    pure dCurSel

  let dAAttr = (\x -> Map.fromList [ ("target", "_blank"), ("href", T.pack x) ]) <$> dCurSel
      dFrameAttr = (\x -> Map.fromList [ ("src", T.pack x) ])                    <$> dCurSel
  divClass "currentSelection" $ elDynAttr "a" dAAttr $ dynText (T.pack <$> dCurSel)
  elDynAttr "iframe" dFrameAttr blank

mkSPOPanel spo eContent =
  divClass (T.pack (show spo ++ "Panel")) $ do
    rec
      dSelection   <- holdDyn (fromSPO spo) (leftmost [ snd <$> eSelection
                                                      , fromSPO spo <$ eResetBtn
                                                      ])
      divClass (T.pack (show spo ++ "Selection")) $ dynText (T.pack <$> dSelection)
      eResetBtn    <- button "*"
      (e, _)       <- elDynAttr' "button" mempty $ dynText label
      currentState <- toggle False (domEvent Click e)
      let label = (\x -> if x then "-" else "+") <$> currentState
      -- Content arriving from the SPARQL response.
      dContent     <- holdDyn [] eContent
      -- Dropdown wants Map k Text.
      let dContentB = zipDynWith (\expanded xs -> T.pack <$> expandContract expanded xs)
                      currentState
                      dContent
      eSelection   <- divClass (T.pack (show spo ++ "List")) $ _dropdown_change <$>
                      dropdown (0, "") dContentB
                      (def & dropdownConfig_attributes .~ constDyn
                       (Map.fromList [ ("style", "width:300px")
                                     , ("size",  "15")
                                     ]))
    pure dSelection

requestSparql :: MonadWidget t m => Dynamic t T.Text -> Event t Req -> m (Event t Resp)
requestSparql dUrl eReq = do
  let eXhrReq = attachPromptlyDynWith mkReq dUrl eReq
  eRespRaw   <- performRequestsAsync eXhrReq
  pure $ handleResponse <$> respDebug eRespRaw
 where
  mkReq url req = ( req
                  , xhrRequest
                    "GET"
                    (url <> "?query=" <> T.pack (urlEncode (toString req)))
                    (def & xhrRequestConfig_headers .~
                      ("Accept" =: "application/sparql-results+json"))
                  )

handleResponse :: (Req, XhrResponse) -> Resp
handleResponse (req, xhrResp) = distributeResponse req resp
 where
  resp = case decodeXhrResponse xhrResp :: Maybe SparqlResults of
           Just x  -> traverseResults x
           Nothing -> []

distributeResponse :: Req -> [(String, [String])] -> Resp
distributeResponse (Req s p o) resp  =
  Resp resp (getSPO "subject" resp s) (getSPO "predicate" resp p) (getSPO "object" resp o)

getSPO :: a1 -> [(a1, [a2])] -> a2 -> [a2]
getSPO spo r d = MB.fromMaybe [d] (lookup spo r)

fromSPO :: SPO -> String
fromSPO = \case
  SUB -> "?subject"
  PRE -> "?predicate"
  OBJ -> "?object"

varOrEmpty :: String -> String
varOrEmpty = \case
  []        -> ""
  x@('?':_) -> x
  _         -> ""

bracket :: String -> String
bracket = \case
  x@('h':'t':'t':'p':_) -> "<" ++ x ++ ">"
  x@('?':_)             -> x
  x                     -> '"' : x ++ "\""

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

respDebug :: Event t (a, XhrResponse) -> Event t (a, XhrResponse)
respDebug x =
  if True
  then traceEventWith (T.unpack . MB.fromJust . _xhrResponse_responseText . snd) x
  else x

urlEncode :: String -> String
urlEncode = handleSharp . N.escapeURIString N.isUnescapedInURI
 where
  handleSharp = \case
    []       -> []
    ('#':xs) -> "%23" ++ handleSharp xs
    (x:xs)   ->  x  :  handleSharp xs

mkToggleBtn :: MonadWidget t m => String -> String -> m (Dynamic t Bool)
mkToggleBtn on off = do
  rec
    (e, _)       <- elDynAttr' "button" mempty $ dynText label
    currentState <- toggle False (domEvent Click e)
    let label     = T.pack . (\x -> if x then on else off) <$> currentState
  pure currentState

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
removeTrailing a = \case
  []                -> []
  as | last as == a -> init as
  as                -> as

------------------------------------------------------------------------------

data SparqlResults = SparqlResults
  { head    :: !VarsObject
  , results :: !BindingsVector
  } deriving (G.Generic, Show)

data VarsObject = VarsObject
  { vars :: ![String]
  } deriving (G.Generic, Show)

data BindingsVector = BindingsVector
  { bindings :: ![Binding]
  } deriving (G.Generic, Show)

data Binding = Binding
  { subject   :: !(Maybe BindingValue)
  , predicate :: !(Maybe BindingValue)
  , object    :: !(Maybe BindingValue)
  } deriving (G.Generic, Show)

data BindingValue = BindingValue
  { -- type :: String
    value :: !String
  } deriving (G.Generic, Show)

instance AT.FromJSON SparqlResults
instance AT.FromJSON VarsObject
instance AT.FromJSON BindingsVector
instance AT.FromJSON Binding
instance AT.FromJSON BindingValue

selectFun :: String -> Binding -> Maybe BindingValue
selectFun ("subject")   = subject
selectFun ("predicate") = predicate
selectFun ("object")    = object
selectFun x             = error ("not supported: " ++ x)

--                                    SPO     values
traverseResults :: SparqlResults -> [(String, [String])]
traverseResults (SparqlResults (VarsObject vs) (BindingsVector bs)) =
  P.zip vs $ P.map L.nub $ L.transpose $ traverseBindings (P.map selectFun vs) bs

traverseBindings :: [t -> Maybe BindingValue] -> [t] -> [[String]]
traverseBindings vs bs =
  case bs of
    []      -> []
    (b:bs') -> P.map (\f -> P.maybe "" Main.value (f b)) vs : traverseBindings vs bs'
