{-
Created       : 2014 Jul 17 (Thu) 04:21:11 by Max Taldykin.
Last Modified : 2014 Jul 24 (Thu) 11:36:20 by Harold Carr.
http://stackoverflow.com/questions/24784883/using-threepenny-gui-reactive-in-client-server-programming
-}

module X where

import           Graphics.UI.Threepenny as UI

main :: IO ()
main = do
    (eventFillListBox, handlerFillListBox) <- newEvent
    initialList <- valuesSupply ""
    behaviorFillListBox <- stepper initialList eventFillListBox

    startGUI defaultConfig $ \win -> do
        list <- ul
        lstBox <- listBox behaviorFillListBox
                          (pure Nothing)
                          (pure $ \it -> UI.span # set text it)
        element lstBox # set (attr "size") "10" # set style [("width","200px")]

        getBody win #+ [element lstBox, element list]
        setFocus $ getElement lstBox

        on selectionChange (getElement lstBox) $ \x -> case x of
            Nothing -> return ()
            Just ix -> do items <- currentValue behaviorFillListBox
                          let it = items !! ix
                          liftIO $ valuesSupply it >>= handlerFillListBox
                          element list #+ [li # set html it]
                          setFocus $ getElement lstBox

------------------------------------------------------------------------------

valuesSupply :: String -> IO [String]
valuesSupply x = do
    putStrLn $ "YES: " ++ x
    return [x ++ show i | i <- [0..9::Int]]
{- This works
valuesSupply x = do
    putStr ("last: " ++ show x ++ "; enter something new: ")
    r <- getLine
    return [r]
-}
-- End of file.
