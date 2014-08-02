{-
Created       : by threepenny-gui/samples/CRUD
Last Modified : 2014 Aug 02 (Sat) 09:27:02 by Harold Carr.
-}

{-# LANGUAGE RecursiveDo #-}

module CRUD where

import           Control.Monad               (void)
import           Data.List                   (isPrefixOf)
import qualified Data.Map                    as Map
import           Data.Maybe
import           Prelude                     hiding (lookup)

import qualified Graphics.UI.Threepenny      as UI
import           Graphics.UI.Threepenny.Core hiding (delete)

------------------------------------------------------------------------------

main :: IO ()
main = startGUI defaultConfig setup

setup :: Window -> UI ()
setup window = void $ mdo
    let s    = mkSPVPanel "?subject"
        p    = mkSPVPanel "?predicate"
        v    = mkSPVPanel "?value"
        nbsp = string " "
    getBody window #+ [ row [s, nbsp, p, nbsp, v] ]
    return window # set title "RDF Triple Browser"

mkSPVPanel :: String -> UI Element
mkSPVPanel name = mdo
    -- GUI elements
    createBtn   <- UI.button #+ [string "Create"]
    deleteBtn   <- UI.button #+ [string "Delete"]
    listBox     <- UI.listBox  bListBoxItems bSelection bDisplayDataItem
    filterEntry <- UI.entry    bFilterString
    (firstname, tDataItem)
                <- dataItem    bSelectionDataItem
    let uiDataItem = grid [[string name, element firstname]]
    element listBox # set (attr "size") "10" # set style [("width","200px")]

    -- events and behaviors
    bFilterString <- stepper "" . rumors $ UI.userText filterEntry
    let tFilter = isPrefixOf <$> UI.userText filterEntry
        bFilter = facts  tFilter
        eFilter = rumors tFilter

    let eSelection  = rumors $ UI.userSelection listBox
        eDataItemIn = rumors $ tDataItem
        eCreate     = UI.click createBtn
        eDelete     = UI.click deleteBtn

    -- database
    -- bDatabase :: Behavior (Database DataItem)
    let update' mkey x = flip update x <$> mkey
    bDatabase <- accumB emptydb $ concatenate <$> unions
        [ create "EXAMPLE" <$ eCreate
        , filterJust $ update' <$> bSelection <@> eDataItemIn
        , delete <$> filterJust (bSelection <@ eDelete)
        ]

    -- selection
    -- bSelection :: Behavior (Maybe DatabaseKey)
    bSelection <- stepper Nothing $ head <$> unions
        [ eSelection
        , Nothing <$ eDelete
        , Just . nextKey <$> bDatabase <@ eCreate
        , (\b s p -> b >>= \a -> if p (s a) then Just a else Nothing)
            <$> bSelection <*> bShowDataItem <@> eFilter
        ]

    let bLookup :: Behavior (DatabaseKey -> Maybe DataItem)
        bLookup = flip lookup <$> bDatabase

        bShowDataItem :: Behavior (DatabaseKey -> String)
        bShowDataItem = (maybe "" showDataItem .) <$> bLookup

        bDisplayDataItem = (UI.string .) <$> bShowDataItem

        bListBoxItems :: Behavior [DatabaseKey]
        bListBoxItems = (\p show0 -> filter (p. show0) . keys)
                    <$> bFilter <*> bShowDataItem <*> bDatabase

        bSelectionDataItem :: Behavior (Maybe DataItem)
        bSelectionDataItem = (=<<) <$> bLookup <*> bSelection

    -- automatically enable / disable editing
    let
        bDisplayItem :: Behavior Bool
        bDisplayItem = maybe False (const True) <$> bSelection

    element deleteBtn # sink UI.enabled bDisplayItem
    element firstname # sink UI.enabled bDisplayItem

    -- GUI layout
    grid [
           [ uiDataItem ]
         , [ element listBox ]
         , [ row [element createBtn, element deleteBtn] ]
         , [ row [string "filter:", element filterEntry] ]
         ]

------------------------------------------------------------------------------
-- Database Model

type DatabaseKey = Int
data Database a  = Database { nextKey :: !Int, db :: Map.Map DatabaseKey a }

emptydb :: Database a
emptydb = Database 0 Map.empty

keys :: Database a -> [DatabaseKey]
keys    = Map.keys . db

create :: a -> Database a -> Database a
create x     (Database newkey db0) = Database (newkey+1) $ Map.insert newkey x db0

update :: DatabaseKey -> a -> Database a -> Database a
update key x (Database newkey db0) = Database newkey     $ Map.insert key    x db0

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
