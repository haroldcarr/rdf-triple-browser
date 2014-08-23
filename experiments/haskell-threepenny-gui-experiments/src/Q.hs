{-
Created       : 2014 Aug 19 (Tue) 17:42:46 by Harold Carr.
Last Modified : 2014 Aug 19 (Tue) 18:01:13 by Harold Carr.
-}

{-# LANGUAGE OverloadedStrings #-}

module Q where

import           Database.HSparql.Connection
import           Database.HSparql.QueryGenerator

endPoint :: String
endPoint = "http://localhost:3029/ds/query"

q0 = qAll endPoint

qAll :: String -> IO (Maybe [[BindingValue]])
qAll url = selectQuery url q
  where
    q = do
        s <- var; p <- var; o <- var
        triple s p o
        return SelectQuery { queryVars = [s, p, o] }

-- End of file.
