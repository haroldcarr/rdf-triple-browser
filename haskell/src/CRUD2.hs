{-
Created       : by threepenny-gui/samples/CRUD
Last Modified : 2014 Aug 04 (Mon) 19:30:31 by Harold Carr.
-}

{-# LANGUAGE RecursiveDo #-}

module CRUD2 where

import qualified Data.Map                    as Map
import           Data.Maybe
import qualified Graphics.UI.Threepenny      as UI
import           Graphics.UI.Threepenny.Core
import           Prelude                     hiding (lookup)
import           System.Random

------------------------------------------------------------------------------

main :: IO ()
main = do
    frp <- mkListBoxFRP
    startGUI defaultConfig $ \window -> do
        return window # set title "RDF Triple Browser"
        getBody window #+ [ mkSPVPanel frp "?subject" ]
        return ()

mkListBoxFRP :: IO (Event [a], Handler [a], Behavior [a])
mkListBoxFRP = do
    (eventFillListBox, handlerFillListBox) <- newEvent
    behaviorFillListBox <- stepper [] eventFillListBox
    return (eventFillListBox, handlerFillListBox, behaviorFillListBox)

mkSPVPanel :: (Event [String], Handler [String], Behavior [String])
              -> String
              -> UI Element
mkSPVPanel (eFillListBox, hFillListBox, bFillListBox) spvType = mdo
    -- GUI elements
    doItBtn           <- UI.button #+ [string "Do It!"]
    addToListBoxBtn   <- UI.button #+ [string "Add To List Box"]
    listBox           <- UI.listBox  bListBoxItems bSelection bDisplayDataItem
    (spvSelection, _) <- dataItem    bSelectionDataItem
    let uiDataItem = grid [[string spvType, element spvSelection]]
    element listBox # set (attr "size") "10" # set style [("width","200px")]

    -- events and behaviors
    let eSelection    = rumors $ UI.userSelection listBox
        eAddToListBox = UI.click addToListBoxBtn

    let doQuery = do {
        r <- liftIO $ sideEffects;
        liftIO $ hFillListBox [r];
        return ()
    }

    -- submit button
    on UI.click doItBtn $ \_ -> do
        doQuery


    -- database
    -- bDatabase :: Behavior (Database DataItem)
    bDatabase <- accumB emptydb $ concatenate <$> unions
        [ hcSubmit <$ eAddToListBox
        , hcEFill <$> eFillListBox
        ]

    -- selection
    -- bSelection :: Behavior (Maybe DatabaseKey)
    bSelection <- stepper Nothing $ eSelection

    let bLookup :: Behavior (DatabaseKey -> Maybe DataItem)
        bLookup = flip lookup <$> bDatabase

        bShowDataItem :: Behavior (DatabaseKey -> String)
        bShowDataItem = (maybe "" showDataItem .) <$> bLookup

        bDisplayDataItem = (UI.string .) <$> bShowDataItem

        bListBoxItems :: Behavior [DatabaseKey]
        bListBoxItems = (\show0 -> filter ((const True) . show0) . keys)
                    <$> bShowDataItem <*> bDatabase

        bSelectionDataItem :: Behavior (Maybe DataItem)
        bSelectionDataItem = (=<<) <$> bLookup <*> bSelection

    -- GUI layout
    grid [
           [ uiDataItem ]
         , [ element listBox ]
         , [ element addToListBoxBtn]
         , [ element doItBtn ]
         ]

------------------------------------------------------------------------------
-- Database Model

type DatabaseKey = Int
data Database a  = Database { nextKey :: !Int, db :: Map.Map DatabaseKey a }

emptydb :: Database a
emptydb = Database 0 Map.empty

keys :: Database a -> [DatabaseKey]
keys    = Map.keys . db

hcSubmit :: Database String -> Database String
hcSubmit     (Database newkey db0) = create "FOO" $ Database newkey     $ Map.map (++ "xx") db0

hcEFill :: [String] -> Database String -> Database String
hcEFill     (s:_) _ = create s emptydb

create :: a -> Database a -> Database a
create x     (Database newkey db0) = Database (newkey+1) $ Map.insert newkey x db0

lookup :: DatabaseKey -> Database a -> Maybe a
lookup key   (Database _      db0) = Map.lookup                       key      db0

------------------------------------------------------------------------------
-- What is stored in data base

type DataItem = String

showDataItem :: String -> String
showDataItem x = x

-- | Data item widget, consisting of two text entries
dataItem :: Behavior (Maybe DataItem) -> UI (Element, Tidings DataItem)
dataItem bItem = do
    entry1 <- UI.entry $ maybe "" id <$> bItem
    return ( getElement  entry1
           , UI.userText entry1
           )

------------------------------------------------------------------------------

sideEffects :: IO String
sideEffects = do
    x <- randomRIO (0,10) :: IO Int
    return (show x)

-- End of file.
