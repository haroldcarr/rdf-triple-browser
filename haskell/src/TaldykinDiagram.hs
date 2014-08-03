{-
Created       : 2014 Jul 24 (Thu) 09:37:09 by Harold Carr.
Last Modified : 2014 Aug 03 (Sun) 11:53:07 by Harold Carr.
-}

{-# LANGUAGE MultiParamTypeClasses #-}
{-# LANGUAGE OverloadedStrings     #-}

module TaldykinDiagram where

import           Data.GraphViz
import           Data.GraphViz.Attributes.Complete
import           Data.GraphViz.HC.Util
import           Data.GraphViz.Types.Generalised   as G
import           Data.GraphViz.Types.Monadic
import           Data.Text.Lazy                    as L

bangBang, behaviorFillListBox, currentValue, eventFillListBox, handlerFillListBox, lstBox, selectionChange, valuesSupply :: Dot L.Text
bangBang            = circle'       "bangBang"                     "!!"
behaviorFillListBox = circle'       "behaviorFillListBox"          "behaviorFillListBox\nm (Behavior a)"
currentValue        = circle'       "currentValue"                 "currentValue\nm a"
eventFillListBox    = rectangle'    "eventFillListBox"             "eventFillListBox\n[(Time,a)]"
handlerFillListBox  = circle'       "handlerFillListBox"           "handlerFillListBox\na -> IO ()"
lstBox              = rectangle'    "lstBox"                       "lstBox\nUI (ListBox a)"
selectionChange     = circle'       "selectionChange"              "selectionChange\nEvent (Maybe Int)"
valuesSupply        = circle'       "valuesSupply"                 "valuesSupply\nIO [String]"

taldykin :: G.DotGraph L.Text
taldykin = digraph (Str "taldykin") $ do

    graphAttrs [RankDir FromLeft]
    selectionChange; eventFillListBox; handlerFillListBox; behaviorFillListBox; lstBox; valuesSupply; currentValue; bangBang;

    "handlerFillListBox"  --> "eventFillListBox"
    "eventFillListBox"    --> "behaviorFillListBox"
    "behaviorFillListBox" --> "lstBox"
    "behaviorFillListBox" --> "currentValue"
    edge "selectionChange"    "currentValue"         [textLabel "causes"]
    "selectionChange"     --> "bangBang"
    "currentValue"        --> "bangBang"
    "bangBang"            --> "valuesSupply"
    "valuesSupply"        --> "handlerFillListBox"
    "lstBox"              --> "selectionChange"

main :: IO ()
main =
    doDots [ ("taldykin" , taldykin)
           ]

-- End of file.
