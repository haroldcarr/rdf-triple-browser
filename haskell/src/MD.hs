{-
Created       : by threepenny-gui MissingDollars sample.
Last Modified : 2014 Jul 24 (Thu) 15:19:58 by Harold Carr.
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
    layout <- mkLayout
    getBody w #+ [UI.div #. "wrap" #+ layout]

mkLayout :: UI [UI Element]
mkLayout = do
    -- input
    (rdfSubject : rdfPredicate : rdfValue : _)
        <- replicateM 3 $ UI.input # set (attr "size") "10" # set (attr "type") "text"
    -- display
    (subjectSpan : predicateSpan : valueSpan : concatSpan : _)
        <- replicateM 4   UI.span

    -- update procedure
    let updateDisplay s p v = do {
        element rdfSubject    # set value s;
        element rdfPredicate  # set value p;
        element rdfValue      # set value v;
        element subjectSpan   # set text  s;
        element predicateSpan # set text  p;
        element valueSpan     # set text  v;
        element concatSpan    # set text  (s ++ p ++ v);
        return ()
    }
    -- init values
    updateDisplay "?subject" "?predicate" "?value"

    -- calculate button
    calculate <- UI.button #+ [string "submit"]
    on UI.click calculate $ \_ -> do
        s <- get value rdfSubject
        p <- get value rdfPredicate
        v <- get value rdfValue
        updateDisplay s p v

    return
        [ string " rdfSubject: "    , element rdfSubject
        , string " rdfPredicate: "  , element rdfPredicate
        , string " rdfValue: "      , element rdfValue
        , string " subjectSpan: "   , element subjectSpan
        , string " predicateSpan: " , element predicateSpan
        , string " valueSpan: "     , element valueSpan
        , string " concatSpan: "    , element concatSpan
        , element calculate
        ]

-- End of file.
