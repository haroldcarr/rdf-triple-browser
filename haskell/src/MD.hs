{-
Created       : by threepenny-gui MissingDollars sample.
Last Modified : 2014 Jul 24 (Thu) 20:01:24 by Harold Carr.
-}

module MD where

import           Control.Monad
import qualified Graphics.UI.Threepenny      as UI
import           Graphics.UI.Threepenny.Core

main :: IO ()
main = do
    frp <- mkLstBoxFRP valuesSupply
    startGUI defaultConfig $ \w -> do
        return w # set title "MD"
        getBody w #+ [row [ mkLayout frp ] ]
        return ()

mkLstBoxFRP :: ([Char] -> IO a) -> IO (Handler a, Behavior a)
mkLstBoxFRP valuesSupply0 = do
    (eventFillListBox, handlerFillListBox) <- newEvent
    initialList <- valuesSupply0 ""
    behaviorFillListBox <- stepper initialList eventFillListBox
    return (handlerFillListBox, behaviorFillListBox)

mkLayout :: (Handler [String], Behavior [String]) -> UI Element
mkLayout (handlerFillListBox, behaviorFillListBox) = do
    -- input elements
    sparqlEndpointURL <- UI.input  # set (attr "size") "80" # set (attr "type") "text"
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

    list <- UI.ul
    lstBox <- UI.listBox behaviorFillListBox
                         (pure Nothing)
                         (pure $ \it -> UI.span # set text it)
    element lstBox # set (attr "size") "10" # set style [("width","200px")]

    on UI.selectionChange (getElement lstBox) $ \x -> case x of
        Nothing -> return ()
        Just ix -> do items <- currentValue behaviorFillListBox
                      let it = items !! ix
                      liftIO $ valuesSupply it >>= handlerFillListBox
                      element list #+ [UI.li # set html it]
                      UI.setFocus $ getElement lstBox


    grid [ [ row [ element sparqlEndpointURL, element submit ] ]
         , [ row [ element ddSub, element currentSub
                 , element ddPre, element currentPre
                 , element ddVal, element currentVal
                 ]
           ]
         , [ row [ column [ element bSub
                          , element lstBox
                          , element list
                          ]
                 , column [ element bPre
                          ]
                 , column [ element bVal
                          ]
                 ]
           ]
         ]

valuesSupply :: String -> IO [String]
valuesSupply x = do
    putStrLn $ "YES: " ++ x
    return [x ++ show i | i <- [0..9::Int]]

-- End of file.
