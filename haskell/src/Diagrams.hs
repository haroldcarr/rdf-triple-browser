{-
Created       : 2014 Jul 24 (Thu) 09:37:09 by Harold Carr.
Last Modified : 2014 Aug 09 (Sat) 14:16:32 by Harold Carr.
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

eFillLB        = event       "eFillLB"            "eFillLB\n[(Time,a)]"
hFillLB        = handler     "hFillLB"            "hFillLB\na -> IO ()"
listBox        = ui          "listBox"            "listBox\nUI (ListBox a)"
doItBtn        = ui          "doItBtn"            "doItBtn\nUI Element"
lbSelection    = ui          "lbSelection"        "lbSelection\n UI Element"
eLBSelection   = event       "eLBSelection"       "eLBSelection"

eDoItClk       = event       "eDoItClk"           "eDoItClk\nElement -> Event"
doRDFQuery     = mfun        "doRDFQuery"         "doRDFQuery\nIO ..."

accumB         = behavior    "accumB"             "accumB\nMonadIO m => a\n-> Event (a -> a)\n-> m (Behavior a)"
emptydb        = uRectangle' "emptydb"            "emptydb\nDB a"
dbFill         = fun         "dbFill"             "dbFill\na -> DB DI -> DB DI"
bDB            = behavior    "bDB"                "bDB\nBehavior\n(DB DI)"

stepper        = fun         "stepper"            "stepper"
bLBSelection   = behavior    "bLBSelection"       "bLBSelection\nBehavior\n(Maybe DBKey)"

bLookup        = behavior    "bLookup"            "bLookup\nBehavior\n(DBKey -> Maybe DI)"
bShowDI        = behavior    "bShowDI"            "bShowDI\nBehavior\n(DBKey -> String)"
bDisplayDI     = behavior    "bDisplayDI"         "bDisplayDI\nString -> UI Element"

bLBItems       = behavior    "bLBItems"           "bLBItems\nBehavior\n[DBKey]"

bLBSelectionDI = behavior    "bLBSelectionDI"     "bLBSelectionDI\nBehavior\n(Maybe DI)"

keys           = fun         "keys"               "keys"

crud2 :: G.DotGraph L.Text
crud2 = digraph (Str "crud2") $ do

    graphAttrs [RankDir FromLeft]
    listBox; eFillLB; hFillLB; doItBtn; lbSelection; eLBSelection; eDoItClk; doRDFQuery;
    accumB; emptydb; dbFill; bDB;
    stepper; bLBSelection; bLookup; bShowDI; bDisplayDI; bLBItems; bLBSelectionDI; keys;

    edge "bLBItems"     "listBox" [textLabel "1"]
    edge "bLBSelection" "listBox" [textLabel "2"]
    edge "bDisplayDI"   "listBox" [textLabel "3"]

    "bLBSelectionDI" --> "lbSelection"

    "doItBtn" --> "eDoItClk"

    "listBox"      --> "eLBSelection"
    "eLBSelection" --> "stepper"
    "stepper"      --> "bLBSelection"

    "eDoItClk"   --> "doRDFQuery"
    "doRDFQuery" --> "hFillLB"
    "hFillLB"    --> "eFillLB"
    "emptydb"    --> "accumB"
    "eFillLB"    --> "dbFill"
    "dbFill"     --> "accumB"
    "accumB"     --> "bDB"

    "bDB"     --> "bLookup"
    "bLookup" --> "bShowDI"
    "bShowDI" --> "bDisplayDI"

    "bDB"     --> "keys"
    "keys"    --> "bShowDI"
    "bShowDI" --> "bLBItems"

    "bLBSelection" --> "bLookup"
    "bLookup"      --> "bLBSelectionDI"

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
