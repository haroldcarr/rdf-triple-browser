{-
Created       : by threepenny-gui MissingDollars sample.
Last Modified : 2014 Jul 24 (Thu) 18:45:04 by Harold Carr.
-}

module MD where

import           Control.Monad
import qualified Graphics.UI.Threepenny      as UI
import           Graphics.UI.Threepenny.Core

main :: IO ()
main = startGUI defaultConfig setup

setup :: Window -> UI ()
setup w = void $ do
    return w # set title "MD"
    getBody w #+ [row [ mkLayout ] ]

mkLayout :: UI Element
mkLayout = do
    -- input elements
    sparqlEndpointURL <- UI.input # set (attr "size") "80" # set (attr "type") "text"
    submit <- UI.button #+ [string "submit"]

    (ddSub : ddPre : ddVal : _ )
        <- replicateM 3 $UI.select # set (attr "width") "10"
                                   #+ [ UI.option # set html "clear", UI.option # set html "show all"
                                      , UI.option # set html "<-"   , UI.option # set html "->" ]

    -- display elements
    (currentSub : currentPre : currentVal : _)
        <- replicateM 3 $ UI.textarea # set (attr "size") "2"  # set (attr "type") "text"

    -- update procedure
    let updateDisplay _ s p v = do {
        element currentSub # set value s;
        element currentPre # set value p;
        element currentVal # set value v;
        return ()
    }
    -- init values
    updateDisplay "sparql" "?subject" "?predicate" "?value"

    -- submit button
    on UI.click submit $ \_ -> do
        sparql <- get value sparqlEndpointURL
        s      <- get value currentSub
        p      <- get value currentPre
        v      <- get value currentVal
        updateDisplay sparql s p v


    grid [ [ row [ element sparqlEndpointURL, element submit ] ]
         , [ row [ element ddSub, element currentSub
                 , element ddPre, element currentPre
                 , element ddVal, element currentVal
                 ]
           ]
         ]

-- End of file.
