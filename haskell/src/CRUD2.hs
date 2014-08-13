{-
Created       : by threepenny-gui/samples/CRUD
Last Modified : 2014 Aug 12 (Tue) 19:48:28 by Harold Carr.
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
import           Prelude                     hiding (lookup)
import           RTBQ

------------------------------------------------------------------------------

main :: IO ()
main =
    startGUI defaultConfig $ \window -> do
        return window # set title "RDF Triple Browser"
        getBody window #+ [ mkSPVPanel "?subject" ]
        return ()

mkSPVPanel :: String
              -> UI Element
mkSPVPanel spvType = mdo
    let decide db = if dbSize db > 1
                       then "?"
                       else case lookup 0 db of
                                Just (s,_) -> s
                                Nothing    -> error "NOT SUPPOSED TO HAPPEN"

        dataItem :: Behavior (Maybe DI) -> UI Element
        dataItem bItem = do
            entry1 <- UI.entry $ decide <$> bDB
            element entry1 # set style [("width", "400px")]
            return $ getElement  entry1

    -- GUI elements
    doItBtn     <- UI.button #+ [string "Do It!"]
    addToLBBtn  <- UI.button #+ [string "Add To List Box"]
    listBox     <- UI.listBox  bLBItems bLBSelection bDisplayDI
    lbSelection <- dataItem    bLBSelectionDI
    let uiDI = grid [[string spvType, element lbSelection]]
    element listBox # set (attr "size") "20" # set style [("width","800px")]

    -- events and behaviors
    let eLBSelection :: Event (Maybe DBKey)
        eLBSelection = rumors $ UI.userSelection listBox
        eAddToLB     :: Event ()
        eAddToLB     = UI.click addToLBBtn

    (eFillLB, hFillLB) <- liftIO newEvent

    let query :: Maybe DBKey -> UI ()
        query mk = do (r,_,_) <- liftIO $ doRDFQuery mk bDB
                      liftIO $ hFillLB r
                      return ()

    on UI.click doItBtn $ \_ -> query Nothing

    onEvent eLBSelection query -- probably not the way to get the selection to the query

    -- database
    let dbFill                    :: [(String,BindingValue)] -> DB DI -> DB DI
        dbFill    ss _            = foldr create  emptydb ss

        dbAddToLB                 :: DB DI -> DB DI
        dbAddToLB (DB newkey db0) = DB newkey $ Map.map (\(x,y) -> (x ++ "xx", y)) db0

    -- bDB :: Behavior (DB DI)
    bDB <- accumB emptydb $ concatenate <$> unions
        [ dbFill    <$> eFillLB
        , dbAddToLB <$  eAddToLB
        ]

    -- selection
    -- bLBSelection :: Behavior (Maybe DBKey)
    bLBSelection <- stepper Nothing eLBSelection

    let bLookup :: Behavior (DBKey -> Maybe DI)
        bLookup = flip lookup <$> bDB

        bDisplayDI :: Behavior (DBKey -> UI Element)
        bDisplayDI = (UI.string .) <$> (maybe "" showDI .) <$> bLookup

        bLBItems :: Behavior [DBKey]
        bLBItems = keys <$> bDB

        bLBSelectionDI :: Behavior (Maybe DI)
        bLBSelectionDI = (=<<) <$> bLookup <*> bLBSelection

    -- GUI layout
    grid [
           [ uiDI ]
         , [ element listBox ]
         , [ element addToLBBtn]
         , [ element doItBtn ]
         ]

------------------------------------------------------------------------------
-- DB Model

type DBKey = Int
data DB a  = DB { nextKey :: !Int, db :: Map.Map DBKey a }

emptydb :: DB a
emptydb = DB 0 Map.empty

dbSize :: DB a -> Int
dbSize = Map.size . db

keys :: DB a -> [DBKey]
keys    = Map.keys . db

create :: a -> DB a -> DB a
create x     (DB newkey db0) = DB (newkey+1) $ Map.insert newkey x db0

lookup :: DBKey -> DB a -> Maybe a
lookup key   (DB _      db0) = Map.lookup                       key      db0

------------------------------------------------------------------------------
-- What is stored in data base

type DI = (String, BindingValue)

showDI :: DI -> String
showDI (x,_) = x

------------------------------------------------------------------------------

doRDFQuery :: Maybe DBKey
              -> Behavior (DB DI)
              -> IO ( [(String, BindingValue)]
                    , [(String, BindingValue)]
                    , [(String, BindingValue)]
                    )
doRDFQuery mk bdb = do
    putStrLn ""
    putStrLn ("MK : " ++ show mk)
    db0 <- currentValue bdb
    case mk of
        (Just k) -> do let v = lookup k db0
                       putStrLn ("DBValue: " ++ show v)
                       case v of
                           Just b -> do rrr <- ttwv b
                                        print rrr
                                        return rrr
                           Nothing             -> ttt
        _        -> do putStrLn "key is NOTHING"
                       ttt

-- End of file.
