{-
Created       : 2014 Jul 17 (Thu) 04:21:11 by Max Taldykin.
Last Modified : 2014 Aug 09 (Sat) 08:07:32 by Harold Carr.
http://stackoverflow.com/questions/24784883/using-threepenny-gui-reactive-in-client-server-programming
-}

module TaldykinFRPAndEventExample where

import           Graphics.UI.Threepenny as UI

{-
Event represents a stream of events in time.
Like an infinite list of values tagged with their time of occurence: `type Event a = [(Time,a)]`

`Behavior a` represents time varying value. Like: `type Behavior a = Time -> a`

Event handler is function given event value.  Performs some computation. `type Handler a = a -> IO ()`
-}

main :: IO ()
main = do
    -- newEvent :: IO (Event a, Handler a)
    (eFillLB, hFillLB) <- newEvent
    -- valuesSupply :: String -> IO [String]
    initialList <- valuesSupply ""
    -- stepper :: MonadIO m => a -> Event a -> m (Behavior a)
    -- Construct time-varying function from initial value and stream of new values.
    bFillLB <- stepper initialList eFillLB

    startGUI defaultConfig $ \win -> do
        list <- ul
        -- listBox :: Ord a => Behavior [a]               -- list of items
        --                  -> Behavior (Maybe a)         -- selected item
        --                  -> Behavior (a -> UI Element) -- display for an item
        --                  -> UI (ListBox a)             -- created ListBox
        lstBox <- listBox bFillLB
                          (pure Nothing)
                          (pure $ \it -> UI.span # set text it)
        element lstBox # set (attr "size") "10" # set style [("width","200px")]

        getBody win #+ [element lstBox, element list]
        setFocus $ getElement lstBox

        -- on :: (element -> Event a) -> element -> (a -> UI void) -> UI ()
        -- selectionChange :: Element -> Event (Maybe Int)
        on selectionChange (getElement lstBox) $ \x -> case x of
            Nothing -> return ()
            Just ix -> do -- currentValue :: MonadIO m => Behavior a -> m a
                          items <- currentValue bFillLB
                          let it = items !! ix
                          liftIO $ valuesSupply it >>= hFillLB
                          element list #+ [li # set html it]
                          setFocus $ getElement lstBox


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
