{-
Created       : by threepenny-gui/samples/CRUD
Last Modified : 2014 Aug 10 (Sun) 11:53:32 by Harold Carr.
-}

{-# LANGUAGE RecursiveDo #-}

module CRUD2 where

import qualified Data.Map                    as Map
import           Data.Maybe
import           Data.RDF.Types              (Node (..))
import qualified Data.Text                   as T (pack)
import           Database.HSparql.Connection
import qualified Graphics.UI.Threepenny      as UI
import           Graphics.UI.Threepenny.Core
import           Prelude                     hiding (lookup)
import           RTBQ

------------------------------------------------------------------------------

main :: IO ()
main = do
    startGUI defaultConfig $ \window -> do
        return window # set title "RDF Triple Browser"
        getBody window #+ [ mkSPVPanel "?subject" ]
        return ()

mkSPVPanel :: String
              -> UI Element
mkSPVPanel spvType = mdo
    -- GUI elements
    doItBtn     <- UI.button #+ [string "Do It!"]
    addToLBBtn  <- UI.button #+ [string "Add To List Box"]
    listBox     <- UI.listBox  bLBItems bLBSelection bDisplayDI
    lbSelection <- dataItem    bLBSelectionDI
    let uiDI = grid [[string spvType, element lbSelection]]
    element listBox # set (attr "size") "20" # set style [("width","800px")]

    -- events and behaviors
    let eLBSelection = rumors $ UI.userSelection listBox
        eAddToLB     = UI.click addToLBBtn

    (eFillLB, hFillLB) <- liftIO newEvent
    on UI.click doItBtn $ \_ -> do
        (r,_,_) <- liftIO doRDFQuery;
        liftIO $ hFillLB r;
        return ()

    -- database
    -- bDB :: Behavior (DB DI)
    bDB <- accumB emptydb $ concatenate <$> unions
        [ dbSubmit <$ eAddToLB
        , dbFill <$> eFillLB
        ]

    -- selection
    -- bLBSelection :: Behavior (Maybe DBKey)
    bLBSelection <- stepper Nothing eLBSelection

    let bLookup :: Behavior (DBKey -> Maybe DI)
        bLookup = flip lookup <$> bDB

        bShowDI :: Behavior (DBKey -> String)
        bShowDI = (maybe "" showDI .) <$> bLookup

        bDisplayDI = (UI.string .) <$> bShowDI

        bLBItems :: Behavior [DBKey]
        bLBItems = keys <$> bDB

        bLBSelectionDI :: Behavior (Maybe DI)
        bLBSelectionDI = (=<<) <$> bLookup <*> bLBSelection

    -- GUI layout
    grid [
           [ uiDI ]
         , [ element listBox ]
         , [ element addToLBBtn]
         , [ element doItBtn ]
         ]

------------------------------------------------------------------------------
-- DB Model

type DBKey = Int
data DB a  = DB { nextKey :: !Int, db :: Map.Map DBKey a }

emptydb :: DB a
emptydb = DB 0 Map.empty

keys :: DB a -> [DBKey]
keys    = Map.keys . db

dbSubmit :: DB DI -> DB DI
dbSubmit     (DB newkey db0) = DB newkey $ Map.map (\(x,y) -> (x ++ "xx", y)) db0

dbFill :: [(String,BindingValue)] -> DB DI -> DB DI
dbFill    ss _ = foldr create  emptydb ss

create :: a -> DB a -> DB a
create x     (DB newkey db0) = DB (newkey+1) $ Map.insert newkey x db0

lookup :: DBKey -> DB a -> Maybe a
lookup key   (DB _      db0) = Map.lookup                       key      db0

------------------------------------------------------------------------------
-- What is stored in data base

type DI = (String, BindingValue)

showDI :: DI -> String
showDI (x,_) = x

-- | Data item widget
dataItem :: Behavior (Maybe DI) -> UI Element
dataItem bItem = do
    entry1 <- UI.entry $ fst . fromMaybe ("",Bound $ UNode (T.pack "foo")) <$> bItem -- TODO something other than Bound as "empty"
    element entry1 # set style [("width", "800px")]
    return $ getElement  entry1

------------------------------------------------------------------------------

doRDFQuery :: IO ( [(String, BindingValue)]
                 , [(String, BindingValue)]
                 , [(String, BindingValue)]
                 )
doRDFQuery = ttt

-- End of file.
