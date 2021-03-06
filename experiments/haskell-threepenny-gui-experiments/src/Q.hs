{-
Created       : 2014 Aug 19 (Tue) 17:42:46 by Harold Carr.
Last Modified : 2016 Feb 04 (Thu) 20:16:57 by Harold Carr.
-}

{-# OPTIONS_GHC -fno-warn-unused-do-bind #-}
{-# LANGUAGE OverloadedStrings #-}

module Q where

import           Database.HSparql.Connection
import           Database.HSparql.QueryGenerator

endPoint :: String
endPoint = "http://localhost:3029/ds/query"

q0 :: IO (Maybe [[BindingValue]])
q0 = qAll endPoint

qAll :: String -> IO (Maybe [[BindingValue]])
qAll url = selectQuery url q
  where
    q = do
        s <- var; p <- var; o <- var
        triple s p o
        limit 2
        return SelectQuery { queryVars = [s, p, o] }

-- End of file.
