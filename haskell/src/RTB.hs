{-
Created       : 2014 Jul 17 (Thu) 08:38:10 by Harold Carr.
Last Modified : 2014 Aug 16 (Sat) 16:43:57 by Harold Carr.

- based on
  - http://stackoverflow.com/questions/24784883/using-threepenny-gui-reactive-in-client-server-programming
  - threepenny-gui MissingDollars sample
-}

-- TODO : expand/contract

{-# LANGUAGE RecursiveDo #-}

module RTB where

import qualified Data.Map                    as Map
import           Data.Maybe                  (fromJust)
import           Data.RDF.Types              (Node (..))
import qualified Data.Text                   as T (pack)
import           Database.HSparql.Connection
import           Debug.Trace
import qualified Graphics.UI.Threepenny      as UI
import           Graphics.UI.Threepenny.Core
import           RTBQ

hcDebug :: c -> String -> c
hcDebug = flip trace

defaultEndPoint :: String
defaultEndPoint = "http://localhost:3030/ds/query"

data SPOType = SUB | PRE | OBJ deriving Eq

instance Show SPOType where
    show SUB = "?subject"
    show PRE = "?predicate"
    show OBJ = "?object"

main :: IO ()
main = startGUI defaultConfig $ \w -> do
    return w # set title "RDF Triple Browser"
    getBody w #+ [ mkLayout ]
    return ()

mkLayout :: UI Element
mkLayout  = mdo
    -- input elements
    sparqlEndpointURL <- UI.input  # set (attr "size") "175" # set (attr "type") "text"
                                   # set (attr "value") defaultEndPoint
    submitBtn         <- UI.button #+ [string "submit"]

    -- input and display elements and more
    (subLBSelection, subClrBtn, subLayout, eSubLBSelection, bSubDB, hSubFillLB) <- mkSPOPanel SUB
    (preLBSelection, preClrBtn, preLayout, ePreLBSelection, bPreDB, hPreFillLB) <- mkSPOPanel PRE
    (objLBSelection, objClrBtn, objLayout, eObjLBSelection, bObjDB, hObjFillLB) <- mkSPOPanel OBJ

    -- display elements
    frame    <- UI.frame # set (attr "name")   "top"
                         # set (attr "target") "top"
                         # set (attr "src")    "http://haroldcarr.com/"
    frameset <- UI.frameset #+ [ element frame ]

    -- action

    -- SUBMIT
    on UI.click submitBtn $ \_ -> do
        liftIO $ putStrLn "submit"
        query aTrueBoundNodePair aTrueBoundNodePair aTrueBoundNodePair
        return ()

    -- SUBJECT
    on UI.click subClrBtn   $ \_  -> do
        element subLBSelection # set value (show SUB);
        slt SUB Nothing True
    onEvent eSubLBSelection $ \mk ->
        slt SUB mk      False

    -- PREDICATE
    on UI.click preClrBtn   $ \_  -> do
        element preLBSelection # set value (show PRE);
        slt PRE Nothing True
    onEvent ePreLBSelection $ \mk ->
        slt PRE mk      False

    -- OBJECT
    on UI.click objClrBtn   $ \_  -> do
        element objLBSelection # set value (show OBJ);
        slt OBJ Nothing True
    onEvent eObjLBSelection $ \mk ->
        slt OBJ mk      False

    let query :: (Bool, BindingValue) -> (Bool, BindingValue) -> (Bool, BindingValue) -> UI ()
        query s p o = do
            url <- get value sparqlEndpointURL;
            liftIO $ putStrLn ("query " ++ show url ++ " " ++ show s ++" " ++ show p ++ " " ++ show o)
            (s',p',o') <- liftIO $ query2 url s p o
            -- These are the MAGIC steps.  A Handler feeds events to its corresponding Event (from newEvent)
            mapM_ liftIO [hSubFillLB s', hPreFillLB p', hObjFillLB o']
            return ()

        setSelAndFrame lbSel k db0 = do
            element lbSel # set value        v
            element frame # set (attr "src") v
          where
            v = (fst (fromJustLookup k db0))

        fromJustLookup n db0 = fromJust $ dbLookup n db0
        sndLookup      n db0 = (False, snd (fromJustLookup n db0))

        varOrSelection :: String -> DB DI -> (Bool, BindingValue)
        varOrSelection lbSel db0 =
            if lbSel `elem` [show SUB, show PRE, show OBJ]
                then aTrueBoundNodePair
                else sndLookup 0 db0

        slt :: SPOType -> Maybe DBKey -> Bool -> UI ()
        slt spoType mk isClear = do
            liftIO $ putStrLn ("slt " ++ show spoType ++ " " ++ show mk)
            [sDB   , pDB   , oDB   ] <- mapM currentValue [bSubDB        , bPreDB        , bObjDB        ]
            [sLBSel, pLBSel, oLBSel] <- mapM (get value)  [subLBSelection, preLBSelection, objLBSelection]
            case mk of
                Just k -> case spoType of
                                 SUB -> setSelAndFrame subLBSelection k sDB
                                 PRE -> setSelAndFrame preLBSelection k pDB
                                 OBJ -> setSelAndFrame objLBSelection k oDB
                _      -> element frame
            let tval     = aTrueBoundNodePair
                fval     = sndLookup (fromJust mk)
                pick spo lbSel db0 = if spoType == spo then
                                         if isClear then tval else fval db0
                                         else varOrSelection lbSel db0
            query (pick SUB sLBSel sDB)
                  (pick PRE pLBSel pDB)
                  (pick OBJ oLBSel oDB)

        aTrueBoundNodePair   :: (Bool, BindingValue)
        aTrueBoundNodePair   =  (True, aBoundNode)

    -- layout
    grid [ [ row [ element sparqlEndpointURL, element submitBtn ] ]
         , [ row [ element subLayout, element preLayout, element objLayout ] ]
         , [ element frameset ]
         ]

mkSPOPanel :: SPOType
           -> UI ( UI.Element -- current selection
                 , UI.Element -- clrBtn
                 , UI.Element -- layout
                 , Event (Maybe DBKey)
                 , Behavior (DB DI)
                 , Handler [(String, BindingValue)]
                 )
mkSPOPanel spoType = mdo
    -- GUI elements
    lbSelection <- UI.input  # set (attr "size") "40" # set (attr "type") "text"
                             # set value (show spoType)
    clrBtn      <- UI.button #+ [string "*"]
    xpdBtn      <- UI.button #+ [string "+"] # set value "+"
    listBox     <- UI.listBox  bLBItems bLBSelection bDisplayDI
    element listBox # set (attr "size") "10" # set style [("width","300px")]

    let eXpdBtn      :: Event ()
        eXpdBtn      = UI.click xpdBtn

        eLBSelection :: Event (Maybe DBKey)
        eLBSelection = rumors $ UI.userSelection listBox

    onEvent eXpdBtn $ \_ -> do
        current <- get value xpdBtn
        let next = if current == "+" then "-" else "+"
        element xpdBtn # set text next # set value next `hcDebug` ("onEvent eXpdBtn c " ++ current ++ " n " ++ next)

    (eFillLB, hFillLB) <- liftIO newEvent

    -- bDB :: Behavior (DB DI)
    bDB <- accumB dbEmpty $ concatenate <$> unions
        [ dbFill  <$> eFillLB
        , dbFill' <$  eXpdBtn
        ]

    -- bLBSelection :: Behavior (Maybe DBKey)
    bLBSelection <- stepper Nothing eLBSelection `hcDebug` "stepper"

    let dbFill         :: [(String,BindingValue)] -> DB DI -> DB DI
        dbFill ss _    = foldr dbCreate dbEmpty ss

        dbFill' :: DB DI -> DB DI
        dbFill' d@(DB newkey db0) =
            let (s1,bn) = fromJust (dbLookup 0 d)
                (s2,_ ) = extract bn
                xpd     = length s1 == length s2
            in DB newkey $ Map.map (\(s,b) -> ((if xpd then "lengthen" else "shorten") ++ s,b)) db0

        bLookup        :: Behavior (DBKey -> Maybe DI)
        bLookup        = flip dbLookup <$> bDB `hcDebug` "bLookup"

        bDisplayDI     :: Behavior (DBKey -> UI Element)
        bDisplayDI     = (UI.string .) <$> (maybe "" showDI .) <$> bLookup

        bLBItems       :: Behavior [DBKey]
        bLBItems       = dbKeys <$> bDB

    layout <- column [ element lbSelection
                     , row [ element clrBtn, element xpdBtn ]
                     , element listBox
                     ]

    return (lbSelection, clrBtn, layout, eLBSelection, bDB, hFillLB)

aBoundNode :: BindingValue
aBoundNode =  Bound (UNode (T.pack "A DUMMY NODE"))

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
dbLookup key   (DB _      db0) =                 Map.lookup    key   db0 `hcDebug` ("dbLookup " ++ show key)

------------------------------------------------------------------------------
-- What is stored in data base

type DI = (String, BindingValue)

showDI :: DI -> String
showDI (x,_) = x

-- End of file.
