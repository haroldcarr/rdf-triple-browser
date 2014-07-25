{-
Created       : by threepenny-gui MissingDollars sample.
Last Modified : 2014 Jul 24 (Thu) 20:47:59 by Harold Carr.
-}

module MD where

import           Control.Monad
import qualified Graphics.UI.Threepenny      as UI
import           Graphics.UI.Threepenny.Core

main :: IO ()
main = do
    (subFRP : preFRP : valFRP : _) <- replicateM 3 $ mkLstBoxFRP valuesSupply
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
        element currentSub # set value (s ++ sp);
        element currentPre # set value (p ++ sp);
        element currentVal # set value (v ++ sp);
        return ()
    }
    -- init values
    updateDisplay "enter SPARQL endpoint URL" "?subject" "?predicate" "?value"

    -- submit button
    on UI.click submit $ \_ -> do
        sparql <- get value sparqlEndpointURL
        s      <- get value currentSub
        p      <- get value currentPre
        v      <- get value currentVal
        updateDisplay sparql s p v

    (subListBox, subList) <- mkListBox hSubFillListBox bSubFillListBox
    (preListBox, preList) <- mkListBox hPreFillListBox bPreFillListBox
    (valListBox, valList) <- mkListBox hValFillListBox bValFillListBox

    grid [ [ row [ element sparqlEndpointURL, element submit ] ]
         , [ row [ column [ row [ element ddSub, element currentSub ]
                          , element bSub
                          , element subListBox
                          , element subList
                          ]
                 , column [ row [ element ddPre, element currentPre ]
                          , element bPre
                          , element preListBox
                          , element preList
                          ]
                 , column [ row [ element ddVal, element currentVal ]
                          , element bVal
                          , element valListBox
                          , element valList
                          ]
                 ]
           ]
         ]

mkLstBoxFRP :: ([Char] -> IO a) -> IO (Handler a, Behavior a)
mkLstBoxFRP valuesSupply0 = do
    (eventFillListBox, handlerFillListBox) <- newEvent
    initialList         <- valuesSupply0 ""
    behaviorFillListBox <- stepper initialList eventFillListBox
    return (handlerFillListBox, behaviorFillListBox)

mkListBox :: ([String] -> IO a) -> Behavior [String] -> UI (UI.ListBox String, Element)
mkListBox hSubFillListBox bSubFillListBox = do
    subList    <- UI.ul
    subListBox <- UI.listBox bSubFillListBox
                             (pure Nothing)
                             (pure $ \it -> UI.span # set text it)
    element subListBox # set (attr "size") "10" # set style [("width","300px")]

    on UI.selectionChange (getElement subListBox) $ \x -> case x of
        Nothing -> return ()
        Just ix -> do items <- currentValue bSubFillListBox
                      let it = items !! ix
                      liftIO $ valuesSupply it >>= hSubFillListBox
                      element subList #+ [UI.li # set html it]
                      UI.setFocus $ getElement subListBox
    return (subListBox, subList)

valuesSupply :: String -> IO [String]
valuesSupply x = do
    putStrLn $ "YES: " ++ x
    return [x ++ show i | i <- [0..9::Int]]

-- End of file.
