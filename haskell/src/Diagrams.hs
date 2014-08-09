{-
Created       : 2014 Jul 24 (Thu) 09:37:09 by Harold Carr.
Last Modified : 2014 Aug 08 (Fri) 20:08:32 by Harold Carr.
-}

{-# LANGUAGE MultiParamTypeClasses #-}
{-# LANGUAGE OverloadedStrings     #-}

module Diagrams where

import           Data.GraphViz
import           Data.GraphViz.Attributes.Complete
import           Data.GraphViz.HC.Util
import           Data.GraphViz.Types.Generalised   as G
import           Data.GraphViz.Types.Monadic
import           Data.Text.Lazy                    as L

------------------------------------------------------------------------------

ui       = uStar'
behavior = uDoubleCircle'
event    = uDiamond'
handler  = uTriangle'
fun      = uRectangle'
mfun     = uDoubleOctagon'

------------------------------------------------------------------------------

doItBtn = ui "doItBtn" "doItBtn"
doItClk = event "doItClk" "doItClk"
doRDFQuery = mfun "doRDFQuery" "doRDFQuery"
bDataBase = behavior "bDataBase" "bDataBase"
bListBoxItems = behavior "bListBoxItems" "bListBoxItems"

bSelection = behavior "bSelection" "bSelection"
eSelection = event "eSelection" "eSelection"

bLookup      = behavior "bLookup" "bLookup"
bShowDataItem = behavior "bShowDataItem" "bShowDataItem"
bDisplayDataItem = behavior "bDisplayDataItem" "bDisplayDataItem"

crud2 :: G.DotGraph L.Text
crud2 = digraph (Str "crud2") $ do

    graphAttrs [RankDir FromLeft]
    doItBtn; doItClk; doRDFQuery; handlerFillListBox; eventFillListBox; bDataBase; bListBoxItems; listBox;
    eSelection; bSelection;
    bLookup; bShowDataItem; bDisplayDataItem;

    "doItBtn" --> "doItClk"
    "doItClk" --> "doRDFQuery"
    "doRDFQuery" --> "handlerFillListBox"
    "handlerFillListBox" --> "eventFillListBox"
    "eventFillListBox" --> "bDataBase"
    "bDataBase" --> "bListBoxItems"
    edge "bListBoxItems" "listBox" [textLabel "1"]

    "eSelection" --> "bSelection"
    edge "bSelection" "listBox" [textLabel "2"]

    "bLookup" --> "bShowDataItem"
    "bShowDataItem" --> "bDisplayDataItem"
    edge "bDisplayDataItem" "listBox" [textLabel "3"]

------------------------------------------------------------------------------

bangBang, behaviorFillListBox, currentValue, eventFillListBox, handlerFillListBox, listBox, selectionChange, valuesSupply :: Dot L.Text
bangBang            = uRectangle'    "bangBang"                     "!!"
behaviorFillListBox = behavior       "behaviorFillListBox"          "behaviorFillListBox\nm (Behavior a)"
currentValue        = uCircle'       "currentValue"                 "currentValue\nm a"
eventFillListBox    = event          "eventFillListBox"             "eventFillListBox\n[(Time,a)]"
handlerFillListBox  = handler        "handlerFillListBox"           "handlerFillListBox\na -> IO ()"
listBox             = ui             "listBox"                      "listBox\nUI (ListBox a)"
selectionChange     = uCircle'       "selectionChange"              "selectionChange\nEvent (Maybe Int)"
valuesSupply        = uCircle'       "valuesSupply"                 "valuesSupply\nIO [String]"

taldykin :: G.DotGraph L.Text
taldykin = digraph (Str "taldykin") $ do

    graphAttrs [RankDir FromLeft]
    selectionChange; eventFillListBox; handlerFillListBox; behaviorFillListBox; listBox; valuesSupply; currentValue; bangBang;

    "handlerFillListBox"  --> "eventFillListBox"
    "eventFillListBox"    --> "behaviorFillListBox"
    "behaviorFillListBox" --> "listBox"
    "behaviorFillListBox" --> "currentValue"
    edge "selectionChange"    "currentValue"         [textLabel "causes"]
    "selectionChange"     --> "bangBang"
    "currentValue"        --> "bangBang"
    "bangBang"            --> "valuesSupply"
    "valuesSupply"        --> "handlerFillListBox"
    "listBox"             --> "selectionChange"

------------------------------------------------------------------------------

main :: IO ()
main =
    doDots [ ("taldykin" , taldykin)
           , ("crud2"    , crud2)
           ]

-- End of file.
