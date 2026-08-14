{-# LANGUAGE ScopedTypeVariables #-}

module EndpointReachable where

import Control.Exception      (try)
import Network.HTTP.Client
import Network.HTTP.Client.TLS (tlsManagerSettings)

endpointReachable :: String -> IO Bool
endpointReachable url = do
  manager <- newManager tlsManagerSettings
  result  <- try $ do
    req0    <- parseRequest url
    let req  = req0 { responseTimeout = responseTimeoutMicro 1000000
                    , checkResponse   = \_ _ -> pure () }
    withResponse req manager $ \_ -> pure ()
  pure $ case result of
    Right ()                         -> True
    Left (_ :: HttpException)        -> False


