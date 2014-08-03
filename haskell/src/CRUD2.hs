{-
Created       : by threepenny-gui/samples/CRUD
Last Modified : 2014 Aug 03 (Sun) 15:25:32 by Harold Carr.
-}

{-# LANGUAGE RecursiveDo #-}

module CRUD2 where

import           Control.Monad               (void)
import qualified Data.Map                    as Map
import           Data.Maybe
import qualified Graphics.UI.Threepenny      as UI
import           Graphics.UI.Threepenny.Core hiding (delete)
import           Prelude                     hiding (lookup)

------------------------------------------------------------------------------

main :: IO ()
main = startGUI defaultConfig setup

setup :: Window -> UI ()
setup window = void $ mdo
    getBody window #+ [ mkSPVPanel "?subject" ]
    return window # set title "RDF Triple Browser"

mkSPVPanel :: String -> UI Element
mkSPVPanel spvType = mdo
    -- GUI elements
    submitBtn   <- UI.button #+ [string "Submit"]
    listBox     <- UI.listBox  bListBoxItems bSelection bDisplayDataItem
    (spvSelection, _)
                <- dataItem    bSelectionDataItem
    let uiDataItem = grid [[string spvType, element spvSelection]]
    element listBox # set (attr "size") "10" # set style [("width","200px")]

    -- events and behaviors
    let eSelection  = rumors $ UI.userSelection listBox
        eSubmit     = UI.click submitBtn

    -- database
    -- bDatabase :: Behavior (Database DataItem)
    bDatabase <- accumB emptydb $ concatenate <$> unions
        [ hcSubmit' <$ eSubmit
        ]

    -- selection
    -- bSelection :: Behavior (Maybe DatabaseKey)
    bSelection <- stepper Nothing $ head <$> unions
        [ eSelection
        , Just . nextKey <$> bDatabase <@ eSubmit
        ]

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

    -- automatically enable / disable editing
    let
        bDisplayItem :: Behavior Bool
        bDisplayItem = maybe False (const True) <$> bSelection

    element spvSelection # sink UI.enabled bDisplayItem

    -- GUI layout
    grid [
           [ uiDataItem ]
         , [ element listBox ]
         , [ row [element submitBtn] ]
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
hcSubmit                      db0 = create "ONE" (create "TWO" (create "THREE" db0))

hcSubmit' :: Database String -> Database String
hcSubmit'    (Database newkey db0) = create "FOO" $ Database newkey     $ Map.map (++ "xx") db0

create :: a -> Database a -> Database a
create x     (Database newkey db0) = Database (newkey+1) $ Map.insert newkey x db0

delete :: DatabaseKey -> Database a -> Database a
delete key   (Database newkey db0) = Database newkey     $ Map.delete key      db0

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

-- End of file.
