{-# LANGUAGE RecursiveDo, ScopedTypeVariables #-}
{-# LANGUAGE GADTs, TemplateHaskell, QuasiQuotes #-}

-- =: creates a singleton Map

import qualified Data.Map as Map
import           Data.Map (Map)
import           Reflex
import           Reflex.Dom
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

main = mainWidget $ el "div" $ do
    -- Dynamic String
    url <- fmap value
                (textInput $ def & textInputConfig_initialValue .~ "http://localhost:3030/ds/query")
    -- Event ()
    btn <- button "Submit"
    rec
        s <- mkSPOPanel SUB (fmap (\(Resp s _ _) -> s) resp)
        p <- mkSPOPanel PRE (fmap (\(Resp _ p _) -> p) resp)
        o <- mkSPOPanel OBJ (fmap (\(Resp _ _ o) -> o) resp)
        f <- combineDyn Req s p
        req <- combineDyn ($) f o
        -- this line replaces the previous two lines
        -- req <- [mkDyn| Req $s $p $o  |]
        display req
        resp <- requesting url (leftmost [updated req, tagDyn req btn])
        -- widgetHold blank (fmap (\x -> text $ show x) resp)
    return ()

data Req = Req String String String deriving Show

data Resp = Resp [String] [String] [String] deriving Show

requesting :: MonadWidget t m => Dynamic t String -> Event t Req -> m (Event t Resp)
requesting url e =
    delay 1 $ attachDynWith doReq url e

doReq :: String -> Req -> Resp
doReq url (Req s p o) =
    Resp (tail $ mkDummyData s) (tail $ mkDummyData p) (tail $ mkDummyData o)

mkDummyData :: String -> [String]
mkDummyData spo = Prelude.map (++ spo) dummyData

dummyData :: [String]
dummyData = [ "http://foo.bar/a"
            , "http://foo.bar/b"
            , "http://foo.bar/c"
            , "http://foo.bar/d"
            , "http://foo.bar/e"
            , "http://foo.bar/f"
            , "http://foo.bar/g"
            , "http://foo.bar/h"
            , "http://foo.bar/i"
            ]

