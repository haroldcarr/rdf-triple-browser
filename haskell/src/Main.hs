{-
Created       : by threepenny-gui/samples/CRUD
Last Modified : 2014 Jul 14 (Mon) 06:51:12 by Harold Carr.
-}

{-# LANGUAGE RecursiveDo #-}

module Main where

import           Control.Monad               (void)
import           Data.List                   (isPrefixOf)
import qualified Data.Map                    as Map
import           Data.Maybe
import           Prelude                     hiding (lookup)

import qualified Graphics.UI.Threepenny      as UI
import           Graphics.UI.Threepenny.Core hiding (delete)

------------------------------------------------------------------------------

test :: IO ()
test = startGUI defaultConfig testSetup

testSetup :: Window -> UI ()
testSetup window = void $ mdo
    return window # set title "CRUD Example (Simple)"


    -- GUI elements
    createBtnS   <- UI.button #+ [string "Create"]
    deleteBtnS   <- UI.button #+ [string "Delete"]
    createBtnP   <- UI.button #+ [string "Create"]
    deleteBtnP   <- UI.button #+ [string "Delete"]
    createBtnO   <- UI.button #+ [string "Create"]
    deleteBtnO   <- UI.button #+ [string "Delete"]
    listBoxS     <- UI.listBox  bListBoxItemsS bSelectionS bDisplayDataItemS
    listBoxO     <- UI.listBox  bListBoxItemsS bSelectionS bDisplayDataItemS
    listBoxP     <- UI.listBox  bListBoxItemsS bSelectionS bDisplayDataItemS
    filterEntryS <- UI.entry    bFilterStringS
    filterEntryP <- UI.entry    bFilterStringS
    filterEntryO <- UI.entry    bFilterStringS
    ((firstnameS, lastnameS), tDataItemS)
                 <- dataItem    bSelectionDataItemS
    ((firstnameP, lastnameP), tDataItemP)
                 <- dataItem    bSelectionDataItemS
    ((firstnameO, lastnameO), tDataItemO)
                 <- dataItem    bSelectionDataItemS

    -- GUI layout
    element listBoxS # set (attr "size") "10" # set style [("width","200px")]
    element listBoxP # set (attr "size") "10" # set style [("width","200px")]
    element listBoxO # set (attr "size") "10" # set style [("width","200px")]

    let uiDataItemS = grid [[string "First Name:", element firstnameS]
                           ,[string "Last Name:" , element lastnameS]]
        uiDataItemP = grid [[string "First Name:", element firstnameP]
                           ,[string "Last Name:" , element lastnameP]]
        uiDataItemO = grid [[string "First Name:", element firstnameO]
                           ,[string "Last Name:" , element lastnameO]]

    let glue = string " "
    getBody window #+
        [ row [ grid [ [ row [ string "subject:" , element filterEntryS ] ]
                     , [ element listBoxS ]
                     , [ element createBtnS ]
                     , [ element deleteBtnS ]
                     , [ uiDataItemS ]
                     ]
              , glue
              , grid [ [ row [ string "object:"  , element filterEntryS ] ]
                     , [ element listBoxP]
                     , [ element createBtnP ]
                     , [ element deleteBtnP ]
                     ]
              , glue
              , grid [ [ row [string "predicate:", element filterEntryS ] ]
                     , [ element listBoxO]
                     , [ element createBtnO ]
                     , [ element deleteBtnO ]
                     ]
              ]
          ]

    -- events and behaviors
    bFilterStringS <- stepper "" . rumors $ UI.userText filterEntryS
    let tFilter = isPrefixOf <$> UI.userText filterEntryS
        bFilter = facts  tFilter
        eFilter = rumors tFilter

    let eSelection  = rumors $ UI.userSelection listBoxS
        eDataItemIn = rumors $ tDataItemS
        eCreate     = UI.click createBtnS
        eDelete     = UI.click deleteBtnS

    -- database
    -- bDatabase :: Behavior (Database DataItem)
    let update' mkey x = flip update x <$> mkey
    bDatabase <- accumB emptydb $ concatenate <$> unions
        [ create ("Emil","Example") <$ eCreate
        , filterJust $ update' <$> bSelectionS <@> eDataItemIn
        , delete <$> filterJust (bSelectionS <@ eDelete)
        ]

    -- selection
    -- bSelectionS :: Behavior (Maybe DatabaseKey)
    bSelectionS <- stepper Nothing $ head <$> unions
        [ eSelection
        , Nothing <$ eDelete
        , Just . nextKey <$> bDatabase <@ eCreate
        , (\b s p -> b >>= \a -> if p (s a) then Just a else Nothing)
            <$> bSelectionS <*> bShowDataItem <@> eFilter
        ]

    let bLookup :: Behavior (DatabaseKey -> Maybe DataItem)
        bLookup = flip lookup <$> bDatabase

        bShowDataItem :: Behavior (DatabaseKey -> String)
        bShowDataItem = (maybe "" showDataItem .) <$> bLookup

        bDisplayDataItemS = (UI.string .) <$> bShowDataItem

        bListBoxItemsS :: Behavior [DatabaseKey]
        bListBoxItemsS = (\p show0 -> filter (p. show0) . keys)
                    <$> bFilter <*> bShowDataItem <*> bDatabase

        bSelectionDataItemS :: Behavior (Maybe DataItem)
        bSelectionDataItemS = (=<<) <$> bLookup <*> bSelectionS

    -- automatically enable / disable editing
    let
        bDisplayItem :: Behavior Bool
        bDisplayItem = maybe False (const True) <$> bSelectionS

    element deleteBtnS # sink UI.enabled bDisplayItem
    element deleteBtnP # sink UI.enabled bDisplayItem
    element deleteBtnO # sink UI.enabled bDisplayItem
    element firstnameS # sink UI.enabled bDisplayItem
    element firstnameP # sink UI.enabled bDisplayItem
    element firstnameO # sink UI.enabled bDisplayItem
    element lastnameS  # sink UI.enabled bDisplayItem
    element lastnameP  # sink UI.enabled bDisplayItem
    element lastnameO  # sink UI.enabled bDisplayItem

------------------------------------------------------------------------------

main :: IO ()
main = startGUI defaultConfig setup

setup :: Window -> UI ()
setup window = void $ mdo
    return window # set title "CRUD Example (Simple)"

    -- GUI elements
    createBtnS   <- UI.button #+ [string "Create"]
    deleteBtnS   <- UI.button #+ [string "Delete"]
    listBoxS     <- UI.listBox  bListBoxItemsS bSelectionS bDisplayDataItemS
    filterEntryS <- UI.entry    bFilterStringS
    ((firstname, lastname), tDataItem)
                 <- dataItem    bSelectionDataItemS

    -- GUI layout
    element listBoxS # set (attr "size") "10" # set style [("width","200px")]

    let uiDataItem = grid [[string "First Name:", element firstname]
                          ,[string "Last Name:" , element lastname]]
    let glue = string " "
    getBody window #+ [grid
        [[row [string "Filter prefix:", element filterEntryS], glue]
        ,[element listBoxS, uiDataItem]
        ,[row [element createBtnS, element deleteBtnS], glue]
        ]]

    -- events and behaviors
    bFilterStringS <- stepper "" . rumors $ UI.userText filterEntryS
    let tFilter = isPrefixOf <$> UI.userText filterEntryS
        bFilter = facts  tFilter
        eFilter = rumors tFilter

    let eSelection  = rumors $ UI.userSelection listBoxS
        eDataItemIn = rumors $ tDataItem
        eCreate     = UI.click createBtnS
        eDelete     = UI.click deleteBtnS

    -- database
    -- bDatabase :: Behavior (Database DataItem)
    let update' mkey x = flip update x <$> mkey
    bDatabase <- accumB emptydb $ concatenate <$> unions
        [ create ("Emil","Example") <$ eCreate
        , filterJust $ update' <$> bSelectionS <@> eDataItemIn
        , delete <$> filterJust (bSelectionS <@ eDelete)
        ]

    -- selection
    -- bSelectionS :: Behavior (Maybe DatabaseKey)
    bSelectionS <- stepper Nothing $ head <$> unions
        [ eSelection
        , Nothing <$ eDelete
        , Just . nextKey <$> bDatabase <@ eCreate
        , (\b s p -> b >>= \a -> if p (s a) then Just a else Nothing)
            <$> bSelectionS <*> bShowDataItem <@> eFilter
        ]

    let bLookup :: Behavior (DatabaseKey -> Maybe DataItem)
        bLookup = flip lookup <$> bDatabase

        bShowDataItem :: Behavior (DatabaseKey -> String)
        bShowDataItem = (maybe "" showDataItem .) <$> bLookup

        bDisplayDataItemS = (UI.string .) <$> bShowDataItem

        bListBoxItemsS :: Behavior [DatabaseKey]
        bListBoxItemsS = (\p show0 -> filter (p. show0) . keys)
                    <$> bFilter <*> bShowDataItem <*> bDatabase

        bSelectionDataItemS :: Behavior (Maybe DataItem)
        bSelectionDataItemS = (=<<) <$> bLookup <*> bSelectionS

    -- automatically enable / disable editing
    let
        bDisplayItem :: Behavior Bool
        bDisplayItem = maybe False (const True) <$> bSelectionS

    element deleteBtnS # sink UI.enabled bDisplayItem
    element firstname  # sink UI.enabled bDisplayItem
    element lastname   # sink UI.enabled bDisplayItem

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
-- Data items that are stored in the data base

type DataItem = (String, String)

showDataItem :: (String, String) -> String
showDataItem (firstname, lastname) = lastname ++ ", " ++ firstname

-- | Data item widget, consisting of two text entries
dataItem
    :: Behavior (Maybe DataItem)
    -> UI ((Element, Element), Tidings DataItem)
dataItem bItem = do
    entry1 <- UI.entry $ fst . maybe ("","") id <$> bItem
    entry2 <- UI.entry $ snd . maybe ("","") id <$> bItem

    return ( (getElement entry1, getElement entry2)
           , (,) <$> UI.userText entry1 <*> UI.userText entry2
           )

-- End of file.
