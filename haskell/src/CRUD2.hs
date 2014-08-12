{-
Created       : by threepenny-gui/samples/CRUD
Last Modified : 2014 Aug 12 (Tue) 16:21:21 by Harold Carr.
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
main = do
    startGUI defaultConfig $ \window -> do
        return window # set title "RDF Triple Browser"
        getBody window #+ [ mkSPVPanel "?subject" ]
        return ()

mkSPVPanel :: String
              -> UI Element
mkSPVPanel spvType = mdo
    -- GUI elements
    doItBtn     <- UI.button #+ [string "Do It!"]
    addToLBBtn  <- UI.button #+ [string "Add To List Box"]
    listBox     <- UI.listBox  bLBItems bLBSelection bDisplayDI
    lbSelection <- dataItem    bLBSelectionDI
    lbSelection2<- dataItem2   bLBSelectionDI
    let uiDI = grid [[string spvType, element lbSelection, string "FOO", element lbSelection2]]
    element listBox # set (attr "size") "20" # set style [("width","800px")]

    -- events and behaviors
    let eLBSelection :: Event (Maybe DBKey)
        eLBSelection = rumors $ UI.userSelection listBox
        eAddToLB     :: Event ()
        eAddToLB     = UI.click addToLBBtn

    (eFillLB, hFillLB) <- liftIO newEvent

    let query :: (Maybe DBKey) -> (Maybe DI) -> UI ()
        query mk mdi = do (r,_,_) <- liftIO $ doRDFQuery mk mdi bDB
                          liftIO $ hFillLB r
                          return ()

    on UI.click doItBtn $ \_ -> query Nothing Nothing

    onEvent eLBSelection $ \mk ->
        (currentValue bLBSelectionDI) >>= \v -> query mk v -- probably not the way to get the selection to the query

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

-- | Data item widget
dataItem :: Behavior (Maybe DI) -> UI Element
dataItem bItem = do
    entry1 <- UI.entry $ fst . fromMaybe ("",Bound $ UNode (T.pack "foo")) <$> bItem -- TODO something other than Bound/UNode as "empty"
    element entry1 # set style [("width", "400px")]
    return $ getElement  entry1

dataItem2 :: Behavior (Maybe DI) -> UI Element
dataItem2 bItem = do
    entry1 <- UI.entry $ (++ "-TWO") . fst . fromMaybe ("",Bound $ UNode (T.pack "foo")) <$> bItem
    element entry1 # set style [("width", "400px")]
    return $ getElement  entry1

------------------------------------------------------------------------------

doRDFQuery :: Maybe DBKey
              -> Maybe DI
              -> Behavior (DB DI)
              -> IO ( [(String, BindingValue)]
                    , [(String, BindingValue)]
                    , [(String, BindingValue)]
                    )
doRDFQuery mk mdi bdb = do
    putStrLn ""
    putStrLn ("MK : " ++ (show mk))
    putStrLn ("MDI: " ++ (show mdi))
    db0 <- (currentValue bdb)
    case mk of
        (Just k) -> do let v = lookup k db0
                       putStrLn ("DBValue: " ++ (show v))
                       case v of
                           Just b -> do rrr <- ttwv b
                                        putStrLn (show rrr)
                                        return rrr
                           Nothing             -> ttt
        _        -> do putStrLn "key is NOTHING"
                       ttt

-- End of file.
