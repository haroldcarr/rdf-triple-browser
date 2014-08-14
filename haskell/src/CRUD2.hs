{-
Created       : by threepenny-gui/samples/CRUD
Last Modified : 2014 Aug 13 (Wed) 20:20:29 by Harold Carr.
-}

{-# LANGUAGE RecursiveDo #-}

module CRUD2 where

import qualified Data.Map                    as Map
import           Data.Maybe
import           Data.RDF.Types              (Node (..))
import qualified Data.Text                   as T (pack)
import           Database.HSparql.Connection
import qualified Graphics.UI.Threepenny      as UI
import           Graphics.UI.Threepenny.Core
import           RTBQ

------------------------------------------------------------------------------

main :: IO ()
main =
    startGUI defaultConfig $ \window -> do
        return window # set title "RDF Triple Browser"
        getBody window #+ [ mkSPOPanel "?subject" ]
        return ()

mkSPOPanel :: String -> UI Element
mkSPOPanel spoType = mdo
    let bad = ("NOT SUPPOSED TO HAPPEN", Bound (UNode (T.pack "BAD")))
        decide db0 | dbSize db0 > 1 = spoType
                   | otherwise      = fst $ fromMaybe bad (dbLookup 0 db0)

        dataItem :: Behavior (Maybe DI) -> UI Element
        dataItem _ = do
            entry1 <- UI.entry $ decide <$> bDB
            element entry1 # set style [("width", "800px")]
            return $ getElement  entry1

    -- GUI elements
    sparqlEndpointURL <- UI.input  # set (attr "size") "175" # set (attr "type") "text"
                                   # set (attr "value") "http://localhost:3030/ds/query"
    submitBtn   <- UI.button #+ [string "Do It!"]
    addToLBBtn  <- UI.button #+ [string "Add To List Box"]
    clearBtn    <- UI.button #+ [string "*"]
    lbSelection <- dataItem    bLBSelectionDI
    listBox     <- UI.listBox  bLBItems bLBSelection bDisplayDI
    element listBox # set (attr "size") "20" # set style [("width","800px")]

    -- events and behaviors
    let eAddToLB     :: Event ()
        eAddToLB     = UI.click addToLBBtn

        eLBSelection :: Event (Maybe DBKey)
        eLBSelection = rumors $ UI.userSelection listBox

    (eFillLB, hFillLB) <- liftIO newEvent

    let query :: String -> Maybe DBKey -> UI ()
        query url mk = do
            (r,_,_) <- liftIO $ doRDFQuery url mk bDB
            liftIO $ hFillLB r
            return ()

        queryNothing :: () -> UI ()
        queryNothing _ = do
            sparql <- get value sparqlEndpointURL
            query sparql Nothing

    on UI.click submitBtn queryNothing
    on UI.click clearBtn  queryNothing

    onEvent eLBSelection $ \mk -> do
        sparql <- get value sparqlEndpointURL
        query sparql mk

    -- database
    let dbFill                    :: [(String,BindingValue)] -> DB DI -> DB DI
        dbFill    ss _            = foldr dbCreate  dbEmpty ss

        dbAddToLB                 :: DB DI -> DB DI
        dbAddToLB (DB newkey db0) = DB newkey $ Map.map (\(x,y) -> (x ++ "xx", y)) db0

    -- bDB :: Behavior (DB DI)
    bDB <- accumB dbEmpty $ concatenate <$> unions
        [ dbFill    <$> eFillLB
        , dbAddToLB <$  eAddToLB
        ]

    -- selection
    -- bLBSelection :: Behavior (Maybe DBKey)
    bLBSelection <- stepper Nothing eLBSelection

    let bLookup :: Behavior (DBKey -> Maybe DI)
        bLookup = flip dbLookup <$> bDB

        bDisplayDI :: Behavior (DBKey -> UI Element)
        bDisplayDI = (UI.string .) <$> (maybe "" showDI .) <$> bLookup

        bLBItems :: Behavior [DBKey]
        bLBItems = dbKeys <$> bDB

        bLBSelectionDI :: Behavior (Maybe DI)
        bLBSelectionDI = (=<<) <$> bLookup <*> bLBSelection

    -- GUI layout
    grid [ [ row [ element sparqlEndpointURL, element submitBtn, element addToLBBtn ] ]
         , [ row [ element clearBtn, element lbSelection ] ]
         , [ element listBox ]
         ]

------------------------------------------------------------------------------
-- DB Model

type DBKey = Int
data DB a  = DB { nextKey :: !Int, db :: Map.Map DBKey a }

dbEmpty  :: DB a
dbEmpty  = DB 0 Map.empty

dbSize   :: DB a -> Int
dbSize   = Map.size . db

dbKeys   :: DB a -> [DBKey]
dbKeys   = Map.keys . db

dbCreate :: a -> DB a -> DB a
dbCreate x     (DB newkey db0) = DB (newkey+1) $ Map.insert newkey x db0

dbLookup :: DBKey -> DB a -> Maybe a
dbLookup key   (DB _      db0) = Map.lookup                       key      db0

------------------------------------------------------------------------------
-- What is stored in data base

type DI = (String, BindingValue)

showDI :: DI -> String
showDI (x,_) = x

------------------------------------------------------------------------------

doRDFQuery :: String
              -> Maybe DBKey
              -> Behavior (DB DI)
              -> IO ( [(String, BindingValue)]
                    , [(String, BindingValue)]
                    , [(String, BindingValue)]
                    )
doRDFQuery url mk bdb = do
    putStrLn ""
    putStrLn url
    putStrLn ("MK : " ++ show mk)
    db0 <- currentValue bdb
    case mk of
        (Just k) -> do let v = dbLookup k db0
                       putStrLn ("DBValue: " ++ show v)
                       case v of
                           Just b -> do rrr <- ttwv url b
                                        print rrr
                                        return rrr
                           Nothing             -> ttt url
        _        -> do putStrLn "key is NOTHING"
                       ttt url

-- End of file.
