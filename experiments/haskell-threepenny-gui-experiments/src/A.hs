{-
Created       : 2015 Feb 05 (Thu) 14:56:08 by Harold Carr.
Last Modified : 2015 Feb 05 (Thu) 17:22:53 by Harold Carr.
-}

module A where

import           Control.Concurrent     (forkIO)
import           Graphics.UI.Threepenny as UI
import           Network

main :: IO ()
main = do
    (ev, hnd) <- newEvent
    bFillLB <- stepper "" ev
    forkIO (a1 hnd 8888)
    forkIO (a1 hnd 9999)
    startGUI defaultConfig $ \win -> do
        lstBox <- listBox bFillLB
                          (pure Nothing)
                          (pure $ \it -> UI.span # set text [it])
        element lstBox # set (attr "size") "10" # set style [("width","200px")]
        getBody win #+ [element lstBox]
        return ()

a1 :: (String -> IO a) -> PortNumber -> IO ()
a1 hnd bindAddr = do
    s <- listenOn  $ PortNumber bindAddr
    acceptLoop hnd bindAddr s
    return ()

acceptLoop :: (String -> IO a) -> PortNumber -> Socket -> IO b
acceptLoop hnd bindAddr s = do
    putStrLn "about to accept"
    (_, hostname, portNumber) <- accept s
    putStrLn hostname
    putStrLn $ show portNumber
    hnd $ show bindAddr
    acceptLoop hnd bindAddr s

-- End of file.
