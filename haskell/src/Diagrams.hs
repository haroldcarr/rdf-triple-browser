{-
Created       : 2014 Jul 24 (Thu) 09:37:09 by Harold Carr.
Last Modified : 2014 Aug 10 (Sun) 13:00:14 by Harold Carr.
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

ui       = uStar          $ uFixedSize [Width 1.5]
behavior = uDoubleCircle  $ uFixedSize [Width 1.5]
event    = uDiamond       $ uFixedSize [Width 1.5, Height 1.5]
handler  = uTriangle      $ uFixedSize [Width 1.5]
fun      = uRectangle     []
mfun     = uDoubleOctagon $ uFixedSize [Width 1.5, Height 1.5]

------------------------------------------------------------------------------

doItBtn        = ui          "doItBtn"            "doItBtn ::\nUI Element"
listBox        = ui          "listBox"            "listBox ::\nUI (ListBox a)"
lbSelection    = ui          "lbSelection"        "lbSelection ::\n UI Element"
userSelection  = fun         "userSelection"      "userSelection ::\nListBox a ->\nTidings (Maybe a)"
rumors         = fun         "rumors"             "rumors ::\nTidings -> \nEvent a"
eLBSelection   = event       "eLBSelection"       "eLBSelection ::\nEvent a"

eFillLB        = event       "eFillLB"            "eFillLB ::\n[(Time,a)]"
hFillLB        = handler     "hFillLB"            "hFillLB ::\na -> IO ()"
on             = fun         "on"                 "on ::\n(element -> Event a)\n-> element\n-> (a -> UI void)\n-> UI ()"
click          = fun         "click"              "click ::\nElement\n-> Event ()"
eDoItClk       = event       "eDoItClk"           "eDoItClk ::\na -> UI void"
doRDFQuery     = mfun        "doRDFQuery"         "doRDFQuery ::\nIO ..."

accumB         = behavior    "accumB"             "accumB ::\nMonadIO m =>\na\n-> Event (a -> a)\n-> m (Behavior a)"
emptydb        = uRectangle' "emptydb"            "emptydb ::\nDB a"
dbFill         = fun         "dbFill"             "dbFill ::\na -> DB DI -> DB DI"
bDB            = behavior    "bDB"                "bDB ::\nBehavior\n(DB DI)"

nothing        = uRectangle' "nothing"            "Nothing"
stepper        = fun         "stepper"            "stepper ::\nMonadIO m =>\na\n-> Event a\n-> m (Behavior a)"
bLBSelection   = behavior    "bLBSelection"       "bLBSelection ::\nBehavior\n(Maybe DBKey)"

bLookup        = behavior    "bLookup"            "bLookup ::\nBehavior\n(DBKey -> Maybe DI)"
bShowDI        = behavior    "bShowDI"            "bShowDI ::\nBehavior\n(DBKey -> String)"
bDisplayDI     = behavior    "bDisplayDI"         "bDisplayDI ::\nString -> UI Element"

bLBItems       = behavior    "bLBItems"           "bLBItems ::\nBehavior\n[DBKey]"

bLBSelectionDI = behavior    "bLBSelectionDI"     "bLBSelectionDI ::\nBehavior\n(Maybe DI)"

keys           = fun         "keys"               "keys ::\nMap k a -> [k]"

crud2 :: G.DotGraph L.Text
crud2 = digraph (Str "crud2") $ do

    graphAttrs [RankDir FromLeft]
    listBox; eFillLB; hFillLB; doItBtn; lbSelection; eLBSelection; userSelection; rumors;
    on; click; eDoItClk; doRDFQuery;
    accumB; emptydb; dbFill; bDB;
    nothing; stepper; bLBSelection; bLookup; bShowDI; bDisplayDI; bLBItems; bLBSelectionDI; keys;

    edge "bLBItems"      "listBox" [textLabel "1. Behavior [a] -- list of items"]
    edge "bLBSelection"  "listBox" [textLabel "2. Behavior (Maybe a) -- selected item"]
    edge "bDisplayDI"    "listBox" [textLabel "3. Behavior (a -> UI Element) -- display for an item"]

    "bLBSelectionDI" --> "lbSelection"

    "listBox"        --> "userSelection"
    "userSelection"  --> "rumors"
    "rumors"         --> "eLBSelection"
    "eLBSelection"   --> "stepper"
    "nothing"        --> "stepper"
    "stepper"        --> "bLBSelection"

    edge "click"         "on" [textLabel "1."]
    edge "doItBtn"       "on" [textLabel "2."]
    edge "eDoItClk"      "on" [textLabel "3."]
    "eDoItClk"       --> "doRDFQuery"
    "doRDFQuery"     --> "hFillLB"
    "hFillLB"        --> "eFillLB"
    "emptydb"        --> "accumB"
    "eFillLB"        --> "dbFill"
    "dbFill"         --> "accumB"
    "accumB"         --> "bDB"

    "bDB"            --> "bLookup"
    "bLookup"        --> "bShowDI"
    "bShowDI"        --> "bDisplayDI"

    "bDB"            --> "keys"
    "keys"           --> "bLBItems"

    "bLBSelection"   --> "bLookup"
    "bLookup"        --> "bLBSelectionDI"

------------------------------------------------------------------------------

bangBang            = uRectangle'    "bangBang"                     "!!"
bFillLB             = behavior       "bFillLB"                      "bFillLB\nm (Behavior a)"
currentValue        = uCircle'       "currentValue"                 "currentValue\nm a"
selectionChange     = uCircle'       "selectionChange"              "selectionChange\nEvent (Maybe Int)"
valuesSupply        = uCircle'       "valuesSupply"                 "valuesSupply\nIO [String]"

taldykin :: G.DotGraph L.Text
taldykin = digraph (Str "taldykin") $ do

    graphAttrs [RankDir FromLeft]
    selectionChange; eFillLB; hFillLB; bFillLB; listBox; valuesSupply; currentValue; bangBang;

    "hFillLB"             --> "eFillLB"
    "eFillLB"             --> "bFillLB"
    "bFillLB"             --> "listBox"
    "bFillLB"             --> "currentValue"
    edge "selectionChange"    "currentValue"         [textLabel "causes"]
    "selectionChange"     --> "bangBang"
    "currentValue"        --> "bangBang"
    "bangBang"            --> "valuesSupply"
    "valuesSupply"        --> "hFillLB"
    "listBox"             --> "selectionChange"

------------------------------------------------------------------------------

main :: IO ()
main =
    doDots [ ("taldykin" , taldykin)
           , ("crud2"    , crud2)
           ]

-- End of file.
