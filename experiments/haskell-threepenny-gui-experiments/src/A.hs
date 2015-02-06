{-
Created       : 2015 Feb 05 (Thu) 14:56:08 by Harold Carr.
Last Modified : 2015 Feb 05 (Thu) 18:01:53 by Harold Carr.
-}

module A where

import           Control.Concurrent     (forkIO)
import           Graphics.UI.Threepenny
import           Network
import           System.IO              (hClose)

main :: IO ()
main = do
    (eAccept, hAccept) <- newEvent
    bAccept <- stepper "" eAccept
    forkIO (listenAndAccept hAccept 6789)
    forkIO (listenAndAccept hAccept 9876)
    startGUI defaultConfig $ \win -> do
        entree <- entry bAccept
        element entree # set (attr "size") "10" # set style [("width","200px")]
        getBody win #+ [element entree]
        return ()

listenAndAccept :: (String -> IO a) -> PortNumber -> IO ()
listenAndAccept hAccept bindAddr = do
    s <- listenOn $ PortNumber bindAddr
    acceptLoop hAccept bindAddr s
    return ()

acceptLoop :: (String -> IO a) -> PortNumber -> Socket -> IO b
acceptLoop hAccept bindAddr s = loop
  where
    loop = do
        (h, hostname, portNumber) <- accept s
        hClose h
        hAccept $ show bindAddr ++ " " ++ hostname ++ " " ++ show portNumber
        loop

-- End of file.
