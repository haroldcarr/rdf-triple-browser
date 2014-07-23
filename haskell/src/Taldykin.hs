{-
Created       : 2014 Jul 17 (Thu) 04:21:11 by Max Taldykin.
Last Modified : 2014 Jul 23 (Wed) 07:33:15 by Harold Carr.
http://stackoverflow.com/questions/24784883/using-threepenny-gui-reactive-in-client-server-programming
-}

module Taldykin where

import           Graphics.UI.Threepenny as UI

main :: IO ()
main = do
    (evFillList, doFillList) <- newEvent
    initialList <- valuesSupply ""
    behFillList <- stepper initialList evFillList

    startGUI defaultConfig $ \win -> do
        list <- ul
        sel <- listBox behFillList
                       (pure Nothing)
                       (pure $ \it -> UI.span # set text it)

        getBody win #+ [grid [[element list, element sel]]]
        setFocus $ getElement sel

        on selectionChange (getElement sel) $ \x -> case x of
            Nothing -> return ()
            Just ix -> do items <- currentValue behFillList
                          let it = items !! ix
                          liftIO $ valuesSupply it >>= doFillList
                          element list #+ [li # set html it]
                          setFocus $ getElement sel


valuesSupply :: String -> IO [String]
valuesSupply x = do
    putStrLn "YES"
    return [x ++ show i | i <- [0..9::Int]]
{- This works
valuesSupply x = do
    putStr ("last: " ++ show x ++ "; enter something new: ")
    r <- getLine
    return [r]
-}
-- End of file.
