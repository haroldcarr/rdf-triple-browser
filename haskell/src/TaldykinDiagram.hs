{-
Created       : 2014 Jul 24 (Thu) 09:37:09 by Harold Carr.
Last Modified : 2014 Jul 24 (Thu) 10:54:23 by Harold Carr.
-}

{-# LANGUAGE MultiParamTypeClasses #-}
{-# LANGUAGE OverloadedStrings     #-}

module TaldykinDiagram where

import           Control.Monad                     (forM_)
import           Data.Graph.Inductive
import           Data.GraphViz
import           Data.GraphViz
import           Data.GraphViz.Attributes.Complete
import           Data.GraphViz.Types.Generalised   as G
import           Data.GraphViz.Types.Monadic
import           Data.Text.Lazy                    as L
import           Data.Word
import           System.FilePath

-- http://www.colorcombos.com/color-schemes/2025/ColorCombo2025.html
myColorCL :: Word8 -> ColorList
myColorCL n | n == 1 = c $ (RGB 127 108 138)
            | n == 2 = c $ (RGB 175 177 112)
            | n == 3 = c $ (RGB 226 206 179)
            | n == 4 = c $ (RGB 172 126 100)
  where c rgb = toColorList [rgb]

myColor :: Word8 -> Attribute
myColor n = Color $ myColorCL n

circle       :: n -> Text -> Dot n
circle       n l = node n [textLabel l, shape Circle,   FixedSize SetNodeSize, Width 1.75, style filled, myColor 3]

rectangle    :: n -> Text -> Dot n
rectangle    n l = node n [textLabel l, shape BoxShape,                        Width 1,    style filled, myColor 3]

bangBang, behaviorFillListBox, currentValue, eventFillListBox, handlerFillListBox, lstBox, selectionChange, valuesSupply :: Dot L.Text
bangBang            = circle       "bangBang"                     "!!"
behaviorFillListBox = circle       "behaviorFillListBox"          "behaviorFillListBox\nm (Behavior a)"
currentValue        = circle       "currentValue"                 "currentValue\nm a"
eventFillListBox    = rectangle    "eventFillListBox"             "eventFillListBox\n[(Time,a)]"
handlerFillListBox  = circle       "handlerFillListBox"           "handlerFillListBox\na -> IO ()"
lstBox              = rectangle    "lstBox"                       "lstBox\nUI (ListBox a)"
selectionChange     = circle       "selectionChange"              "selectionChange\nEvent (Maybe Int)"
valuesSupply        = circle       "valuesSupply"                 "valuesSupply\nIO [String]"

xxx :: G.DotGraph L.Text
xxx = digraph (Str "ex4") $ do

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

doDots :: PrintDotRepr dg n => [(FilePath, dg n)] -> IO ()
doDots cases = forM_ cases createImage

createImage :: PrintDotRepr dg n => (FilePath, dg n) -> IO FilePath
createImage (n, g) = createImageInDir "/tmp" n Png g

createImageInDir :: PrintDotRepr dg n => FilePath -> FilePath -> GraphvizOutput -> dg n -> IO FilePath
createImageInDir d n o g = Data.GraphViz.addExtension (runGraphvizCommand Dot g) o (combine d n)

main :: IO ()
main = do
    doDots [ ("xxx" , xxx)
           ]

-- End of file.
