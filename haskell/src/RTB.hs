{-
Created       : 2014 Jul 17 (Thu) 08:38:10 by Harold Carr.
Last Modified : 2014 Aug 02 (Sat) 09:44:56 by Harold Carr.

- based on
  - http://stackoverflow.com/questions/24784883/using-threepenny-gui-reactive-in-client-server-programming
  - threepenny-gui MissingDollars sample
-}

module RTB where

import           Control.Monad
import qualified Graphics.UI.Threepenny      as UI
import           Graphics.UI.Threepenny.Core
import           RTBQ

data SPVType = SUB | PRE | VAL

main :: IO ()
main = do
    (subFRP : preFRP : valFRP : _) <- replicateM 3 mkListBoxFRP
    startGUI defaultConfig $ \w -> do
        return w # set title "MD"
        getBody w #+ [row [ mkLayout subFRP preFRP valFRP ] ]
        return ()

mkLayout ::    (Handler [String], Behavior [String])
            -> (Handler [String], Behavior [String])
            -> (Handler [String], Behavior [String])
            -> UI Element
mkLayout (hSubFillListBox, bSubFillListBox)
         (hPreFillListBox, bPreFillListBox)
         (hValFillListBox, bValFillListBox) = do
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

    subListBox <- mkListBox SUB bSubFillListBox doSelectionQuery
    preListBox <- mkListBox PRE bPreFillListBox doSelectionQuery
    valListBox <- mkListBox VAL bValFillListBox doSelectionQuery

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

mkListBoxFRP :: IO (Handler [String], Behavior [String])
mkListBoxFRP = do
    (eventFillListBox, handlerFillListBox) <- newEvent
    behaviorFillListBox <- stepper [] eventFillListBox
    return (handlerFillListBox, behaviorFillListBox)

mkListBox :: SPVType
             -> Behavior [String]
             -> ((SPVType,String) -> UI ())
             -> UI (UI.ListBox String)
mkListBox spvType bFillListBox doSelectionQuery = do
    listBox <- UI.listBox bFillListBox
                          (pure Nothing)
                          (pure $ \it -> UI.span # set text it)
    element listBox # set (attr "size") "10" # set style [("width","300px")]

    on UI.selectionChange (getElement listBox) $ \x -> case x of
        Nothing -> return ()
        Just i  -> do items <- currentValue bFillListBox
                      let selection = items !! i
                      doSelectionQuery (spvType, selection)
                      UI.setFocus $ getElement listBox
    return listBox

------------------------------------------------------------------------------

doRDFQuery :: String -> String -> String -> String -> IO ([String],[String],[String])
{-
doRDFQuery endpoint s p v = do
    putStrLn endpoint
    let s' = [ s ++ show i | i <- [0..19::Int]]
    let p' = [ p ++ show i | i <- [0..19::Int]]
    let v' = [ v ++ show i | i <- [0..19::Int]]
    return (s',p',v')
-}
doRDFQuery _ _ _ _ = tt

-- End of file.