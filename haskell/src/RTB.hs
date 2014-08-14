{-
Created       : 2014 Jul 17 (Thu) 08:38:10 by Harold Carr.
Last Modified : 2014 Aug 13 (Wed) 19:30:53 by Harold Carr.

- based on
  - http://stackoverflow.com/questions/24784883/using-threepenny-gui-reactive-in-client-server-programming
  - threepenny-gui MissingDollars sample
-}

{-# LANGUAGE RecursiveDo #-}

module RTB where

import           Control.Monad
import qualified Graphics.UI.Threepenny      as UI
import           Graphics.UI.Threepenny.Core
import           RTBQ

data SPVType = SUB | PRE | VAL

main :: IO ()
main = startGUI defaultConfig $ \w -> do
    return w # set title "RDF Triple Browser"
    getBody w #+ [ mkLayout ]
    return ()

mkLayout :: UI Element
mkLayout  = mdo
    -- input elements
    sparqlEndpointURL <- UI.input  # set (attr "size") "175" # set (attr "type") "text"
    submit            <- UI.button #+ [string "submit"]
    (bSub  : bPre  : bVal  : _)
        <- replicateM 3 $ UI.button #+ [string "+"]

    (ddSub : ddPre : ddVal : _ )
        <- replicateM 3 $ UI.select # set (attr "width") "10"
                                    #+ map (\x -> UI.option # set html x) ["clear", "show all", "<-", "->" ]

    -- display elements
    (currentSub : currentPre : currentVal : _)
        <- replicateM 3 $ UI.input # set (attr "size") "40" # set (attr "type") "text"

    (subListBox, hSubFillListBox) <- mkListBox SUB doSelectionQuery
    (preListBox, hPreFillListBox) <- mkListBox PRE doSelectionQuery
    (valListBox, hValFillListBox) <- mkListBox VAL doSelectionQuery

    -- update procedure
    let updateDisplay sp s p v = do {
        element sparqlEndpointURL # set value sp;
        element currentSub # set value s;
        element currentPre # set value p;
        element currentVal # set value v;
        return ()
    }

    -- initial values
    updateDisplay "enter SPARQL endpoint URL" "?subject" "?predicate" "?value"

    let doQuery = do {
        sparql <- get value sparqlEndpointURL;
        s      <- get value currentSub;
        p      <- get value currentPre;
        v      <- get value currentVal;
        (sr,pr,vr) <- liftIO $ doRDFQuery sparql s p v;
        liftIO $ hSubFillListBox sr;
        liftIO $ hPreFillListBox pr;
        liftIO $ hValFillListBox vr;
        return ()
    }

    -- submit button
    on UI.click submit $ \_ -> do
        sparql <- get value sparqlEndpointURL
        updateDisplay sparql "?subject" "?predicate" "?value"
        doQuery

    frame <- UI.frame # set (attr "name")   "top"
                      # set (attr "target") "top"
                      # set (attr "src")    "http://haroldcarr.com/"

    frameset <- UI.frameset #+ [ element frame ]

    let doSelectionQuery (spv, selection) = do {
        element (case spv of
                    SUB -> currentSub
                    PRE -> currentPre
                    VAL -> currentVal)
                # set value selection;
        element frame # set (attr "src") selection;
        doQuery
    }

    grid [ [ row [ element sparqlEndpointURL, element submit ] ]
         , [ row [ column [ row [ element ddSub, element currentSub ]
                          , element bSub
                          , element subListBox
                          ]
                 , column [ row [ element ddPre, element currentPre ]
                          , element bPre
                          , element preListBox
                          ]
                 , column [ row [ element ddVal, element currentVal ]
                          , element bVal
                          , element valListBox
                          ]
                 ]
           ]
         , [ element frameset ]
         ]

mkListBox :: SPVType
             -> ((SPVType,String) -> UI ())
             -> UI (UI.ListBox String, Handler [String])
mkListBox spvType doSelectionQuery = do
    (eventFillListBox, handlerFillListBox) <- liftIO newEvent
    behaviorFillListBox <- stepper [] eventFillListBox
    listBox <- UI.listBox behaviorFillListBox
                          (pure Nothing)
                          (pure $ \it -> UI.span # set text it)
    element listBox # set (attr "size") "10" # set style [("width","300px")]

    on UI.selectionChange (getElement listBox) $ \x -> case x of
        Nothing -> return ()
        Just i  -> do items <- currentValue behaviorFillListBox
                      let selection = items !! i
                      doSelectionQuery (spvType, selection)
                      UI.setFocus $ getElement listBox
    return (listBox, handlerFillListBox)

------------------------------------------------------------------------------

doRDFQuery :: String -> String -> String -> String -> IO ([String],[String],[String])
doRDFQuery _ _ _ _ = tt

-- End of file.
