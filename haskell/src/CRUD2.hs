{-
Created       : by threepenny-gui/samples/CRUD
Last Modified : 2014 Aug 04 (Mon) 21:13:28 by Harold Carr.
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
    frp <- mkListBoxFRP
    startGUI defaultConfig $ \window -> do
        return window # set title "RDF Triple Browser"
        getBody window #+ [ mkSPVPanel frp "?subject" ]
        return ()

mkListBoxFRP :: IO (Event [a], Handler [a])
mkListBoxFRP = do
    (eventFillListBox, handlerFillListBox) <- newEvent
    return (eventFillListBox, handlerFillListBox)

mkSPVPanel :: (Event [(String,BindingValue)], Handler [(String,BindingValue)])
              -> String
              -> UI Element
mkSPVPanel (eFillListBox, hFillListBox) spvType = mdo
    -- GUI elements
    doItBtn           <- UI.button #+ [string "Do It!"]
    addToListBoxBtn   <- UI.button #+ [string "Add To List Box"]
    listBox           <- UI.listBox  bListBoxItems bSelection bDisplayDataItem
    spvSelection      <- dataItem    bSelectionDataItem
    let uiDataItem = grid [[string spvType, element spvSelection]]
    element listBox # set (attr "size") "20" # set style [("width","800px")]

    -- events and behaviors
    let eSelection    = rumors $ UI.userSelection listBox
        eAddToListBox = UI.click addToListBoxBtn

    -- submit button
    on UI.click doItBtn $ \_ -> do
        (r,_,_) <- liftIO doRDFQuery;
        liftIO $ hFillListBox r;
        return ()

    -- database
    -- bDatabase :: Behavior (Database DataItem)
    bDatabase <- accumB emptydb $ concatenate <$> unions
        [ hcSubmit <$ eAddToListBox
        , hcEFill <$> eFillListBox
        ]

    -- selection
    -- bSelection :: Behavior (Maybe DatabaseKey)
    bSelection <- stepper Nothing eSelection

    let bLookup :: Behavior (DatabaseKey -> Maybe DataItem)
        bLookup = flip lookup <$> bDatabase

        bShowDataItem :: Behavior (DatabaseKey -> String)
        bShowDataItem = (maybe "" showDataItem .) <$> bLookup

        bDisplayDataItem = (UI.string .) <$> bShowDataItem

        bListBoxItems :: Behavior [DatabaseKey]
        bListBoxItems = (\show0 -> filter (const True . show0) . keys)
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

hcSubmit :: Database DataItem -> Database DataItem
hcSubmit     (Database newkey db0) = Database newkey $ Map.map (\(x,y) -> (x ++ "xx", y)) db0

hcEFill :: [(String,BindingValue)] -> Database DataItem -> Database DataItem
hcEFill    ss _ = foldr create  emptydb ss

create :: a -> Database a -> Database a
create x     (Database newkey db0) = Database (newkey+1) $ Map.insert newkey x db0

lookup :: DatabaseKey -> Database a -> Maybe a
lookup key   (Database _      db0) = Map.lookup                       key      db0

------------------------------------------------------------------------------
-- What is stored in data base

type DataItem = (String, BindingValue)

showDataItem :: DataItem -> String
showDataItem (x,_) = x

-- | Data item widget
dataItem :: Behavior (Maybe DataItem) -> UI Element
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
